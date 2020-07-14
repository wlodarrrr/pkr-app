package db;

public class User {

	private String name;
	private String pass;
	private double cash;
	private double buyin;

	public User(String login, String pass, double cash, double buyin) {
		super();
		this.name = login;
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
}
