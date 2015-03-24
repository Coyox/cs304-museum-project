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

CREATE TABLE TimePeriod
(startDate DATE NOT NULL,
endDate DATE NOT NULL,
startTime DATE NOT NULL,
endTime DATE NOT NULL,
PRIMARY KEY (startDate, endDate, startTime, endTime));

