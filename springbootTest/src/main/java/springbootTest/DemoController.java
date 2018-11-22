package springbootTest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.service.WorkFlowService;

@Controller
public class DemoController {
	@Autowired
	private WorkFlowService workFlowService;
	/*@RequestMapping("/hello")
	public String hello(){
		System.out.println("index.jsp");
		try {
			workFlowService.getActivitiProccessImage("20001");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "index.jsp";
	}*/
}
