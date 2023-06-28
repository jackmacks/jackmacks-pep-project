package DAO;

import Model.Message;
import java.sql.*;
import Util.ConnectionUtil;
import java.util.*;

public class MessageDAO {
    private final Connection conn;

    public MessageDAO() {
        this.conn = ConnectionUtil.getConnection();
    }

    public MessageDAO(Connection conn) {
        this.conn = conn;
    }

    public List<Message> getAllMessages() throws SQLException {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT * FROM message";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            messages.add(new Message(rs.getInt("message_id"), rs.getInt("posted_by"), rs.getString("message_text"), rs.getLong("time_posted_epoch")));
        }
        return messages;
    }
    

    public Message getMessageById(int id) throws SQLException {
        String sql = "SELECT * FROM message WHERE message_id = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, id);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            return new Message(rs.getInt("message_id"), rs.getInt("posted_by"), rs.getString("message_text"), rs.getLong("time_posted_epoch"));
        }
        return null;
    }

    public List<Message> getMessagesByUserId(int userId) throws SQLException {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT * FROM message WHERE posted_by = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, userId);
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            messages.add(new Message(rs.getInt("message_id"), rs.getInt("posted_by"), rs.getString("message_text"), rs.getLong("time_posted_epoch")));
        }
        return messages;
    }
    

    public Message createMessage(Message message) throws SQLException {
        String sql = "INSERT INTO message (posted_by, message_text, time_posted_epoch) VALUES (?, ?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        pstmt.setInt(1, message.getPosted_by());
        pstmt.setString(2, message.getMessage_text());
        pstmt.setLong(3, message.getTime_posted_epoch());
        int affectedRows = pstmt.executeUpdate();

        if (affectedRows == 0) {
            throw new SQLException("Creating message failed, no rows affected.");
        }

        try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                message.setMessage_id(generatedKeys.getInt(1));
            } else {
                throw new SQLException("Creating message failed, no ID obtained.");
            }
        }

        return message;
    }

    public void deleteMessage(int id) throws SQLException {
        String sql = "DELETE FROM message WHERE message_id = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, id);
        int affectedRows = pstmt.executeUpdate();
        if (affectedRows == 0) {
            throw new SQLException("Deleting message failed, no rows affected.");
        }
    }

    public void updateMessage(Message message) throws SQLException {
        String sql = "UPDATE message SET posted_by = ?, message_text = ?, time_posted_epoch = ? WHERE message_id = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, message.getPosted_by());
        pstmt.setString(2, message.getMessage_text());
        pstmt.setLong(3, message.getTime_posted_epoch());
        pstmt.setInt(4, message.getMessage_id());
        pstmt.executeUpdate();
    }
}
