package recipes.client.dtos;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.groups.Default;
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
	
	@Size(max = 255, message = "Длина наименования не может превышать 255 символов!", 
			groups = {Default.class, OnlyBasicPropertiesChecks.class})
	@NotBlank(message = "Название рецепта не может быть пустым!")
	private String name;
	
	@Size(max = 255, message = "Длина описания не может превышать 255 символов!", 
			groups = {Default.class, OnlyBasicPropertiesChecks.class})
	private String description;
	
	/*private byte[] image;*/ 	/* Поддержка изображений */
	private String imageUrl; 	/* Поддержка изображений */
	
	@Builder.Default
	private List<IngredientDTO> ingredients = new ArrayList<>();
	
	public Recipe(Long id, String name, String description) {
		this.id = id;
		this.name = name;
		this.description = description;
	}
	
	public Recipe(Long id, String name, String description, String imageUrl) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.imageUrl = imageUrl;
	}
	
	public RecipeWrapper createRecipeWrapper(byte[] image) {
		return RecipeWrapper.builder()
				.recipe(createRecipeDTO(image))
				.ingredients(ingredients)
				.build();
	}
	
	public RecipeDTO createRecipeDTO(byte[] image) {
		return RecipeDTO.builder()
				.id(id)
				.name(name)
				.description(description)
				.image(image) /* Поддержка изображений */
				.build();
	}
	
} // это NEWEST тестовый комментарий
