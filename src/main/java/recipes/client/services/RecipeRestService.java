package recipes.client.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import recipes.client.dto.Recipe;

@Service
@RequiredArgsConstructor
public class RecipeRestService {
	
	private final SessionService sessionService;
	
	private RestTemplate restTemplate 	= new RestTemplate();
	
	private String url 					= "http://localhost:8080/api/v1/recipes";
	private String urlWithSort 			= "http://localhost:8080/api/v1/recipes?sort={field}";
	private String urlWithSortAndPage 	= "http://localhost:8080/api/v1/recipes?sort={field}"
													+ "&page={page}&size={size}";
	private String urlById 				= "http://localhost:8080/api/v1/recipes/{id}";
	private String urlCount 			= "http://localhost:8080/api/v1/recipes/count";
	private String urlCountByQuery 		= "http://localhost:8080/api/v1/recipes/count?value={value}";
	private String urlQuery 			= "http://localhost:8080/api/v1/recipes?value={value}";
	private String urlPagingQuery 		= "http://localhost:8080/api/v1/recipes?value={value}"
													+ "&offset={offset}&limit={limit}";
	
	public List<Recipe> getAllRecipes() throws HttpClientErrorException {
		List<Recipe> recipes = new ArrayList<>();
		
		HttpHeaders httpHeaders = new HttpHeaders();
		
		httpHeaders.add("Authorization", sessionService.getAuthHeader());
		httpHeaders.add("Accept", "application/json");
		
		HttpEntity<String> requestEntity = new HttpEntity<>(httpHeaders);
		
		ResponseEntity<Recipe[]> response = 
				this.restTemplate.exchange(
						this.urlWithSort, HttpMethod.GET, 
						requestEntity, Recipe[].class, "id");
		
		if (response.getStatusCode().is2xxSuccessful()) {
			recipes = Arrays.asList(response.getBody());
		}
		
		return recipes;
	}
		
	public List<Recipe> getAllRecipes(
			Integer curPage, Integer pageSize
			) throws HttpClientErrorException {
		List<Recipe> recipes = new ArrayList<>();
		
		HttpHeaders httpHeaders = new HttpHeaders();
		
		httpHeaders.add("Authorization", sessionService.getAuthHeader());
		
		HttpEntity<String> requestEntity = new HttpEntity<>(httpHeaders);
		
		ResponseEntity<Recipe[]> response = 
				this.restTemplate.exchange(
						this.urlWithSortAndPage, HttpMethod.GET, 
						requestEntity, Recipe[].class, "id", curPage, pageSize);
		
		if (response.getStatusCode().is2xxSuccessful()) {
			recipes = Arrays.asList(response.getBody());
		}
		
		return recipes;
	}
	
	public List<Recipe> getAllRecipes(String value) throws HttpClientErrorException {
		List<Recipe> recipes = new ArrayList<>();
		
		HttpHeaders httpHeaders = new HttpHeaders();
		
		httpHeaders.add("Authorization", sessionService.getAuthHeader());
		
		HttpEntity<String> requestEntity = new HttpEntity<>(httpHeaders);
		
		ResponseEntity<Recipe[]> response = 
				this.restTemplate.exchange(
						this.urlQuery, HttpMethod.GET, 
						requestEntity, Recipe[].class, value);
		
		if (response.getStatusCode().is2xxSuccessful()) {
			recipes = Arrays.asList(response.getBody());
		}
		
		return recipes;
	}
	
	public List<Recipe> getAllRecipes(
			Integer curPage, Integer pageSize, String value
			) throws HttpClientErrorException {
		List<Recipe> recipes = new ArrayList<>();
		
		HttpHeaders httpHeaders = new HttpHeaders();
		
		httpHeaders.add("Authorization", sessionService.getAuthHeader());
		
		HttpEntity<String> requestEntity = new HttpEntity<>(httpHeaders);
		
		ResponseEntity<Recipe[]> response = 
				this.restTemplate.exchange(
						this.urlPagingQuery, HttpMethod.GET, 
						requestEntity, Recipe[].class, value, curPage*pageSize, pageSize);
		
		if (response.getStatusCode().is2xxSuccessful()) {
			recipes = Arrays.asList(response.getBody());
		}
		
		return recipes;
	}
		
	public Integer countAll(Boolean isFiltering, String value) throws HttpClientErrorException {
		ResponseEntity<Integer> response = 
				ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		
		HttpHeaders httpHeaders = new HttpHeaders();
		
		httpHeaders.add("Authorization", sessionService.getAuthHeader());
		
		HttpEntity<String> requestEntity = new HttpEntity<>(httpHeaders);
		
		if (isFiltering)
			response = this.restTemplate.exchange(
									this.urlCountByQuery, HttpMethod.GET, 
										requestEntity, Integer.class, value);
		else
			response = this.restTemplate.exchange(
									this.urlCount, HttpMethod.GET, 
										requestEntity, Integer.class);
		
		if (response.getStatusCode().is2xxSuccessful())
			return response.getBody();
		return 0;
	}
	
	public Recipe getRecipeById(Long id) throws HttpClientErrorException {
		HttpHeaders httpHeaders = new HttpHeaders();
		
		httpHeaders.add("Authorization", sessionService.getAuthHeader());
		
		HttpEntity<String> requestEntity = new HttpEntity<>(httpHeaders);
		
		ResponseEntity<Recipe> response = 
				this.restTemplate.exchange(this.urlById, HttpMethod.GET, 
										requestEntity, Recipe.class, id);
		
		if (response.getStatusCode().is2xxSuccessful()) {
			return response.getBody();
		}
		return null;
	}
	
	public Recipe postRecipe(Recipe recipe) throws HttpClientErrorException {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		
		httpHeaders.add("Authorization", sessionService.getAuthHeader());
				
		HttpEntity<Recipe> requestEntity = new HttpEntity<Recipe>(recipe, httpHeaders);
		
		ResponseEntity<Recipe> response = 
				this.restTemplate.postForEntity(this.url, requestEntity, 
												Recipe.class);
		
		if (response.getStatusCode().is2xxSuccessful()) {
			return response.getBody();
		}
		return null;
	}
	
	public Recipe putRecipe(Recipe patch, Long id) throws HttpClientErrorException {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		
		httpHeaders.add("Authorization", sessionService.getAuthHeader());
		
		HttpEntity<Recipe> requestEntity = 
				new HttpEntity<Recipe>(patch, httpHeaders);
		
		ResponseEntity<Recipe> response = 
				this.restTemplate.exchange(this.urlById, HttpMethod.PUT, 
						requestEntity, Recipe.class, id);
		
		if (response.getStatusCode().is2xxSuccessful()) {
			return response.getBody();
		}
		return null;
	}

	public void deleteRecipe(Long id) throws HttpClientErrorException {
		HttpHeaders httpHeaders = new HttpHeaders();
		
		httpHeaders.add("Authorization", sessionService.getAuthHeader());
				
		HttpEntity<Object> requestEntity = new HttpEntity<>(httpHeaders);
		
		this.restTemplate.exchange(this.urlById, HttpMethod.DELETE, 
										requestEntity, Recipe.class, id);
	}
	
}
