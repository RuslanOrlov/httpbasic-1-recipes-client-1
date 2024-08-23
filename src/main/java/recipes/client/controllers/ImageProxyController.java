package recipes.client.controllers;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import recipes.client.dtos.Recipe;
//import lombok.extern.slf4j.Slf4j;
import recipes.client.services.SessionService;

@Controller
@SessionAttributes("recipe")
@RequiredArgsConstructor
//@Slf4j
public class ImageProxyController {
	
	private final SessionService sessionService;
	
	private RestTemplate restTemplate = new RestTemplate();
	
	/*private String url = "http://localhost:8080/api/v1/recipes/{id}/image";*/
	
	@GetMapping("/recipes/{id}/image")
	public ResponseEntity<byte[]> getRecipeImage(
			@PathVariable Long id, 
			@ModelAttribute Recipe recipe
			) {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("Authorization", sessionService.getAuthHeader());
		
		ResponseEntity<byte[]> response = 
				this.restTemplate.exchange(recipe.getImageUrl(), HttpMethod.GET, 
						new HttpEntity<>(httpHeaders), byte[].class);
		
		if (response.getStatusCode().is2xxSuccessful()) {
			return response;
		}
		return null;
	}
	
}
