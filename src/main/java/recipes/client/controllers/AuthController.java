package recipes.client.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.HttpClientErrorException;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import recipes.client.dto.AuthRequest;
import recipes.client.services.UserRestService;

@Controller
@RequiredArgsConstructor
public class AuthController {
	
	private final UserRestService service;
	
	@GetMapping("/auth")
	public String authForm() {
		return "authentication";
	}
	
	@PostMapping("/auth")
	public String authUser(@Valid AuthRequest request, BindingResult errors, Model model) {
		if (errors.hasErrors()) {
			model.addAttribute("error", true);
			return "authentication";
		}
		try {
			service.authUser(request);
		} catch (HttpClientErrorException ex) {
			int statusCode = ex.getStatusCode().value();
			String body = ex.getResponseBodyAsString();			
			return "redirect:/error?statusCode=" + statusCode + "&body=" + body;
		}
		return "redirect:/";
	}
	
	@GetMapping("/logout")
	public String logout() {
		service.logout();
		return "redirect:/";
	}
	
}
