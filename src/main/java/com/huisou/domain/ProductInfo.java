package com.huisou.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
@Entity
@Table(name = "ds_product")
public class ProductInfo {
	private Integer id;//商品的id
	private Integer wid;//店铺的id
	private Integer category_id;//商品品类映射的id
	private Integer buy_way;
	private String out_buy_link;
	private String group_id;
	private Integer type;
	private Integer presell_flag;
	private Integer presell_delivery_type;
	private Date presell_delivery_time;
	private Integer presell_delivery_payafter;
	private Integer sku_flag;
	private Integer stock;
	private Integer stock_show;
	private Double weight;
	private String goods_no;
	private String title;//商品名
	private String img;//商品的图片
	private BigDecimal price;//商品的价格
	private BigDecimal oprice;//商品原价
	private Integer freight_type;//运费的类型
	private BigDecimal freight_price;//同意的运费价格
	private Integer freight_id;//运费的末班id
	private Integer quota;//限购数量
	private Integer buy_permissions_flag;//购买权限
	private String buy_permissions_level_id;//购买权限，
	private Integer note_flag;//留言表示
	private Integer sale_time_flag;//开售时间标志
	private Date sale_time;//开售时间
	private Integer sort;//排序字段
	private Integer status;
	private Integer uv_num;
	private Integer pv_num;
	private Integer is_distribution;
	private Integer is_discount;
	private String hash_code;
	private Integer discount_way;
	private String introduce;
	private Integer templete_use_id;
	private Integer vip_discount_way;
	private String vip_card_price_json;
	private Date created_at;//创建时间
	private Date updated_at;//更新时间
	private Date deleted_at;//删除时间
	private Integer distribute_template_id;
	private Integer is_default;
	private String summary;
	private String content;
	private String share_title;
	private String share_desc;
	private String share_img;
	private Integer is_hexiao;
	private Date hexiao_start;
	private Date hexiao_end;
	private Integer sold_num;//总的销量
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
	public Integer getBuy_way() {
		return buy_way;
	}
	public void setBuy_way(Integer buy_way) {
		this.buy_way = buy_way;
	}
	public String getOut_buy_link() {
		return out_buy_link;
	}
	public void setOut_buy_link(String out_buy_link) {
		this.out_buy_link = out_buy_link;
	}
	public String getGroup_id() {
		return group_id;
	}
	public void setGroup_id(String group_id) {
		this.group_id = group_id;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Integer getPresell_flag() {
		return presell_flag;
	}
	public void setPresell_flag(Integer presell_flag) {
		this.presell_flag = presell_flag;
	}
	public Integer getPresell_delivery_type() {
		return presell_delivery_type;
	}
	public void setPresell_delivery_type(Integer presell_delivery_type) {
		this.presell_delivery_type = presell_delivery_type;
	}
	public Date getPresell_delivery_time() {
		return presell_delivery_time;
	}
	public void setPresell_delivery_time(Date presell_delivery_time) {
		this.presell_delivery_time = presell_delivery_time;
	}
	public Integer getPresell_delivery_payafter() {
		return presell_delivery_payafter;
	}
	public void setPresell_delivery_payafter(Integer presell_delivery_payafter) {
		this.presell_delivery_payafter = presell_delivery_payafter;
	}
	public Integer getSku_flag() {
		return sku_flag;
	}
	public void setSku_flag(Integer sku_flag) {
		this.sku_flag = sku_flag;
	}
	public Integer getStock() {
		return stock;
	}
	public void setStock(Integer stock) {
		this.stock = stock;
	}
	public Integer getStock_show() {
		return stock_show;
	}
	public void setStock_show(Integer stock_show) {
		this.stock_show = stock_show;
	}
	public Double getWeight() {
		return weight;
	}
	public void setWeight(Double weight) {
		this.weight = weight;
	}
	public String getGoods_no() {
		return goods_no;
	}
	public void setGoods_no(String goods_no) {
		this.goods_no = goods_no;
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
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public BigDecimal getOprice() {
		return oprice;
	}
	public void setOprice(BigDecimal oprice) {
		this.oprice = oprice;
	}
	public Integer getFreight_type() {
		return freight_type;
	}
	public void setFreight_type(Integer freight_type) {
		this.freight_type = freight_type;
	}
	public BigDecimal getFreight_price() {
		return freight_price;
	}
	public void setFreight_price(BigDecimal freight_price) {
		this.freight_price = freight_price;
	}
	public Integer getFreight_id() {
		return freight_id;
	}
	public void setFreight_id(Integer freight_id) {
		this.freight_id = freight_id;
	}
	public Integer getQuota() {
		return quota;
	}
	public void setQuota(Integer quota) {
		this.quota = quota;
	}
	public Integer getBuy_permissions_flag() {
		return buy_permissions_flag;
	}
	public void setBuy_permissions_flag(Integer buy_permissions_flag) {
		this.buy_permissions_flag = buy_permissions_flag;
	}
	public String getBuy_permissions_level_id() {
		return buy_permissions_level_id;
	}
	public void setBuy_permissions_level_id(String buy_permissions_level_id) {
		this.buy_permissions_level_id = buy_permissions_level_id;
	}
	public Integer getNote_flag() {
		return note_flag;
	}
	public void setNote_flag(Integer note_flag) {
		this.note_flag = note_flag;
	}
	public Integer getSale_time_flag() {
		return sale_time_flag;
	}
	public void setSale_time_flag(Integer sale_time_flag) {
		this.sale_time_flag = sale_time_flag;
	}
	public Date getSale_time() {
		return sale_time;
	}
	public void setSale_time(Date sale_time) {
		this.sale_time = sale_time;
	}
	public Integer getSort() {
		return sort;
	}
	public void setSort(Integer sort) {
		this.sort = sort;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
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
	public Integer getIs_distribution() {
		return is_distribution;
	}
	public void setIs_distribution(Integer is_distribution) {
		this.is_distribution = is_distribution;
	}
	public Integer getIs_discount() {
		return is_discount;
	}
	public void setIs_discount(Integer is_discount) {
		this.is_discount = is_discount;
	}
	public String getHash_code() {
		return hash_code;
	}
	public void setHash_code(String hash_code) {
		this.hash_code = hash_code;
	}
	public Integer getDiscount_way() {
		return discount_way;
	}
	public void setDiscount_way(Integer discount_way) {
		this.discount_way = discount_way;
	}
	public String getIntroduce() {
		return introduce;
	}
	public void setIntroduce(String introduce) {
		this.introduce = introduce;
	}
	public Integer getTemplete_use_id() {
		return templete_use_id;
	}
	public void setTemplete_use_id(Integer templete_use_id) {
		this.templete_use_id = templete_use_id;
	}
	public Integer getVip_discount_way() {
		return vip_discount_way;
	}
	public void setVip_discount_way(Integer vip_discount_way) {
		this.vip_discount_way = vip_discount_way;
	}
	public String getVip_card_price_json() {
		return vip_card_price_json;
	}
	public void setVip_card_price_json(String vip_card_price_json) {
		this.vip_card_price_json = vip_card_price_json;
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
	public Integer getDistribute_template_id() {
		return distribute_template_id;
	}
	public void setDistribute_template_id(Integer distribute_template_id) {
		this.distribute_template_id = distribute_template_id;
	}
	public Integer getIs_default() {
		return is_default;
	}
	public void setIs_default(Integer is_default) {
		this.is_default = is_default;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getShare_title() {
		return share_title;
	}
	public void setShare_title(String share_title) {
		this.share_title = share_title;
	}
	public String getShare_desc() {
		return share_desc;
	}
	public void setShare_desc(String share_desc) {
		this.share_desc = share_desc;
	}
	public String getShare_img() {
		return share_img;
	}
	public void setShare_img(String share_img) {
		this.share_img = share_img;
	}
	public Integer getIs_hexiao() {
		return is_hexiao;
	}
	public void setIs_hexiao(Integer is_hexiao) {
		this.is_hexiao = is_hexiao;
	}
	public Date getHexiao_start() {
		return hexiao_start;
	}
	public void setHexiao_start(Date hexiao_start) {
		this.hexiao_start = hexiao_start;
	}
	public Date getHexiao_end() {
		return hexiao_end;
	}
	public void setHexiao_end(Date hexiao_end) {
		this.hexiao_end = hexiao_end;
	}
	public Integer getSold_num() {
		return sold_num;
	}
	public void setSold_num(Integer sold_num) {
		this.sold_num = sold_num;
	}
	
	

}
