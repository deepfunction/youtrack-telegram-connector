package youtrack.telegram.connector.service.telegram;

import io.quarkus.arc.DefaultBean;
import org.apache.log4j.Logger;
import org.eclipse.microprofile.config.ConfigProvider;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.ApiContext;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import youtrack.telegram.connector.service.telegram.message.TelegramMessage;
import youtrack.telegram.connector.service.telegram.message.TelegramSendMessage;
import youtrack.telegram.connector.service.telegram.state.BotState;
import youtrack.telegram.connector.service.telegram.state.BotStateContext;
import youtrack.telegram.connector.service.telegram.user.UserDataCache;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

@Dependent
public class TelegramBotService extends TelegramWebhookBot {

    private static final Logger LOG = Logger.getLogger(TelegramBotService.class);

    private final String webHookPath;

    private final String botUserName;

    private final String botToken;

    @Inject
    UserDataCache userDataCache;

    @Inject
    BotStateContext botStateContext;

    public TelegramBotService() {
        super(ApiContext.getInstance(DefaultBotOptions.class));
        this.webHookPath = ConfigProvider.getConfig().getValue("telegram.bot.webHookPath", String.class);
        this.botUserName = ConfigProvider.getConfig().getValue("telegram.bot.userName", String.class);
        this.botToken = ConfigProvider.getConfig().getValue("telegram.bot.token", String.class);
    }

    @DefaultBean
    @Produces
    TelegramBotService produceTelegramBot() {
        return this;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        try {
            List<TelegramSendMessage> telegramSendMessages;
            if (update.hasCallbackQuery()) {
                telegramSendMessages = processCallbackQuery(update.getCallbackQuery());
            } else {
                telegramSendMessages = processMessage(update);
            }
            for (TelegramSendMessage telegramSendMessage : telegramSendMessages) {
                sendMessageToTelegram(telegramSendMessage);
            }
        } catch (TelegramApiException e) {
            LOG.error(e.getMessage(), e.getCause());
        }
        return null;
    }

    public void sendMessageToTelegram(TelegramSendMessage telegramSendMessage) throws TelegramApiException {
        execute(telegramSendMessage.getSendMessage());
    }

    private List<TelegramSendMessage> processMessage(Update update) {
        if (update.getMessage() != null && update.getMessage().getFrom() != null) {
            BotState botState;
            List<TelegramSendMessage> replyMessages;
            var telegramMessage = new TelegramMessage(update.getMessage(), update.getMessage().getFrom().getId());
            if ("/start".equals(update.getMessage().getText())) {
                botState = BotState.START;
            } else if (ResourceBundle.getBundle("i18n/message", Locale.getDefault()).getString("mainMenuButton").equals(update.getMessage().getText())) {
                botState = BotState.SHOW_MAIN_MENU;
            } else {
                botState = userDataCache.getUsersCurrentBotState(update.getMessage().getFrom().getId());
            }
            userDataCache.setUsersCurrentBotState(update.getMessage().getFrom().getId(), botState);
            replyMessages = botStateContext.processInputMessage(botState, telegramMessage);
            return replyMessages;
        } else {
            LOG.info("Входящие данные от telegram null. Отправитель неизвестен.");
            return new ArrayList<>();
        }
    }

    private List<TelegramSendMessage> processCallbackQuery(CallbackQuery buttonQuery) {
        var userId = buttonQuery.getFrom().getId();
        var telegramMessage = new TelegramMessage(buttonQuery.getMessage(), userId);
        if (buttonQuery.getData().contains(ResourceBundle.getBundle("i18n/message", Locale.getDefault()).getString("buttonLink"))) {
            userDataCache.setUsersCurrentBotState(userId, BotState.LINKING_ACCOUNTS);
            return botStateContext.processInputMessage(userDataCache.getUsersCurrentBotState(userId), telegramMessage);
        } else if (buttonQuery.getData().contains(ResourceBundle.getBundle("i18n/message", Locale.getDefault()).getString("buttonUnlink"))) {
            userDataCache.setUsersCurrentBotState(userId, BotState.UNLINKING_ACCOUNTS);
            return botStateContext.processInputMessage(userDataCache.getUsersCurrentBotState(userId), telegramMessage);
        } else {
            userDataCache.clearUserCache(userId);
            return botStateContext.processInputMessage(BotState.START, telegramMessage);
        }
    }

    @Override
    public String getBotUsername() {
        return botUserName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public String getBotPath() {
        return webHookPath;
    }
}
