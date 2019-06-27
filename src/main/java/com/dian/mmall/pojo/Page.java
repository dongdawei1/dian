package com.dian.mmall.pojo;

import java.util.List;

public class Page<T> {

	private List<T> datas;
	private int currentPage;  //当前页
	private long totalno; //总条数
	private int pageSize; //每页的条数
	public List<T> getDatas() {
		return datas;
	}
	public void setDatas(List<T> datas) {
		this.datas = datas;
	}
	public int getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(int pageno) {
		this.currentPage = pageno;
	}
	public long getTotalno() {
		return totalno;
	}
	public void setTotalno(long totalno) {
		this.totalno = totalno;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int totalsize) {
		this.pageSize = totalsize;
	}
	
	
}
