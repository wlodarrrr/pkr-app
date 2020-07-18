package app.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bankroll")
public class User {

	@Id
	@Column(name = "name")
	private String name;
	@Column(name = "pass")
	private String pass;
	@Column(name = "cash")
	private double cash;
	@Column(name = "buyin")
	private double buyin;

	public User() {
	}

	public User(String name, String pass, double cash, double buyin) {
		super();
		this.name = name;
		this.pass = pass;
		this.cash = cash;
		this.buyin = buyin;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public double getCash() {
		return cash;
	}

	public void setCash(double cash) {
		this.cash = cash;
	}

	public double getBuyin() {
		return buyin;
	}

	public void setBuyin(double buyin) {
		this.buyin = buyin;
	}

	@Override
	public String toString() {
		return name;
	}
}
