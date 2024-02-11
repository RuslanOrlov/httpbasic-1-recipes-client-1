package recipes.client.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.client.HttpClientErrorException;

import lombok.RequiredArgsConstructor;
import recipes.client.dtos.IngredientDTO;
import recipes.client.dtos.Recipe;
import recipes.client.services.IngredientRestService;
import recipes.client.services.SessionService;

@Controller
@RequestMapping("/ingredients")
@SessionAttributes("recipe")
@RequiredArgsConstructor
public class IngredientController {
	
	private final IngredientRestService ingredientService;
	private final SessionService sessionService;
	
	/*
	 * Ниже представлен метод, возвращающий признак того, 
	 * аутентифицирован ли пользователь в приложении или нет
	 * 
	 * */

	@ModelAttribute("isLoggedIn")
	public Boolean isLoggedIn() {
		return sessionService.isLoggedIn();
	}
	
	/*
	 * Ниже представлены методы работы с данными (CRUD) посредством REST сервиса
	 * 
	 * */
	
	@GetMapping
	public String getAllIngredients(Model model) {
		Recipe recipe = (Recipe) model.getAttribute("recipe");
		try {
			model.addAttribute("ingredients", formIngredientsList(recipe));
		} catch (HttpClientErrorException ex) {
			int statusCode = ex.getStatusCode().value();
			String body = ex.getResponseBodyAsString();			
			return "redirect:/error?statusCode=" + statusCode + "&body=" + body;
		}
		return "ingredients-list";
	}
	
	private List<IngredientDTO> formIngredientsList(Recipe recipe) 
			throws HttpClientErrorException {
		List<IngredientDTO> ingredients = new ArrayList<>();
		
		ingredients = ingredientService.getAllIngredients(recipe);
		
		return ingredients;
	}
	
}
