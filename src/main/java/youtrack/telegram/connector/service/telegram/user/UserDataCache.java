package youtrack.telegram.connector.service.telegram.user;

import youtrack.telegram.connector.service.telegram.state.BotState;

import javax.enterprise.context.ApplicationScoped;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class UserDataCache {

    private final Map<Integer, BotState> usersBotStates = new ConcurrentHashMap<>();

    private final Map<Integer, UserProfileData> usersProfileData = new ConcurrentHashMap<>();

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

    public UserProfileData getUserProfileData(int userId) {
        UserProfileData userProfileData = usersProfileData.get(userId);
        if (userProfileData == null) {
            userProfileData = new UserProfileData();
        }
        return userProfileData;
    }

    public void saveUserProfileData(int userId, UserProfileData userProfileData) {
        usersProfileData.put(userId, userProfileData);
    }

    public void clearUserCache(int userId) {
        usersBotStates.keySet().removeIf(k -> k.equals(userId));
        usersProfileData.keySet().removeIf(k -> k.equals(userId));
    }

    public Map<Integer, BotState> getUsersBotStates() {
        return usersBotStates;
    }
}
