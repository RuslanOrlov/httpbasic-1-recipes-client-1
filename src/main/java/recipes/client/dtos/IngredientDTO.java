package recipes.client.dtos;

import jakarta.validation.constraints.NotBlank;
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
	
	@NotBlank(message = "Название ингредиента не может быть пустым!")
	private String name;
	
}
