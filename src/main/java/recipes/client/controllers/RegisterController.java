package recipes.client.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.HttpClientErrorException;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import recipes.client.services.UserRestService;

@Controller
@RequestMapping("/register")
@RequiredArgsConstructor
public class RegisterController {
	
	private final UserRestService service;
	
	@GetMapping
	public String openRegisterForm() {
		return "registration";
	}
	
	@PostMapping
	public String registerUser(@Valid RegistrationForm form, BindingResult errors, Model model) {
		if (errors.hasErrors() || !form.isConfirmEqualsPassword()) {
			model.addAllAttributes(extractErrorTypesFromRegistrationForm(form));
			return "registration";
		}
		try {
			service.registerUser(form);
		} catch (HttpClientErrorException ex) {
			int statusCode = ex.getStatusCode().value();
			String body = ex.getResponseBodyAsString();
			return "redirect:/error?statusCode=" + statusCode + "&body=" + body;
		}
		return "redirect:/";
	}
	
	private Map<String, Boolean> extractErrorTypesFromRegistrationForm(RegistrationForm form) {
		Map<String, Boolean> mapErrorTypes = new HashMap<>();
		if (form.isBlancUsername())
			mapErrorTypes.put("errorName", true);
		if (form.isBlancEmail())
			mapErrorTypes.put("errorEmail", true);
		if (!form.isConfirmEqualsPassword())
			mapErrorTypes.put("errorPass", true);
		return mapErrorTypes;
	}
	
}
