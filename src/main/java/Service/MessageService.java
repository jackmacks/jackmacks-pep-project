package Service;

import DAO.MessageDAO;
import Model.Message;
import java.util.*;

import java.sql.SQLException;

public class MessageService {
    private final MessageDAO messageDAO;

    public MessageService(MessageDAO messageDAO) {
        this.messageDAO = messageDAO;
    }

    public List<Message> getAllMessages() throws SQLException {
        return messageDAO.getAllMessages();
    }

    public Message getMessageById(int id) throws SQLException {
        return messageDAO.getMessageById(id);
    }

    public List<Message> getMessagesByUserId(int userId) throws SQLException {
        return messageDAO.getMessagesByUserId(userId);
    }

    public Message createMessage(Message message) throws SQLException {
        if (message.getMessage_text().isEmpty()) {
            throw new IllegalArgumentException("Message text cannot be blank.");
        }
        return messageDAO.createMessage(message);
    }

    public Message deleteMessage(int id) throws SQLException {
        Message deletedMessage = messageDAO.getMessageById(id);
        if (deletedMessage == null) {
            return null;
        }
        messageDAO.deleteMessage(id);
        return deletedMessage;
    }

    public Message updateMessage(Message message) throws SQLException {
        messageDAO.updateMessage(message);
        return message;
    }
    
}
