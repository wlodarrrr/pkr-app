package db;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Route("admin")
public class DbView extends VerticalLayout {

	@Autowired
	private UserService service;

	private User customer;

	private Grid<User> grid = new Grid<>(User.class);
	private Button save = new Button("Save", e -> saveUser());

	public DbView() {
		updateGrid();
		grid.setColumns("firstName", "lastName");
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