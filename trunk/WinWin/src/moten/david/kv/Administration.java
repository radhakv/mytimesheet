package moten.david.kv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class Administration {

	private final Set<String> administratorEmails;
	private final UserService userService;

	public Administration() {
		administratorEmails = new HashSet<String>();
		BufferedReader br = new BufferedReader(new InputStreamReader(getClass()
				.getResourceAsStream("/administrators.txt")));
		String line;
		try {
			while ((line = br.readLine()) != null)
				administratorEmails.add(line.trim());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		userService = UserServiceFactory.getUserService();

	}

	public Set<String> getAdministratorEmails() {
		return administratorEmails;
	}

	public boolean currentUserIsAdministrator() {
		if (userService.getCurrentUser() == null)
			return false;
		else
			return administratorEmails.contains(userService.getCurrentUser()
					.getEmail());
	}

	public String getCurrentUser() {
		if (userService.getCurrentUser() == null)
			return null;
		else
			return userService.getCurrentUser().getEmail();
	}
}
