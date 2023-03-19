
alter table "order-service".tea_order_line
    add column if not exists tea_upc varchar(255);
