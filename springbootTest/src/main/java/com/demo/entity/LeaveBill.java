package com.demo.entity;
 
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
@Entity
@Table(name = "leave_bill")
public class LeaveBill {
	@Override
	public String toString() {
		return "LeaveBill [id=" + id + ", days=" + days + ", content=" + content + ", remark=" + remark + ", leaveDate="
				+ leaveDate + ", state=" + state + ", userId=" + userId + "]";
	}

	@Id
	private String id;
	@Column(name="days")
	private String days;
	@Column(name="comtent")
	private String content;
	@Column(name="remark")
	private String remark;
	@Column(name="leave_date")
	private Date leaveDate;
	@Column(name="state")
	private int state;	//0-初始录入   1-开始审批     2-审批完成
	@Column(name="user_id")
	private String userId;
	@Transient
	private String stateVo;
	
	public String getStateVo() {
		switch (state) {
		case 0:
			stateVo = "审批中";
			break;
		case 1:
			stateVo = "完成";
			break;
		default:
			break;
		}
		return stateVo;
	}
	public void setStateVo(String stateVo) {
		this.stateVo = stateVo;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Date getLeaveDate() {
		return leaveDate;
	}
	public void setLeaveDate(Date leaveDate) {
		this.leaveDate = leaveDate;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getDays() {
		return days;
	}
	public void setDays(String days) {
		this.days = days;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	
	public LeaveBill() {
		super();
	}
}
