package su.medsoft.youtrack.telegram.connector.service.telegram.message;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
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
public class MainMenuMessageHandler implements InputMessageHandler {

    @Inject
    UserDataCacheService userDataCacheService;

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
        int userId = telegramMessage.getUserId();
        long chatId = telegramMessage.getMessage().getChatId();
        BotState botState = userDataCacheService.getUsersCurrentBotState(userId);
        if (botState.equals(BotState.SHOW_MAIN_MENU)) {
            TelegramSendMessage telegramSendMessage = new TelegramSendMessage(new SendMessage(chatId, ResourceBundle.getBundle("i18n/message", Locale.getDefault()).getString("chooseAction")));
            telegramSendMessage.getSendMessage().setReplyMarkup(createButtons());
            telegramSendMessages.add(telegramSendMessage);
            userDataCacheService.clearUserCache(userId);
        }
        return telegramSendMessages;
    }

    private InlineKeyboardMarkup createButtons() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton buttonLink = new InlineKeyboardButton().setText(ResourceBundle.getBundle("i18n/message", Locale.getDefault()).getString("linkWithYoutrack"));
        buttonLink.setCallbackData(ResourceBundle.getBundle("i18n/message", Locale.getDefault()).getString("buttonLink"));
        InlineKeyboardButton buttonUnlink = new InlineKeyboardButton().setText(ResourceBundle.getBundle("i18n/message", Locale.getDefault()).getString("unlinkFromYoutrack"));
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
