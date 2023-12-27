package recipes.client.controllers;

import java.util.Arrays;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import recipes.client.dto.Recipe;

@Slf4j
@Controller
public class RecipesController { // Доработать контроллер!
	
	@GetMapping("/recipes")
	public String getAllRecipes(HttpSession session) {
		RestTemplate restTemplate = new RestTemplate();
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", (String) session.getAttribute("token"));
		
		HttpEntity<String> entity = new HttpEntity<>(headers);
		
		Recipe[] array = restTemplate.exchange("http://localhost:8080/api/v1/recipes", 
				HttpMethod.GET, entity, Recipe[].class).getBody();
		
		log.info("Result: {}", Arrays.asList(array));
		
		return "redirect:/";
	}
	
}
