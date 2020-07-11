package bank;

public interface BankClient {

	void bankrollChanged(double bankroll);

	default void deregister() {
		BankServer.deregister(this);
	}

	default void register() {
		BankServer.register(this);
	}

	String getName();
}