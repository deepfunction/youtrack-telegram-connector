package su.medsoft.youtrack.telegram.connector.service.telegram.message.dto;

import org.telegram.telegrambots.meta.api.objects.Message;

public class TelegramMessage  {

    private Message message;

    private int userId;

    public TelegramMessage() {
    }

    public TelegramMessage(Message message, int userId) {
        this.message = message;
        this.userId = userId;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

}
