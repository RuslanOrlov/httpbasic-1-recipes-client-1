package recipes.client.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.client.HttpClientErrorException;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
import recipes.client.dtos.IngredientDTO;
import recipes.client.dtos.IngredientWrapper;
import recipes.client.dtos.Recipe;
import recipes.client.dtos.RecipeDTO;
import recipes.client.props.RecipeProps;
import recipes.client.services.IngredientRestService;
import recipes.client.services.SessionService;

//@Slf4j
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

	@ModelAttribute("props")
	public RecipeProps props() {
		return new RecipeProps();
	}
	
	/*
	 * Ниже представлены методы работы с данными (CRUD) посредством REST сервиса
	 * 
	 * */
	
	@GetMapping
	public String getAllIngredientsOfOneRecipe(Model model, @ModelAttribute Recipe recipe) {
		// Здесь получаем из модели атрибут "recipe" 
		// путем внедрения его как параметр метода
		try {
			Recipe requested = formIngredientsList(recipe);
			model.addAttribute("recipe", requested);
			model.addAttribute("ingredients", requested.getIngredients());
		} catch (HttpClientErrorException ex) {
			int statusCode = ex.getStatusCode().value();
			String body = ex.getResponseBodyAsString();			
			return "redirect:/error?statusCode=" + statusCode + "&body=" + body;
		}
		
		return "ingredients-list";
	}
	
	private Recipe formIngredientsList(Recipe recipe) 
			throws HttpClientErrorException {
		return ingredientService.getAllIngredientsOfOneRecipe(recipe).createRecipe();
	}
	
	@GetMapping("/{id}")
	public String getOneIngredientOfOneRecipe(@PathVariable Long id, Model model) {
		// Здесь получаем атрибут "recipe", извлекая 
		// его из модели с помощью метода getAttribute() 
		Recipe recipe = (Recipe) model.getAttribute("recipe");
		
		try {
			IngredientWrapper requested = ingredientService
											.getOneIngredientOfOneRecipe(recipe, id);
			model.addAttribute("ingredient", requested.getIngredient());
		} catch (HttpClientErrorException ex) {
			int statusCode = ex.getStatusCode().value();
			String body = ex.getResponseBodyAsString();			
			return "redirect:/error?statusCode=" + statusCode + "&body=" + body;
		}
		
		return "ingredient-card";
	}
	
	@GetMapping("/new")
	public String openCreateIngredientForm(
			@ModelAttribute("ingredient") IngredientDTO ingredient) {
		return "ingredient-create";
	}
	
	@PostMapping
	public String postIngredient(
			@Valid IngredientDTO ingredient, 
			BindingResult errors, 
			@ModelAttribute Recipe recipe) {
		// Здесь получаем из модели атрибут "recipe" 
		// путем внедрения его как параметр метода
		if (errors.hasErrors()) 
			return "ingredient-create";
		
		IngredientWrapper wrapper = new IngredientWrapper();
		wrapper.setRecipe(RecipeDTO.builder()
				.id(recipe.getId())
				.name(recipe.getName())
				.description(recipe.getDescription())
				.build());
		wrapper.setIngredient(ingredient);
		try {
			ingredientService.postIngredient(wrapper);
		} catch (HttpClientErrorException ex) {
			int statusCode = ex.getStatusCode().value();
			String body = ex.getResponseBodyAsString();			
			return "redirect:/error?statusCode=" + statusCode + "&body=" + body;
		}
		
		return "redirect:/ingredients";
	}
	
	@GetMapping("/{id}/edit")
	public String openEditIngredientForm(@PathVariable Long id, Model model) {
		// Здесь получаем атрибут "recipe", извлекая 
		// его из модели с помощью метода getAttribute() 
		Recipe recipe = (Recipe) model.getAttribute("recipe");
		
		IngredientWrapper wrapper = new IngredientWrapper();
		
		try {
			wrapper = ingredientService.getOneIngredientOfOneRecipe(recipe, id);
		} catch (HttpClientErrorException ex) {
			int statusCode = ex.getStatusCode().value();
			String body = ex.getResponseBodyAsString();			
			return "redirect:/error?statusCode=" + statusCode + "&body=" + body;
		}
		
		model.addAttribute("oldName", wrapper.getIngredient().getName());
		model.addAttribute("ingredient", 
								new IngredientDTO(wrapper.getIngredient().getId(), null));
		
		return "ingredient-edit";
	}
	
	@PutMapping
	public String putRecipe(
			@Valid IngredientDTO ingredient, 
			BindingResult errors,
			Model model) {
		// Здесь получаем атрибут "recipe", извлекая 
		// его из модели с помощью метода getAttribute() 
		Recipe recipe = (Recipe) model.getAttribute("recipe");
		
		if (errors.hasErrors()) 
			return "ingredient-edit";
		
		IngredientDTO patch = new IngredientDTO(ingredient.getId(), null);
		
		if (ingredient.getName() != null && ingredient.getName().trim().length() > 0)
			patch.setName(ingredient.getName().trim());
		
		IngredientWrapper wrapper = new IngredientWrapper();
		wrapper.setRecipe(RecipeDTO.builder()
				.id(recipe.getId())
				.name(recipe.getName())
				.description(recipe.getDescription())
				.build());
		wrapper.setIngredient(patch);
		try {
			ingredientService.putIngredient(wrapper);
		} catch (HttpClientErrorException ex) {
			int statusCode = ex.getStatusCode().value();
			String body = ex.getResponseBodyAsString();			
			return "redirect:/error?statusCode=" + statusCode + "&body=" + body;
		}
		
		return "redirect:/ingredients";
	}
	
	@GetMapping("/{id}/delete")
	public String deleteIngredient(@PathVariable Long id) {
		try {
			ingredientService.deleteIngredient(id);
		} catch (HttpClientErrorException ex) {
			int statusCode = ex.getStatusCode().value();
			String body = ex.getResponseBodyAsString();			
			return "redirect:/error?statusCode=" + statusCode + "&body=" + body;
		}
		return "redirect:/ingredients";
	}
	
	@GetMapping("/{id}/delete-all")
	public String deleteIngredients(@PathVariable Long id, @ModelAttribute Recipe recipe) {
		// Здесь получаем из модели атрибут "recipe" 
		// путем внедрения его как параметр метода
		try {
			ingredientService.deleteIngredients(recipe);
		} catch (HttpClientErrorException ex) {
			int statusCode = ex.getStatusCode().value();
			String body = ex.getResponseBodyAsString();			
			return "redirect:/error?statusCode=" + statusCode + "&body=" + body;
		}
		return "redirect:/ingredients";
	}
	
}
