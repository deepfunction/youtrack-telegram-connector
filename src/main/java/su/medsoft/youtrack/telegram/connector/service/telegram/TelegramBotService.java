package su.medsoft.youtrack.telegram.connector.service.telegram;

import io.quarkus.arc.DefaultBean;
import io.quarkus.runtime.Startup;
import org.apache.log4j.Logger;
import org.eclipse.microprofile.config.ConfigProvider;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.ApiContext;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import su.medsoft.youtrack.telegram.connector.service.telegram.message.dto.TelegramMessage;
import su.medsoft.youtrack.telegram.connector.service.telegram.message.dto.TelegramSendMessage;
import su.medsoft.youtrack.telegram.connector.service.telegram.state.BotState;
import su.medsoft.youtrack.telegram.connector.service.telegram.state.BotStateService;
import su.medsoft.youtrack.telegram.connector.service.telegram.user.UserDataCacheService;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

@Startup
@Dependent
public class TelegramBotService extends TelegramWebhookBot {

    private final Logger LOG = Logger.getLogger(TelegramBotService.class);

    private final String webHookPath;

    private final String botUserName;

    private final String botToken;

    @Inject
    UserDataCacheService userDataCacheService;

    @Inject
    BotStateService botStateService;

    public TelegramBotService() {
        super(ApiContext.getInstance(DefaultBotOptions.class));
        this.webHookPath = ConfigProvider.getConfig().getValue("telegram.bot.webHookPath", String.class);
        this.botUserName = ConfigProvider.getConfig().getValue("telegram.bot.name", String.class);
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
            LOG.error("Error while send message to telegram: " + e.getMessage());
        }
        return null;
    }

    public void sendMessageToTelegram(TelegramSendMessage telegramSendMessage) throws TelegramApiException {
        execute(telegramSendMessage.getSendMessage());
    }

    private List<TelegramSendMessage> processMessage(Update update) {
        BotState botState;
        List<TelegramSendMessage> replyMessages;
        TelegramMessage telegramMessage = new TelegramMessage(update.getMessage(), update.getMessage().getFrom().getId());
        if ("/start".equals(update.getMessage().getText())) {
            botState = BotState.START;
        } else if (ResourceBundle.getBundle("i18n/message", Locale.getDefault()).getString("mainMenuButton").equals(update.getMessage().getText())) {
            botState = BotState.SHOW_MAIN_MENU;
        } else {
            botState = userDataCacheService.getUsersCurrentBotState(update.getMessage().getFrom().getId());
        }
        userDataCacheService.setUsersCurrentBotState(update.getMessage().getFrom().getId(), botState);
        replyMessages = botStateService.processInputMessage(botState, telegramMessage);
        return replyMessages;
    }

    private List<TelegramSendMessage> processCallbackQuery(CallbackQuery buttonQuery) {
        int userId = buttonQuery.getFrom().getId();
        TelegramMessage telegramMessage = new TelegramMessage(buttonQuery.getMessage(), userId);
        if (buttonQuery.getData().contains(ResourceBundle.getBundle("i18n/message", Locale.getDefault()).getString("buttonLink"))) {
            userDataCacheService.setUsersCurrentBotState(userId, BotState.LINKING_ACCOUNTS);
            return botStateService.processInputMessage(userDataCacheService.getUsersCurrentBotState(userId), telegramMessage);
        } else if (buttonQuery.getData().contains(ResourceBundle.getBundle("i18n/message", Locale.getDefault()).getString("buttonUnlink"))) {
            userDataCacheService.setUsersCurrentBotState(userId, BotState.UNLINKING_ACCOUNTS);
            return botStateService.processInputMessage(userDataCacheService.getUsersCurrentBotState(userId), telegramMessage);
        } else {
            userDataCacheService.clearUserCache(userId);
            return botStateService.processInputMessage(BotState.START, telegramMessage);
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
