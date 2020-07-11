package bank;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BankServer {

	static final Map<String, Double> bankrolls = new HashMap<>();
	static final Set<BankClient> clients = new HashSet<>();

	static void deregister(BankClient bc) {
		if (BankServer.clients.contains(bc)) {
			BankServer.clients.remove(bc);
		}
	}

	private static double getBankrollOf(String name) {
		// return value of bank roll defaulting to 0
		return BankServer.bankrolls.getOrDefault(name, 0.0);
	}

	private static BankClient getClientOf(String name) {
		for (final BankClient bc : BankServer.clients) {
			if (bc.getName().contentEquals(name)) {
				return bc;
			}

		}
		return null;

	}

	public static boolean increase(String name, double amount) {

		// assign new bank roll to name
		final double bankroll = BankServer.getBankrollOf(name) + amount;
		BankServer.bankrolls.put(name, bankroll);

		// inform client about change of his bank roll
		final BankClient c = BankServer.getClientOf(name);
		if (c != null) {
			c.bankrollChanged(bankroll);
		}

		return true;

	}

	static void register(BankClient bc) {
		clients.add(bc);
		bc.bankrollChanged(BankServer.getBankrollOf(bc.getName()));
	}
}
