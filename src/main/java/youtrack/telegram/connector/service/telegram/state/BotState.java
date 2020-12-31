package youtrack.telegram.connector.service.telegram.state;

public enum BotState {
    START,
    SHOW_MAIN_MENU,
    LINKING_ACCOUNTS,
    ASK_YOUTRACK_LOGIN_FOR_LINK_WITH_TELEGRAM,
    LINK_ACCOUNTS,
    UNLINKING_ACCOUNTS,
    ASK_YOUTRACK_LOGIN_FOR_UNLINK_FROM_TELEGRAM,
    UNLINK_ACCOUNTS;
}
