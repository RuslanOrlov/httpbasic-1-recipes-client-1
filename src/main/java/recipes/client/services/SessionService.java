package recipes.client.services;

import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SessionService {
	
	private final HttpSession httpSession;
	
	public Boolean isLoggedIn() {
		if (httpSession.getAttribute("isAuth") == null) {
			return false;
		}
		return (Boolean) httpSession.getAttribute("isAuth");
	}

}
