package springbootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.demo.entity.FDeployment;
import com.demo.service.WorkFlowService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=Application.class) 
@WebAppConfiguration
public class DeployProcess {
	@Resource
	private RepositoryService repositoryService;
	
	@Resource
	private ProcessEngine processEngine;
	
	@Resource
	private RuntimeService runtimeService;
	
	@Resource
	private TaskService taskService;
	
	@Autowired
	private WorkFlowService workFlowService;

	@Test
	public void initDeploy(){
		workFlowService.saveNewDeploy("leaveBill.zip", "请假流程");
	}

	 @Test
		public void startProcess(){
//		Map variableMap = new HashMap();
//		ProcessInstance pi = runtimeService.startProcessInstanceByKey("leaveBill", "LeaveBill.1112");
		workFlowService.startProcess("leaveBill", "111", "2222");
		}
		
	 @Test
		public void queryTask(){
		List<Task> listTask = taskService.createTaskQuery()
				.taskAssignee("1").list();
			for(Task task : listTask){
			System.out.println("待处理的任务id:" + task.getId() + "  name:"
					+ task.getName());
			}
		}
		
	@Test
		public void completeTask(){
			//taskService.complete("12515");
			Map variableMap = new HashMap<String,Object>();
		variableMap.put("type", 1);
		int completeProcess = workFlowService.completeProcess("同意", "32506", "110", null);
			queryTask();
		}
	@Test
	public void findDeployList(){
		List<FDeployment> list = workFlowService.findDeployList();//获取流程部署
	}
	/*
     * 查询流程定义
     */
    @Test
    public void findProcessDefinition(){
        List<ProcessDefinition> list = processEngine.getRepositoryService()//与流程定义和部署对象相关的Service
                        .createProcessDefinitionQuery()//创建一个流程定义查询
                        /*指定查询条件,where条件*/
//                        .deploymentId("12501")//使用部署对象ID查询
                        //.processDefinitionId(processDefinitionId)//使用流程定义ID查询
                        //.processDefinitionKey(processDefinitionKey)//使用流程定义的KEY查询
                        //.processDefinitionNameLike(processDefinitionNameLike)//使用流程定义的名称模糊查询
                        /*排序*/
                        .orderByProcessDefinitionVersion().asc()//按照版本的升序排列
                        //.orderByProcessDefinitionName().desc()//按照流程定义的名称降序排列
                        
                        .list();//返回一个集合列表，封装流程定义
                        //.singleResult();//返回唯一结果集
                        //.count();//返回结果集数量
                        //.listPage(firstResult, maxResults)//分页查询
        
        if(list != null && list.size()>0){
            for(ProcessDefinition processDefinition:list){
                System.out.println("流程定义ID:"+processDefinition.getId());//流程定义的key+版本+随机生成数
                System.out.println("流程定义名称:"+processDefinition.getName());//对应HelloWorld.bpmn文件中的name属性值
                System.out.println("流程定义的key:"+processDefinition.getKey());//对应HelloWorld.bpmn文件中的id属性值
                System.out.println("流程定义的版本:"+processDefinition.getVersion());//当流程定义的key值相同的情况下，版本升级，默认从1开始
                System.out.println("资源名称bpmn文件:"+processDefinition.getResourceName());
                System.out.println("资源名称png文件:"+processDefinition.getDiagramResourceName());
                System.out.println("部署对象ID:"+processDefinition.getDeploymentId());
                System.out.println("################################");
            }
        }
        
    }
	/*@Test
	public void getImage(){
		try {
			workFlowService.getActivitiProccessImage("20001");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/
}
