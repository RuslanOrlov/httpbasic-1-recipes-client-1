package recipes.client.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import recipes.client.dtos.IngredientDTO;
import recipes.client.dtos.IngredientWrapper;
import recipes.client.dtos.Recipe;
import recipes.client.dtos.RecipeWrapper;

@Service
@RequiredArgsConstructor
public class IngredientRestService {
	
private final SessionService sessionService;
	
	private RestTemplate restTemplate 	= new RestTemplate();
	
	private String url 						= "http://localhost:8080/api/v1/ingredients";
	private String urlForGetIngredients 	= "http://localhost:8080/api/v1/ingredients"
												+ "?recipeId={id}";
	
//	private String urlWithSort 				= "http://localhost:8080/api/v1/ingredients"
//												+ "?recipeId={id}&sort={field}";
//	private String urlWithSortAndPage 		= "http://localhost:8080/api/v1/ingredients"
//												+ "?recipeId={id}&sort={field}&page={page}&size={size}";
	
	private String urlForGetIngredient		= "http://localhost:8080/api/v1/ingredients"
												+ "?recipeId={recipeId}"
												+ "&ingredientId={ingredientId}";
	private String urlByIngredientId		= "http://localhost:8080/api/v1/ingredients/{id}";
	
//	private String urlCount 			= "http://localhost:8080/api/v1/ingredients/count";
//	private String urlCountByQuery 		= "http://localhost:8080/api/v1/ingredients/count?value={value}";
//	private String urlQuery 			= "http://localhost:8080/api/v1/ingredients?value={value}";
//	private String urlPagingQuery 		= "http://localhost:8080/api/v1/ingredients?value={value}"
//											+ "&offset={offset}&limit={limit}";
	
	public List<IngredientDTO> getAllIngredientsOfOneRecipe(
			Recipe recipe) throws HttpClientErrorException {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("Authorization", sessionService.getAuthHeader());
		httpHeaders.add("Accept", "application/json");
		
		HttpEntity<String> requestEntity = new HttpEntity<>(httpHeaders);
		
		ResponseEntity<RecipeWrapper> response = 
				this.restTemplate.exchange(
						this.urlForGetIngredients, HttpMethod.GET, 
						requestEntity, RecipeWrapper.class, recipe.getId());
		
		List<IngredientDTO> ingredients = new ArrayList<>();
		if (response.getStatusCode().is2xxSuccessful()) {
			if (response.getBody().getIngredients() != null)
				ingredients = response.getBody().getIngredients();
		}
		
		return ingredients;
	}

	public IngredientWrapper getOneIngredientOfOneRecipe(
			Recipe recipe, Long ingredientId) throws HttpClientErrorException {
		Long recipeId = recipe.getId();
		
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("Authorization", sessionService.getAuthHeader());
		httpHeaders.add("Accept", "application/json");
		
		HttpEntity<String> requestEntity = new HttpEntity<>(httpHeaders);
		
		ResponseEntity<IngredientWrapper> response = 
				this.restTemplate.exchange(
						this.urlForGetIngredient, HttpMethod.GET, 
						requestEntity, IngredientWrapper.class, recipeId, ingredientId);
		
		return response.getBody();
	}

	public IngredientWrapper postIngredient(
			IngredientWrapper wrapper) throws HttpClientErrorException {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("Authorization", sessionService.getAuthHeader());
		httpHeaders.add("Accept", "application/json");
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		
		HttpEntity<IngredientWrapper> requestEntity = new HttpEntity<>(wrapper, httpHeaders);
		
		ResponseEntity<IngredientWrapper> response = 
				this.restTemplate.exchange(
						this.url, HttpMethod.POST, 
						requestEntity, IngredientWrapper.class);
		
		return response.getBody();
	}

	public IngredientWrapper putIngredient(
			IngredientWrapper wrapper) throws HttpClientErrorException {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("Authorization", sessionService.getAuthHeader());
		httpHeaders.add("Accept", "application/json");
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		
		HttpEntity<IngredientWrapper> requestEntity = new HttpEntity<>(wrapper, httpHeaders);
		
		ResponseEntity<IngredientWrapper> response = 
				this.restTemplate.exchange(
						this.url, HttpMethod.PUT, 
						requestEntity, IngredientWrapper.class);
		
		return response.getBody();
	}

	public void deleteIngredient(Long id) {
		restTemplate.delete(urlByIngredientId, id);
	}
	
}
