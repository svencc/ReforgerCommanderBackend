CREATE DATABASE recom_db;

USE recom_db;

CREATE TABLE account
(
    account_uuid uuid         not null primary key,
    access_key   varchar(255) null
);

INSERT INTO recom_db.account (account_uuid, access_key)
VALUES ('d7840e5e-7fd8-48ee-a3f8-86ff15aacd0d', '06c6db2a-e49e-4153-906a-14beb0e8fc29');
