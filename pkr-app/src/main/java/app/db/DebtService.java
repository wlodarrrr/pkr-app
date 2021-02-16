package app.db;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

@Service
public class DebtService {

	private static final Logger LOGGER = Logger.getLogger(UserService.class.getName());

	private DebtRepository debtRepo;

	public DebtService(DebtRepository debtRepo) {
		this.debtRepo = debtRepo;
	}
	
	public List<Debt> findAll(){
		return debtRepo.findAll();
	}
	
	public void delete(Debt debt) {
		debtRepo.delete(debt);
	}

	public void save(Debt debt) {
		if (debt == null) {
			LOGGER.log(Level.SEVERE, "No data for debt.");
			return;
		}
		debtRepo.save(debt);
	}
}
