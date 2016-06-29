-------------------------------------------------------------------
-- DATABASE CREATE
-------------------------------------------------------------------
CREATE DATABASE remote:localhost/finance-coral root BFE376F0EBA538288064DBE92F0A56B6ED154B619187FA9AB16D1B3A16DB1D4F plocal document;
-------------------------------------------------------------------
-- TRANSACTION START
-------------------------------------------------------------------
DECLARE INTENT massiveinsert;
BEGIN;
-------------------------------------------------------------------
-- SCHEMA: APP_USERS
-------------------------------------------------------------------
CREATE CLASS APP_USERS;
CREATE PROPERTY APP_USERS.USER_ID STRING;
CREATE PROPERTY APP_USERS.PASSWORD STRING;
CREATE PROPERTY APP_USERS.USER_NAME STRING;
CREATE PROPERTY APP_USERS.EMAIL STRING;
CREATE PROPERTY APP_USERS.ACTIVE BOOLEAN;
CREATE PROPERTY APP_USERS.LAST_LOGIN DATETIME;
ALTER PROPERTY APP_USERS.USER_ID MANDATORY true;
ALTER PROPERTY APP_USERS.USER_ID NOTNULL true;
ALTER PROPERTY APP_USERS.PASSWORD MANDATORY true;
ALTER PROPERTY APP_USERS.PASSWORD NOTNULL true;
ALTER PROPERTY APP_USERS.USER_NAME MANDATORY true;
ALTER PROPERTY APP_USERS.USER_NAME NOTNULL true;
ALTER PROPERTY APP_USERS.EMAIL MANDATORY true;
ALTER PROPERTY APP_USERS.EMAIL NOTNULL true;
ALTER PROPERTY APP_USERS.ACTIVE MANDATORY true;
ALTER PROPERTY APP_USERS.ACTIVE NOTNULL true;
CREATE INDEX APP_USERS.USER_ID UNIQUE;
-------------------------------------------------------------------
-- SCHEMA: APP_USER_GROUPS
-------------------------------------------------------------------
CREATE CLASS APP_USER_GROUPS;
CREATE PROPERTY APP_USER_GROUPS.USER_ID STRING;
CREATE PROPERTY APP_USER_GROUPS.GROUP_NAME STRING;
ALTER PROPERTY APP_USER_GROUPS.USER_ID MANDATORY true;
ALTER PROPERTY APP_USER_GROUPS.USER_ID NOTNULL true;
ALTER PROPERTY APP_USER_GROUPS.GROUP_NAME MANDATORY true;
ALTER PROPERTY APP_USER_GROUPS.GROUP_NAME NOTNULL true;
CREATE INDEX APP_USER_GROUPS_INDEX ON APP_USER_GROUPS (USER_ID, GROUP_NAME) UNIQUE;
-------------------------------------------------------------------
-- SCHEMA: APP_USER_CONFIGS
-------------------------------------------------------------------
CREATE CLASS APP_USER_CONFIGS;
CREATE PROPERTY APP_USER_CONFIGS.CFG_ID INTEGER;
CREATE PROPERTY APP_USER_CONFIGS.CFG_USER STRING;
CREATE PROPERTY APP_USER_CONFIGS.CFG_NAME STRING;
CREATE PROPERTY APP_USER_CONFIGS.CFG_TYPE STRING;
CREATE PROPERTY APP_USER_CONFIGS.CFG_INFO STRING;
ALTER PROPERTY APP_USER_CONFIGS.CFG_ID MANDATORY true;
ALTER PROPERTY APP_USER_CONFIGS.CFG_ID NOTNULL true;
ALTER PROPERTY APP_USER_CONFIGS.CFG_USER MANDATORY true;
ALTER PROPERTY APP_USER_CONFIGS.CFG_USER NOTNULL true;
ALTER PROPERTY APP_USER_CONFIGS.CFG_NAME MANDATORY true;
ALTER PROPERTY APP_USER_CONFIGS.CFG_NAME NOTNULL true;
ALTER PROPERTY APP_USER_CONFIGS.CFG_TYPE MANDATORY true;
ALTER PROPERTY APP_USER_CONFIGS.CFG_TYPE NOTNULL true;
ALTER PROPERTY APP_USER_CONFIGS.CFG_INFO MANDATORY true;
ALTER PROPERTY APP_USER_CONFIGS.CFG_INFO NOTNULL true;
CREATE INDEX APP_USER_CONFIGS.CFG_ID UNIQUE;
-------------------------------------------------------------------
-- SCHEMA: APP_ID_GENERATOR
-------------------------------------------------------------------
CREATE CLASS APP_ID_GENERATOR;
CREATE PROPERTY APP_ID_GENERATOR.ID_NAME STRING;
CREATE PROPERTY APP_ID_GENERATOR.ID_VALUE INTEGER;
ALTER PROPERTY APP_ID_GENERATOR.ID_NAME MANDATORY true;
ALTER PROPERTY APP_ID_GENERATOR.ID_NAME NOTNULL true;
ALTER PROPERTY APP_ID_GENERATOR.ID_VALUE MANDATORY true;
ALTER PROPERTY APP_ID_GENERATOR.ID_VALUE NOTNULL true;
CREATE INDEX APP_ID_GENERATOR.ID_NAME UNIQUE;
-------------------------------------------------------------------
-- SCHEMA: APP_FILES
-------------------------------------------------------------------
CREATE CLASS APP_FILES;
CREATE PROPERTY APP_FILES.USER_ID STRING;
CREATE PROPERTY APP_FILES.CFG_ID INTEGER;
CREATE PROPERTY APP_FILES.FILE LINK;
ALTER PROPERTY APP_FILES.USER_ID MANDATORY true;
ALTER PROPERTY APP_FILES.USER_ID NOTNULL true;
ALTER PROPERTY APP_FILES.CFG_ID MANDATORY true;
ALTER PROPERTY APP_FILES.CFG_ID NOTNULL true;
ALTER PROPERTY APP_FILES.FILE MANDATORY true;
ALTER PROPERTY APP_FILES.FILE NOTNULL true;
CREATE INDEX APP_FILES.CFG_ID UNIQUE;
-------------------------------------------------------------------
-- DATA INSERTS
-------------------------------------------------------------------
INSERT INTO APP_USERS (USER_ID, PASSWORD,USER_NAME,EMAIL,ACTIVE) VALUES('root', 'Qb0iVj2Z+flmV/K2O/pZF0/r5xF2wd+EGBZ5FkPWGWk=$6lj0wD6xdlvhkrpECO5RPlnMv4B8R5b/PpbMudcL4Ko=','Administrator','rajib@mindparabole.com',true);
INSERT INTO APP_USER_GROUPS (USER_ID, GROUP_NAME) VALUES('root', 'ADMIN');
INSERT INTO APP_ID_GENERATOR SET ID_NAME = 'APP_USER_CONFIGS', ID_VALUE = 0;
INSERT INTO APP_USERS (USER_ID, PASSWORD,USER_NAME,EMAIL,ACTIVE) VALUES('dac', 'Qb0iVj2Z+flmV/K2O/pZF0/r5xF2wd+EGBZ5FkPWGWk=$6lj0wD6xdlvhkrpECO5RPlnMv4B8R5b/PpbMudcL4Ko=','Administrator','rajib@mindparabole.com',true);
INSERT INTO APP_USER_GROUPS (USER_ID, GROUP_NAME) VALUES('dac', 'DAC');
INSERT INTO APP_USERS (USER_ID, PASSWORD,USER_NAME,EMAIL,ACTIVE) VALUES('ewg', 'Qb0iVj2Z+flmV/K2O/pZF0/r5xF2wd+EGBZ5FkPWGWk=$6lj0wD6xdlvhkrpECO5RPlnMv4B8R5b/PpbMudcL4Ko=','Administrator','rajib@mindparabole.com',true);
INSERT INTO APP_USER_GROUPS (USER_ID, GROUP_NAME) VALUES('ewg', 'EWG');
INSERT INTO APP_USERS (USER_ID, PASSWORD,USER_NAME,EMAIL,ACTIVE) VALUES('bacwg', 'Qb0iVj2Z+flmV/K2O/pZF0/r5xF2wd+EGBZ5FkPWGWk=$6lj0wD6xdlvhkrpECO5RPlnMv4B8R5b/PpbMudcL4Ko=','Administrator','rajib@mindparabole.com',true);
INSERT INTO APP_USER_GROUPS (USER_ID, GROUP_NAME) VALUES('bacwg', 'BACWG');
-------------------------------------------------------------------
-- TRANSACTION END
-------------------------------------------------------------------
COMMIT;