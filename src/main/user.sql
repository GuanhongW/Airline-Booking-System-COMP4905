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

-- Table flightRoute route
create table flightRoute
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

alter table flightRoute modify overbooking decimal(4,2) default 0 not null;
rename table flightRoute to flight_route;
alter table flight_route change id flight_number int not null;
alter table flight_route drop column available_seat;

alter table flight_route change capacity aircraft_id int not null;

alter table flight_route
	add constraint flight_route_aircraft_aircraft_id_fk
		foreign key (aircraft_id) references aircraft (aircraft_id)
			on update cascade on delete cascade;




-- Table flight
create table flight
(
	flight_id int auto_increment,
	flight_number int not null,
	flight_date date not null,
	constraint flight_pk
		primary key (flight_id),
	constraint flight_flight_route_flight_number_fk
		foreign key (flight_number) references flight_route (flight_number)
			on update cascade on delete cascade
);

alter table flight
	add available_seats int not null;

alter table flight
    add version int not null;

alter table flight
    change available_seats available_tickets int not null;






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
		foreign key (flight_id) references flight (flight_id)
			on delete cascade
);

alter table ticket
	add flight_date date not null;

alter table ticket
	add constraint ticket_unavailable_seat_info_seat_number_flight_id_fk
		foreign key (seat_number, flight_id) references unavailable_seat_info (seat_number, flight_id)
			on update cascade on delete set null;





-- Table flight seat info

create table unavailable_seat_info
(
	flight_id int not null,
	seat_number int not null,
	seat_status enum('AVAILABLE', 'UNAVAILABLE', 'BOOKED') not null,
	constraint flight_seat_info_pk
		primary key (flight_id, seat_number),
	constraint flight_seat_info_flight_flight_id_fk
		foreign key (flight_id) references flight (flight_id)
			on delete cascade
);



-- Table aircraft
create table aircraft
(
	aircraft_id int not null,
	capacity int not null,
	constraint aircraft_pk
		primary key (aircraft_id)
);

-- Default Aircraft
INSERT INTO aircraft (aircraft_id, capacity)
VALUES (737, 169);

INSERT INTO aircraft (aircraft_id, capacity)
VALUES (777, 400);

INSERT INTO aircraft (aircraft_id, capacity)
VALUES (320, 146);


INSERT INTO aircraft (aircraft_id, capacity)
VALUES (900, 76);

INSERT INTO aircraft (aircraft_id, capacity)
VALUES (200, 50);







