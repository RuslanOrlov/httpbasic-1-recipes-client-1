package recipes.client.services;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import recipes.client.controllers.RegistrationForm;
import recipes.client.dto.AuthRequest;
import recipes.client.dto.RegisterRequest;

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
		
		// - Ниже приводится ПЕРВЫЙ СПОСОБ проверки наличия пользователя в БД на сервере и 
		//   правильности его пароля, а также его аутентификации. В этом способе клиент сам 
		//   формирует проверочные запросы в БД на сервер, сам анализирует их результаты и 
		//   если результат проверки отрицательный сам выбрасывает исключение, которое будет 
		//   обработано контроллером ClientErrorController. В случае успешности в/у проверок 
		//   клиент передает запрос на аутентификацию на сервер. 
		// - Данный способ концептуально является НЕПРАВИЛЬНЫМ, так как клиент занимается 
		//   несвойственными для себя проверками, которые относятся к зоне ответственности сервера. 
		
		/*
		 * 
		Boolean isChecked = null;
		// 1. Проверить наличие пользователя, если его нет - выбросить исключение.
		isChecked = restTemplate.exchange(url + "?existsusername={value}", 
					HttpMethod.GET, null, Boolean.class, request.getUsername()).getBody();
		if (!isChecked) {
			throw new HttpClientErrorException(HttpStatus.NOT_FOUND, 
					"Database has no user with name '" + request.getUsername() + "'.", 
					("Database has no user with name '" + request.getUsername() + "'.")
						.getBytes(), null);
		}
		
		// 2. Проверить правильность пароля, если он неправильный - выбросить исключение.
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		HttpEntity<AuthRequest> entity = new HttpEntity<>(request, headers);
		isChecked = restTemplate.exchange(url + "/checkpassword", 
					HttpMethod.POST, entity, Boolean.class).getBody();
		if (!isChecked) {
			throw new HttpClientErrorException(HttpStatus.NOT_FOUND, 
					"Incorrect password specified for user '" + request.getUsername() + "'.", 
					("Incorrect password specified for user '" + request.getUsername() + "'.")
						.getBytes(), null);
		}
		
		// 3. Аутентифицируем пользователя путем получения токена и сохранения его в сессии. 
		// Данный код выполняется если будут успешно выполнены вышеуказанные проверки. 
		String token = restTemplate.exchange(url + "/token", 
							HttpMethod.POST, entity, String.class).getBody();
		httpSession.setAttribute("token", token);
		httpSession.setAttribute("isAuth", true);
		httpSession.setAttribute("username", request.getUsername());
		 *
		 * */
		
		// - Ниже приводится ВТОРОЙ СПОСОБ проверки наличия пользователя в БД на сервере и 
		//   правильности его пароля, а также его аутентификации. Данный способ является: 
		//   a) НАИБОЛЕЕ ИНКАПСУЛИРОВАННЫМ (так как обьединяет в одно действие проверку пользователя 
		//      и его аутентификацию) и 
		//   b) НАИБОЛЕЕ ПРАВИЛЬНЫМ (так как для клиента становится несущественным сам факт проверки 
		//      пользователя на сервере). 
		// - В данном способе сервер полностью самостоятельно обрабатывает поступивший запрос на 
		//   аутентификацию и в случае его некорректности выбрасывает исключение, которое клиент 
		//   получает и обрабатывает в контроллере ClientErrorController. В случае же успешности 
		//   проверки пользователя сервер возвращает клиенту токен аутентификации пользователя. 
		// - Однако в целях лучшей удобочитаемости кода в данной реализации класса UserRestService 
		//   используется третий способ (который см. ниже). 
		
		/*
		 * 
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		HttpEntity<AuthRequest> entity = new HttpEntity<>(request, headers);
		String token = restTemplate.exchange(url + "/checkUser-getToken", 
							HttpMethod.POST, entity, String.class).getBody();
		httpSession.setAttribute("token", token);
		httpSession.setAttribute("isAuth", true);
		httpSession.setAttribute("username", request.getUsername());
		 *
		 * */
		
		// - Ниже приводится ТРЕТИЙ СПОСОБ проверки наличия пользователя в БД на сервере и 
		//   правильности его пароля, а также его аутентификации. В этом способе клиент дважды 
		//   передает запрос на аутентификацию на сервер. Сначала запрос на аутентификацию 
		//   передается для проверки наличия пользователя в БД и правильности его пароля, а 
		//   затем (если проверка пользователя оказалась успешной) запрос на аутентификацию 
		//   передается на сервер уже для аутентификации. 
		// - Также как во втором способе сервер самостоятельно обрабатывает поступивший запрос на 
		//   аутентифиацию, и в случае если данные о пользователе в запросе некорректны, выбрасывает 
		//   исключение, которое клиент получает и обрабатывает в контроллере ClientErrorController. 
		// - Данный способ ТАКЖЕ является ПРАВИЛЬНЫМ, НО в сравнении со вторым способом - МЕНЕЕ 
		//   ИНКАПСУЛИРОВАННЫМ, так как клиент направляет серверу запрос на аутентификацю дважды 
		//   (СНАЧАЛА для проверки пользователя, ЗАТЕМ для аутентификации). 
		
		
		// 1. Проверить наличие пользователя и правильность его пароля на сервере. Если 
		// пользователя нет в БД или его пароль не правильный, сервер выбросит исключение. 
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		HttpEntity<AuthRequest> entity = new HttpEntity<>(request, headers);
		restTemplate.exchange(url + "/checkUsernamePassword", 
				HttpMethod.POST, entity, String.class);
		
		// 2. Аутентифицируем пользователя путем получения токена и сохранения его в сессии. 
		// Данный код выполняется, если будут успешно выполнены вышеуказанные проверки. 
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
