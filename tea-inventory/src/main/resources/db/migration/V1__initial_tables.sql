
create table "inventory-service".tea_inventory
(
    id               uuid not null primary key,
    tea_id           uuid not null,
    create_date      timestamp(6) without time zone,
    update_date      timestamp(6) without time zone,
    price            numeric(38, 2),
    quantity_on_hand integer,
    version          integer
);

alter table "inventory-service".tea_inventory
    owner to "inventory-user";
