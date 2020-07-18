package app.db;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route
@PageTitle("Pkr - database")
public class UsersView extends VerticalLayout {

	private TextField tfUsername;
	private Grid<User> grid;
	private Database db;
	private DebtRepository debtRepository;
	private UserRepository userRepository;

	public UsersView(@Autowired Database db, @Autowired DebtRepository debtRepository,
			@Autowired UserRepository userRepository) {
		this.db = db;
		this.debtRepository = debtRepository;
		this.userRepository = userRepository;
		grid = new Grid<>(User.class);
		grid.addClassName("table");
		grid.setColumns("name", "cash", "buyin");
		grid.setSelectionMode(SelectionMode.MULTI);
		grid.setItems(db.findAll());
		add(grid);

		tfUsername = new TextField("Username");
		Button bNewUser = new Button("Create user", new Icon(VaadinIcon.PLUS), e -> {
			db.add(tfUsername.getValue());
			grid.setItems(db.findAll());
		});
		bNewUser.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

		Button bResetPass = new Button("Reset pass", new Icon(VaadinIcon.REFRESH), e -> reset());
		bResetPass.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

		Button bDelete = new Button("Remove user", new Icon(VaadinIcon.MINUS), e -> remove());
		bDelete.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

		Button bOrganize = new Button("Organize", new Icon(VaadinIcon.SCALE), e -> organize());
		bOrganize.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

		HorizontalLayout hl = new HorizontalLayout(tfUsername, bNewUser, bResetPass, bDelete, bOrganize);
		hl.setSizeFull();
		hl.setAlignItems(Alignment.END);
		add(hl);
	}

	private void reset() {
		Set<User> selection = grid.getSelectedItems();
		for (User user : selection) {
			boolean success = db.resetPass(user.getName());
			if (success) {
				Notification.show("Password of " + user.getName() + " has been reset.");
			}
		}
	}

	private void remove() {
		Set<User> selection = grid.getSelectedItems();
		for (User user : selection) {
			boolean success = db.remove(user.getName());
			if (success) {
				grid.setItems(db.findAll());
				Notification.show(user.getName() + " has been deleted.");
			}
		}
	}

	private void organize() {
		Set<User> selection = grid.getSelectedItems();
		List<User> sel = new ArrayList<User>();
		sel.addAll(selection);
		while (sel.size() > 1) {
			User minUser = null;
			User maxUser = null;
			double min = 0;
			double max = 0;
			for (User u : sel) {
				min = Math.min(min, u.getCash());
				if (min == u.getCash()) {
					minUser = u;
				}
				max = Math.max(max, u.getCash());
				if (max == u.getCash()) {
					maxUser = u;
				}
			}
			double debt = Math.min(max, -min);
			Debt d = new Debt(maxUser.getName(), minUser.getName(), debt, LocalDate.now());
			minUser.setCash(minUser.getCash() + debt);
			maxUser.setCash(maxUser.getCash() - debt);
			if (minUser.getCash() == 0) {
				sel.remove(minUser);
			}
			if (maxUser.getCash() == 0) {
				sel.remove(maxUser);
			}
			debtRepository.save(d);
			userRepository.save(minUser);
			userRepository.save(maxUser);
			Notification.show(minUser.getName() + " -> " + maxUser.getName() + " : " + debt + ".");
		}
		grid.setItems(db.findAll());
	}
}
