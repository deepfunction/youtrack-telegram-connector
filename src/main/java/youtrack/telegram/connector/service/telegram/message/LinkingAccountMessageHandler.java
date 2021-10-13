package youtrack.telegram.connector.service.telegram.message;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import youtrack.telegram.connector.service.PersonService;
import youtrack.telegram.connector.service.telegram.state.BotState;
import youtrack.telegram.connector.service.telegram.user.UserDataCache;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

@RequestScoped
public class LinkingAccountMessageHandler implements InputMessageHandler {

    @Inject
    UserDataCache userDataCache;

    @Inject
    PersonService personService;

    @Override
    public List<TelegramSendMessage> handle(TelegramMessage telegramMessage) {
        if (userDataCache.getUsersCurrentBotState(telegramMessage.getUserId()).equals(BotState.LINKING_ACCOUNTS)) {
            userDataCache.setUsersCurrentBotState(telegramMessage.getUserId(), BotState.ASK_YOUTRACK_LOGIN_FOR_LINK_WITH_TELEGRAM);
        }
        return processUsersInput(telegramMessage);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.LINKING_ACCOUNTS;
    }

    private List<TelegramSendMessage> processUsersInput(TelegramMessage telegramMessage) {
        List<TelegramSendMessage> telegramSendMessages = new ArrayList<>();
        var userAnswer = telegramMessage.getMessage().getText();
        var userId = telegramMessage.getUserId();
        var chatId = telegramMessage.getMessage().getChatId();

        var profileData = userDataCache.getUserProfileData(userId);
        var botState = userDataCache.getUsersCurrentBotState(userId);

        if (botState.equals(BotState.ASK_YOUTRACK_LOGIN_FOR_LINK_WITH_TELEGRAM)) {
            telegramSendMessages.add(setMessageAndState(userId, chatId, ResourceBundle.getBundle("i18n/message", Locale.getDefault()).getString("enterYoutrackLogin"), BotState.LINK_ACCOUNTS, false));
        } else if (botState.equals(BotState.LINK_ACCOUNTS)) {
            var linkId = personService.addLinkWithTelegram(userAnswer, chatId);
            if (linkId != null) {
                telegramSendMessages.add(setMessageAndState(userId, chatId, ResourceBundle.getBundle("i18n/message", Locale.getDefault()).getString("accountsLinked"), null, true));
            } else {
                telegramSendMessages.add(setMessageAndState(userId, chatId, ResourceBundle.getBundle("i18n/message", Locale.getDefault()).getString("dataBaseError"), null, true));
            }
        }

        userDataCache.saveUserProfileData(userId, profileData);

        return telegramSendMessages;
    }

    private TelegramSendMessage setMessageAndState(int userId, long chatId, String message, BotState botState, boolean clearState) {
        var telegramSendMessage = new TelegramSendMessage(new SendMessage(chatId, message));
        if (clearState) {
            userDataCache.clearUserCache(userId);
        } else {
            userDataCache.setUsersCurrentBotState(userId, botState);
        }
        return telegramSendMessage;
    }
}
