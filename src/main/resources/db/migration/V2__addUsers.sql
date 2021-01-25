create extension if not exists pgcrypto;

insert into users(id, username, email, full_name, password)
values (1, 'u1', 'a@b.ru', 'user1', crypt('1', gen_salt('bf', 8)));

insert into users(id, username, email, full_name, password)
values (2, 'u2', 'b@c.ru', 'user2', crypt('1', gen_salt('bf', 8)));

insert into user_role (user_id, roles)
values (1, 'USER'),
       (2, 'USER');
