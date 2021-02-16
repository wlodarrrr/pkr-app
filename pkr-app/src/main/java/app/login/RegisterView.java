package app.login;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import app.db.User;
import app.db.UserService;

public class RegisterView extends Dialog {

	private UserService userService;

	public RegisterView(UserService userService) {
		this.userService = userService;

		H2 title = new H2("Register");
		title.getStyle().set("margin-bottom", "0px");
		TextField username = new TextField("Username");
		username.setWidth("300px");
		PasswordField password = new PasswordField("Password");
		password.setWidth("300px");
		PasswordField password2 = new PasswordField("Confirm password");
		password2.setWidth("300px");
		Button save = new Button("Register");
		save.setWidth("300px");
		save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		VerticalLayout vl = new VerticalLayout(title, username, password, password2, save);
		vl.setSizeFull();
		vl.setAlignItems(Alignment.BASELINE);
		add(vl);

		Binder<User> binder = new Binder<>(User.class);
		binder.forField(username).withValidator(s -> s.length() >= 3, "Min. 3 characters")
				.withValidator(s -> s.length() <= 25, "Max. 25 characters").bind("username");

		binder.forField(password).withValidator(s -> s.length() >= 3, "Min. 3 characters")
				.withValidator(s -> s.length() <= 25, "Max. 25 characters").bind("pass");

		binder.forField(password2).withValidator(s -> password.getValue().contentEquals(s), "Passwords does not match")
				.bind("pass");

		save.addClickShortcut(Key.ENTER).listenOn(this, username, password, password2, save);
		save.addClickListener(e -> {
			binder.validate();
			if (binder.isValid()) {
				save(username.getValue(), password.getValue());
			}
		});

	}

	private void save(String username, String password) {
		if (userService.register(username, password)) {
			close();
			Notification.show("Registered. Please log in.", 2222, Position.MIDDLE);
		} else {
			Notification.show("Username already taken", 2222, Position.MIDDLE);
		}
	}

}
