package funFarm.infrastructure.storage;

import funFarm.core.Warehouse;
import funFarm.core.WarehouseTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqlWarehouseTest extends WarehouseTest {

    private SqlWarehouse warehouse;
    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");
        warehouse = new SqlWarehouse(connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @Override
    protected Warehouse getWarehouse() {
        return warehouse;
    }
}