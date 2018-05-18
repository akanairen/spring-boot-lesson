CREATE TABLE BOOK (
  id        INT AUTO_INCREMENT PRIMARY KEY,
  name      VARCHAR(128) NOT NULL,
  author    VARCHAR(128),
  publisher VARCHAR(128),
  price     NUMERIC(5, 2)
);
