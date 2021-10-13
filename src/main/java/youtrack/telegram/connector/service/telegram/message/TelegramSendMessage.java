package youtrack.telegram.connector.service.telegram.message;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public class TelegramSendMessage {

    private SendMessage sendMessage;

    private Integer previousMessageId;

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

    public Integer getPreviousMessageId() {
        return previousMessageId;
    }

    public void setPreviousMessageId(Integer previousMessageId) {
        this.previousMessageId = previousMessageId;
    }
}
