package recipes.client.controllers;

import org.springframework.util.StringUtils;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import recipes.client.dtos.RegisterRequest;

@Data
public class RegistrationForm {
	
	@NotBlank(message = "Аккаунт не может быть пустым!")
	private String username;
	private String firstname;
	private String lastname;
	
	@NotBlank(message = "Почта не может быть пустой!")
	private String email;
	private String password;
	private String confirm;
	private String roleType;
	
	public boolean isConfirmEqualsPassword() {
		return confirm.equals(password);
	}

	public boolean isBlancUsername() {
		return !StringUtils.hasText(username);
	}

	public boolean isBlancEmail() {
		return !StringUtils.hasText(email);
	}
	
	public RegisterRequest getRegisterRequest() {
		return RegisterRequest.builder()
				.username(username)
				.firstname(firstname)
				.lastname(lastname)
				.email(email)
				.password(password)
				.roleType(roleType)
				.build();
	}
}
