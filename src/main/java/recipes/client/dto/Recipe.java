package recipes.client.dto;

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
	
}
