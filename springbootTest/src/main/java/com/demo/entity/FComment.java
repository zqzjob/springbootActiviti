package com.demo.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
public class FComment {
	private String id;
	private String userId;
	private String userName;
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	private Date time;
	private String taskId;
	private String processInstanceId;
	private String type;
	private String fullMessage;
	public FComment() {
		super();
	}
	
	public FComment(String id, String userId, String userName, Date time, String taskId, String processInstanceId,
			String type, String fullMessage) {
		super();
		this.id = id;
		this.userId = userId;
		this.userName = userName;
		this.time = time;
		this.taskId = taskId;
		this.processInstanceId = processInstanceId;
		this.type = type;
		this.fullMessage = fullMessage;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getFullMessage() {
		return fullMessage;
	}
	public void setFullMessage(String fullMessage) {
		this.fullMessage = fullMessage;
	}
}
