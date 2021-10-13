package youtrack.telegram.connector.service.telegram.message;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import youtrack.telegram.connector.service.telegram.state.BotState;
import youtrack.telegram.connector.service.telegram.user.UserDataCache;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

@RequestScoped
public class MainMenuMessageHandler implements InputMessageHandler {

    @Inject
    UserDataCache userDataCache;

    @Override
    public List<TelegramSendMessage> handle(TelegramMessage telegramMessage) {
        return processUsersInput(telegramMessage);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.SHOW_MAIN_MENU;
    }

    private List<TelegramSendMessage> processUsersInput(TelegramMessage telegramMessage) {
        List<TelegramSendMessage> telegramSendMessages = new ArrayList<>();
        var userId = telegramMessage.getUserId();
        var chatId = telegramMessage.getMessage().getChatId();
        var botState = userDataCache.getUsersCurrentBotState(userId);
        if (botState.equals(BotState.SHOW_MAIN_MENU)) {
            telegramSendMessages.add(setMessageAndState(userId, chatId, ResourceBundle.getBundle("i18n/message", Locale.getDefault()).getString("chooseAction")));
        }
        return telegramSendMessages;
    }

    private TelegramSendMessage setMessageAndState(int userId, long chatId, String message) {
        var telegramSendMessage = new TelegramSendMessage(new SendMessage(chatId, message));
        telegramSendMessage.getSendMessage().setReplyMarkup(createButtons());
        userDataCache.clearUserCache(userId);
        return telegramSendMessage;
    }

    private InlineKeyboardMarkup createButtons() {
        var inlineKeyboardMarkup = new InlineKeyboardMarkup();
        var buttonLink = new InlineKeyboardButton().setText(ResourceBundle.getBundle("i18n/message", Locale.getDefault()).getString("linkWithYoutrack"));
        buttonLink.setCallbackData(ResourceBundle.getBundle("i18n/message", Locale.getDefault()).getString("buttonLink"));
        var buttonUnlink = new InlineKeyboardButton().setText(ResourceBundle.getBundle("i18n/message", Locale.getDefault()).getString("unlinkFromYoutrack"));
        buttonUnlink.setCallbackData(ResourceBundle.getBundle("i18n/message", Locale.getDefault()).getString("buttonUnlink"));

        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
        keyboardButtonsRow.add(buttonLink);
        keyboardButtonsRow.add(buttonUnlink);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow);

        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }
}
