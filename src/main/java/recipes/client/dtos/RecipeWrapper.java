package recipes.client.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecipeWrapper {
	
	private RecipeDTO recipe;
	private List<IngredientDTO> ingredients;
	
	public Recipe createRecipe() {
		return Recipe.builder()
				.id(recipe.getId())
				.name(recipe.getName())
				.description(recipe.getDescription())
				
				// Поддержка изображений
				.image(recipe.getImage())
				// Поддержка изображений
				.imageUrl(recipe.getImageUrl())
				
				.ingredients(ingredients)
				.build();
	}
	
}
