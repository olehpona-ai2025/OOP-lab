package funFarm.infrastructure.storage;

import funFarm.service.FarmStore;
import funFarm.service.FarmStoreTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqlStateStoreTest extends FarmStoreTest {
    private SqlStateStore store;
    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");
        store = new SqlStateStore(connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @Override
    protected FarmStore getFarmStore() {
        return store;
    }
}
