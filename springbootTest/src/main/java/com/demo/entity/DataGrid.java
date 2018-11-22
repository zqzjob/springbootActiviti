package com.demo.entity;

import java.util.List;

public class DataGrid<T> {
	private long total;
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		this.total = total;
	}
	public List<T> getRows() {
		return rows;
	}
	public void setRows(List<T> rows) {
		this.rows = rows;
	}
	private List<T> rows;
}
