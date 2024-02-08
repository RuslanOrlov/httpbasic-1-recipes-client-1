package recipes.client.dtos;

import java.util.List;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Recipe {
	
	private Long id;
	
	@Size(max = 255, message = "Длина наименования не может превышать 255 символов!")
	private String name;
	
	@Size(max = 255, message = "Длина описания не может превышать 255 символов!")
	private String description;
	
	private List<IngredientDTO> ingredients;
	
	public RecipeWrapper getRecipeWrapper() {
		return RecipeWrapper.builder()
				.recipe(RecipeDTO.builder()
						.id(id)
						.name(name)
						.description(description)
						.build())
				.ingredients(ingredients)
				.build();
	}
	
	public RecipeDTO getRecipeDTO() {
		return RecipeDTO.builder()
				.id(id)
				.name(name)
				.description(description)
				.build();
	}
	
}
