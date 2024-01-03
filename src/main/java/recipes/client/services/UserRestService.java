package recipes.client.services;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
import recipes.client.controllers.RegistrationForm;
import recipes.client.dto.AuthRequest;
import recipes.client.dto.RegisterRequest;

//@Slf4j
@Service
@RequiredArgsConstructor
public class UserRestService {
	
	private RestTemplate restTemplate = new RestTemplate();
	private String url = "http://localhost:8080/api/v1/users";
	
	private final HttpSession httpSession;
		
	public void registerUser(RegistrationForm form) throws HttpClientErrorException {
		HttpHeaders headers = new HttpHeaders();
		
		headers.add("Content-Type", "application/json");
		headers.add("Accept", "application/json");
		
		HttpEntity<RegisterRequest> entity = new HttpEntity<>(form.getRegisterRequest(), headers);
		
		restTemplate.exchange(url + "/register", HttpMethod.POST, entity, String.class);
	}

	public void authUser(AuthRequest request) throws HttpClientErrorException {
//		Boolean isChecked = null;
//		// 1 Проверить наличие пользователя, если его нет - выбросить исключение 
//		isChecked = restTemplate.exchange(url + "?existsusername={value}", 
//					HttpMethod.GET, null, Boolean.class, request.getUsername()).getBody();
//		if (!isChecked) {
//			throw new HttpClientErrorException(HttpStatus.NOT_FOUND, 
//					"Database has no user with name '" + request.getUsername() + "'.", 
//					("Database has no user with name '" + request.getUsername() + "'.")
//						.getBytes(), null);
//		}
//		
//		// 2 Проверить правильность пароля, если он неправильный - выбросить исключение 
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json");
//		HttpEntity<AuthRequest> entity = new HttpEntity<>(request, headers);
//		isChecked = restTemplate.exchange(url + "/checkpassword", 
//					HttpMethod.POST, entity, Boolean.class).getBody();
//		if (!isChecked) {
//			throw new HttpClientErrorException(HttpStatus.NOT_FOUND, 
//					"Incorrect password specified for user '" + request.getUsername() + "'.", 
//					("Incorrect password specified for user '" + request.getUsername() + "'.")
//						.getBytes(), null);
//		}
//		
//		// 3 Аутентифицируем пользователя путем получения токена и сохранения его в сессии
//		String token = restTemplate.exchange(url + "/token", HttpMethod.POST, entity, String.class).getBody();
//		httpSession.setAttribute("token", token);
//		httpSession.setAttribute("isAuth", true);
//		httpSession.setAttribute("username", request.getUsername());
		
		// ----------------------------------------------------------------
		
		// Проверить наличие пользователя и правильность его пароля на сервере. Если 
		// пользователя нет или его пароль не правильный, сервер выбросит исключение, 
		// которое будет перехвачено и обработано контроллером ClientErrorController. 
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		HttpEntity<AuthRequest> entity = new HttpEntity<>(request, headers);
		restTemplate.exchange(url + "/checkUsernamePassword", 
				HttpMethod.POST, entity, String.class);
		
		// Аутентифицируем пользователя путем получения токена и сохранения его в сессии. 
		// Данный код выполняется если будут успешно выполнены вышеуказанные проверки. 
		String token = restTemplate.exchange(url + "/token", 
							HttpMethod.POST, entity, String.class).getBody();
		httpSession.setAttribute("token", token);
		httpSession.setAttribute("isAuth", true);
		httpSession.setAttribute("username", request.getUsername());
	}

	public void logout() {
		httpSession.removeAttribute("token");
		httpSession.removeAttribute("isAuth");
		httpSession.removeAttribute("username");
	}

}
