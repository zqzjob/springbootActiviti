package com.demo.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.demo.Utils.IdGenerator;
import com.demo.entity.LeaveBill;
import com.demo.service.LeaveBillService;
import com.demo.service.WorkFlowService;

@Controller
@RequestMapping("/leaveBill")
public class LeaveBillController {
	@Autowired
	private LeaveBillService leaveBillService;
	@Autowired
	private WorkFlowService workFlowService;
	@RequestMapping(value="/add",method=RequestMethod.POST)
	public String createLeaveBill(LeaveBill leaveBill){
		String id = IdGenerator.getId();
		leaveBill.setId(id);
		leaveBill.setUserId("1");
		leaveBill.setLeaveDate(new Date());
		leaveBillService.save(leaveBill);
		return "redirect:index?userId=1";
	}
	@RequestMapping(value="/add",method=RequestMethod.GET)
	public String toAdd(LeaveBill leaveBill){
		return "home";
	}
	
	@RequestMapping(value="/index")
	public ModelAndView index(String userId){
		List<LeaveBill> leaveBills = leaveBillService.findByUserId(userId);
		System.out.println(leaveBills.get(0).getContent());
		System.out.println("leaveBills===>"+leaveBills);
		ModelAndView mv = new ModelAndView();
		mv.addObject("leaveBills",leaveBills);
		mv.setViewName("index");
		return mv;
	}
	@RequestMapping(value = "getImage/{formId}",method=RequestMethod.GET)
	public void getImage(@PathVariable("formId") String formId,HttpServletResponse response){
		// 设置页面不缓存
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/png");
        try {
			OutputStream os = response.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String bussinessKey = "leaveBill." + formId;
		workFlowService.getImageByBussinessKey(bussinessKey,response);
	}
	@RequestMapping(value="/check/{id}/{taskId}",method=RequestMethod.GET)
	public ModelAndView check(@PathVariable String id,@PathVariable String taskId,ModelAndView mv){
		LeaveBill leaveBill = leaveBillService.findById(id);
		mv.addObject("leaveBill",leaveBill);
		mv.addObject("taskId",taskId);
		mv.setViewName("leaveBill/check");
		return mv;
	}
	@RequestMapping(value="/check/{id}/{taskId}",method=RequestMethod.POST)
	public String doCheck(@PathVariable String id,@PathVariable String taskId,String remark){
		LeaveBill leaveBill = leaveBillService.findById(id);
		int state = workFlowService.completeProcess(remark, taskId, "lisi", null);
		if(state == 1){
			leaveBill.setState(state);
			leaveBillService.save(leaveBill);
		}
		return "index";
	}
}
