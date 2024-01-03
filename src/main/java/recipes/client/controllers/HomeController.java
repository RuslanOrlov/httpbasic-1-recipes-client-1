package recipes.client.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
import recipes.client.services.SessionService;

//@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {
	
	private final SessionService sessionService;
	
	@GetMapping("/")
	public String home(Model model) {		
		model.addAttribute("isLoggedIn", sessionService.isLoggedIn());		
		return "home";
	}
	
}
