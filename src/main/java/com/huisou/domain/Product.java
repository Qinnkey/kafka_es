package com.huisou.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * 商品的记录
 * @author Administrator
 * @Date 2017年10月16日 下午2:33:20
 *
 */
public class Product {
	private Integer id;//商品的id
	private Integer wid;//店铺的id
	private Integer category_id;//商品品类映射的id
	private BigDecimal price;//商品的价格
	private Integer sold_num;//总的销量
	private Integer uv_num;
	private Integer pv_num;
	private String title;//商品名
	private Integer status;
	private String img;//商品的图片
	@JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
	private Date created_at;//创建时间
	@JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
	private Date updated_at;//更新时间
	@JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
	private Date deleted_at;//删除时间
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getWid() {
		return wid;
	}
	public void setWid(Integer wid) {
		this.wid = wid;
	}
	public Integer getCategory_id() {
		return category_id;
	}
	public void setCategory_id(Integer category_id) {
		this.category_id = category_id;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public Integer getSold_num() {
		return sold_num;
	}
	public void setSold_num(Integer sold_num) {
		this.sold_num = sold_num;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getImg() {
		return img;
	}
	public void setImg(String img) {
		this.img = img;
	}
	public Date getCreated_at() {
		return created_at;
	}
	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}
	public Date getUpdated_at() {
		return updated_at;
	}
	public void setUpdated_at(Date updated_at) {
		this.updated_at = updated_at;
	}
	public Date getDeleted_at() {
		return deleted_at;
	}
	public void setDeleted_at(Date deleted_at) {
		this.deleted_at = deleted_at;
	}
	
	public Integer getUv_num() {
		return uv_num;
	}
	public void setUv_num(Integer uv_num) {
		this.uv_num = uv_num;
	}
	public Integer getPv_num() {
		return pv_num;
	}
	public void setPv_num(Integer pv_num) {
		this.pv_num = pv_num;
	}
	
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	@Override
	public String toString() {
		return "Product [id=" + id + ", wid=" + wid + ", category_id="
				+ category_id + ", price=" + price + ", sold_num=" + sold_num
				+ ",uv_num=" + uv_num + ",pv_num=" + pv_num + ", title=" + title + ", img=" + img + ", created_at="
				+ created_at + ", updated_at=" + updated_at + ", deleted_at="
				+ deleted_at  + "]";
	}
	
	
	
}
