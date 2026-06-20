CREATE TABLE IF NOT EXISTS players (
                                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       name VARCHAR(100) NOT NULL UNIQUE
    );

CREATE TABLE IF NOT EXISTS games (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     started_at TIMESTAMP NOT NULL,
                                     completed_at TIMESTAMP NOT NULL,
                                     winner_player_id BIGINT,
                                     total_rounds INT NOT NULL,
                                     FOREIGN KEY (winner_player_id) REFERENCES players(id)
    );

CREATE TABLE IF NOT EXISTS rounds (
                                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                      game_id BIGINT NOT NULL,
                                      round_number INT NOT NULL,
                                      winner_player_id BIGINT,
                                      points_scored INT NOT NULL,
                                      completed_at TIMESTAMP NOT NULL,
                                      FOREIGN KEY (game_id) REFERENCES games(id),
    FOREIGN KEY (winner_player_id) REFERENCES players(id)
    );

CREATE TABLE IF NOT EXISTS player_scores (
                                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                             game_id BIGINT NOT NULL,
                                             player_id BIGINT NOT NULL,
                                             final_score INT NOT NULL,
                                             FOREIGN KEY (game_id) REFERENCES games(id),
    FOREIGN KEY (player_id) REFERENCES players(id)
    );

