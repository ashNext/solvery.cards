alter table if exists operations
    drop constraint if exists fk_operations_card_id_cards_id;
alter table if exists cards
    drop constraint if exists fk_cards_user_id_users_id;
alter table if exists user_role
    drop constraint if exists fk_roles_user_id_users_id;

drop table if exists operations cascade;
drop table if exists cards cascade;
drop table if exists user_role cascade;
drop table if exists users cascade;

drop sequence if exists cards_id_seq;
drop sequence if exists operations_id_seq;
drop sequence if exists users_id_seq;

create sequence users_id_seq start 1 increment 1;
create table users
(
    id        int4 primary key  not null,
    username  varchar(100)      not null,
    email     varchar(100)      not null,
    full_name varchar(100)      not null,
    password  varchar(100)      not null,
    enabled   bool default true not null,
    constraint users_unique_username_idx unique (username),
    constraint users_unique_email_idx unique (email)
);

create table user_role
(
    user_id int4 not null,
    roles   varchar(255),
    constraint user_roles_unique_idx unique (user_id, roles),
    constraint fk_roles_user_id_users_id foreign key (user_id) references users (id) on delete cascade
);

create sequence cards_id_seq start 1 increment 1;
create table cards
(
    id      int4 primary key  not null,
    user_id int4              not null,
    numb    varchar(16)       not null,
    balance int4 default 0    not null check (balance <= 999999999 AND balance >= 0),
    enabled bool default true not null,
    constraint cards_unique_numb_idx unique (numb),
    constraint fk_cards_user_id_users_id foreign key (user_id) references users (id) on delete cascade
);

create sequence operations_id_seq start 1 increment 1;
create table operations
(
    id                  int8 primary key        not null,
    card_id             int4                    not null,
    recipient_card_numb varchar(16),
    date_time           timestamp default now() not null,
    sum                 int4                    not null check (sum >= -999999999 AND sum <= 999999999),
    card_balance        int4                    not null check (card_balance <= 999999999 AND card_balance >= 0),
    constraint fk_operations_card_id_cards_id foreign key (card_id) references cards (id) on delete cascade
);