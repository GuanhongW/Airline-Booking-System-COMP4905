create database airline_booking_system;

-- Table: User
create table user
(
	id int auto_increment,
	username char(255) not null,
	password char(255) not null,
	role enum("ADMIN", "USER") default 'USER' not null,
	constraint user_pk
		primary key (id)
);

create unique index user_username_uindex
	on user (username);

-- Table Customer Info
create table customer_info
(
	id int not null,
	name char(255) not null,
	birth_date char(11) not null,
	gender enum("male", "female") not null,
	constraint customer_info_pk
		primary key (id),
	constraint customer_info_user_id_fk
		foreign key (id) references user (id)
			on delete cascade
);

-- Table flight
create table flight
(
	id int not null,
	departure_city char(255) not null,
	destination_city char(255) not null,
	departure_time TIME not null,
	arrival_time TIME not null,
	capacity int not null,
	overbooking int default 0 not null,
	start_date date not null,
	end_date date null,
	available_seat int not null,
	constraint flight_pk
		primary key (id)
);

-- Table ticket
create table ticket
(
	id int auto_increment,
	customer_id int not null,
	flight_id int not null,
	seat_number int null,
	constraint ticket_pk
		primary key (id),
	constraint ticket_customer_info_id_fk
		foreign key (customer_id) references customer_info (id)
			on delete cascade,
	constraint ticket_flight_id_fk
		foreign key (flight_id) references flight (id)
			on delete cascade
);

-- Table flight seat info
create table flight_seat_info
(
	flight_id int not null,
	seat_list json not null,
	constraint flight_seat_info_pk
		primary key (flight_id),
	constraint flight_seat_info_flight_id_fk
		foreign key (flight_id) references flight (id)
			on delete cascade
);






