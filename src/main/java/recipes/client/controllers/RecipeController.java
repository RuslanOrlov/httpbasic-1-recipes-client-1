package recipes.client.controllers;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;

import com.lowagie.text.DocumentException;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

//import com.itextpdf.text.DocumentException;

import lombok.RequiredArgsConstructor;
import recipes.client.dtos.OnlyBasicPropertiesChecks;
import lombok.extern.slf4j.Slf4j;
import recipes.client.dtos.Recipe;
import recipes.client.props.RecipeProps;
import recipes.client.services.RecipeRestService;
import recipes.client.services.SessionService;
import recipes.client.tools.PDFGenerator;
import recipes.client.tools.ReportType;

@Slf4j
@Controller
@RequestMapping("/recipes")
@SessionAttributes("recipe")
@RequiredArgsConstructor
public class RecipeController {
	
	private final RecipeRestService recipeService;
	private final RecipeProps recipeProps;
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
	 * Ниже представлены методы управления постраничным просмотром данных
	 * 
	 * */
	
	@GetMapping("/paging")
	public String swithPaging() {
		if (recipeProps.getIsPaging())
			recipeProps.setIsPaging(false);
		else 
			recipeProps.setIsPaging(true);
		return "redirect:/recipes";
	}
	
	@GetMapping("/first")
	public String firstPage() {
		recipeProps.setCurPage(0);
		return "redirect:/recipes";
	}
	
	@GetMapping("/prev")
	public String prevPage() {
		if (recipeProps.getCurPage() > 0) {
			recipeProps.setCurPage(recipeProps.getCurPage() - 1);
		}
		return "redirect:/recipes";
	}
	
	@GetMapping("/next")
	public String nextPage() {
		Integer countRecipes = 0;
		try {
			countRecipes = recipeService.countAll(
									recipeProps.getIsFiltering(), 
									recipeProps.getFilteringValue());
		} catch (HttpClientErrorException ex) {
			int statusCode = ex.getStatusCode().value();
			String body = ex.getResponseBodyAsString();			
			return "redirect:/error?statusCode=" + statusCode + "&body=" + body;
		}
		
		if (recipeProps.getCurPage() < recipeProps.getTotalPages(countRecipes))
			recipeProps.setCurPage(recipeProps.getCurPage() + 1);
		
		return "redirect:/recipes";
	}

	@GetMapping("/last")
	public String lastPage() {
		Integer countRecipes = 0;
		try {
			countRecipes = recipeService.countAll(
									recipeProps.getIsFiltering(), 
									recipeProps.getFilteringValue());
		} catch (HttpClientErrorException ex) {
			int statusCode = ex.getStatusCode().value();
			String body = ex.getResponseBodyAsString();			
			return "redirect:/error?statusCode=" + statusCode + "&body=" + body;
		}
		
		recipeProps.setCurPage(recipeProps.getTotalPages(countRecipes));
		
		return "redirect:/recipes";
	}
	
	@PostMapping("/change-page-size")
	public String changePageSize(@ModelAttribute("props") RecipeProps props) {
		if (props.getPageSize() <= 0)
			props.setPageSize(1);
		recipeProps.setPageSize(props.getPageSize());
		return "redirect:/recipes";
	}
	
	/*
	 * Ниже представлены методы управления фильтром просмотра данных
	 * 
	 * */
	
	@GetMapping("/filter")
	public String switchFilter() {
		recipeProps.setIsFiltering(!recipeProps.getIsFiltering());
		
		Integer countRecipes = 0;
		try {
			countRecipes = recipeService.countAll(
									recipeProps.getIsFiltering(), 
									recipeProps.getFilteringValue());
		} catch (HttpClientErrorException ex) {
			int statusCode = ex.getStatusCode().value();
			String body = ex.getResponseBodyAsString();			
			return "redirect:/error?statusCode=" + statusCode + "&body=" + body;
		}
			
		Integer totalPages = recipeProps.getTotalPages(countRecipes);
		if (recipeProps.getCurPage() > totalPages) 
			recipeProps.setCurPage(totalPages);
		
		return "redirect:/recipes";
	}
	
	@GetMapping(value = "/query", params = "value")
	public String runFilteringQuery(@RequestParam("value") String value, Model model) {
		recipeProps.setFilteringValueUI(value);
		recipeProps.setFilteringValue("%" + value + "%");
		recipeProps.setCurPage(0);
		return "redirect:/recipes";
	}
	
	/*
	 * Ниже представлен метод экспорта данных о заметках во внешний PDF файл
	 * 
	 * */
	
	@GetMapping("/export-to-pdf") 
	public void exportToPDF(HttpServletResponse response) throws IOException, DocumentException {
		response.setContentType("application/pdf");
		 
		String headerName = "Content-Disposition"; 
		String headerValue = "attachment; filename=pdf_" + LocalDateTime.now().toString() + ".pdf";
		response.setHeader(headerName, headerValue);
		
		PDFGenerator generator = new PDFGenerator();
		generator.generate(response, formRecipesList(), ReportType.LIST_TYPE); 
	}
	
	/*
	 * Ниже представлены методы работы с данными (CRUD) посредством REST сервиса
	 * 
	 * */
	
	@GetMapping
	public String getAllRecipes(Model model, SessionStatus sessionStatus) {
		sessionStatus.setComplete();
		
		model.addAttribute("props", recipeProps);
		try {
			model.addAttribute("recipes", formRecipesList());
		} catch (HttpClientErrorException ex) {
			int statusCode = ex.getStatusCode().value();
			String body = ex.getResponseBodyAsString();			
			return "redirect:/error?statusCode=" + statusCode + "&body=" + body;
		}
		return "recipes-list";
	}
	
	private List<Recipe> formRecipesList() throws HttpClientErrorException {
		List<Recipe> recipes = new ArrayList<>();
		
		if (!recipeProps.getIsPaging() && !recipeProps.getIsFiltering()) {
			recipes = recipeService.getAllRecipes();
		}
		else if (recipeProps.getIsPaging() && !recipeProps.getIsFiltering()) {
			recipes = recipeService.getAllRecipes(
						recipeProps.getCurPage(), recipeProps.getPageSize());
		}
		else if (!recipeProps.getIsPaging() && recipeProps.getIsFiltering()) {
			recipes = recipeService.getAllRecipes(
						recipeProps.getFilteringValue());
		}
		else if (recipeProps.getIsPaging() && recipeProps.getIsFiltering()) {
			recipes = recipeService.getAllRecipes(
						recipeProps.getCurPage(), recipeProps.getPageSize(), 
						recipeProps.getFilteringValue());
		}
		return recipes;
	}
	
	@GetMapping("/{id}")
	public String getRecipeById(@PathVariable Long id, Model model) {
		Recipe recipe = null;
		try {
			recipe = recipeService.getRecipeById(id);
		} catch (HttpClientErrorException ex) {
			int statusCode = ex.getStatusCode().value();
			String body = ex.getResponseBodyAsString();			
			return "redirect:/error?statusCode=" + statusCode + "&body=" + body;
		}
		
		model.addAttribute("recipe", recipe);
		return "recipe-card";
	}
	
	@GetMapping("/new")
	public String openCreateRecipeForm(Recipe recipe) {
		return "recipe-create";
	}
	
	@PostMapping
	public String postRecipe(
			@Valid Recipe recipe, 
			BindingResult errors, 
			@RequestParam(required = false) MultipartFile recipeImage 	/* Поддержка изображений */
			) throws IOException {
		
		if (errors.hasErrors()) { 
			log.info("Ошибки валидации:");
		    for (FieldError error : errors.getFieldErrors()) {
		        log.info(error.getField() + ": " + error.getDefaultMessage());
		    }
			return "recipe-create"; 
		}
		
		/*recipe.setImage(recipeImage.getBytes());*/					/* Поддержка изображений */
		
		try {
			recipeService.postRecipe(recipe, recipeImage.getBytes()); 	/* Поддержка изображений */
		} catch (HttpClientErrorException ex) {
			int statusCode = ex.getStatusCode().value();
			String body = ex.getResponseBodyAsString();			
			return "redirect:/error?statusCode=" + statusCode + "&body=" + body;
		}
		
		return "redirect:/recipes";
	}
	
	@GetMapping("/{id}/edit")
	public String openEditRecipeForm(@PathVariable Long id, Model model) {
		Recipe recipe = null;
		try {
			recipe = recipeService.getRecipeById(id);
		} catch (HttpClientErrorException ex) {
			int statusCode = ex.getStatusCode().value();
			String body = ex.getResponseBodyAsString();			
			return "redirect:/error?statusCode=" + statusCode + "&body=" + body;
		}
		
		model.addAttribute("oldName", recipe.getName());
		model.addAttribute("oldDescription", recipe.getDescription());
		model.addAttribute("recipe", new Recipe(recipe.getId(), null, null, recipe.getImageUrl()));
		
		return "recipe-edit";
	}
	
	@PutMapping
	public String putRecipe(
			@Validated(value = OnlyBasicPropertiesChecks.class) Recipe recipe, 
			BindingResult errors,
			@RequestParam(required = false) MultipartFile recipeImage 	/* Поддержка изображений */
			) throws IOException {
		
		if (errors.hasErrors()) 
			return "recipe-edit";
		
		Recipe patch = new Recipe(null, null, null);
		
		if (recipe.getName() != null && recipe.getName().trim().length() > 0)
			patch.setName(recipe.getName().trim());
		if (recipe.getDescription() != null && recipe.getDescription().trim().length() > 0)
			patch.setDescription(recipe.getDescription().trim());
		
		try {
			recipeService.putRecipe(patch, recipe.getId(), 				/* Поддержка изображений */
									recipeImage.getBytes().length != 0? recipeImage.getBytes() : null);
		} catch (HttpClientErrorException ex) {
			int statusCode = ex.getStatusCode().value();
			String body = ex.getResponseBodyAsString();			
			return "redirect:/error?statusCode=" + statusCode + "&body=" + body;
		}
		
		return "redirect:/recipes";
	}
	
	@GetMapping("/{id}/ingredients")
	public String showIngredients(@PathVariable Long id, Model model) {
		Recipe recipe = null;
		try {
			recipe = recipeService.getRecipeById(id);
		} catch (HttpClientErrorException ex) {
			int statusCode = ex.getStatusCode().value();
			String body = ex.getResponseBodyAsString();			
			return "redirect:/error?statusCode=" + statusCode + "&body=" + body;
		}
		
		model.addAttribute("recipe", recipe);
		
		return "redirect:/ingredients";
	}
	
	@GetMapping("/{id}/delete")
	public String deleteRecipe(@PathVariable Long id) {
		try {
			recipeService.deleteRecipe(id);
		} catch (HttpClientErrorException ex) {
			int statusCode = ex.getStatusCode().value();
			String body = ex.getResponseBodyAsString();
			return "redirect:/error?statusCode=" + statusCode + "&body=" + body;
		}
		return "redirect:/recipes";
	}
	
	/* Поддержка изображений */
	@GetMapping("/{id}/image-delete")
	public String deleteRecipeImage(@PathVariable Long id) {
		Recipe recipe = null;
		try {
			recipe = recipeService.getRecipeById(id);
			recipeService.deleteRecipeImage(recipe.getImageUrl());
		} catch (HttpClientErrorException ex) {
			int statusCode = ex.getStatusCode().value();
			String body = ex.getResponseBodyAsString();
			return "redirect:/error?statusCode=" + statusCode + "&body=" + body;
		}
		return "redirect:/recipes";
	}
	
}
