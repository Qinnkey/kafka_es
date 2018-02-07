package com.huisou.domain;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Id;


import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 商品
 * @author Administrator
 *
 */
public class Good implements Serializable {
	@JsonIgnore

	private Integer good_id;
	private String brand_name;
	private String comment_count;
	

	private Integer count;

	private String good_rate;
	private String goodname;

	private String jd_good_id;

	private String jd_price;

	private String poor_rate;
	
	private Date updatetime;
	public Integer getGood_id() {
		return good_id;
	}
	public void setGood_id(Integer good_id) {
		this.good_id = good_id;
	}
	public String getBrand_name() {
		return brand_name;
	}
	public void setBrand_name(String brand_name) {
		this.brand_name = brand_name;
	}
	public String getComment_count() {
		return comment_count;
	}
	public void setComment_count(String comment_count) {
		this.comment_count = comment_count;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
	public String getGood_rate() {
		return good_rate;
	}
	public void setGood_rate(String good_rate) {
		this.good_rate = good_rate;
	}
	public String getGoodname() {
		return goodname;
	}
	public void setGoodname(String goodname) {
		this.goodname = goodname;
	}
	public String getJd_good_id() {
		return jd_good_id;
	}
	public void setJd_good_id(String jd_good_id) {
		this.jd_good_id = jd_good_id;
	}
	public String getJd_price() {
		return jd_price;
	}
	public void setJd_price(String jd_price) {
		this.jd_price = jd_price;
	}
	public String getPoor_rate() {
		return poor_rate;
	}
	public void setPoor_rate(String poor_rate) {
		this.poor_rate = poor_rate;
	}
	public Date getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}
	@Override
	public String toString() {
		return "Good [good_id=" + good_id + ", brand_name=" + brand_name
				+ ", comment_count=" + comment_count + ", count=" + count
				+ ", good_rate=" + good_rate + ", goodname=" + goodname
				+ ", jd_good_id=" + jd_good_id + ", jd_price=" + jd_price
				+ ", poor_rate=" + poor_rate + ", updatetime=" + updatetime
				+ "]";
	}
		
}
