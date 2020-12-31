package youtrack.telegram.connector.service.telegram.message.dto;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public class TelegramSendMessage {

    private SendMessage sendMessage;

    public TelegramSendMessage() {
    }

    public TelegramSendMessage(SendMessage sendMessage) {
        this.sendMessage = sendMessage;
    }

    public SendMessage getSendMessage() {
        return sendMessage;
    }

    public void setSendMessage(SendMessage sendMessage) {
        this.sendMessage = sendMessage;
    }

}
