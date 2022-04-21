package youtrack.telegram.connector.service;

import io.quarkus.cache.CacheInvalidate;
import io.quarkus.cache.CacheInvalidateAll;
import io.quarkus.cache.CacheKey;
import io.quarkus.cache.CacheResult;
import youtrack.telegram.connector.model.Person;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class CacheService {

    @Inject
    PersonService personService;

    @CacheResult(cacheName = "person")
    public Person findPerson(@CacheKey String youTrackLogin) {
        return personService.findPerson(youTrackLogin).orElse(null);
    }

    @CacheInvalidate(cacheName = "person")
    public void invalidate(@CacheKey String youTrackLogin) {
    }

    @CacheInvalidateAll(cacheName = "person")
    public void invalidateAllCache() {
    }
}
