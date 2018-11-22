package com.demo.controller;

import java.util.Map;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.demo.entity.DataGrid;
import com.demo.service.WorkFlowService;

@Controller
@RequestMapping("/workflow")
public class WorkFlowController {
	@Autowired
	private TaskService taskService;
	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	private WorkFlowService workFlowService;
	@RequestMapping("check/{taskId}")
	public String check(@PathVariable String taskId){
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		String processInstanceId = task.getProcessInstanceId();
		System.out.println(processInstanceId);
		ProcessInstance inst = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
		String businessKey = inst.getBusinessKey();
		System.out.println("bussinessKey:"+businessKey);
		String[] split = businessKey.split("\\.");
		System.out.println("redirect:/"+split[0]+"/check/"+split[1]+"/"+taskId);
		return "redirect:/"+split[0]+"/check/"+split[1]+"/"+taskId;
	}
	
	@RequestMapping(value="/task/{userId}")
	public ModelAndView task(@PathVariable String userId,ModelAndView mv){
		DataGrid<Map<String, Object>> page = workFlowService.mytasklist(userId, 1, 20);
		mv.addObject("page", page);
		mv.setViewName("task");
		return mv;
	}
}
