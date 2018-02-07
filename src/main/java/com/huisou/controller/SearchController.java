package com.huisou.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.aggregations.metrics.sum.Sum;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder.Field;
import org.elasticsearch.search.sort.SortOrder;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.huisou.common.FileUtil;
import com.huisou.common.MD5;
import com.huisou.common.ResUtils;
import com.huisou.constant.ContextConstant;
import com.huisou.domain.Page;
import com.huisou.domain.PageInfo;
import com.huisou.domain.Product;

@RestController
public class SearchController {
	
	@Autowired
	private TransportClient client;
	
	private ObjectMapper jackson = new ObjectMapper();
	private static final Logger logger = LoggerFactory.getLogger(SearchController.class);
	/**
	 * 根据管家子查询对应的价格区间的商品,加上分页
	 * 更具销量排序
	 * @param upper  上限
	 * @param lower  下限
	 * @param keyword  关键字
	 * @param wid  店铺的id
	 * @param page 页面数
	 * @param sort desc递减，asc递增
	 * @param pageNum;
	 * @Param pageSize; 
	 * @Param startFrom;
	 * @return
	 */
    @RequestMapping(value = "/search")
    public String solveProblem(Double upper, Double lower, String keyword, String wid,String status,Page page,String orderBy,String sort,String timestamp,String token){
    	logger.info("查询接口请求===========keyword"+keyword+";wid===="+wid);
    	  String tokenSign = FileUtil.getApplicationPro("http.token");
    	  //如果是1，开启校验时间戳双层md5加密
    	  if("1".equals(tokenSign)){
    		  MD5 md = new MD5();
    		  if(StringUtils.isEmpty(token)||StringUtils.isEmpty(timestamp)||!token.equals(md.getMD5ofStr(md.getMD5ofStr(timestamp)))){
    			  return ResUtils.errRes("104", "请求参数错误！");
    		  }
    	  }
    	  SearchRequestBuilder builder = client.prepareSearch("product").setTypes("jd");
    	  BoolQueryBuilder  boolQuery = QueryBuilders.boolQuery();
    	  //判断关键字是否为空
    	  if (StringUtils.isNotBlank(keyword)) {
    		  boolQuery.must(QueryBuilders.matchQuery("title", keyword.trim()));
	  	  }else  {
	  		  boolQuery.must(QueryBuilders.matchAllQuery());
		  }
    	  if(StringUtils.isNotBlank(status)){
    		  if(status.contains(",")){
    			  String[] strs = status.split(",");
    			  boolQuery.must(QueryBuilders.termsQuery("status", strs));
    		  }else{
    			  boolQuery.must(QueryBuilders.termsQuery("status", status));
    		  }
    	  }
    	  boolQuery.must(QueryBuilders.termQuery("wid", wid));	  
    		//综合查询	
    		
    	  builder.setQuery(boolQuery).setFrom(page.getStartFrom()).setSize(page.getPageSize());
     
    	  //进行种类的聚合
    	  builder.addAggregation(AggregationBuilders.terms("agg")
    			 .field("category_id"));
    	 
    	   //設置高亮 		
     		HighlightBuilder highlightBuilder = new HighlightBuilder();
     		highlightBuilder.preTags("<em>");
     		highlightBuilder.postTags("</em>");
     		highlightBuilder.field("title");
     	
    	 //进行价格过滤
    	 if (upper != null && lower != null) {
			builder.setPostFilter(QueryBuilders.rangeQuery("price").lte(lower).gte(upper));
		}
    	//根据销量排序
    	if (StringUtils.isEmpty(orderBy)) {
    		orderBy = "sold_num";
		}
    	if (StringUtils.isEmpty(sort)) {
    		sort = ContextConstant.SORT_DESC;
		}
    	if (sort.equals(ContextConstant.SORT_DESC)) {
			builder.addSort(orderBy, SortOrder.DESC);
		}else{
			//根据销量的递减
			builder.addSort(orderBy, SortOrder.ASC);
		}
 		SearchResponse searchResponse =builder.highlighter(highlightBuilder).get();    

    	 //显示所有的内容
    	SearchHits hits = searchResponse.getHits();
     	long totalHits = hits.getTotalHits();
     	SearchHit[] searchHits = hits.getHits();
     	
     	ArrayList<Product> list = new ArrayList<Product>();
     	for (SearchHit s : searchHits) {
     		try {
     			String id = s.getId();
     			String json = s.getSourceAsString();
				Product product = jackson.readValue(json, Product.class);
				product.setId(Integer.valueOf(id));
				 //获取对应的高亮域
				  Map<String, HighlightField> result = s.getHighlightFields();
				//从设定的高亮域中取得指定域
				 HighlightField highlightField = result.get("title");
				//取得定义的高亮标签
				 Text[] fragments = highlightField.getFragments();
				 //为title串值增加自定义的高亮标签
				 String title="";
				 for (Text text : fragments) {
					title+=text;
				}
				 product.setTitle(title);
				list.add(product);
			} catch (Exception e) {
				e.printStackTrace();
			}
 		}
     	PageInfo<Product> pageInfo = new PageInfo<Product>(list, page.getPageNum(),page.getPageSize() ,totalHits);
     	return ResUtils.okRes(pageInfo);
     	
    }
	
	
	
	
	/**
	 * 查询所有的数据
	 */
	public String matchAllQuery(String index,String typeName){
		long last = System.currentTimeMillis();
		SearchRequestBuilder prepareSearch = client.prepareSearch("good").setTypes("jd");
		SearchResponse searchResponse = prepareSearch.setQuery(QueryBuilders.queryStringQuery("内衣"))
			//.setSearchType(SearchType.DEFAULT)  //搜索时执行的类型
			.setFrom(0).setSize(20) //分页
			.get();
		  //.setQuery(QueryBuilders.matchQuery("name", "tom").operator(Operator.AND)) //根据tom分词查询name,默认or  
        //.setQuery(QueryBuilders.multiMatchQuery("tom", "name", "age")) //指定查询的字段  
        //.setQuery(QueryBuilders.queryString("name:to* AND age:[0 TO 19]")) //根据条件查询,支持通配符大于等于0小于等于19  
        //.setQuery(QueryBuilders.termQuery("name", "tom"))//查询时不分词  
		
		//QueryBuilders.idsQuery().addIds(ids)根据 ids 去查询对应的结果
		
		//获取搜索的数据
	  	SearchHits hits = searchResponse.getHits();  
        long total = hits.getTotalHits();  
        System.out.println(total);  
        SearchHit[] searchHits = hits.getHits();  
        for(SearchHit s : searchHits)  
        {  
        	String id = s.getId();
        	System.out.println(id);
            System.out.println(s.getSourceAsString());  
        }  
        long now = System.currentTimeMillis();
        System.out.println(last - now);
        return "ok";
	}
	
	
	
	/**
	 * 多重查询
	 */
	public void mulitSearch(){
		SearchRequestBuilder srb1 = client
			    .prepareSearch().setQuery(QueryBuilders.queryStringQuery("苹果")).setSize(1);
			SearchRequestBuilder srb2 = client
			    .prepareSearch().setQuery(QueryBuilders.matchQuery("goodname", "shou")).setSize(1);

			MultiSearchResponse sr = client.prepareMultiSearch()
			        .add(srb1)
			        .add(srb2)
			        .get();

			// You will get all individual responses from MultiSearchResponse#getResponses()
			long nbHits = 0;
			for (MultiSearchResponse.Item item : sr.getResponses()) {
			    SearchResponse response = item.getResponse();
			    nbHits += response.getHits().getTotalHits();
			}
			System.out.println(nbHits);
	}
	
	
	/**
	 * 分组,将查询出来的数据进行分组
	 */
	public void group(){
		SearchResponse searchResponse = client.prepareSearch("good")
			.setTypes("jd")
			.addAggregation(AggregationBuilders.terms("good_state")
					.field("good_rate").size(20))//根据good_rate来进行分组
			.get();
		
		Terms terms = searchResponse.getAggregations().get("good_state");
		List<? extends Bucket> buckets = terms.getBuckets();
		for (Bucket bt : buckets) {
			System.out.println(bt.getKey() + " " + bt.getDocCount());  
		}
	}
	
	
	 /** 
     * 聚合查询：
     *  
     */
    public void testMethod(String index,String type) {  
        SearchResponse searchResponse = client.prepareSearch(index).setTypes(type)  
                .setQuery(QueryBuilders.matchAllQuery())  
                .setSearchType(SearchType.QUERY_THEN_FETCH)  
                .addAggregation(AggregationBuilders.terms("group_name").field("name")  
                        .subAggregation(AggregationBuilders.sum("sum_age").field("count")))  
                .get();  
          
        Terms terms = searchResponse.getAggregations().get("group_name");  
        List<? extends Bucket> buckets = terms.getBuckets();  
        for(Bucket bt : buckets)  
        {  
            Sum sum = bt.getAggregations().get("sum_age");  
            System.out.println(bt.getKey() + "  " + bt.getDocCount() + " "+ sum.getValue());  
        }  
          
    } 
    
    /**
     * 范围查询，或者过滤
     * lt 是小于
     * gt 是大于
     * lte 大于等于
     * gte 大于等于
     */
    public void rangeSearch(String index,String typeName){
    	SearchResponse searchResponse = client.prepareSearch(index)
    		.setTypes(typeName)
    		.setQuery(QueryBuilders.matchAllQuery())//查询所有
    		.setPostFilter(QueryBuilders.rangeQuery("count").lte(5000))
    		//.setExplain(true)//explain为true表示根据数据相关度排序和关键字匹配最高的排在前面,这个这能用于测试环境，不能用于生产环境
    		.get();
    	//查询出的是json的字符串
    	SearchHits hits = searchResponse.getHits();
    	long totalHits = hits.getTotalHits();
    	System.out.println("查询的总的数量----"+totalHits);
    	SearchHit[] searchHits = hits.getHits();
    	for (SearchHit s : searchHits) {
    		System.out.println(s.getSourceAsString());
		}
    	//查询多的是对应的字段名称和内容
    	
    	for (int i = 0; i < searchHits.length; i++) {
			SearchHit hit = searchHits[i];
			Map<String, SearchHitField> fields = hit.getFields();
			Set<Entry<String, SearchHitField>> entrySet = fields.entrySet();
			for (Entry<String, SearchHitField> entry : entrySet) {
				System.out.println("字段名称=" + entry.getKey());
				System.out.println("字段内容=" + entry.getValue());
			}
		}
    	
    	
    }
    
    
    
    /**
     * 滚动搜索Scroll
     * 一般的搜索的数量超过10000，就无法使用了，只能用from +size来限制
     *  scroll的创建并不是为了实施的用户响应，而是处理大量的数据。
     *  scroll请求返回的结果只是反映了search发生的那一时刻的索引状态，后续的对文档的改动都是只会影响后面的搜索请求
     *  @param index 是索引的名称
     *  @param type  是类型的名称
     */
    public void scrollSearch(String index,String type){
    	SearchResponse response = client.prepareSearch(index)
    		//设置搜索的类型
    		.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
    		.setSize(10).setScroll(new TimeValue(20000)).get();//注意第一次搜搜并不包含数据
    	
    	//获取总的数量
    	long totalHits = response.getHits().getTotalHits();
    	int page = (int) (totalHits / (50)); //计算分页的数量
    	System.out.println("total----" + totalHits + "---page的数量-------" + page);
    	for (int i = 0; i < page; i++) {
			SearchResponse searchResponse = client.prepareSearchScroll(response.getScrollId()).setScroll(new TimeValue(20000)).get();
			//获取搜索结果，数量比较大，所以显示就是数量
			long total = searchResponse.getHits().getTotalHits();
			System.out.println(total);
		}
    }
    
    /**
     * 查询的权重，可以增加搜索的排序的考前，一般用于竞价排名
     *  @param index 是索引的名称
     *  @param type  是类型的名称
     */
    public void functionScore(String index,String type,String goodName,String brandName){
    	index = "good";
    	type = "jd";
    	goodName = "笔";
    	brandName = "京东";
    	SearchResponse searchResponse = client.prepareSearch(index)
    		.setTypes(type)
    		.setQuery(QueryBuilders.functionScoreQuery(QueryBuilders.matchQuery("goodname", goodName), ScoreFunctionBuilders.weightFactorFunction(100)))
    		//.setQuery(QueryBuilders.functionScoreQuery(QueryBuilders.matchQuery("brand_name", brandName), ScoreFunctionBuilders.weightFactorFunction(1000)))
    		.setExplain(true)
    		.get();
    	
    	SearchHits hits = searchResponse.getHits();
    	long totalHits = hits.getTotalHits();
    	System.out.println("查询的总的数量----"+totalHits);
    	SearchHit[] searchHits = hits.getHits();
    	for (SearchHit s : searchHits) {
    		System.out.println(s.getSourceAsString());
		}
    }
    

    
    
    

//	/**
//	 * 用于搜过后的关键字的高亮
//	 */
//	@RequestMapping("/hightlight")
//	public String testSearch3() throws Exception{
//		//设置查询索引
//		SearchRequestBuilder setQuery = client.prepareSearch("good")
//				.setTypes("jd")
//				.setQuery(QueryBuilders.
//						boolQuery().must(QueryBuilders.matchQuery("title", "苹果"))).setSize(50);
//		//设置高亮builder
//		HighlightBuilder highlightBuilder = new HighlightBuilder();
//		highlightBuilder.preTags("<h2>");
//		highlightBuilder.postTags("</h2>").field("brand_name").field("goodname");
//		SearchResponse actionGet = setQuery.highlighter(highlightBuilder).get();    
//		//获取搜索的文档结果
//		SearchHits hits = actionGet.getHits();
//		SearchHit[] hits2 = hits.getHits();
//		ObjectMapper mapper = new ObjectMapper();
//		for (int i = 0; i < hits2.length; i++) {
//			 SearchHit hit = hits2[i];
//			 //将文档中的每一个对象转换json串值
//			 String json = hit.getSourceAsString();
//			 //将json串值转换成对应的实体对象
//			 Product readValue = mapper.readValue(json, Product.class);
//			 //获取对应的高亮域
//			  Map<String, HighlightField> result = hit.getHighlightFields();
//			//从设定的高亮域中取得指定域
//			 HighlightField highlightField = result.get("goodname");
//			 
//			 HighlightField highlightField2 = result.get("brand_name");
//			 System.out.println(highlightField2.getFragments()[0]);
//			 
//			//取得定义的高亮标签
//			 Text[] fragments = highlightField.getFragments();
//			 //为title串值增加自定义的高亮标签
//			 String goodnameString="";
//			 for (Text text : fragments) {
//				goodnameString+=text;
//			}
//			//将追加了高亮标签的串值重新填充到对应的对象
//			readValue.setGoodname(goodnameString);
//			System.out.println(readValue);
//			 
//		}
//		return "ok";
//	}
//	
    
    
    
    
    
    /**
     * 更新对象中的内容
     * 
     */
    public void update(String index,String type,String id){
    	//更新单个的
    	String something = "{"+
    				"\"goodname\":\"李四\""+
    			"}";
    	//更新如果冲入的话，重试几次。
    	client.prepareUpdate(index,type,id).setRetryOnConflict(5).setDoc(something,XContentType.JSON).get();
    }
}
