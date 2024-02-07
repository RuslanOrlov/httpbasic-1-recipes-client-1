package recipes.client.dtos;

import java.util.List;

import lombok.Data;

@Data
public class RecipeWrapper {
	
	private RecipeDTO recipe;
	private List<IngredientDTO> ingredients;
	
	public Recipe getRecipe() {
		return Recipe.builder()
				.id(recipe.getId())
				.name(recipe.getName())
				.description(recipe.getDescription())
				.ingredients(ingredients)
				.build();
	}
	
}
