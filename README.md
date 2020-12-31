# youtrack-telegram-connect project

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./gradlew quarkusDev
```

## Packaging and running the application

The application can be packaged using:
```shell script
./gradlew build
```
It produces the `youtrack-telegram-1.0.0-SNAPSHOT-runner.jar` file in the `/build` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `build/lib` directory.

If you want to build an _über-jar_, execute the following command:
```shell script
./gradlew build -Dquarkus.package.type=uber-jar
```

The application is now runnable using `java -jar build/youtrack-telegram-1.0.0-SNAPSHOT-runner.jar`.

## Creating a native executable

You can create a native executable using: 
```shell script
./gradlew build -Dquarkus.package.type=native
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 
```shell script
./gradlew build -Dquarkus.package.type=native -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./build/youtrack-telegram-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/gradle-tooling.

# Settings

1) Workflow script in the resources/youtrackWorkflow/youtrackScript.js, just set your url to the var connection.

2) For test you can use ngrok for open you localhost to the internet (telegram webhook needs https).

3) Sql script for postgresql in the resources/sql/youtrack_telegram_connect.sql

4) You should create a bot and set webhook by following link https://api.telegram.org/bot{my_bot_token}/setWebhook?url={url_to_send_updates_to}

5) If you need i18n you can extend message.properties with needed lang and get locale by Locale.forLanguageTag(telegramMessage.getMessage().getFrom().getLanguageCode());

6) Other settings in the application.properties.
