package com.demo.entity;

import javax.persistence.Entity;

public class FProcessDefinition {
	private String id;
	private String category;
	private String name;
	private String key;
	private String description;
	private int version;
	private String resourceName;
	private String deploymentId;
	private String diagramResourceName;
	private boolean hasStartFormKey;
	
	private boolean isSuspended;
	public FProcessDefinition(String id, String category, String name, String key, String description, int version,
			String resourceName, String deploymentId, String diagramResourceName, boolean hasStartFormKey,
			boolean isSuspended, String tenantId) {
		super();
		this.id = id;
		this.category = category;
		this.name = name;
		this.key = key;
		this.description = description;
		this.version = version;
		this.resourceName = resourceName;
		this.deploymentId = deploymentId;
		this.diagramResourceName = diagramResourceName;
		this.hasStartFormKey = hasStartFormKey;
		this.isSuspended = isSuspended;
		this.tenantId = tenantId;
	}
	@Override
	public String toString() {
		return "FProcessDefinition [id=" + id + ", category=" + category + ", name=" + name + ", key=" + key
				+ ", description=" + description + ", version=" + version + ", resourceName=" + resourceName
				+ ", deploymentId=" + deploymentId + ", diagramResourceName=" + diagramResourceName
				+ ", hasStartFormKey=" + hasStartFormKey + ", isSuspended=" + isSuspended + ", tenantId=" + tenantId
				+ "]";
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public String getResourceName() {
		return resourceName;
	}
	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}
	public String getDeploymentId() {
		return deploymentId;
	}
	public void setDeploymentId(String deploymentId) {
		this.deploymentId = deploymentId;
	}
	public String getDiagramResourceName() {
		return diagramResourceName;
	}
	public void setDiagramResourceName(String diagramResourceName) {
		this.diagramResourceName = diagramResourceName;
	}
	public boolean isHasStartFormKey() {
		return hasStartFormKey;
	}
	public void setHasStartFormKey(boolean hasStartFormKey) {
		this.hasStartFormKey = hasStartFormKey;
	}
	public boolean isSuspended() {
		return isSuspended;
	}
	public void setSuspended(boolean isSuspended) {
		this.isSuspended = isSuspended;
	}
	public String getTenantId() {
		return tenantId;
	}
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
	private String tenantId;
}
