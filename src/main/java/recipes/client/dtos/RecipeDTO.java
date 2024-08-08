package recipes.client.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecipeDTO {
	
	private Long id;
	private String name;
	private String description;
	
	// Поддержка изображений
	private byte[] image;
	// Поддержка изображений
	private String imageUrl;
	
}
