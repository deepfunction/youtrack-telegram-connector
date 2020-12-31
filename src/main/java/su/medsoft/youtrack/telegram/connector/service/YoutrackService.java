package su.medsoft.youtrack.telegram.connector.service;

import org.apache.log4j.Logger;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import su.medsoft.youtrack.telegram.connector.model.Person;
import su.medsoft.youtrack.telegram.connector.service.telegram.TelegramBotService;
import su.medsoft.youtrack.telegram.connector.service.telegram.message.dto.TelegramSendMessage;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

@RequestScoped
public class YoutrackService {

    private final Logger LOG = Logger.getLogger(YoutrackService.class);

    @Inject
    CacheService cacheService;

    @Inject
    TelegramBotService telegramBotService;

    public boolean onWebhookUpdateReceived(String youTrackLogin, String text) {
        Person person = cacheService.findPerson(youTrackLogin);
        if (person != null) {
            TelegramSendMessage telegramSendMessage = new TelegramSendMessage(new SendMessage(person.getTelegramChatId(), text));
            try {
                telegramBotService.sendMessageToTelegram(telegramSendMessage);
            } catch (TelegramApiException e) {
                LOG.error("Error while send message to telegram: " + e.getMessage());
                return false;
            }
        }
        return true;
    }

}
