CREATE DATABASE flow_manage;

USE flow_manage;

CREATE TABLE status (
	id INT AUTO_INCREMENT,
	status VARCHAR(10) NOT NULL,
	PRIMARY KEY(id)
);

CREATE TABLE project (
	id INT AUTO_INCREMENT NOT NULL,
	title VARCHAR(255) NOT NULL,
	overview TEXT NOT NULL,
	deadline DATE NOT NULL,
	status_id INT NOT NULL DEFAULT 1,
	priority INT NOT NULL,
	PRIMARY KEY (id),
	FOREIGN KEY (status_id) REFERENCES status(id)
);

CREATE TABLE task (
	id INT AUTO_INCREMENT,
	user_id INT NOT NULL,
	project_id INT NOT NULL,
	status_id INT NOT NULL DEFAULT 1,
	title VARCHAR(255) NOT NULL,
	description TEXT NOT NULL,
	priority INT NOT NULL,
	deadline DATE NOT NULL,
	PRIMARY KEY (id),
	FOREIGN KEY (user_id, project_id) REFERENCES project_worker(user_id, project_id),
	FOREIGN KEY (status_id) REFERENCES status(id)
);

CREATE TABLE `user` (
	id INT AUTO_INCREMENT,
	first_name VARCHAR(50) NOT NULL,
	last_name VARCHAR(50),
	email VARCHAR(100) NOT NULL UNIQUE,
	password VARCHAR(60) NOT NULL,
	PRIMARY KEY(id)
);

CREATE TABLE project_worker (
	user_id INT NOT NULL,
	project_id INT NOT NULL,
	role VARCHAR(50) NOT NULL,
	PRIMARY KEY (user_id, project_id),
	FOREIGN KEY (user_id) REFERENCES user(id),
	FOREIGN KEY (project_id) REFERENCES project(id)
);


INSERT INTO status (status) VALUES ("on-going");
INSERT INTO status (status) VALUES ("done");
