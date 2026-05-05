package funFarm.infrastructure.storage;

import funFarm.core.warehouse.Warehouse;
import funFarm.core.warehouse.WarehouseTest;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.SQLException;

public class SqlWarehouseTest extends WarehouseTest {

    private SqlWarehouse warehouse;

    @BeforeEach
    void setUp() throws SQLException {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.sqlite.JDBC");
        dataSource.setUrl("jdbc:sqlite::memory:");
        warehouse = new SqlWarehouse(dataSource);
    }

    @Override
    protected Warehouse getWarehouse() {
        return warehouse;
    }
}