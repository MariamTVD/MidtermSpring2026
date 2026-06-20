import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameHistoryRepository {

    private final SqlSessionFactory sqlSessionFactory;

    public GameHistoryRepository(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public void initializeSchema(String schemaSql) {
        try (SqlSession session = sqlSessionFactory.openSession();
             Connection connection = session.getConnection();
             Statement statement = connection.createStatement()) {

            String[] statements = schemaSql.split(";");

            for (String sql : statements) {
                String trimmed = sql.trim();

                if (!trimmed.isEmpty()) {
                    statement.execute(trimmed);
                }
            }

            session.commit();
        } catch (Exception exception) {
            throw new RuntimeException("Failed to initialize database schema.", exception);
        }
    }

    public void saveCompletedGame(List<String> playerNames,
                                  int[] finalScores,
                                  int winnerIndex,
                                  int pointsScored,
                                  LocalDateTime startedAt,
                                  LocalDateTime completedAt) {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            Map<String, Long> playerIdsByName = new HashMap<String, Long>();

            for (String playerName : playerNames) {
                session.insert("GameHistoryMapper.insertPlayer", playerName);

                Long playerId = session.selectOne(
                        "GameHistoryMapper.findPlayerIdByName",
                        playerName
                );

                playerIdsByName.put(playerName, playerId);
            }

            String winnerName = playerNames.get(winnerIndex);
            Long winnerPlayerId = playerIdsByName.get(winnerName);

            Map<String, Object> game = new HashMap<String, Object>();
            game.put("startedAt", startedAt);
            game.put("completedAt", completedAt);
            game.put("winnerPlayerId", winnerPlayerId);
            game.put("totalRounds", 1);

            session.insert("GameHistoryMapper.insertGame", game);

            Long gameId = ((Number) game.get("id")).longValue();

            Map<String, Object> round = new HashMap<String, Object>();
            round.put("gameId", gameId);
            round.put("roundNumber", 1);
            round.put("winnerPlayerId", winnerPlayerId);
            round.put("pointsScored", pointsScored);
            round.put("completedAt", completedAt);

            session.insert("GameHistoryMapper.insertRound", round);

            for (int i = 0; i < playerNames.size(); i++) {
                String playerName = playerNames.get(i);
                Long playerId = playerIdsByName.get(playerName);

                Map<String, Object> score = new HashMap<String, Object>();
                score.put("gameId", gameId);
                score.put("playerId", playerId);
                score.put("finalScore", finalScores[i]);

                session.insert("GameHistoryMapper.insertPlayerScore", score);
            }

            session.commit();
        }
    }

    public List<String> listRecentGames(int limit) {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            List<Map<String, Object>> rows = session.selectList(
                    "GameHistoryMapper.listRecentGames",
                    limit
            );

            List<String> output = new ArrayList<String>();

            for (Map<String, Object> row : rows) {
                output.add(
                        "Game #" + row.get("GAME_ID")
                                + " | completed: " + row.get("COMPLETED_AT")
                                + " | winner: " + row.get("WINNER_NAME")
                                + " | rounds: " + row.get("TOTAL_ROUNDS")
                );
            }

            return output;
        }
    }

    public List<String> playerWinCounts() {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            List<Map<String, Object>> rows = session.selectList(
                    "GameHistoryMapper.playerWinCounts"
            );

            List<String> output = new ArrayList<String>();

            for (Map<String, Object> row : rows) {
                output.add(
                        row.get("PLAYER_NAME") + ": "
                                + row.get("WIN_COUNT") + " win(s)"
                );
            }

            return output;
        }
    }

    public List<String> highestScores(int limit) {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            List<Map<String, Object>> rows = session.selectList(
                    "GameHistoryMapper.highestScores",
                    limit
            );

            List<String> output = new ArrayList<String>();

            for (Map<String, Object> row : rows) {
                output.add(
                        row.get("PLAYER_NAME") + ": "
                                + row.get("FINAL_SCORE")
                );
            }

            return output;
        }
    }
}

