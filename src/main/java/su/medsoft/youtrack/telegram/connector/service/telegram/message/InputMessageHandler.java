package su.medsoft.youtrack.telegram.connector.service.telegram.message;

import su.medsoft.youtrack.telegram.connector.service.telegram.message.dto.TelegramMessage;
import su.medsoft.youtrack.telegram.connector.service.telegram.message.dto.TelegramSendMessage;
import su.medsoft.youtrack.telegram.connector.service.telegram.state.BotState;

import java.util.List;

public interface InputMessageHandler {

    List<TelegramSendMessage> handle(TelegramMessage telegramMessage);

    BotState getHandlerName();

}
