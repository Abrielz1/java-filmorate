# java-filmorate
Template repository for Filmorate project.

This is my sql scheme

![schema](https://user-images.githubusercontent.com/106871954/204872360-218b1473-9f84-4830-adf0-e1cad206a938.png)


CREATE TABLE IF NOT EXISTS genre (
genre_id        int PRIMARY KEY,
name            varchar(20) not null
);

CREATE TABLE IF NOT EXISTS users (
id              int generated by default as identity primary key,
email           varchar(50) not null,
login           varchar(20) not null,
name            varchar(50),
birthday        date not null
);

CREATE TABLE IF NOT EXISTS films (
id              int generated by default as identity primary key,
name            varchar(100) not null,
description     varchar(200) not null,
release_date    date not null,
duration        int not null
);

CREATE TABLE IF NOT EXISTS mutual_friendship (
user_id         int not null,
friend_id       int not null,
status          varchar(10) not null,
CONSTRAINT fk_user_friendship
FOREIGN KEY (user_id)
REFERENCES users(id),
CONSTRAINT fk_friend_friendship
FOREIGN KEY (friend_id)
REFERENCES USERS(id)
);

CREATE TABLE IF NOT EXISTS films_likes (
film_id         int not null,
user_id         int not null,
CONSTRAINT fk_film_like
FOREIGN KEY (film_id)
REFERENCES films(id),
CONSTRAINT fk_user_like
FOREIGN KEY (user_id)
REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS film_genre (
film_id         int not null,
genre_id        int not null,
CONSTRAINT fk_film_genre
FOREIGN KEY (film_id)
REFERENCES films(id),
CONSTRAINT fk_genre_id
FOREIGN KEY (genre_id)
REFERENCES genre(genre_id)
);

CREATE TABLE IF NOT EXISTS mpa (
id              int not null PRIMARY KEY,
name            varchar(6) not null
);

CREATE TABLE IF NOT EXISTS mpa_films (
film_id         int PRIMARY KEY,
mpa_id          int not null,
CONSTRAINT fk_mpa
FOREIGN KEY (mpa_id)
REFERENCES mpa(id),
CONSTRAINT fk_film_mpa
FOREIGN KEY (film_id)
REFERENCES films(id)
);

-------------------------------------------------------------------------------

DROP TABLE film_genre, films, films_likes, mpa_films, mutual_friendship, users;