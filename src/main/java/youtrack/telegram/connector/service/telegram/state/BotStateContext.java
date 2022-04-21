package youtrack.telegram.connector.service.telegram.state;

import youtrack.telegram.connector.service.telegram.message.InputMessageHandler;
import youtrack.telegram.connector.service.telegram.message.TelegramMessage;
import youtrack.telegram.connector.service.telegram.message.TelegramSendMessage;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class BotStateContext {

    private final Map<BotState, InputMessageHandler> messageHandlerMap = new HashMap<>();

    @Inject
    Instance<InputMessageHandler> messageHandlerList;

    @PostConstruct
    void putMessageHandlers() {
        messageHandlerList.forEach(handler -> this.messageHandlerMap.put(handler.getHandlerName(), handler));
    }

    public List<TelegramSendMessage> processInputMessage(BotState currentState, TelegramMessage telegramMessage) {
        var currentMessageHandler = findMessageHandler(currentState);
        return currentMessageHandler.handle(telegramMessage);
    }

    private InputMessageHandler findMessageHandler(BotState currentState) {
        if (isLinkingAccountsState(currentState)) {
            return messageHandlerMap.get(BotState.LINKING_ACCOUNTS);
        } else if (isUnLinkingAccountsState(currentState)) {
            return messageHandlerMap.get(BotState.UNLINKING_ACCOUNTS);
        } else {
            return messageHandlerMap.get(currentState);
        }
    }

    private boolean isLinkingAccountsState(BotState currentState) {
        switch (currentState) {
            case LINKING_ACCOUNTS:
            case ASK_YOUTRACK_LOGIN_FOR_LINK_WITH_TELEGRAM:
            case LINK_ACCOUNTS:
                return true;
            default:
                return false;
        }
    }

    private boolean isUnLinkingAccountsState(BotState currentState) {
        switch (currentState) {
            case UNLINKING_ACCOUNTS:
            case ASK_YOUTRACK_LOGIN_FOR_UNLINK_FROM_TELEGRAM:
            case UNLINK_ACCOUNTS:
                return true;
            default:
                return false;
        }
    }
}

