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
import lombok.extern.slf4j.Slf4j;
import recipes.client.dtos.Recipe;
import recipes.client.dtos.RecipeDTO;
import recipes.client.dtos.RecipeWrapper;

@Slf4j
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
		List<RecipeWrapper> wrappers = new ArrayList<>();
		
		HttpHeaders httpHeaders = new HttpHeaders();
		
		httpHeaders.add("Authorization", sessionService.getAuthHeader());
		httpHeaders.add("Accept", "application/json");
		
		HttpEntity<String> requestEntity = new HttpEntity<>(httpHeaders);
		
		ResponseEntity<RecipeWrapper[]> response = 
				this.restTemplate.exchange(
						this.urlWithSort, HttpMethod.GET, 
						requestEntity, RecipeWrapper[].class, "id");
		
		if (response.getStatusCode().is2xxSuccessful()) {
			wrappers = Arrays.asList(response.getBody());
			for (RecipeWrapper wrapper : wrappers) {
				recipes.add(wrapper.createRecipe());
			}
		}
		
		return recipes;
	}
		
	public List<Recipe> getAllRecipes(
			Integer curPage, Integer pageSize
			) throws HttpClientErrorException {
		List<Recipe> recipes = new ArrayList<>();
		List<RecipeWrapper> wrappers = new ArrayList<>();
		
		HttpHeaders httpHeaders = new HttpHeaders();
		
		httpHeaders.add("Authorization", sessionService.getAuthHeader());
		
		HttpEntity<String> requestEntity = new HttpEntity<>(httpHeaders);
		
		ResponseEntity<RecipeWrapper[]> response = 
				this.restTemplate.exchange(
						this.urlWithSortAndPage, HttpMethod.GET, 
						requestEntity, RecipeWrapper[].class, "id", curPage, pageSize);
		
		if (response.getStatusCode().is2xxSuccessful()) {
			wrappers = Arrays.asList(response.getBody());
			for (RecipeWrapper wrapper : wrappers) {
				recipes.add(wrapper.createRecipe());
			}
		}
		
		return recipes;
	}
	
	public List<Recipe> getAllRecipes(String value) throws HttpClientErrorException {
		List<Recipe> recipes = new ArrayList<>();
		List<RecipeWrapper> wrappers = new ArrayList<>();
		
		HttpHeaders httpHeaders = new HttpHeaders();
		
		httpHeaders.add("Authorization", sessionService.getAuthHeader());
		
		HttpEntity<String> requestEntity = new HttpEntity<>(httpHeaders);
		
		ResponseEntity<RecipeWrapper[]> response = 
				this.restTemplate.exchange(
						this.urlQuery, HttpMethod.GET, 
						requestEntity, RecipeWrapper[].class, value);
		
		if (response.getStatusCode().is2xxSuccessful()) {
			wrappers = Arrays.asList(response.getBody());
			for (RecipeWrapper wrapper : wrappers) {
				recipes.add(wrapper.createRecipe());
			}
		}
		
		return recipes;
	}
	
	public List<Recipe> getAllRecipes(
			Integer curPage, Integer pageSize, String value
			) throws HttpClientErrorException {
		List<Recipe> recipes = new ArrayList<>();
		List<RecipeWrapper> wrappers = new ArrayList<>();
		
		HttpHeaders httpHeaders = new HttpHeaders();
		
		httpHeaders.add("Authorization", sessionService.getAuthHeader());
		
		HttpEntity<String> requestEntity = new HttpEntity<>(httpHeaders);
		
		ResponseEntity<RecipeWrapper[]> response = 
				this.restTemplate.exchange(
						this.urlPagingQuery, HttpMethod.GET, 
						requestEntity, RecipeWrapper[].class, value, curPage*pageSize, pageSize);
		
		if (response.getStatusCode().is2xxSuccessful()) {
			wrappers = Arrays.asList(response.getBody());
			for (RecipeWrapper wrapper : wrappers) {
				recipes.add(wrapper.createRecipe());
			}
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
		
		ResponseEntity<RecipeWrapper> response = 
				this.restTemplate.exchange(this.urlById, HttpMethod.GET, 
										requestEntity, RecipeWrapper.class, id);
		
		if (response.getStatusCode().is2xxSuccessful()) {
			return response.getBody().createRecipe();
		}
		return null;
	}
	
	/* Поддержка изображений */
	public ResponseEntity<byte[]> getRecipeImage(String imageUrl) {

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("Authorization", sessionService.getAuthHeader());
		
		ResponseEntity<byte[]> response = 
				this.restTemplate.exchange(imageUrl, HttpMethod.GET, 
						new HttpEntity<>(httpHeaders), byte[].class);
		
		if (response.getStatusCode().is2xxSuccessful()) {
			return response;
		}
		return null;
	}
	
	public Recipe postRecipe(Recipe recipe, byte[] image) throws HttpClientErrorException {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		
		httpHeaders.add("Authorization", sessionService.getAuthHeader());
				
		HttpEntity<RecipeWrapper> requestEntity = 
				new HttpEntity<RecipeWrapper>(recipe.createRecipeWrapper(image), httpHeaders);
		
		ResponseEntity<RecipeWrapper> response = 
				this.restTemplate.postForEntity(this.url, requestEntity, 
												RecipeWrapper.class);
		
		if (response.getStatusCode().is2xxSuccessful()) {
			return response.getBody().createRecipe();
		}
		return null;
	}
	
	public Recipe putRecipe(Recipe patch, Long id, byte[] image) throws HttpClientErrorException {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		
		httpHeaders.add("Authorization", sessionService.getAuthHeader());
		
		HttpEntity<RecipeDTO> requestEntity = 
				new HttpEntity<RecipeDTO>(patch.createRecipeDTO(image), httpHeaders);
		
		ResponseEntity<RecipeWrapper> response = 
				this.restTemplate.exchange(this.urlById, HttpMethod.PUT, 
						requestEntity, RecipeWrapper.class, id);
		
		if (response.getStatusCode().is2xxSuccessful()) {
			return response.getBody().createRecipe();
		}
		return null;
	}

	public void deleteRecipe(Long id) throws HttpClientErrorException {
		HttpHeaders httpHeaders = new HttpHeaders();
		
		httpHeaders.add("Authorization", sessionService.getAuthHeader());
				
		HttpEntity<Object> requestEntity = new HttpEntity<>(httpHeaders);
		
		this.restTemplate.exchange(this.urlById, HttpMethod.DELETE, 
										requestEntity, Void.class, id);
	}
	
}
