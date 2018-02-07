package com.huisou;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

import com.huisou.config.KafkaConsumeToES;


/**
 * springboot的启动项
 * @author Administrator
 * @Date 2017年10月16日 上午9:58:10
 *
 */
@SpringBootApplication
@MapperScan("com.huisou.dao")
@EnableKafka
@Configuration
public class Application {

	public static void main(String[] args) {
			new SpringApplication(Application.class).run(args);
			
	}
	@Value("${spring.elasticsearch.cluster-name}")
	private String clustername;
	@Value("${spring.elasticsearch..cluster-nodes}")
	private String clusterNodes;
	/**
	 * 创建Client对象然后交给spring去管理
	 */
	
	private static final Logger logger = LoggerFactory.getLogger(KafkaConsumeToES.class);

	@Bean
	public TransportClient getTransportClient(){
		//设置属性
		TransportClient client=null;
		try {
			Settings settings = Settings.builder().put("cluster.name", clustername)
				.put("client.transport.sniff", true).build();
			 client = new PreBuiltTransportClient(settings);
			 //切割多个nodes的地址
			 List<InetSocketTransportAddress> list = new ArrayList<InetSocketTransportAddress>();
			 if(clusterNodes!=null&&StringUtils.isNotBlank(clusterNodes)){
				 String[] nodes = clusterNodes.split(",");
				 for (String node : nodes) {
					 String[] split = node.split(":");
					 InetSocketTransportAddress address = new InetSocketTransportAddress(new InetSocketAddress(split[0], new Integer(split[1])));
					list.add(address);
				}
			 }
			 if (null == list && list.size()<1){
				 throw new NullPointerException();
			 }
			 TransportAddress[] array = list.toArray(new InetSocketTransportAddress[list.size()]);
			 //添加对应的ip地址和端口号
			 client.addTransportAddresses(array);
			 return client;
		} catch (Exception e) {
			e.printStackTrace();
			if (client != null){
				client.close();
			}
			
			
		}
		return null;
	}
	
	@Bean
	public BulkProcessor  bulkProcessor() {
       BulkProcessor bulkProcessor = BulkProcessor.builder(
    		   getTransportClient(),
                new BulkProcessor.Listener() {
                    @Override
                    public void beforeBulk(long executionId, BulkRequest request) {
                        logger.info("---尝试插入{}条数据---", request.numberOfActions());
                    }

                    @Override
                    public void afterBulk(long executionId,
                                          BulkRequest request, BulkResponse response) {
                        logger.info("---插入{}条数据成功---", request.numberOfActions());
                    }
                    @Override
                    public void afterBulk(long executionId,
                                          BulkRequest request, Throwable failure) {
                        logger.error("[es错误]---尝试插入数据失败---", failure);
                    }
                })
                //每次的请求量
                .setBulkActions(10000)
                //每5MB
                .setBulkSize(new ByteSizeValue(50, ByteSizeUnit.MB))
                //每5秒钟刷新一次无论多少请求
                //.setFlushInterval(TimeValue.timeValueSeconds(5))
                //并发次数
                .setConcurrentRequests(8)
                .build();
       return bulkProcessor;
    }
	
	
}
