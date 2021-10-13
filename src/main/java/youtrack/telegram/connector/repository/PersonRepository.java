package youtrack.telegram.connector.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import youtrack.telegram.connector.model.Person;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.Optional;

@ApplicationScoped
public class PersonRepository implements PanacheRepository<Person> {

    public Optional<Person> findByLogin(String youTrackLogin) {
        return find("SELECT person FROM youtrack_telegram_connect person WHERE youtrack_login = ?1", youTrackLogin).singleResultOptional();
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public long addLinkWithTelegram(Person person) {
        var query = getEntityManager()
                .createNativeQuery(
                        "INSERT INTO youtrack_telegram_connect (telegram_chat_id, youtrack_login)\n" +
                                "VALUES (?, ?)\n" +
                                "ON CONFLICT (telegram_chat_id) DO UPDATE SET youtrack_login = EXCLUDED.youtrack_login\n" +
                                "WHERE youtrack_telegram_connect.telegram_chat_id = ?\n" +
                                "RETURNING id"
                )
                .setParameter(1, person.getTelegramChatId())
                .setParameter(2, person.getYouTrackLogin())
                .setParameter(3, person.getTelegramChatId());
        var id = (BigInteger) query.getSingleResult();
        return id.longValue();
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public long unLinkFromTelegram(String youTrackLogin) {
        var person = findByLogin(youTrackLogin).orElse(null);
        if (person != null) {
            delete(person);
            return 1;
        } else {
            return 0;
        }
    }
}
