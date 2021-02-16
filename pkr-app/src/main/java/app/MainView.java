package app;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;

import app.game.Game;
import app.table.TableView;

@Route
@Push
@PageTitle("Pkr")
@PWA(name = "Poker app", shortName = "pkr-app", description = "Poker app.", enableInstallPrompt = false)
@CssImport("./styles/shared-styles.css")
@CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")
public class MainView extends HorizontalLayout {

	public MainView(@Autowired Game game) {
		setClassName("bg");
		add(new TableView(game));

	}
}