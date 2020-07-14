package gui;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;

@Route
@Push
@PageTitle("Poker")
@PWA(name = "Poker app", shortName = "pkr-app", description = "Poker app.", enableInstallPrompt = true)
@CssImport("./styles/shared-styles.css")
@CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")
public class MainView extends HorizontalLayout {

	private final ChatView chatView;
	private final LoginView loginView;
	private final TableView tableView;
	private String name;

	public MainView() {
		setSizeFull();
		addClassName("centered");
		loginView = new LoginView(this);
		chatView = new ChatView(this);
		tableView = new TableView(this);

		add(new HorizontalLayout(new VerticalLayout(loginView, chatView), tableView));

		chatView.setVisible(false);
		tableView.setVisible(false);

	}

	public String getName() {
		return name;
	}

	public void login(String name, String pass) {
		this.name = name;
		loginView.login(true);

		chatView.setVisible(chatView.register(name, pass));

		tableView.setVisible(tableView.join(name, pass));

	}

	public void logout() {
		loginView.login(false);

		chatView.deregister();
		chatView.setVisible(false);

		tableView.setVisible(false);

	}
}
