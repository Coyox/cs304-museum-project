alter session set nls_date_format = 'yyyy-mm-dd';

-- drop tables in order of reference
-- no references
drop table member_2;
drop table object_has_2;

-- 1st layer
drop table exhibits;
drop table object_has_3;
drop table RSVPs;
drop table creates;

-- 2nd layer
drop table event;
drop table object_has_1;
drop table artist;
drop table exhibit;
drop table member_1;

-- recreate all tables
CREATE TABLE event
(title VARCHAR(100),
startDate TIMESTAMP NOT NULL,
endDate TIMESTAMP NOT NULL,
fee INTEGER,
PRIMARY KEY (title));

CREATE TABLE exhibit
(ename VARCHAR(100),
startDate TIMESTAMP NOT NULL,
endDate TIMESTAMP NOT NULL,
specialist VARCHAR(50),
PRIMARY KEY (ename));

CREATE TABLE member_1
(mname VARCHAR(50) NOT NULL,
age INTEGER NOT NULL,
addr VARCHAR(50),
email VARCHAR(50),
phone VARCHAR(12) NOT NULL,
signUpDate DATE,
PRIMARY KEY (mname, phone));

CREATE TABLE member_2
(age INTEGER NOT NULL,
fee INTEGER NOT NULL,
PRIMARY KEY (age));

CREATE TABLE artist
(aname VARCHAR(20),
dateOfBirth DATE,
nationality VARCHAR(20),
PRIMARY KEY (aname, dateOfBirth));

CREATE TABLE object_has_1
(objectID INTEGER,
type VARCHAR(20),
location VARCHAR(100),
placeMade VARCHAR(50),
dateMade DATE,
PRIMARY KEY (objectID));

CREATE TABLE object_has_2
(placeMade VARCHAR(50),
dateMade DATE,
culture VARCHAR(50),
PRIMARY KEY (placeMade, dateMade));

CREATE TABLE object_has_3
(location VARCHAR(100),
ename VARCHAR(100),
PRIMARY KEY (location),
FOREIGN KEY (ename) REFERENCES exhibit
ON DELETE CASCADE);

CREATE TABLE creates
(aname VARCHAR(50),
dateOfBirth DATE,
objectID INTEGER,
PRIMARY KEY (aname, dateOfBirth, objectID),
FOREIGN KEY (aname, dateOfBirth) REFERENCES artist,
FOREIGN KEY (objectID) REFERENCES object_has_1);

CREATE TABLE exhibits
(title VARCHAR(100),
objectID INTEGER,
PRIMARY KEY (title, objectID),
FOREIGN KEY (title) REFERENCES event,
FOREIGN KEY (objectID) REFERENCES object_has_1);

CREATE TABLE RSVPs
(title VARCHAR(100),
mname VARCHAR(50),
phone VARCHAR(12),
PRIMARY KEY (title, mname, phone),
FOREIGN KEY (title) REFERENCES event,
FOREIGN KEY (mname, phone) REFERENCES member_1);

-- populate the tables with data!

-- event
INSERT INTO event values
('Jones'' Wedding', '2014-04-30 10:00:00', '2014-05-09 16:00:00', 0);

INSERT INTO event values
('Legacy Awards Dinner','2015-04-30 10:00:00', '2015-05-09 16:00:00', 50);

INSERT INTO event values
('Spring Break Camp','2015-10-30 10:00:00', '2014-11-09 16:00:00', 20);

INSERT INTO event values
('Exhibit Opening Luncheon','2014-12-20 10:00:00', '2014-12-31 16:00:00', 60);

INSERT INTO event values
('Exhibit Closing Dinner','2014-01-01 10:00:00', '2014-01-09 16:00:00', 0);

-- exhibit
INSERT INTO exhibit values
('Claiming Space: Voices of Urban Aboriginal Youth', '2014-04-30 10:00:00', '2014-05-09 16:00:00', 'Pam Brown');

INSERT INTO exhibit values
('Don''t Give Up!','2015-04-30 10:00:00', '2015-05-09 16:00:00', NULL);

INSERT INTO exhibit values
('Safar/Voyage','2015-10-30 10:00:00', '2014-11-09 16:00:00', 'Dr. Fereshteh Daftari');

INSERT INTO exhibit values
('Pigapicha! 100 Years of Studio Photography in Nairobi','2014-12-20 10:00:00', '2014-12-31 16:00:00', 'Katharina Greven');

INSERT INTO exhibit values
('Speaking to Memory','2014-01-01 10:00:00', '2014-01-09 16:00:00', NULL);

-- artist
INSERT INTO artist values
('Pablo', '1899-04-30', 'Spanish');

INSERT INTO artist values
('Frida', '1929-04-15', 'Mexican');

INSERT INTO artist values
('Theodore', '1800-12-25', 'American');

INSERT INTO artist values
('Dorian', '1891-06-20', 'British');

INSERT INTO artist values
('Gorgo', '1818-09-27', 'Spartan');

INSERT INTO artist values
('Gorgo', '1833-04-05', 'American');

INSERT INTO artist values
('Picasso', '1900-11-05', 'American');

INSERT INTO artist values
('Glitter', '1840-11-05', 'British');

-- members
INSERT INTO member_1 values
('Suzie', 17, 'Wreak Beach', NULL, '7781122334', NULL);
INSERT INTO member_2 values
(17, 45);

INSERT INTO member_1 values
('Suzie', 17, 'Rek Beach', NULL, '1122334455', NULL);
--INSERT INTO member_2 values
--(17, 45);

INSERT INTO member_1 values
('Farshid', 50, '1234 EOSC', 'il-os@ubc.ca', '0314897556', '0001-01-01');
INSERT INTO member_2 values
(50, 50);

INSERT INTO member_1 values
('Darla', 101, NULL, 'marlborough@school.ca', '5703040404', '2007-12-13');
INSERT INTO member_2 values
(101, 45);

INSERT INTO member_1 values
('Kimmy', 30, '5589 NYC', 'molewoman@babysitter.com', '0000000000', '2014-06-06');
INSERT INTO member_2 values
(30, 50);

INSERT INTO member_1 values
('Lady', 29, '6565 Gage', 'sheep@calender.com', '6048756681', '2015-02-06');
INSERT INTO member_2 values
(29, 50);

-- object_has
INSERT INTO object_has_1 values
(4, 'Painting', 'F1R18', 'France', '1915-02-06');
INSERT INTO object_has_2 values
('France', '1915-02-06', 'Expressionism');
INSERT INTO object_has_3 values
('F1R18', 'Claiming Space: Voices of Urban Aboriginal Youth');

INSERT INTO object_has_1 values
(5, 'Painting', 'F1R18', 'France', '1945-12-06');
INSERT INTO object_has_2 values
('France', '1945-12-06', 'Romanticism');
--INSERT INTO object_has_3 values
--('F1R20', 'Don''t Give Up!');

INSERT INTO object_has_1 values
(8, 'Painting', 'F1R14', 'France', '1945-11-06');
INSERT INTO object_has_2 values
('France', '1945-11-06', 'Romanticism');
INSERT INTO object_has_3 values
('F1R14', 'Claiming Space: Voices of Urban Aboriginal Youth');

INSERT INTO object_has_1 values
(7, 'Sculpture', 'F2R06', 'England', '1815-02-06');
INSERT INTO object_has_2 values
('England', '1815-02-06', 'Realism');
INSERT INTO object_has_3 values
('F2R06', 'Safar/Voyage');

INSERT INTO object_has_1 values
(10, 'Photography', 'F3R01', 'America', '1915-02-06');
INSERT INTO object_has_2 values
('America', '1915-02-06', 'Romanticism');
INSERT INTO object_has_3 values
('F3R01', 'Pigapicha! 100 Years of Studio Photography in Nairobi');

INSERT INTO object_has_1 values
(13, 'Photography', 'F3R02', 'Canada', '1915-02-06');
INSERT INTO object_has_2 values
('Canada', '1915-02-06', 'Impressionism');
INSERT INTO object_has_3 values
('F3R02', 'Speaking to Memory');

-- creates
INSERT INTO creates values
('Pablo', '1899-04-30', 4);

INSERT INTO creates values
('Pablo', '1899-04-30', 5);

INSERT INTO creates values
('Theodore', '1800-12-25', 7);

INSERT INTO creates values
('Dorian', '1891-06-20', 10);

INSERT INTO creates values
('Gorgo', '1818-09-27', 13);

-- exhibits
INSERT INTO exhibits values
('Jones'' Wedding', 4);

INSERT INTO exhibits values
('Jones'' Wedding', 5);

INSERT INTO exhibits values
('Jones'' Wedding', 10);

INSERT INTO exhibits values
('Legacy Awards Dinner',5);

INSERT INTO exhibits values
('Spring Break Camp',7);

INSERT INTO exhibits values
('Spring Break Camp',13);

INSERT INTO exhibits values
('Exhibit Opening Luncheon',10);

INSERT INTO exhibits values
('Exhibit Closing Dinner',13);

-- RSVPs
INSERT INTO RSVPs values
('Jones'' Wedding', 'Suzie', '7781122334');

INSERT INTO RSVPs values
('Jones'' Wedding', 'Lady', '6048756681');

INSERT INTO RSVPs values
('Jones'' Wedding', 'Farshid', '0314897556');

INSERT INTO RSVPs values
('Jones'' Wedding', 'Kimmy', '0000000000');

INSERT INTO RSVPs values
('Jones'' Wedding', 'Darla', '5703040404');

INSERT INTO RSVPs values
('Jones'' Wedding', 'Suzie', '1122334455');

INSERT INTO RSVPs values
('Legacy Awards Dinner','Farshid', '0314897556');

INSERT INTO RSVPs values
('Legacy Awards Dinner','Lady', '6048756681');

INSERT INTO RSVPs values
('Spring Break Camp','Kimmy', '0000000000');

INSERT INTO RSVPs values
('Exhibit Opening Luncheon','Lady', '6048756681');

INSERT INTO RSVPs values
('Exhibit Closing Dinner','Lady', '6048756681');
