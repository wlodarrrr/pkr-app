package app.db;

import java.time.LocalDate;
import java.util.ArrayList;
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
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route
@PageTitle("Pkr - debts")
public class DebtsView extends VerticalLayout {

	private Grid<Debt> grid;

	public DebtsView(@Autowired DebtRepository debtRepository, @Autowired UserRepository userRepository) {

		List<Debt> debts = new ArrayList<Debt>();
		debtRepository.findAll().forEach(debts::add);

		List<User> users = new ArrayList<User>();
		userRepository.findAll().forEach(users::add);

		grid = new Grid<>(Debt.class);
		grid.addClassName("table");
		grid.setColumns("creditor", "debtor", "amount", "date");
		grid.setSelectionMode(SelectionMode.MULTI);
		grid.setItems(debts);

		ComboBox<User> cbCreditor = new ComboBox<>("Creditor", users);
		ComboBox<User> cbDebtor = new ComboBox<>("Debtor", users);
		NumberField nfDebt = new NumberField("Debt");
		DatePicker dpDate = new DatePicker("Date", LocalDate.now());
		
		Button bAdd = new Button("Add", new Icon(VaadinIcon.PLUS), e -> {

			String creditor = cbCreditor.getValue().getName();
			String debtor = cbDebtor.getValue().getName();
			Double amount = nfDebt.getValue();
			LocalDate date = dpDate.getValue();

			Debt debt = new Debt(creditor, debtor, amount, date);
			debtRepository.save(debt);
			refreshGrid(debtRepository);
		});

		bAdd.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

		Button bRemove = new Button("Paid", new Icon(VaadinIcon.CHECK), e -> {
			debtRepository.deleteAll(grid.getSelectedItems());
			refreshGrid(debtRepository);
		});
		bRemove.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

		HorizontalLayout hl = new HorizontalLayout(cbCreditor, cbDebtor, nfDebt, dpDate, bAdd, bRemove);
		hl.setAlignItems(Alignment.END);

		add(grid, hl);
	}

	private void refreshGrid(DebtRepository debtRepository) {
		List<Debt> debts = new ArrayList<Debt>();
		debtRepository.findAll().forEach(debts::add);
		grid.setItems(debts);
	}
}
