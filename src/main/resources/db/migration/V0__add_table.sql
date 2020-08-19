CREATE TABLE image(
  id             varchar(50),
  author         varchar(50),
  camera         varchar(50),
  tags           varchar(400),
  cropped_picture varchar(100),
  full_picture    varchar(100),
  page           int,
PRIMARY KEY (id));