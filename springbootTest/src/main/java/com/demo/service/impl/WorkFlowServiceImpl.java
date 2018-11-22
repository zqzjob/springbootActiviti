package com.demo.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.activiti.validation.ValidationError;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import com.demo.dao.IApprovalBeanConfDao;
import com.demo.dao.ILeaveBillDao;
import com.demo.entity.ApprovalBeanConf;
import com.demo.entity.DataGrid;
import com.demo.entity.FComment;
import com.demo.entity.FDeployment;
import com.demo.entity.FProcessDefinition;
import com.demo.entity.LeaveBill;
import com.demo.service.WorkFlowService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
@Service
public class WorkFlowServiceImpl implements WorkFlowService{
	@Autowired
	private ProcessEngine processEngine;
	@Autowired
	private RepositoryService repositoryService;
	
	@Autowired
	private RuntimeService runtimeService;
	
	@Autowired
	private TaskService taskService;
	
	@Autowired
	private FormService formService;
	
	@Autowired
	private HistoryService historyService;
	
	@Autowired
	private ILeaveBillDao leavebiilldao;
	
	//流程图zip包存储目录
	private static final String FLOW_FOLDER="D://activiti/";
	/**
	 * 用zip包部署流程定义
	 */
	@Override
	public boolean saveNewDeploy(String filepath,String filename) {
		filepath = FLOW_FOLDER+filepath;
		boolean returnflag  = true;
		ZipInputStream zipInputStream;
		try {
			
			System.out.println(filepath);
			zipInputStream = new ZipInputStream(new FileInputStream(new File(filepath)));
			repositoryService.createDeployment()
			.name(filename)
			.addZipInputStream(zipInputStream)
			.deploy();
			System.out.println("部署完成！");
			return returnflag;
		} catch (FileNotFoundException e) {
			returnflag = false;
			e.printStackTrace();
		}
		return returnflag;
	}
 
	/**
	 * 查询部署对象信息
	 */
	@Override
	public List<FDeployment> findDeployList() {
		List<FDeployment> relist = null;
		List<Deployment> list = repositoryService.createDeploymentQuery().orderByDeploymenTime().desc().list();
		if(list!=null&&list.size()>0){
			relist = new ArrayList<FDeployment>();
			for (Deployment dm : list) {
				FDeployment fDeployment = new FDeployment(dm.getId(), dm.getName(), dm.getDeploymentTime(), dm.getCategory(), dm.getTenantId());
				relist.add(fDeployment);
				System.err.println(fDeployment.toString());
			}
		}
		return relist;
	}
	
	
	/**
	 * 删除部署信息 
	 */
	@Override
	public boolean deldeployment(String deploymentid) {
		try{
			repositoryService.deleteDeployment(deploymentid, true);
		}catch(Exception e){
			return false;
		}
		return true;
	}
	
	
	
	/**
	 * 启动流程实例
	 * formKey:流程定义的key（流程图中ID属性）
	 * userid:用户id
	 * formid:业务id（如请假单编号）
	 */
	@Override
	public boolean startProcess(String formKey,String userid,String formid) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userId", userid);
		//使用流程变量设置字符串（格式 ： LeaveBill.Id 的形式）
		//使用正在执行对象表中的一个字段BUSINESS_KEY(Activiti提供的一个字段)，让启动的流程（流程实例）关联业务
		String objId = formKey + "." +formid;
		map.put("objId", objId);
		
		runtimeService.startProcessInstanceByKey(formKey, objId, map);
		return true;
	}
	
	/**
	 * 根据业务bussinessKey查看流程图
	 */
	@Override
	public void getImageByBussinessKey(String bussinessKey,HttpServletResponse response){
		System.out.println(bussinessKey);
		ProcessInstance inst = runtimeService.createProcessInstanceQuery().processInstanceBusinessKey(bussinessKey).singleResult();
		try {
			getActivitiProccessImage(inst.getId(),response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	 /** 
     * 根据流程实例id获取流程图像，已执行节点和流程线高亮显示
     */
	@Override
    public void getActivitiProccessImage(String pProcessInstanceId,HttpServletResponse response) throws Exception {
        // 设置页面不缓存
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/png");
        OutputStream os = response.getOutputStream();
        try {
            //  获取历史流程实例
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(pProcessInstanceId).singleResult();

            if (historicProcessInstance == null) {
                throw new Exception();
            } else {

                // 获取流程历史中已执行节点，并按照节点在流程中执行先后顺序排序
                List<HistoricActivityInstance> historicActivityInstanceList = historyService.createHistoricActivityInstanceQuery()
                        .processInstanceId(pProcessInstanceId).orderByHistoricActivityInstanceId().asc().list();

                // 已执行的节点ID集合
                List<String> executedActivityIdList = new ArrayList<String>();
                @SuppressWarnings("unused")
                int index = 1;
                for (HistoricActivityInstance activityInstance : historicActivityInstanceList) {
                    executedActivityIdList.add(activityInstance.getActivityId());
                    index++;
                }
                ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(pProcessInstanceId).singleResult();
                String processDefinitionId = processInstance.getProcessDefinitionId();
                BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
                System.out.println(bpmnModel);
                // 获取流程图图像字符流
                InputStream imageStream = new DefaultProcessDiagramGenerator().generateDiagram(bpmnModel,"png",
                						executedActivityIdList, 
                						new ArrayList<String>(),
                						"黑体","黑体","黑体",null,1.0);

//                response.setContentType("image/png");
//                OutputStream os = response.getOutputStream();
//                OutputStream os = new FileOutputStream("D:/workflow.png");
                int bytesRead = 0;
                byte[] buffer = new byte[8192];
                while ((bytesRead = imageStream.read(buffer, 0, 8192)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
                os.close();
                imageStream.close();
            }
        } catch (Exception e) {
            throw new Exception(e);
        }
    }
	
	/**
	 * 查询流程定义信息
	 */
	@Override
	public List<FProcessDefinition> findProcessDefinitionList(){
		List<FProcessDefinition> relist = null;
		List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().orderByProcessDefinitionVersion().desc().list();
		if(list!=null&&list.size()>0){
			relist = new ArrayList<FProcessDefinition>();
			for (ProcessDefinition pd : list) {
				FProcessDefinition fProcessDefinition = new FProcessDefinition(pd.getId(), pd.getCategory(), pd.getName(), pd.getKey(), pd.getDescription(), pd.getVersion(), pd.getResourceName(), pd.getDeploymentId(),pd.getDiagramResourceName(), pd.hasStartFormKey(), pd.isSuspended(), pd.getTenantId());
				relist.add(fProcessDefinition);
				System.out.println(fProcessDefinition.toString());
			}
		}
		return relist;
	}
	
 
	
	/**
	 * 查看总体流程图
	 */
	@Override
	public InputStream lookProcessImage(String deploymentid, String imagename) {
		InputStream in = repositoryService.getResourceAsStream(deploymentid, imagename);
		return in;
	}
 
	
	/**
	 * 查看当前流程图(1)
	 * @param taskid
	 */
	@Override
	public ProcessDefinition lookCurrentProcessImage(String taskId) {
		//任务ID
		//1：获取任务ID，获取任务对象，使用任务对象获取流程定义ID，查询流程定义对象
		//使用任务ID，查询任务对象
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		//获取流程定义ID
		String processDefinitionId = task.getProcessDefinitionId();
		//查询流程定义的对象
		ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()//创建流程定义查询对象，对应表act_re_procdef 
							.processDefinitionId(processDefinitionId)//使用流程定义ID查询
							.singleResult();
		return pd;
	}
	
	
	
	/**  查看当前流程图(2)	--	公用
	 *   查看当前活动，获取当期活动对应的坐标x,y,width,height，将4个值存放到Map<String,Object>中
	 * 	 map集合的key：表示坐标x,y,width,height
	 * 	 map集合的value：表示坐标对应的值
	 */
	@Override
	public Map<String, Object> findCoordingByTask(String taskId) {
		//存放坐标
		Map<String, Object> map = new HashMap<String,Object>();
		//使用任务ID，查询任务对象
		Task task = taskService.createTaskQuery()//
					.taskId(taskId)//使用任务ID查询
					.singleResult();
		//获取流程定义的ID
		String processDefinitionId = task.getProcessDefinitionId();
		//获取流程定义的实体对象（对应.bpmn文件中的数据）
		ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity)repositoryService.getProcessDefinition(processDefinitionId);
		//流程实例ID
		String processInstanceId = task.getProcessInstanceId();
		//使用流程实例ID，查询正在执行的执行对象表，获取当前活动对应的流程实例对象
		ProcessInstance pi = runtimeService.createProcessInstanceQuery()//创建流程实例查询
					.processInstanceId(processInstanceId)//使用流程实例ID查询
					.singleResult();
		//获取当前活动的ID
		String activityId = pi.getActivityId();
		//获取当前活动对象
		ActivityImpl activityImpl = processDefinitionEntity.findActivity(activityId);//活动ID
		//获取坐标
		map.put("x", activityImpl.getX());
		map.put("y", activityImpl.getY());
		map.put("width", activityImpl.getWidth());
		map.put("height", activityImpl.getHeight());
		
		return map;
	}
	
	
	
	
	@Override
	public ProcessDefinition lookCurrentProcessImgByFormId(String formid, String formKey) {
		Task task = taskService.createTaskQuery().processInstanceBusinessKey(formKey+"."+formid).singleResult();
		String processDefinitionId = task.getProcessDefinitionId();
		ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()//创建流程定义查询对象，对应表act_re_procdef 
				.processDefinitionId(processDefinitionId)//使用流程定义ID查询
				.singleResult();
		return pd;
	}
	
	
	
	@Override
	public Map<String, Object> findCoordingByTaskByFormId(String formid, String formKey) {
		
		//存放坐标
		Map<String, Object> map = new HashMap<String,Object>();
		
		Task task = taskService.createTaskQuery().processInstanceBusinessKey(formKey+"."+formid).singleResult();
		
		//获取流程定义的ID
		String processDefinitionId = task.getProcessDefinitionId();
		//获取流程定义的实体对象（对应.bpmn文件中的数据）
		ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity)repositoryService.getProcessDefinition(processDefinitionId);
		//流程实例ID
		String processInstanceId = task.getProcessInstanceId();
		//使用流程实例ID，查询正在执行的执行对象表，获取当前活动对应的流程实例对象
		ProcessInstance pi = runtimeService.createProcessInstanceQuery()//创建流程实例查询
					.processInstanceId(processInstanceId)//使用流程实例ID查询
					.singleResult();
		//获取当前活动的ID
		String activityId = pi.getActivityId();
		//获取当前活动对象
		ActivityImpl activityImpl = processDefinitionEntity.findActivity(activityId);//活动ID
		//获取坐标
		map.put("x", activityImpl.getX());
		map.put("y", activityImpl.getY());
		map.put("width", activityImpl.getWidth());
		map.put("height", activityImpl.getHeight());
		
		return map;
	}
	
	
	
	
	
	
 
	/**
	 * 查看个人任务列表
	 */
	@Override
	public DataGrid<Map<String, Object>> mytasklist(String userid,int pageIndex,int pageSize) {
		DataGrid<Map<String, Object>> dataGrid = new DataGrid<Map<String, Object>>();
		
		int firstResult = (pageIndex-1)*pageSize;
		int maxResults = firstResult+pageSize;
		
		TaskQuery taskQuery = taskService.createTaskQuery().taskAssignee(userid);
		long allcount = taskQuery.count();
		System.out.println(allcount);
		dataGrid.setTotal(allcount);
		
		List<Task> list = taskQuery.orderByTaskCreateTime().desc().listPage(firstResult, maxResults);
		
		List<Map<String, Object>> listmap = new ArrayList<Map<String, Object>>();
		for (Task task : list) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", task.getId());
			map.put("name", task.getName());
			map.put("description", task.getDescription());
			map.put("priority", task.getPriority());
			map.put("owner", task.getOwner());
			map.put("assignee", task.getAssignee());
			map.put("delegationState", task.getDelegationState());
			map.put("processInstanceId", task.getProcessInstanceId());
			map.put("executionId", task.getExecutionId());
			map.put("processDefinitionId", task.getProcessDefinitionId());
			map.put("createTime", task.getCreateTime());
			map.put("taskDefinitionKey", task.getTaskDefinitionKey());
			map.put("dueDate", task.getDueDate());
			map.put("category", task.getCategory());
			map.put("parentTaskId", task.getParentTaskId());
			map.put("tenantId", task.getTenantId());
			
			Map<String, Object> FormModel_map = findFormModelByTaskId(task.getId());
			//表单中避免使用 hfmx_actWorkFlow_formType 关键字
			/*map.put("hfmx_actWorkFlow_formType", FormModel_map.get("hfmx_actWorkFlow_formType"));
			map.put("formid", FormModel_map.get("id"));*/
			
			map.putAll(FormModel_map);
			
			/*List<Map<String,Object>> searchForMap = leavebilldao.searchForMap("select * from sysuser where id="+task.getAssignee());
			map.put("processUser", searchForMap.get(0).get("name"));*/
			
			listmap.add(map);
		}
		dataGrid.setRows(listmap);
		
		return dataGrid;
	}
 
	
	
	
	@Override
	public Map<String, Object> findFormModelByTaskId(String taskId) {
		//任务Id 查询任务对象
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		
		//任务对象  获取流程实例Id
		String processInstanceId = task.getProcessInstanceId();
 
		//流程实例Id 查询正在执行的执行对象表  返回流程实例对象
		ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
		
		//流程实例对象 获取 BUSINESS_KEY ，从而获取表单对象
		String businessKey = processInstance.getBusinessKey();
		String[] split = businessKey.split("\\.");
		
		
		Map<String, Object> map = new HashMap<String,Object>();
 
		map.put("hfmx_actWorkFlow_formType", split[0]);
		map.put("formid", split[1]);
		
		return map;
	}
	
	
 
	/**
	 * 完成提交任务
	 */
	@Override
	public int completeProcess(String remark, String taskId,String userId,String outcome) {
		
		//任务Id 查询任务对象
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		
		//任务对象  获取流程实例Id
		String processInstanceId = task.getProcessInstanceId();
		
		//设置审批人的userId
		Authentication.setAuthenticatedUserId(userId);
		
		//添加记录
		taskService.addComment(taskId, processInstanceId, remark);
		
		/**
		 * 如果连线的名称是'默认提交'，那么就不需要设置，如果不是，就需要设置流程变量
		 * 在完成任务之前，设置流程变量，按照连线的名称，去完成任务
				 流程变量的名称：outcome
				 流程变量的值：连线的名称
		 */
		Map<String, Object> variables = new HashMap<String,Object>();
		if(outcome!=null && !outcome.equals("默认提交")){
			variables.put("outcome", outcome);
		}
		
		
	    //完成办理
	    taskService.complete(taskId,variables);
	    
	    
	    //执行结束  更改状态
		ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
		if(processInstance==null){
			return 1;
		}else{
			return 0;
		}
	}
 
	
	/**
	 * 通过当前任务Id 获取 批注时的备注信息
	 */
	@Override
	public List<FComment> getComment(String currenttaskId) {
		List<FComment> relist = new ArrayList<FComment>();
		List<Comment> list = new ArrayList<Comment>();
		Task task = taskService.createTaskQuery().taskId(currenttaskId).singleResult();
		
		String processInstanceId = task.getProcessInstanceId();
		
		List<HistoricTaskInstance> htilist = historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).list();
		if(htilist!=null&&htilist.size()>0){
			for (HistoricTaskInstance hti : htilist) {
				String htaskid = hti.getId();
				List<Comment> tasklist = taskService.getTaskComments(htaskid);
				list.addAll(tasklist);
			}
		}
		
		for (Comment com : list) {
			FComment fc = new FComment();
			fc.setId(com.getId());
			fc.setUserId(com.getUserId());
			/*List<Map<String,Object>> searchForMap = leavebilldao.searchForMap("select * from sysuser where id="+com.getUserId());
			fc.setUserName(searchForMap.get(0).get("name").toString());*/
			fc.setTime(com.getTime());
			fc.setTaskId(com.getTaskId());
			fc.setProcessInstanceId(com.getProcessInstanceId());
			fc.setType(com.getType());
			fc.setFullMessage(com.getFullMessage());
			relist.add(fc);
		}
		
		System.out.println(relist.toString());
		
		return relist;
	}
 
	
	
	
	
	/**
	 * 通过BussinessKey查找批注信息
	 */
	@Override
	public List<FComment> getCommentByLeavebillId(String bussinessKey) {
		List<FComment> relist = new ArrayList<FComment>();
		ProcessInstance processInst = runtimeService.createProcessInstanceQuery().processInstanceBusinessKey(bussinessKey).singleResult();
		List<Comment> list = taskService.getProcessInstanceComments(processInst.getId());
		
		for (Comment com : list) {
			FComment fc = new FComment();
			fc.setId(com.getId());
			fc.setUserId(com.getUserId());
			/*List<Map<String,Object>> searchForMap = leavebilldao.searchForMap("select * from sysuser where id="+com.getUserId());
			fc.setUserName(searchForMap.get(0).get("name").toString());*/
			fc.setTime(com.getTime());
			fc.setTaskId(com.getTaskId());
			fc.setProcessInstanceId(com.getProcessInstanceId());
			fc.setType(com.getType());
			fc.setFullMessage(com.getFullMessage());
			relist.add(fc);
		}
		Collections.reverse(relist);
		return relist;
	}
 
	
	/**
	 * 已知任务ID，查询ProcessDefinitionEntiy对象，从而获取当前任务完成之后的连线名称，并放置到List<String>集合中
	 */
	@Override
	public List<String> findOutComeListByTaskId(String taskId) {
		//返回存放连线的名称集合
		List<String> list = new ArrayList<String>();
		//1:使用任务ID，查询任务对象
		Task task = taskService.createTaskQuery()//
					.taskId(taskId)//使用任务ID查询
					.singleResult();
		//2：获取流程定义ID
		String processDefinitionId = task.getProcessDefinitionId();
		//3：查询ProcessDefinitionEntiy对象
		ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(processDefinitionId);
		//使用任务对象Task获取流程实例ID
		String processInstanceId = task.getProcessInstanceId();
		//使用流程实例ID，查询正在执行的执行对象表，返回流程实例对象
		ProcessInstance pi = runtimeService.createProcessInstanceQuery()//
					.processInstanceId(processInstanceId)//使用流程实例ID查询
					.singleResult();
		//获取当前活动的id
		String activityId = pi.getActivityId();
		//4：获取当前的活动
		ActivityImpl activityImpl = processDefinitionEntity.findActivity(activityId);
		//5：获取当前活动完成之后连线的名称
		List<PvmTransition> pvmList = activityImpl.getOutgoingTransitions();
		if(pvmList!=null && pvmList.size()>0){
			for(PvmTransition pvm:pvmList){
				String name = (String) pvm.getProperty("name");
				if(StringUtils.isNotBlank(name)){
					list.add(name);
				}else{
					list.add("默认提交");
				}
			}
		}
		return list;
	}
	/**
	 * 根据modelId部署流程对象
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping({"deployModel"})
	  @ResponseBody
	  public String deployModel(HttpServletRequest request, HttpServletResponse response)
	    throws Exception
	  {
	    String modelId = request.getParameter("modelId");
	    Model modelData = this.repositoryService.getModel(modelId);
	    ObjectNode modelNode = (ObjectNode)new ObjectMapper().readTree(this.repositoryService.getModelEditorSource(modelId));
	    BpmnModel model = new BpmnJsonConverter().convertToBpmnModel(modelNode);
	    
	    List<ValidationError> errors = this.repositoryService.validateProcess(model);
	    if (errors.size() > 0)
	    {
	      StringBuffer errSb = new StringBuffer();
	      for (ValidationError ve : errors) {
	        errSb.append(ve.toString()).append("\n");
	      }
	      return "error";
	    }
	    byte[] bpmnBytes = new BpmnXMLConverter().convertToXML(model);
	    
	    String processName = modelData.getName() + ".bpmn20.xml";
	    Deployment deployment = this.repositoryService.createDeployment()
	      .name(modelData.getName())
	      .addString(processName, new String(bpmnBytes))
	      .deploy();
	    return "success";
	  }

	@Override
	public Map<String, Object> setvariables(String formid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void startRunTask(String formid) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void endRunTask(String formid) {
		// TODO Auto-generated method stub
		
	}
}
