package com.demo.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
public class FDeployment {
	private String id;
	private String name;
	private Date deploymentTime;
	private String category;
	private String tenantId;
	public FDeployment(String id, String name, Date deploymentTime, String category, String tenantId) {
		super();
		this.id = id;
		this.name = name;
		this.deploymentTime = deploymentTime;
		this.category = category;
		this.tenantId = tenantId;
	}
	@Override
	public String toString() {
		return "FDeployment [id=" + id + ", name=" + name + ", deploymentTime=" + deploymentTime + ", category="
				+ category + ", tenantId=" + tenantId + "]";
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getDeploymentTime() {
		return deploymentTime;
	}
	public void setDeploymentTime(Date deploymentTime) {
		this.deploymentTime = deploymentTime;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getTenantId() {
		return tenantId;
	}
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
}
