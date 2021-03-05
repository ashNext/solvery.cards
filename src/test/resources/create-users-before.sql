delete from user_role;
delete from users;

alter sequence users_id_seq restart with 1;

create extension if not exists pgcrypto;

insert into users(username, email, full_name, password)
values ('u1', 'user1@a.ru', 'user1', crypt('1', gen_salt('bf', 8)));

insert into users(username, email, full_name, password)
values ('u2', 'user2@a.ru', 'user2', crypt('1', gen_salt('bf', 8)));

insert into users(username, email, full_name, password)
values ('u3', 'user3@a.ru', 'user3', crypt('1', gen_salt('bf', 8)));

insert into users(username, email, full_name, password)
values ('au4', 'auser4@a.ru', 'advanced user 4', crypt('1', gen_salt('bf', 8)));

insert into user_role (user_id, roles)
values (1, 'USER'),
       (2, 'USER'),
       (3, 'USER'),
       (4, 'USER'),
       (4, 'USER_ADVANCED');
