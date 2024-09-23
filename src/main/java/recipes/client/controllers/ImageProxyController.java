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
import lombok.extern.slf4j.Slf4j;

@Controller
@SessionAttributes("recipe")
@RequiredArgsConstructor
@Slf4j
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
	
	@GetMapping("/recipes/{id}/image-download")
	public void downloadRecipeImage(
			@PathVariable Long id, 
			@ModelAttribute Recipe recipe, 
			HttpServletResponse response
			) throws IOException {
		
		byte[] image = recipeService.getRecipeImage(recipe.getImageUrl()).getBody();
		
		// - Если изображение в БД отсутствует, с сервера будет приходить значение null. 
		//   В таком случае массив image инициализируется значением пустого массива. 
		// - В то же время в данной реализации клиента кнопка выгрузки изображения 
		//   в интерфейсе будет заблокирована, если свойство URL изображения будет 
		//   равно null, поэтому данный код не будет доступен. 
		// - Однако если кнопка выгрузки изображения будет доступна при любых условиях, 
		//   то при отсутствии изображения в БД будет происходить выгрузка пустого 
		//   файла с расширением .jpg.
		if (image == null) image = new byte[0];
		
		response.setContentType("image/jpeg");
		response.setHeader("Content-Disposition", 
				"attachment; filename=\"recipe-image-" + LocalDateTime.now() + ".jpg\"");
		response.getOutputStream().write(image);
		response.getOutputStream().flush();
	}
	
}
