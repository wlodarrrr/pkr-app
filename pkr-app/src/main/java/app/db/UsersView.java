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
	private DebtRepository debtRepository;
	private UserService userService;

	public UsersView(@Autowired DebtRepository debtRepository, @Autowired UserService userService) {
		this.userService = userService;
		this.debtRepository = debtRepository;
		grid = new Grid<>(User.class);
		grid.addClassName("table");
		grid.setColumns("name", "cash", "buyin");
		grid.setSelectionMode(SelectionMode.MULTI);
		grid.setItems(userService.findAll());
		add(grid);

		tfUsername = new TextField("Username");
		Button bNewUser = new Button("Create user", new Icon(VaadinIcon.PLUS), e -> {

			grid.setItems(userService.findAll());
		});
		bNewUser.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

		Button bDelete = new Button("Remove user", new Icon(VaadinIcon.MINUS), e -> delteUser());
		bDelete.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

		Button bOrganize = new Button("Organize", new Icon(VaadinIcon.SCALE), e -> organizeDebts());
		bOrganize.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

		HorizontalLayout hl = new HorizontalLayout(tfUsername, bNewUser, bDelete, bOrganize);
		hl.setSizeFull();
		hl.setAlignItems(Alignment.END);
		add(hl);
	}

	private void delteUser() {
		Set<User> selection = grid.getSelectedItems();
		for (User user : selection) {
			userService.delete(user);
			grid.setItems(userService.findAll());
			Notification.show(user.getName() + " has been deleted.");
		}
	}

	private void organizeDebts() {

		List<Debt> debts = userService.organize(grid.getSelectedItems());
		for (Debt debt : debts) {
			Notification.show(debt.getDebtor() + " -> " + debt.getCreditor() + " : " + debt.getAmount() + ".");
		}
		debtRepository.saveAll(debts);
		grid.setItems(userService.findAll());
	}
}
