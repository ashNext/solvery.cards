delete from cards;

alter sequence cards_id_seq restart with 1;

insert into cards(user_id, numb)
values (1, '11'),
       (1, '12'),
       (1, '13'),
       (1, '10'), --4
       (2, '21'),
       (2, '22'),
       (2, '20'), --7
       (3, '31'); --8

update cards set enabled=false where id in (4, 7, 8);