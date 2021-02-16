package app.db;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route
@PageTitle("Pkr - debts")
public class DebtsView extends VerticalLayout {

	private DebtService debtService;
	private Grid<Debt> grid;
	private ComboBox<User> cbCreditor;
	private ComboBox<User> cbDebtor;
	private NumberField nfDebt;
	private DatePicker dpDate;

	public DebtsView(@Autowired DebtService debtService, @Autowired UserService userService) {
		this.debtService = debtService;
		List<User> users = userService.findAll();

		grid = new Grid<>(Debt.class);
		grid.addClassName("table");
		grid.setColumns("creditor", "debtor", "amount", "date");
		grid.setSelectionMode(SelectionMode.MULTI);
		grid.setItems(debtService.findAll());

		cbCreditor = new ComboBox<>("Creditor", users);
		cbDebtor = new ComboBox<>("Debtor", users);
		nfDebt = new NumberField("Debt");
		dpDate = new DatePicker("Date", LocalDate.now());

		Button bAdd = new Button("Add", new Icon(VaadinIcon.PLUS), e -> addDebt());

		bAdd.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

		Button bRemove = new Button("Paid", new Icon(VaadinIcon.CHECK), e -> removeDebt());
		bRemove.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

		HorizontalLayout hl = new HorizontalLayout(cbCreditor, cbDebtor, nfDebt, dpDate, bAdd, bRemove);
		hl.setAlignItems(Alignment.END);

		add(grid, hl);
	}

	private void removeDebt() {
		for (Debt debt : grid.getSelectedItems()) {
			debtService.delete(debt);
			Notification.show(debt.getDebtor() + "->" + debt.getCreditor() + ": " + debt.getAmount() + " paid.");
		}
		grid.setItems(debtService.findAll());
	}

	private void addDebt() {
		String creditor = cbCreditor.getValue().getName();
		String debtor = cbDebtor.getValue().getName();
		Double amount = nfDebt.getValue();
		LocalDate date = dpDate.getValue();

		Debt debt = new Debt(creditor, debtor, amount, date);
		debtService.save(debt);
		grid.setItems(debtService.findAll());
	}
}
