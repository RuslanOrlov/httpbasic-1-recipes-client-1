package recipes.client.dtos;

import lombok.Data;

@Data
public class IngredientWrapper {
	
	private RecipeDTO recipe;
	private IngredientDTO ingredient;
	
}
