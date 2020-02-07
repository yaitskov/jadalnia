--liquibase formatted sql
--changeset diaitskov:r1.0.001

ALTER DATABASE jadalnia CHARACTER SET utf8 COLLATE utf8_general_ci;

create table festival(
    fid int(11) not null auto_increment primary key,
    state varchar(10) not null, -- pending, open, closed
    opens_at timestamp(3) not null,
    name varchar(90) not null unique,
    menu text,
    created timestamp(3) default current_timestamp(3)
);

create table users(
    uid int(11) not null auto_increment primary key,
    festival_id int(11) not null,
    session_key varchar(40) not null,
    name varchar(40) not null,  -- Magda
    type varchar(10) not null,  -- kelner, admin, kasier, cook, customer
    state varchar(10) not null, -- pending, approved, banned
    created timestamp(3) default current_timestamp(3),
    foreign key (festival_id) references festival(fid)
);

create table web_push(
    uid int(11) not null primary key,
    expires_at timestamp(3) not null,
    created timestamp(3) default current_timestamp(3),
    url varchar(300) not null,
    p256dh varchar(60) not null,
    auth varchar(60) not null,
    foreign key (uid) references users(uid));

create table token(
    festival_id int(11) not null,
    tid int(11) not null,
    operation varchar(3) not null, -- Buy Sell
    amount int(11) not null,
    customer_id int(11) not null,
    kasier_id int(11) null,
    created timestamp(3) default current_timestamp(3),
    primary key (festival_id, tid),
    foreign key (festival_id) references festival(fid),
    foreign key (customer_id) references users(uid),
    foreign key (kasier_id) references users(uid)
);

create table orders(
    oid int(11) not null auto_increment primary key,
    label int(11) not null,
    festival_id int(11) not null,
    kelner_id int(11) null,
    points_cost int(11) not null,
    customer_id int(11) not null,
    requirements text not null, -- json
    created timestamp(3) default current_timestamp(3),
    state varchar(10) not null, -- sent, paid, ready, handed, cancelled, returned
    cancel_reason varchar(8) null, -- nomeal, customer, festover
    foreign key (customer_id) references users(uid),
    foreign key (festival_id) references festival(fid),
    foreign key (kelner_id) references users(uid)
);

create table delayed_order(
    label int(11) not null,
    created timestamp(3) default current_timestamp(3),
    festival_id int(11) not null,
    missing_dish varchar(100) not null,
    foreign key (festival_id) references festival(fid),
    primary key (festival_id, label)
);

create table labels (
    festival_id int(11) not null,
    label int(11) not null,
    oid int(11) null,
    created timestamp(3) default current_timestamp(3),
    foreign key (oid) references orders(oid),
    foreign key (festival_id) references festival(fid),
    primary key (festival_id, label)
);

--rollback
