package youtrack.telegram.connector.service.telegram.message;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import youtrack.telegram.connector.service.CacheService;
import youtrack.telegram.connector.service.PersonService;
import youtrack.telegram.connector.service.telegram.message.dto.TelegramMessage;
import youtrack.telegram.connector.service.telegram.message.dto.TelegramSendMessage;
import youtrack.telegram.connector.service.telegram.state.BotState;
import youtrack.telegram.connector.service.telegram.user.UserDataCacheService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

@ApplicationScoped
public class UnlinkAccountMessageHandler implements InputMessageHandler {

    @Inject
    UserDataCacheService userDataCacheService;

    @Inject
    PersonService personService;

    @Inject
    CacheService cacheService;

    @Override
    public List<TelegramSendMessage> handle(TelegramMessage telegramMessage) {
        if (userDataCacheService.getUsersCurrentBotState(telegramMessage.getUserId()).equals(BotState.UNLINKING_ACCOUNTS)) {
            userDataCacheService.setUsersCurrentBotState(telegramMessage.getUserId(), BotState.ASK_YOUTRACK_LOGIN_FOR_UNLINK_FROM_TELEGRAM);
        }
        return processUsersInput(telegramMessage);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.UNLINKING_ACCOUNTS;
    }

    private List<TelegramSendMessage> processUsersInput(TelegramMessage telegramMessage) {
        List<TelegramSendMessage> telegramSendMessages = new ArrayList<>();
        String userAnswer = telegramMessage.getMessage().getText();
        int userId = telegramMessage.getUserId();
        long chatId = telegramMessage.getMessage().getChatId();

        BotState botState = userDataCacheService.getUsersCurrentBotState(userId);

        if (botState.equals(BotState.ASK_YOUTRACK_LOGIN_FOR_UNLINK_FROM_TELEGRAM)) {
            telegramSendMessages.add(setMessageAndState(userId, chatId, ResourceBundle.getBundle("i18n/message", Locale.getDefault()).getString("enterYoutrackLogin"), BotState.UNLINK_ACCOUNTS, false));
        } else if (botState.equals(BotState.UNLINK_ACCOUNTS)) {
            long deletedCount = personService.unLinkFromTelegram(userAnswer);
            if (deletedCount > 0) {
                telegramSendMessages.add(setMessageAndState(userId, chatId, ResourceBundle.getBundle("i18n/message", Locale.getDefault()).getString("accountsUnlinked"), null, true));
                cacheService.invalidate(userAnswer);
            } else {
                telegramSendMessages.add(setMessageAndState(userId, chatId, ResourceBundle.getBundle("i18n/message", Locale.getDefault()).getString("accountNotFound"), null, true));
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
