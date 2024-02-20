package recipes.client.services;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
import recipes.client.dtos.IngredientWrapper;
import recipes.client.dtos.Recipe;
import recipes.client.dtos.RecipeDTO;
import recipes.client.dtos.RecipeWrapper;

//@Slf4j
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
	
	public RecipeWrapper getAllIngredientsOfOneRecipe(
			Recipe recipe) throws HttpClientErrorException {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("Authorization", sessionService.getAuthHeader());
		httpHeaders.add("Accept", "application/json");
		
		HttpEntity<String> requestEntity = new HttpEntity<>(httpHeaders);
		
		ResponseEntity<RecipeWrapper> response = 
				this.restTemplate.exchange(
						this.urlForGetIngredients, HttpMethod.GET, 
						requestEntity, RecipeWrapper.class, recipe.getId());
		
		RecipeWrapper requested = null;
		if (response.getStatusCode().is2xxSuccessful()) {
			requested = response.getBody();
		}
		return requested;
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
		
		IngredientWrapper requested = null;
		if (response.getStatusCode().is2xxSuccessful()) {
			requested = response.getBody();
		}
		return requested;
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
		
		IngredientWrapper created = null;
		if (response.getStatusCode().is2xxSuccessful()) {
			created = response.getBody();
		}
		return created;
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
		
		IngredientWrapper updated = null;
		if (response.getStatusCode().is2xxSuccessful()) {
			updated = response.getBody();
		}
		return updated;
	}

	public void deleteIngredient(Long id) throws HttpClientErrorException {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("Authorization", sessionService.getAuthHeader());
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		
		HttpEntity<Void> requestEntity = new HttpEntity<>(httpHeaders);
		
		restTemplate.exchange(urlByIngredientId, HttpMethod.DELETE, requestEntity, Void.class, id);
	}

	public void deleteIngredients(Recipe recipe) throws HttpClientErrorException {
		RecipeDTO dto = RecipeDTO.builder()
				.id(recipe.getId())
				.name(recipe.getName())
				.description(recipe.getDescription())
				.build();
		
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("Authorization", sessionService.getAuthHeader());
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		
		HttpEntity<RecipeDTO> requestEntity = new HttpEntity<>(dto, httpHeaders);
		
		restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, Void.class);
	}
}
