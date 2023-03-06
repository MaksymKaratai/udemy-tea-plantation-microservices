create table "order-service".customer
(
    id          uuid not null primary key,
    create_date timestamp(6) without time zone,
    update_date timestamp(6) without time zone,
    version     integer,
    name        varchar(255)
);

alter table "order-service".customer
    owner to "order-user";

create table "order-service".tea_order
(
    id           uuid not null primary key,
    create_date  timestamp(6) without time zone,
    update_date  timestamp(6) without time zone,
    version      integer,
    order_status smallint,
    customer_id  uuid
        constraint fk_tea_order_to_customer
            references customer
);

alter table "order-service".tea_order
    owner to "order-user";

create table "order-service".tea_order_line
(
    id                 uuid not null primary key,
    create_date        timestamp(6) without time zone,
    update_date        timestamp(6) without time zone,
    version            integer,
    order_quantity     integer,
    quantity_allocated integer,
    tea_id             uuid,
    tea_order_id       uuid
        constraint fk_order_line_to_order
            references tea_order
);

alter table "order-service".tea_order_line
    owner to "order-user";
