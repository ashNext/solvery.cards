insert into cards(id, user_id, numb)
values (1, 1, '11'),
       (2, 1, '12'),
       (3, 1, '13'),
       (4, 1, '10'),
       (5, 2, '21'),
       (6, 2, '22'),
       (7, 2, '20');

update cards set enabled=false where id in (4, 7);