package app.db;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Debt {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY, generator = "debt_id_seq")
	private Long id;

	private String creditor;
	private String debtor;
	private double amount;
	private LocalDate date;

	public Debt(String creditor, String debtor, double amount, LocalDate date) {
		super();
		this.creditor = creditor;
		this.debtor = debtor;
		this.amount = amount;
		this.date = date;
	}

	public Debt() {
	}

	public Long getId() {
		return id;
	}

	public String getCreditor() {
		return creditor;
	}

	public void setCreditor(String creditor) {
		this.creditor = creditor;
	}

	public String getDebtor() {
		return debtor;
	}

	public void setDebtor(String debtor) {
		this.debtor = debtor;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}
}
