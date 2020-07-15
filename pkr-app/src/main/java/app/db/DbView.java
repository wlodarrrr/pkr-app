package app.db;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route
public class DbView extends VerticalLayout {

	public DbView(@Autowired Database db) {
		Grid<User> grid = new Grid<>();
		grid.setColumns("name", "pass", "cash", "buyin");
		grid.setItems(db.findAll());
		add(grid);
	}
}
