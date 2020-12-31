package youtrack.telegram.connector.service.telegram.message;

import youtrack.telegram.connector.service.telegram.message.dto.TelegramMessage;
import youtrack.telegram.connector.service.telegram.message.dto.TelegramSendMessage;
import youtrack.telegram.connector.service.telegram.state.BotState;

import java.util.List;

public interface InputMessageHandler {

    List<TelegramSendMessage> handle(TelegramMessage telegramMessage);

    BotState getHandlerName();

}
