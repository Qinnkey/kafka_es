package com.huisou.domain;

public class Page {
		private Integer pageNum;
		private Integer pageSize;
		private Integer startFrom;
		
		public Integer getStartFrom() {
			if (pageNum == null || pageNum == 0) {
				return 0;
			}else {
				return (getPageNum()-1) * getPageSize();
			}
		}
		public void setStartFrom(Integer startFrom) {
			this.startFrom = startFrom;
		}
		public Integer getPageNum() {
			if(pageNum == null){
				return 1;
			}else {
				return pageNum;
			}
		}
		public void setPageNum(Integer pageNum) {
			this.pageNum = pageNum;
		}
		public Integer getPageSize() {
			return pageSize == null ? 10 : pageSize;
		}
		public void setPageSize(Integer pageSize) {
			this.pageSize = pageSize;
		}
}
