package gui;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import db.User;
import db.UserService;

@Route("admin")
@Push
@PageTitle("Poker")
@CssImport("./styles/shared-styles.css")
@CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")
@Component
public class DbView extends VerticalLayout {

	@Autowired
	private UserService service;

	private User customer;

	private Grid<User> grid = new Grid<>(User.class);
	private Button save = new Button("Save", e -> saveUser());

	public DbView() {
		updateGrid();
		grid.setColumns("name", "pass", "cash", "buyin");
		add(grid, save);
	}

	private void updateGrid() {
		List<User> customers = service.findAll();
		grid.setItems(customers);
	}

	private void saveUser() {
		service.update(customer);
		updateGrid();
	}
}