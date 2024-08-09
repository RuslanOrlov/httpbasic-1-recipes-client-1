package recipes.client.dtos;

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
public class IngredientDTO {
	
	private Long id;
	
	@Size(max = 255, message = "Длина наименования не может превышать 255 символов!", 
			groups = {Default.class, OnlyBasicPropertiesChecks.class})
	@NotBlank(message = "Название ингредиента не может быть пустым!")
	private String name;
	
}
