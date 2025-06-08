CREATE TABLE players (
    player_id INT AUTO_INCREMENT PRIMARY KEY,
    player_name VARCHAR(50) NOT NULL
);

CREATE TABLE games (
    game_id INT AUTO_INCREMENT PRIMARY KEY,
    player_x_id INT,
    player_o_id INT,
    winner_id INT NULL,
    game_date DATETIME,
    moves TEXT,
    FOREIGN KEY (player_x_id) REFERENCES players(player_id),
    FOREIGN KEY (player_o_id) REFERENCES players(player_id),
    FOREIGN KEY (winner_id) REFERENCES players(player_id)
);

CREATE TABLE scores (
    score_id INT AUTO_INCREMENT PRIMARY KEY,
    player_id INT,
    wins INT DEFAULT 0,
    losses INT DEFAULT 0,
    draws INT DEFAULT 0,
    FOREIGN KEY (player_id) REFERENCES players(player_id)
);