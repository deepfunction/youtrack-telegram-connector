package org.acme.resteasy;

import io.quarkus.test.junit.NativeImageTest;
import su.medsoft.youtrack.telegram.connector.resource.YouTrackTelegramResourceTest;

@NativeImageTest
public class NativeYouTrackTelegramResourceIT extends YouTrackTelegramResourceTest {

    // Execute the same tests but in native mode.
}
