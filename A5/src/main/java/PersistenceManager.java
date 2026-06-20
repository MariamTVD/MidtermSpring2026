import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

public final class PersistenceManager {

    private PersistenceManager() {
    }

    public static GameHistoryRepository createRepository() {
        try {
            Reader configReader = Resources.getResourceAsReader("mybatis-config.xml");

            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder()
                    .build(configReader);

            InputStream schemaStream = Resources.getResourceAsStream("schema.sql");
            String schemaSql = new String(schemaStream.readAllBytes(), StandardCharsets.UTF_8);

            GameHistoryRepository repository = new GameHistoryRepository(sqlSessionFactory);
            repository.initializeSchema(schemaSql);

            return repository;
        } catch (Exception exception) {
            throw new RuntimeException("Failed to create persistence repository.", exception);
        }
    }
}
