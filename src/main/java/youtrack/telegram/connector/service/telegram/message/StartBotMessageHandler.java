package youtrack.telegram.connector.service.telegram.message;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import youtrack.telegram.connector.service.telegram.state.BotState;

import javax.enterprise.context.RequestScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

@RequestScoped
public class StartBotMessageHandler implements InputMessageHandler {

    @Override
    public List<TelegramSendMessage> handle(TelegramMessage telegramMessage) {
        return processUsersInput(telegramMessage);
    }

    private List<TelegramSendMessage> processUsersInput(TelegramMessage telegramMessage) {
        return getMainMenuMessage(telegramMessage.getMessage().getChatId());
    }

    @Override
    public BotState getHandlerName() {
        return BotState.START;
    }

    private List<TelegramSendMessage> getMainMenuMessage(long chatId) {
        var replyKeyboardMarkup = getMainMenuKeyboard();
        return createMessageWithKeyboard(chatId, replyKeyboardMarkup);
    }

    private ReplyKeyboardMarkup getMainMenuKeyboard() {
        var replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        List<KeyboardRow> keyboard = new ArrayList<>();
        var keyboardRow = new KeyboardRow();
        keyboardRow.add(new KeyboardButton(ResourceBundle.getBundle("i18n/message", Locale.getDefault()).getString("mainMenuButton")));
        keyboard.add(keyboardRow);
        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }

    private List<TelegramSendMessage> createMessageWithKeyboard(long chatId, ReplyKeyboardMarkup replyKeyboardMarkup) {
        List<TelegramSendMessage> telegramSendMessages = new ArrayList<>();
        var sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setText(ResourceBundle.getBundle("i18n/message", Locale.getDefault()).getString("mainMenu"));
        if (replyKeyboardMarkup != null) {
            sendMessage.setReplyMarkup(replyKeyboardMarkup);
        }
        telegramSendMessages.add(new TelegramSendMessage(sendMessage));
        return telegramSendMessages;
    }
}
