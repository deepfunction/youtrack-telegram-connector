package su.medsoft.youtrack.telegram.connector.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import su.medsoft.youtrack.telegram.connector.model.Person;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.Optional;

@ApplicationScoped
public class PersonRepository implements PanacheRepository<Person> {

    public Optional<Person> findByLogin(String youTrackLogin){
        return find("SELECT person FROM youtrack_telegram_connect person WHERE youtrack_login = ?1", youTrackLogin).singleResultOptional();
    }

    @Transactional
    public void addLinkWithTelegram(Person person) {
        persist(person);
    }

    @Transactional
    public long unLinkFromTelegram(String youTrackLogin) {
        return delete("FROM youtrack_telegram_connect WHERE youtrack_login = ?1", youTrackLogin);
    }

}
