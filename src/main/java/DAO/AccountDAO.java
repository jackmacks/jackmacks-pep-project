package DAO;

import Model.Account;
import java.sql.*;
import Util.ConnectionUtil;

public class AccountDAO {
    private final Connection conn;

    public AccountDAO() {
        this.conn = ConnectionUtil.getConnection();
    }
    
    public AccountDAO(Connection conn) {
        this.conn = conn;
    }

    public Account getAccountById(int id) throws SQLException {
        String sql = "SELECT * FROM account WHERE account_id = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, id);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            return new Account(rs.getInt("account_id"), rs.getString("username"), rs.getString("password"));
        }
        return null;
    }

    public Account createAccount(Account account) throws SQLException {
        String sql = "INSERT INTO account (username, password) VALUES (?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        pstmt.setString(1, account.getUsername());
        pstmt.setString(2, account.getPassword());
        int affectedRows = pstmt.executeUpdate();

        if (affectedRows == 0) {
            throw new SQLException("Creating account failed, no rows affected.");
        }

        try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                account.setAccount_id(generatedKeys.getInt(1));
            } else {
                throw new SQLException("Creating account failed, no ID obtained.");
            }
        }

        return account;
    }

    public void deleteAccount(int id) throws SQLException {
        String sql = "DELETE FROM account WHERE account_id = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, id);
        pstmt.executeUpdate();
    }

    public void updateAccount(Account account) throws SQLException {
        String sql = "UPDATE account SET username = ?, password = ? WHERE account_id = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, account.getUsername());
        pstmt.setString(2, account.getPassword());
        pstmt.setInt(3, account.getAccount_id());
        pstmt.executeUpdate();
    }

    public Account getAccountByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM account WHERE username = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, username);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            return new Account(rs.getInt("account_id"), rs.getString("username"), rs.getString("password"));
        }
        return null;
    }
    
}
