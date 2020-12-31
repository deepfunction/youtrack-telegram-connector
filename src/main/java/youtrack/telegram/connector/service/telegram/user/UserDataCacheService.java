package youtrack.telegram.connector.service.telegram.user;

import youtrack.telegram.connector.service.telegram.state.BotState;

import javax.enterprise.context.ApplicationScoped;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class UserDataCacheService {

    private final Map<Integer, BotState> usersBotStates = new ConcurrentHashMap<>();

    public void setUsersCurrentBotState(int userId, BotState botState) {
        usersBotStates.put(userId, botState);
    }

    public BotState getUsersCurrentBotState(int userId) {
        BotState botState = usersBotStates.get(userId);
        if (botState == null) {
            botState = BotState.START;
        }
        return botState;
    }

    public void clearUserCache(int userId) {
        usersBotStates.keySet().removeIf(k -> k.equals(userId));
    }

}
