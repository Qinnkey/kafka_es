package com.huisou.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.admin.indices.alias.Alias;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesAction;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequestBuilder;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huisou.domain.Product;
import com.huisou.mapper.ProductMapper;

/**
 * 用于索引的创建，mapping的创建的，以及别名的更换等，并且添加数据
 * @author Administrator
 * @Date 2017年10月16日 上午10:04:01
 *
 */
@RestController
public class ElasticSearchOp {
	
	@Autowired
	private TransportClient client;

	
	private static final Logger logger = LoggerFactory.getLogger(ElasticSearchOp.class);
	
	/**
	 * 创建分词器的索引，分词器名称为ik_pinyin_analyzer
	 */
	  public  void createIndex(String indexName){  
	        try {  
	           
	            XContentBuilder xContentBuilder = XContentFactory.jsonBuilder()
    	    		.startObject()
    	    			.startObject("index")
    	    				.startObject("analysis")
	    	    				.startObject("analyzer")
	    	    					.startObject("ik_pinyin_analyzer")
	    	    						.field("type", "custom")
	    	    						.field("tokenizer","ik_max_word")
	    	    						.field("filter",new String[]{ "word_delimiter"})
	    	    					.endObject()
	    	    				.endObject()
	    	    				.startObject("filter")
	    	    					.startObject("my_pinyin")
	    	    						.field("type", "pinyin")
	    	    						.field("first_letter","prefix")
	    	    						.field("padding_char","")
	    	    					.endObject()
	    	    				.endObject()
	    	    			.endObject()
	    	    		.endObject()
	            	.endObject();
	            /**1.创建索引映射*/  
	            client.admin().indices()
	            //使用拼音和ik分词器
	            .prepareCreate(indexName)
	            	//.setSettings(xContentBuilder)
	            	.addAlias(new Alias("good")).get();		
	            System.out.println("索引创建成功");
	            
	        } catch (Exception e) {  
	            e.printStackTrace();  
	            System.out.println("索引创建失败");  
	        }  
	    }
	  
	  
	  /** 
	     * 创建映射，并指定分词器
	     * @param indexName 索引名 
	     * @param typeName  类型名 
	     * @throws IOException 
	     */  
	    public  void createMapping(String indexName,String typeName) throws IOException{ 
	    	XContentBuilder xContentBuilder = XContentFactory.jsonBuilder()
	    		.startObject()
			//开启倒计时功能
					.startObject("properties")
					.startObject("title")
						.field("type","text")
						.field("analyzer","ik_max_word")
						.field("term_vector", "with_positions_offsets")
						//下面是配置全局序数
						.field("fielddata",true)
						.field("eager_global_ordinals",true)
					.endObject()
					.startObject("wid")
						.field("type","integer")
						.field("index","not_analyzed")
					.endObject()
					.startObject("category_id")
						.field("type","integer")
						.field("index","not_analyzed")
					.endObject()
					.startObject("price")
						.field("type","double")
						.field("index","not_analyzed")
					.endObject()
					.startObject("pv_num")
						.field("type","long")
						.field("index","not_analyzed")
					.endObject()
					.startObject("uv_num")
						.field("type","long")
						.field("index","not_analyzed")
					.endObject()
					.startObject("status")
						.field("type","integer")
						.field("index","not_analyzed")
					.endObject()
					.startObject("sold_num")
						.field("type","integer")
						.field("index","not_analyzed")
					.endObject()
					.startObject("uv_num")
						.field("type","integer")
						.field("index","not_analyzed")
					.endObject()
					.startObject("pv_num")
						.field("type","integer")
						.field("index","not_analyzed")
					.endObject()
					.startObject("image")
						.field("type","string")
						.field("index", "no")
					.endObject()
					.startObject("created_at")
						.field("type","date").field("format","yyyy-MM-dd HH:mm:ss")
						.field("index", "not_analyzed")
					.endObject()
					.startObject("updated_at")
						.field("type","date").field("format","yyyy-MM-dd HH:mm:ss")
						.field("index","not_analyzed")
					.endObject()
					.startObject("deleted_at")
					.field("type","date").field("format","yyyy-MM-dd HH:mm:ss")
					.field("index","no")
					.endObject()
					
					//关联数据，可以关联对象
					//.startObject("list").field("type","object").endObject()
				.endObject()
			.endObject();
	        //创建mapping  
	        PutMappingRequest mapping = Requests.putMappingRequest(indexName).type(typeName).source(xContentBuilder);  
	          
	        client.admin().indices().putMapping(mapping).actionGet();  
	        System.out.println("mapping创建成功");
	    }
		 
	    
		/**
		 * 删除索引库，删除的时候同时删除所有的数据
		 */
	    
	    public void testDeleteeIndex(String indexName)  
	    {  
	        try {
				client.admin().indices().prepareDelete(indexName).get();
			} catch (Exception e) {
				e.printStackTrace();
			}  
	    }
	    
	    //添加数据
	    /**
		 * 批量添加数据
		 * @return
		 * @throws JsonProcessingException 
		 */
		@RequestMapping("/addData")
		public String put(String indexName,String typeName) {	
			try {
				//testDeleteeIndex("good2");
				createIndex(indexName);
				createMapping(indexName, typeName);
				//先查出所有的商品然后进行同步
//				List<Product> list = productDao.findAll();
//				//排除good_id字段的序列化,加上@jsonIgnore
//				if (null != list && list.size() > 0){
//					logger.info("查询出的商品的数量="+ list.size());
//					save(indexName,typeName,list);
//					System.out.println(list.get(2));
//				 }
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return "ok";
		}
		
		

		/**
		 * 添加单个数据到Elasticsearch
		 * @param index		索引
		 * @param type		类型
		 * @param idName	Id字段名称
		 * @param object  一个对象
		 * @return
		 */
		@SuppressWarnings("unchecked")
		public   Map save(String index, String type, String idName,Object object) {
				ObjectMapper mapper = new ObjectMapper();
				String json = "";
				IndexResponse indexResponse = null;
				Map resultMap = new HashMap();
				try {
					json = mapper.writeValueAsString(object);
				
				//没有指定idName 那就让Elasticsearch自动生成
				if(StringUtils.isBlank(idName)){
					indexResponse = client.prepareIndex(index, type).setSource(json,XContentType.JSON).get();
				}
				else{
					if(StringUtils.isNumeric(idName)){
						indexResponse  = client.prepareIndex(index, type,idName)
								.setSource(json,XContentType.JSON).execute().get();
					}else{
						throw new RuntimeException("输出的id必须是字母或者数字");
					}
				}
			} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					resultMap.put("500", "保存ES失败!");
					return resultMap;
			}
			System.out.println(indexResponse.getIndex());
			resultMap.put("200", "保存ES成功");
			return resultMap;
		}
		
		
		/**
		 * 批量添加数据到Elasticsearch
		 * @param index		索引
		 * @param type		类型
		 * @param idName	Id字段名称
		 * @param listData  一个对象集合
		 * @return
		 */
		@SuppressWarnings("unchecked")
		public   Map save(String index, String type,List listData) {
			BulkRequestBuilder bulkRequest = client.prepareBulk().setRefreshPolicy(RefreshPolicy.IMMEDIATE);
			Map resultMap = new HashMap();
			
			
			for (Object object : listData) {
				
				ObjectMapper mapper = new ObjectMapper();
				String json = "";
				try {
					Product good = (Product) object;
					json = mapper.writeValueAsString(object);
					IndexRequestBuilder lrb = client.prepareIndex(index, type,good.getId() + "")
							.setSource(json,XContentType.JSON);
					bulkRequest.add(lrb);	
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
				//没有指定idName 那就让Elasticsearch自动生成
//					IndexRequestBuilder lrb = client.prepareIndex(index, type).setSource(json,XContentType.JSON);
//					bulkRequest.add(lrb);
					
				}
				
			BulkResponse bulkResponse = bulkRequest.execute().actionGet();
			if (bulkResponse.hasFailures()) {
				// process failures by iterating through each bulk response item
				System.out.println(bulkResponse.getItems().toString());
				resultMap.put("500", "保存ES失败!");
				return resultMap;
			}
			resultMap.put("200", "保存ES成功!");
			return resultMap;
		}
		
		  /**
	     * 不停机更换索引库名称
	     * 先创建一个索引
	     */
	    @RequestMapping("/changeIndex")
	    public String changeIndex(String remove,String add){
	    	IndicesAliasesRequestBuilder builder = new IndicesAliasesRequestBuilder(client, IndicesAliasesAction.INSTANCE);
	    	//client.prepareDelete().setIndex("good1").setType("product").get();
	    	builder.removeAlias(remove, "good");
	    	builder.addAlias(add, "good");
	    	builder.get();
	    	//testDeleteeIndex("good1");
	    	return "ok";
	    }
	    
	    
	  
		
	  
}
