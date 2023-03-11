
insert into "inventory-service".tea_inventory(id, tea_id, create_date, version, quantity_on_hand) values
    ((select gen_random_uuid()), 'da148c3a-3e95-4fa9-9bb6-b36548cfa665', now(), 0, 3),
    ((select gen_random_uuid()), 'da148c3a-3e95-4fa9-9bb6-b36548cfa665', now(), 0, 4),
    ((select gen_random_uuid()), '52a17d9f-ec81-4d73-b5f7-31accfcb24dc', now(), 0, 6),
    ((select gen_random_uuid()), '52a17d9f-ec81-4d73-b5f7-31accfcb24dc', now(), 0, 7),
    ((select gen_random_uuid()), 'd58a9597-a8cf-428f-a309-b807351e96e2', now(), 0, 9),
    ((select gen_random_uuid()), 'd58a9597-a8cf-428f-a309-b807351e96e2', now(), 0, 4),
    ((select gen_random_uuid()), 'bfe2d288-fff6-4d08-81f9-ce6f59bd3f3e', now(), 0, 12);
