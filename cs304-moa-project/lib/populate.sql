-- drop tables in order of reference
-- no references
drop table member_2;
drop table member_3;
drop table object_has_2;

-- 1st layer
drop table exhibits;
drop table object_has_3;
drop table RSVPs;
drop table creates;

-- 2nd layer
drop table event_hold_for;
drop table object_has_1;
drop table artist;
drop table exhibit;
drop table member_1;

-- 3rd layer
drop table TimePeriod;

-- recreate all tables
CREATE TABLE TimePeriod
(startDate DATE NOT NULL,
endDate DATE NOT NULL,
startTime DATE NOT NULL,
endTime DATE NOT NULL,
PRIMARY KEY (startDate, endDate, startTime, endTime));

CREATE TABLE event_hold_for
(title VARCHAR(100),
startDate DATE NOT NULL,
endDate DATE NOT NULL,
startTime DATE NOT NULL,
endTime DATE NOT NULL,
fee INTEGER,
PRIMARY KEY (title),
FOREIGN KEY (startDate, endDate, startTime, endTime) REFERENCES TimePeriod);

CREATE TABLE exhibit
(ename VARCHAR(100),
startDate DATE NOT NULL,
endDate DATE NOT NULL,
startTime DATE NOT NULL,
endTime DATE NOT NULL,
specialist VARCHAR(50),
PRIMARY KEY (ename),
FOREIGN KEY (startDate, endDate, startTime, endTime) REFERENCES TimePeriod);

CREATE TABLE member_1
(mname VARCHAR(50) NOT NULL,
age INTEGER NOT NULL,
addr VARCHAR(50),
email VARCHAR(50),
phone VARCHAR(12) NOT NULL,
PRIMARY KEY (mname, phone),
UNIQUE(mname, email));

CREATE TABLE member_2
(age INTEGER NOT NULL,
fee INTEGER NOT NULL,
PRIMARY KEY (age));

CREATE TABLE member_3
(email VARCHAR(50) NOT NULL,
signUpDate DATE,
PRIMARY KEY (email));

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
FOREIGN KEY (ename) REFERENCES exhibit);

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
FOREIGN KEY (title) REFERENCES event_hold_for,
FOREIGN KEY (objectID) REFERENCES object_has_1);

CREATE TABLE RSVPs
(title VARCHAR(100),
mname VARCHAR(50),
phone VARCHAR(12),
PRIMARY KEY (title, mname, phone),
FOREIGN KEY (title) REFERENCES event_hold_for,
FOREIGN KEY (mname, phone) REFERENCES member_1);
