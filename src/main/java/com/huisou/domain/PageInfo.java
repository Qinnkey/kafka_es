package com.huisou.domain;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;


/**
 * 用来封装页面对象
 * @author Administrator
 * @Date 2017年10月17日 下午6:27:08
 *
 */
public class PageInfo<T> implements Serializable{

	  private int page = 1; // 当前页
	    public int totalPages = 0; // 总页数
	    private int pageRecorders;// 每页5条数据
	    private long totalRows = 0; // 总数据数
	    private int pageStartRow = 1;// 每页的起始数
	    private int pageEndRow = 0; // 每页显示数据的终止数
	    private boolean hasNextPage = false; // 是否有下一页
	    private boolean hasPreviousPage = false; // 是否有前一页
	    private List<T> list;

	   /**
	    * 构造函数
	    * @param list 对象
	    * @param pageRecorders 页面的记录数
	    * @param totalRows 总的记录数
	    */
	    public PageInfo(List<T> list, int page,int pageRecorders,long totalRows) {
	        init(list, pageRecorders,totalRows,page);// 通过对象集，记录总数划分
	    }

	    /** */
	    /**
	     * 初始化list，并告之该list每页的记录数
	     * 
	     * @param list
	     * @param pageRecorders
	     */
	    public void init(List<T> list, int pageRecorders,long totalRows,int page) {
	        this.pageRecorders = pageRecorders;
	        this.list = list;
	        this.totalRows = totalRows;
	        this.page = page;
	        hasPreviousPage = false;

	        if ((totalRows % pageRecorders) == 0) {
	            totalPages = (int) (totalRows / pageRecorders);
	        } else {
	            totalPages = (int) (totalRows / pageRecorders + 1);
	        }

	        if (page >= totalPages) {
	            hasNextPage = false;
	        } else {
	            hasNextPage = true;
	        }

	        if (totalRows < pageRecorders) {
	            this.pageStartRow = 1;
	            this.pageEndRow = (int) totalRows;
	        } else {
	            this.pageStartRow = 1;
	            this.pageEndRow = pageRecorders;
	        }
	    }

		public int getPage() {
			return page;
		}

		public void setPage(int page) {
			this.page = page;
		}

		public int getTotalPages() {
			return totalPages;
		}

		public void setTotalPages(int totalPages) {
			this.totalPages = totalPages;
		}

		public int getPageRecorders() {
			return pageRecorders;
		}

		public void setPageRecorders(int pageRecorders) {
			this.pageRecorders = pageRecorders;
		}

		public long getTotalRows() {
			return totalRows;
		}

		public void setTotalRows(long totalRows) {
			this.totalRows = totalRows;
		}

		public int getPageStartRow() {
			return pageStartRow;
		}

		public void setPageStartRow(int pageStartRow) {
			this.pageStartRow = pageStartRow;
		}

		public int getPageEndRow() {
			return pageEndRow;
		}

		public void setPageEndRow(int pageEndRow) {
			this.pageEndRow = pageEndRow;
		}

		public boolean isHasNextPage() {
			return hasNextPage;
		}

		public void setHasNextPage(boolean hasNextPage) {
			this.hasNextPage = hasNextPage;
		}

		public boolean isHasPreviousPage() {
			return hasPreviousPage;
		}

		public void setHasPreviousPage(boolean hasPreviousPage) {
			this.hasPreviousPage = hasPreviousPage;
		}

		public List<T> getList() {
			return list;
		}

		public void setList(List<T> list) {
			this.list = list;
		}
	    
	  
}