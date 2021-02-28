package youtrack.telegram.connector.service;

import youtrack.telegram.connector.model.Person;
import youtrack.telegram.connector.repository.PersonRepository;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.util.Optional;

@RequestScoped
public class PersonService {

    @Inject
    PersonRepository personRepository;

    public Long addLinkWithTelegram(String youTrackLogin, long telegramChatId) {
        Person person = findPersonByLogin(youTrackLogin).orElse(null);
        if (person == null) {
            person = findPersonByChatId(telegramChatId).orElse(createPerson(youTrackLogin, telegramChatId));
        }
        if (person.getId() == null) {
            personRepository.addLinkWithTelegram(person);
        }
        return person.getId();
    }

    public long unLinkFromTelegram(String youTrackLogin) {
        return personRepository.unLinkFromTelegram(youTrackLogin);
    }

    public Optional<Person> findPersonByLogin(String youTrackLogin) {
        return personRepository.findByLogin(youTrackLogin);
    }

    public Optional<Person> findPersonByChatId(long chatId) {
        return personRepository.findByChatId(chatId);
    }

    private Person createPerson(String youTrackLogin, long telegramChatId) {
        return new Person(youTrackLogin, telegramChatId);
    }

}
