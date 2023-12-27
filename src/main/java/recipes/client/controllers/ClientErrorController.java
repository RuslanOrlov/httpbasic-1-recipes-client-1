package recipes.client.controllers;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ClientErrorController implements ErrorController {
	
	@RequestMapping(method = {	RequestMethod.GET, 		RequestMethod.POST, 
								RequestMethod.HEAD, 	RequestMethod.OPTIONS, 
								RequestMethod.PUT, 		RequestMethod.PATCH, 
								RequestMethod.DELETE, 	RequestMethod.TRACE	}, 
					path = "/error")
	public String showErrorPage(
			@RequestParam int statusCode, 
			@RequestParam String body, 
			Model model
			) {
		model.addAttribute("statusCode", statusCode);
		model.addAttribute("body", body);
		return "error";
	}
	
}
