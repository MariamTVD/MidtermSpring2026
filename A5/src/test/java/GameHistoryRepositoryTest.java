import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class GameHistoryRepositoryTest {

    @Test
    void savesCompletedGameAndQueriesReports() {
        GameHistoryRepository repository = PersistenceManager.createRepository();

        repository.saveCompletedGame(
                List.of("TestBot1", "TestBot2", "TestBot3"),
                new int[]{0, 0, 71},
                2,
                71,
                LocalDateTime.now().minusMinutes(1),
                LocalDateTime.now()
        );

        assertFalse(repository.listRecentGames(10).isEmpty());
        assertFalse(repository.playerWinCounts().isEmpty());
        assertFalse(repository.highestScores(10).isEmpty());
    }
}
