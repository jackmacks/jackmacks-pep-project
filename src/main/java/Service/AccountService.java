package Service;

import DAO.AccountDAO;
import Model.Account;

import java.sql.SQLException;

public class AccountService {
    private final AccountDAO accountDAO;

    public AccountService(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    public Account getAccountById(int id) throws SQLException {
        return accountDAO.getAccountById(id);
    }

    public Account createAccount(Account account) throws SQLException {
        return accountDAO.createAccount(account);
    }

    public void deleteAccount(int id) throws SQLException {
        accountDAO.deleteAccount(id);
    }

    public void updateAccount(Account account) throws SQLException {
        accountDAO.updateAccount(account);
    }

    public Account getAccountByUsername(String username) throws SQLException {
        return accountDAO.getAccountByUsername(username);
    }
    
}
