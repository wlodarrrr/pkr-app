package app.login;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import app.db.UserService;

@Route("login")
@PageTitle("Login")

public class LoginView extends VerticalLayout implements BeforeEnterObserver {

	private LoginForm login = new LoginForm();
	private RegisterView registerView;

	public LoginView(UserService userService) {
		setSizeFull();
		setAlignItems(Alignment.CENTER);
		setJustifyContentMode(JustifyContentMode.CENTER);

		login.setAction("login");
		login.setForgotPasswordButtonVisible(false);

		Button register = new Button("Register");
		register.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
		register.addClickListener(e -> {
			if (registerView == null) {
				registerView = new RegisterView(userService);
			}
			registerView.open();
		});

		add(login, register);
	}

	@Override
	public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
		if (beforeEnterEvent.getLocation().getQueryParameters().getParameters().containsKey("error")) {
			login.setError(true);
		}
	}
}