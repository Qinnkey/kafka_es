package com.huisou.config;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Time;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.apache.kafka.common.metrics.stats.Count;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import tk.mybatis.mapper.common.base.delete.DeleteMapper;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 1.0用于和canal的客户端的删除，更新，修改的等操作，单个操作
 * 
 * 2.0 版本 elasticsearch 为了防止数据丢失，我们使用elasticsearch批量操作（批量操作的标准在1000~50000个
 * 数据之间，或者数据大小在5M 到15M之间）
 * @author Administrator
 * @Date 2017年10月12日 下午5:24:02
 *
 */
@SuppressWarnings("all")
@Component
public class KafkaConsumeToES implements Runnable{
	@Autowired
	private BulkProcessor bulk;
	
	private ObjectMapper mapper = new ObjectMapper();
	@Value(value="${elasticsearch.type}")
	private String type;
	@Value(value="${elasticsearch.index}")
	private String index;
	private Map<String, String> deleteMap = new ConcurrentHashMap<String, String>();
	private Map<String, Map> updateMap = new ConcurrentHashMap<String, Map>();
	
	@Autowired
	private TransportClient client;
	private static final Logger logger = LoggerFactory.getLogger(KafkaConsumeToES.class);
	private volatile long time = System.currentTimeMillis();
	/**
	 * 数据库删除，同时elasticsearch也是需要同步删除，用于监听 kafka端口
	 * @param id 主键，要求保存的时候主键和数据库相同
	 */
	@KafkaListener(topics = {"${kafka.delete.topic}"})
	public void delete(String message){
		try {
			List<String> deleteList = mapper.readValue(message, List.class);
			for (String id : deleteList) {
				//判断该id在elasticsearch是否处在
				boolean exists = client.prepareGet(this.index,this.type,id).get().isExists();
				if (exists) {
					bulk.add(new DeleteRequest(index, type,id));
				}else {
					synchronized ("s" + id) {
						this.deleteMap.put(id,"");
					}
				}
			}
			   bulk.flush();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
//		if (logger.isInfoEnabled()){
//		logger.info("delete---id-- " + message +  "---" + (System.currentTimeMillis() - time));
//		}
	}
	
	/**
	 * 用于同步根据数据库中的内容
	 * @param index
	 * @param type
	 * @param id
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	
	@KafkaListener(topics = "${kafka.update.topic}")
	 public void update(String message) throws Exception {
		try {
			System.out.println(Thread.currentThread());
				List<String> updateList = mapper.readValue(message, List.class);
				for (String string : updateList) {
					HashMap readValue = mapper.readValue(string, HashMap.class);
					String id = (String)readValue.get("id");
					readValue.remove("id");
					//更新如果冲入的话，重试几次。
					boolean exists = client.prepareGet(this.index, this.type,id ).get().isExists();
					if (exists) {
						bulk.add(new UpdateRequest(index,type,id).doc(readValue,XContentType.JSON));
					}else {
						synchronized (id) {
							this.updateMap.put(id,readValue);
						}
					}
				}
				bulk.flush();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
//			if (logger.isInfoEnabled()){
//				logger.info("update--id--" + message);
//			}
	  }
	 /**
	  * 用于插入操作
	  * @param index
	  * @param type
	  * @param id
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	  */
	
	@SuppressWarnings("unchecked")
	@KafkaListener(topics = "${kafka.insert.topic}")
	 public void insert(String message) throws JsonParseException, JsonMappingException, IOException{
		
		try {
			List<String> insertList = mapper.readValue(message, List.class);
			for (String string : insertList) {
				HashMap readValue = mapper.readValue(string, HashMap.class);
				String id = (String)readValue.get("id");
				readValue.remove("id");
				//更新如果冲入的话，重试几次。
				bulk.add(new IndexRequest(index, type, id).source(readValue, XContentType.JSON));
			}
		    bulk.flush();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
				
//    	if (logger.isInfoEnabled()){
//			logger.info("insert--" + message);
//		}
	 }
	
	/**
	 *  核心是用了保证 数据库执行顺序的有序
	 *  比如 kafka消费的删除在插入之前，修改在添加之前等操作
	 * @throws InterruptedException
	 */
	@SuppressWarnings("all")
	public  void circleTask() {
		ConcurrentHashMap<String, Integer> deleteHashMap = new ConcurrentHashMap<String, Integer>();
		ConcurrentHashMap<String, Integer> updateHashMap = new ConcurrentHashMap<String, Integer>();
		while (true) {
			System.out.println("times---");
			if (!updateMap.isEmpty()) {
				for (Iterator<Entry<String, Map>> iterator = this.updateMap.entrySet().iterator(); iterator.hasNext();) {
					Entry<String, Map> entry = iterator.next();
					String id = entry.getKey();
					if (StringUtils.isNotBlank(id)) {
							boolean exists = this.client.prepareGet(this.index, this.type, id).get().isExists();
							if (exists) {
								client.prepareUpdate(index, type, id).setDoc(entry.getValue()).execute().actionGet();
								this.updateMap.remove(id);
								updateHashMap.remove(id);
								if (logger.isInfoEnabled()) {
									logger.info("circleTask-----update--" + id + "-value-" + entry.getValue());
								}
							}else {
								if (updateHashMap.containsKey(id)) {
									Integer reply = updateHashMap.get(id);
									if (reply > 5) {
										this.updateMap.remove(id);
										updateHashMap.remove(id);
									}else {
										updateHashMap.put(id, reply + 1);
									}
								}else {
									updateHashMap.put(id, 1);
								}
							}
						}
				}
			}
			if (!this.deleteMap.isEmpty()) {
				for (Iterator<Entry<String, String>> iterator = this.deleteMap.entrySet().iterator(); iterator.hasNext();) {
					Entry<String, String> entry = iterator.next();
					String key = entry.getKey();
					if (StringUtils.isNotBlank(key)) {
						boolean exists = this.client.prepareGet(this.index, this.type, key).get().isExists();
						if (exists) {
							this.client.prepareDelete(this.index, this.type, key).get();
							this.deleteMap.remove(key);
							deleteHashMap.remove(key);
							if (logger.isInfoEnabled()) {
								logger.info("circleTask-----delete--" + key);
							}
						}else {
							//如果不存的话，那么久等待5个循环
							if (deleteHashMap.containsKey(key)) {
								Integer reply1 = deleteHashMap.get(key);
								if (reply1 > 5) {
									this.deleteMap.remove(key);
									deleteHashMap.remove(key);
								}else {
									deleteHashMap.put(key, reply1 + 1);
								}
							}else {
								deleteHashMap.put(key, 1);
							}
						}
					}
				}
			}
			//进行线程的休息
			try {
				TimeUnit.SECONDS.sleep(40);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void run() {
		circleTask();
	}
	
}
