package app;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;

import app.db.Database;
import app.game.Game;
import app.table.TableView;

@Route
@Push
@PageTitle("Pkr")
@PWA(name = "Poker app", shortName = "pkr-app", description = "Poker app.", enableInstallPrompt = false)
@CssImport("./styles/shared-styles.css")
@CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")
public class MainView extends HorizontalLayout {

	public MainView(@Autowired Database db, @Autowired Game game) {
		setClassName("bg");
		LoginOverlay login = new LoginOverlay();
		login.setOpened(true);

		login.setTitle("Poker app");
		login.setDescription(" ");
		login.setForgotPasswordButtonVisible(false);
		login.addLoginListener(e -> {
			boolean auth = db.authenticate(e);
			if (auth) {
				add(new TableView(game, e.getUsername()));
				login.close();
			} else {
				login.setError(true);
			}
		});

	}
}
