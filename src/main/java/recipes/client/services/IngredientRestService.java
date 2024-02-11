package recipes.client.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import recipes.client.dtos.IngredientDTO;
import recipes.client.dtos.Recipe;
import recipes.client.dtos.RecipeWrapper;

@Service
@RequiredArgsConstructor
public class IngredientRestService {
	
private final SessionService sessionService;
	
	private RestTemplate restTemplate 	= new RestTemplate();
	
	private String url 					= "http://localhost:8080/api/v1/ingredients"
											+ "?recipeId={id}";
	
//	private String urlWithSort 			= "http://localhost:8080/api/v1/ingredients"
//											+ "?recipeId={id}&sort={field}";
//	private String urlWithSortAndPage 	= "http://localhost:8080/api/v1/ingredients"
//											+ "?recipeId={id}&sort={field}&page={page}&size={size}";
	
	private String urlById				= "http://localhost:8080/api/v1/ingredients/{id}";
	private String urlByIds				= "http://localhost:8080/api/v1/ingredients"
											+ "?recipeId={recipeId}&ingredientId={ingredientId}";
	
//	private String urlCount 			= "http://localhost:8080/api/v1/ingredients/count";
//	private String urlCountByQuery 		= "http://localhost:8080/api/v1/ingredients/count?value={value}";
//	private String urlQuery 			= "http://localhost:8080/api/v1/ingredients?value={value}";
//	private String urlPagingQuery 		= "http://localhost:8080/api/v1/ingredients?value={value}"
//											+ "&offset={offset}&limit={limit}";
	
	public List<IngredientDTO> getAllIngredients(Recipe recipe) {
		List<IngredientDTO> ingredients = new ArrayList<>();
		RecipeWrapper wrapper = new RecipeWrapper();
		
		HttpHeaders httpHeaders = new HttpHeaders();
		
		httpHeaders.add("Authorization", sessionService.getAuthHeader());
		httpHeaders.add("Accept", "application/json");
		
		HttpEntity<String> requestEntity = new HttpEntity<>(httpHeaders);
		
		ResponseEntity<RecipeWrapper> response = 
				this.restTemplate.exchange(
						this.url, HttpMethod.GET, 
						requestEntity, RecipeWrapper.class, recipe.getId());
		
		if (response.getStatusCode().is2xxSuccessful()) {
			wrapper = response.getBody();
			for (IngredientDTO ingredientDTO : wrapper.getIngredients()) {
				ingredients.add(ingredientDTO);
			}
		}
		
		return ingredients;
	}
	
}
