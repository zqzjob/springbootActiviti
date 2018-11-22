package com.demo.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
@Entity(name="approvalbean")
public class ApprovalBeanConf {
	@Id
	private String id;
	@Column(name="bean_name")
	private String beanName;
	@Column(name="showform_url")
	private String showFormUrl;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getBeanName() {
		return beanName;
	}
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}
	
	public String getShowFormUrl() {
		return showFormUrl;
	}
	public void setShowFormUrl(String showFormUrl) {
		this.showFormUrl = showFormUrl;
	}
	
	public ApprovalBeanConf(String id, String beanName) {
		super();
		this.id = id;
		this.beanName = beanName;
	}
	
	public ApprovalBeanConf() {
		super();
	}
	
	public ApprovalBeanConf(String id, String beanName, String showFormUrl) {
		super();
		this.id = id;
		this.beanName = beanName;
		this.showFormUrl = showFormUrl;
	}
	@Override
	public String toString() {
		return "ApprovalBeanConf [id=" + id + ", beanName=" + beanName + ", showFormUrl=" + showFormUrl + "]";
	}
}
