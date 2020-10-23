CREATE TABLE IF NOT EXISTS CUSTOMER (
     id  INTEGER  PRIMARY KEY AUTO_INCREMENT,
     name VARCHAR(50) NOT NULL,
     email VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS ORDERS (
     id INTEGER  PRIMARY KEY AUTO_INCREMENT,
     name VARCHAR(50) NOT NULL,
     price INTEGER  NOT NULL,
     customer_id INTEGER
);

INSERT INTO CUSTOMER (name, email) VALUES ( 'John', 'john@test.com');