package su.medsoft.youtrack.telegram.connector.model;

import javax.persistence.*;
import java.util.Objects;

@Entity(name = "youtrack_telegram_connect")
public class Person {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public Long id;

    @Column(name = "youtrack_login")
    private String youTrackLogin;

    @Column(name = "telegram_chat_id")
    private long telegramChatId;

    public Person() {
    }

    public Person(String youTrackLogin, long telegramChatId) {
        this.youTrackLogin = youTrackLogin;
        this.telegramChatId = telegramChatId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getYouTrackLogin() {
        return youTrackLogin;
    }

    public void setYouTrackLogin(String login) {
        this.youTrackLogin = login;
    }

    public long getTelegramChatId() {
        return telegramChatId;
    }

    public void setTelegramChatId(long chatId) {
        this.telegramChatId = chatId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equals(id, person.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
