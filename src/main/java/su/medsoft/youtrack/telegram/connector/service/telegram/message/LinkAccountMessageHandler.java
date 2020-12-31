package su.medsoft.youtrack.telegram.connector.service.telegram.message;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import su.medsoft.youtrack.telegram.connector.service.PersonService;
import su.medsoft.youtrack.telegram.connector.service.telegram.message.dto.TelegramMessage;
import su.medsoft.youtrack.telegram.connector.service.telegram.message.dto.TelegramSendMessage;
import su.medsoft.youtrack.telegram.connector.service.telegram.state.BotState;
import su.medsoft.youtrack.telegram.connector.service.telegram.user.UserDataCacheService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

@ApplicationScoped
public class LinkAccountMessageHandler implements InputMessageHandler {

    @Inject
    UserDataCacheService userDataCacheService;

    @Inject
    PersonService personService;

    @Override
    public List<TelegramSendMessage> handle(TelegramMessage telegramMessage) {
        if (userDataCacheService.getUsersCurrentBotState(telegramMessage.getUserId()).equals(BotState.LINKING_ACCOUNTS)) {
            userDataCacheService.setUsersCurrentBotState(telegramMessage.getUserId(), BotState.ASK_YOUTRACK_LOGIN_FOR_LINK_WITH_TELEGRAM);
        }
        return processUsersInput(telegramMessage);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.LINKING_ACCOUNTS;
    }

    private List<TelegramSendMessage> processUsersInput(TelegramMessage telegramMessage) {
        List<TelegramSendMessage> telegramSendMessages = new ArrayList<>();
        String userAnswer = telegramMessage.getMessage().getText();
        int userId = telegramMessage.getUserId();
        long chatId = telegramMessage.getMessage().getChatId();

        BotState botState = userDataCacheService.getUsersCurrentBotState(userId);

        if (botState.equals(BotState.ASK_YOUTRACK_LOGIN_FOR_LINK_WITH_TELEGRAM)) {
            telegramSendMessages.add(setMessageAndState(userId, chatId, ResourceBundle.getBundle("i18n/message", Locale.getDefault()).getString("enterYoutrackLogin"), BotState.LINK_ACCOUNTS, false));
        } else if (botState.equals(BotState.LINK_ACCOUNTS)) {
            Long linkId = personService.addLinkWithTelegram(userAnswer, chatId);
            if (linkId != null) {
                telegramSendMessages.add(setMessageAndState(userId, chatId, ResourceBundle.getBundle("i18n/message", Locale.getDefault()).getString("accountsLinked"), null, true));
            } else {
                telegramSendMessages.add(setMessageAndState(userId, chatId, ResourceBundle.getBundle("i18n/message", Locale.getDefault()).getString("dataBaseError"), null, true));
            }
        }

        return telegramSendMessages;
    }

    private TelegramSendMessage setMessageAndState(int userId, long chatId, String message, BotState botState, boolean clearState) {
        TelegramSendMessage telegramSendMessage = new TelegramSendMessage(new SendMessage(chatId, message));
        if (clearState) {
            userDataCacheService.clearUserCache(userId);
        } else {
            userDataCacheService.setUsersCurrentBotState(userId, botState);
        }
        return telegramSendMessage;
    }

}
