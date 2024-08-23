package recipes.client.controllers;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.SessionAttributes;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import recipes.client.dtos.Recipe;
import recipes.client.services.RecipeRestService;
//import lombok.extern.slf4j.Slf4j;

@Controller
@SessionAttributes("recipe")
@RequiredArgsConstructor
//@Slf4j
public class ImageProxyController {
	
	private final RecipeRestService recipeService;
	
	/*private String url = "http://localhost:8080/api/v1/recipes/{id}/image";*/
	
	@GetMapping("/recipes/{id}/image")
	public ResponseEntity<byte[]> getRecipeImage(
			@PathVariable Long id, 
			@ModelAttribute Recipe recipe
			) {
		return recipeService.getRecipeImage(recipe.getImageUrl());
	}
	
	@GetMapping("/recipes/{id}/download")
	public void downloadRecipeImage(
			@PathVariable Long id, 
			@ModelAttribute Recipe recipe, 
			HttpServletResponse response
			) throws IOException {
		
		byte[] image = recipeService.getRecipeImage(recipe.getImageUrl()).getBody();
		
		response.setContentType("image/jpeg");
		response.setHeader("Content-Disposition", 
				"attachment; filename=\"recipe-image-" + LocalDateTime.now() + ".jpg\"");
		response.getOutputStream().write(image);
		response.getOutputStream().flush();
	}
	
}
