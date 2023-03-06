insert into "order-service".customer(id, create_date, version, name)
values ((select gen_random_uuid()), now(), 1, 'Vanco Shop'),
       ((select gen_random_uuid()), now(), 1, 'Osmantus shop'),
       ((select gen_random_uuid()), now(), 1, 'China store'),
       ((select gen_random_uuid()), now(), 1, 'Tea town');
