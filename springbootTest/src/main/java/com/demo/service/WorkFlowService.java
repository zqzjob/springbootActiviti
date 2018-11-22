package com.demo.service;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.repository.ProcessDefinition;
import org.springframework.stereotype.Service;

import com.demo.entity.DataGrid;
import com.demo.entity.FComment;
import com.demo.entity.FDeployment;
import com.demo.entity.FProcessDefinition;
public interface WorkFlowService {
	/**
	 * 设置流程变量
	 * @param formid
	 * @return
	 */
	public Map<String, Object> setvariables(String formid);
	
	
	/**
	 * 整个流程开始时需要执行的任务
	 * @param formid
	 */
	public void startRunTask(String formid);
	
	
	/**
	 * 整个流程结束需要执行的任务
	 * @param formid
	 */
	public void endRunTask(String formid);


	List<String> findOutComeListByTaskId(String taskId);


	boolean saveNewDeploy(String filepath, String filename);


	List<FDeployment> findDeployList();


	boolean deldeployment(String deploymentid);


	boolean startProcess(String formKey, String userid, String formid);


	List<FProcessDefinition> findProcessDefinitionList();


	InputStream lookProcessImage(String deploymentid, String imagename);


	ProcessDefinition lookCurrentProcessImage(String taskId);


	Map<String, Object> findCoordingByTask(String taskId);


	ProcessDefinition lookCurrentProcessImgByFormId(String formid, String formKey);


	Map<String, Object> findCoordingByTaskByFormId(String formid, String formKey);


	Map<String, Object> findFormModelByTaskId(String taskId);


	int completeProcess(String remark, String taskId, String userId, String outcome);


	List<FComment> getComment(String currenttaskId);


	List<FComment> getCommentByLeavebillId(String leaveBillId);


	DataGrid<Map<String, Object>> mytasklist(String userid, int pageIndex, int pageSize);


	void getActivitiProccessImage(String pProcessInstanceId,HttpServletResponse response) throws Exception;


	void getImageByBussinessKey(String bussinessKey,HttpServletResponse response);
}
