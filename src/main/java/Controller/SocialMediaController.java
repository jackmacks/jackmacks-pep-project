package Controller;
import java.util.*;
import Model.Account;
import Model.Message;
import Service.AccountService;
import Service.MessageService;
import io.javalin.Javalin;
import io.javalin.http.Context;
import java.sql.SQLException;
import DAO.AccountDAO;
import DAO.MessageDAO;

public class SocialMediaController {
    private AccountService accountService;
    private MessageService messageService;

    public SocialMediaController(AccountService accountService, MessageService messageService) {
        this.accountService = accountService;
        this.messageService = messageService;
    }
    
    public SocialMediaController() {
        this.accountService = new AccountService(new AccountDAO());
        this.messageService = new MessageService(new MessageDAO());
    }

    public Javalin startAPI() {
        Javalin app = Javalin.create();
        
        app.post("/account", this::createAccount);
        app.get("/account/{id}", this::getAccountById);
        app.post("/messages", this::createMessage);
        app.get("/messages/{id}", this::getMessageById);
        app.delete("/messages/{id}", this::deleteMessage);
        app.get("/accounts/{id}/messages", this::getMessagesByUserId);
        app.get("/messages", this::getAllMessages);
        app.patch("/messages/{id}", this::updateMessage);
        app.post("/login", this::login);
        app.post("/register", this::registerUser);


        return app;
    }

    private void createAccount(Context ctx) {
        try {
            Account newAccount = ctx.bodyAsClass(Account.class);
            Account createdAccount = accountService.createAccount(newAccount);
            ctx.json(createdAccount);
        } catch (SQLException e) {
            ctx.status(500).result("Database error: " + e.getMessage());
        }
    }

    private void getMessagesByUserId(Context ctx) {
        try {
            int userId = Integer.parseInt(ctx.pathParam("id"));
            List<Message> messages = messageService.getMessagesByUserId(userId);
            ctx.json(messages);
        } catch (SQLException e) {
            ctx.status(500).result("Database error: " + e.getMessage());
        }
    }

    private void getAllMessages(Context ctx) {
        try {
            List<Message> messages = messageService.getAllMessages();
            ctx.json(messages);
        } catch (SQLException e) {
            ctx.status(500).result("Database error: " + e.getMessage());
        }
    }

    private void getAccountById(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Account account = accountService.getAccountById(id);
            ctx.json(account);
        } catch (SQLException e) {
            ctx.status(500).result("Database error: " + e.getMessage());
        }
    }

    private void createMessage(Context ctx) {
        try {
            Message newMessage = ctx.bodyAsClass(Message.class);
    
            if (newMessage.getMessage_text().isEmpty()) {
                ctx.status(400).result("");
                return;
            }
    
            if (newMessage.getMessage_text().length() > 254) {
                ctx.status(400).result("");
                return;
            }
    
            // Check if the posted_by ID exists in the database
            Account account = accountService.getAccountById(newMessage.getPosted_by());
            if (account == null) {
                ctx.status(400).result("");
                return;
            }
    
            // Create the message
            Message createdMessage = messageService.createMessage(newMessage);
            ctx.json(createdMessage);
        } catch (SQLException e) {
            ctx.status(500).result("Database error: " + e.getMessage());
        }
    }
    

    private void getMessageById(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Message message = messageService.getMessageById(id);
            if (message == null) {
                ctx.status(200).result("");
                return;
            }
            ctx.json(message);
        } catch (SQLException e) {
            ctx.status(500).result("Database error: " + e.getMessage());
        }
    }

    private void deleteMessage(Context ctx) {
        try {
            int messageId = Integer.parseInt(ctx.pathParam("id"));
            // Delete the message
            Message deletedMessage = messageService.deleteMessage(messageId);
            if (deletedMessage == null) {
                ctx.status(200).result("");
                return;
            }
            ctx.status(200);
            // Return the deleted message in the response body
            ctx.json(deletedMessage);
        } catch (SQLException e) {
            ctx.status(500).result("Database error: " + e.getMessage());
        }
    }

    private void updateMessage(Context ctx) {
        try {
            int messageId = Integer.parseInt(ctx.pathParam("id"));
    
            // Get the request body and map it to a Message object
            Message updatedMessageRequest = ctx.bodyAsClass(Message.class);

            // check for empty string or excessive length
            if (updatedMessageRequest.getMessage_text().isEmpty() || updatedMessageRequest.getMessage_text().length() >= 255) {
                ctx.status(400);
                return;
            }
    
            // Get the existing message
            Message existingMessage = messageService.getMessageById(messageId);
            if (existingMessage == null) {
                ctx.status(400).result("");
                return;
            }
    
            // Update the message text
            existingMessage.setMessage_text(updatedMessageRequest.getMessage_text());
    
            // Update the message in the database
            messageService.updateMessage(existingMessage);
    
            // Return the updated message in the response body
            ctx.json(existingMessage);
        } catch (SQLException e) {
            ctx.status(500).result("Database error: " + e.getMessage());
        }
    }

    private void login(Context ctx) {
        try {
            Account inputAccount = ctx.bodyAsClass(Account.class);
            Account existingAccount = accountService.getAccountByUsername(inputAccount.getUsername());
    
            if (existingAccount == null || !existingAccount.getPassword().equals(inputAccount.getPassword())) {
                ctx.status(401).result("");
                return;
            }
    
            ctx.json(existingAccount);
        } catch (SQLException e) {
            ctx.status(500).result("Database error: " + e.getMessage());
        }
    }

    private void registerUser(Context ctx) {
        try {
            Account inputAccount = ctx.bodyAsClass(Account.class);
            Account existingAccount = accountService.getAccountByUsername(inputAccount.getUsername());
    
            // If the username already exists, return a 400 error
            if (existingAccount != null || inputAccount.getPassword().length() < 4){
                ctx.status(400);
                return;
            }

            if (inputAccount.getUsername().trim().isEmpty()) {
                ctx.status(400);
                return;
            }
    
            // Otherwise, create a new account
            Account newAccount = accountService.createAccount(inputAccount);
    
            ctx.status(200).json(newAccount);
        } catch (SQLException e) {
            ctx.status(500).result("Database error: " + e.getMessage());
        }
    }
    
    
    
    

    
}
