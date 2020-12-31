create table youtrack_telegram_connect
(
    id               bigserial    not null
        constraint youtrack_telegram_connect_pk
            primary key,
    youtrack_login   varchar(255) not null,
    telegram_chat_id bigint       not null
);

create unique index youtrack_telegram_connect_id_uindex
    on youtrack_telegram_connect (id);

create unique index youtrack_telegram_connect_youtrack_login_uindex
    on youtrack_telegram_connect (youtrack_login);

create unique index youtrack_telegram_connect_telegram_chat_id_uindex
    on youtrack_telegram_connect (telegram_chat_id);