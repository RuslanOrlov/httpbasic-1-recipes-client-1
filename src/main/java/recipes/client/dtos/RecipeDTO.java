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
	private byte[] image;		/* Поддержка изображений */
	private String imageUrl;	/* Поддержка изображений */

	// При отправке DTO объекта с клиента на сервер: 
	// 		- поле image может содержать изображение 
	// 		- поле imageUrl всегда содержит null
	// При поступлении DTO объекта с сервера на клиент: 
	// 		- поле image всегда содержит значение null 
	// 		- поле imageUrl всегда содержит значение URL адреса 
	
}
