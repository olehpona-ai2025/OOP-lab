package funFarm.infrastructure.storage;

import funFarm.core.Warehouse;
import funFarm.core.WarehouseTest;
import funFarm.infrastructure.storage.HibernateDB.Entity.FarmAreaEntity;
import funFarm.infrastructure.storage.HibernateDB.Entity.WarehouseEntity;
import funFarm.infrastructure.storage.HibernateDB.HibernateWarehouse;
import funFarm.infrastructure.storage.HibernateDB.WarehouseRepository_;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.jpa.HibernatePersistenceConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.ObjectProvider;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class HibernateWarehouseTest extends WarehouseTest {
    private SessionFactory sessionFactory;
    private StatelessSession session;
    private File dbFile;

    @BeforeEach
    void setUp() throws IOException {
        dbFile = Files.createTempFile("test-warehouse-", ".db").toFile();
        dbFile.deleteOnExit();

        // Use a real temp file instead of :memory: so that:
        //   1) hbm2ddl creates tables on this file
        //   2) the StatelessSession connects to the same file and sees the tables
        sessionFactory = new HibernatePersistenceConfiguration("test")
                .managedClasses(WarehouseEntity.class, FarmAreaEntity.class)
                .property("jakarta.persistence.jdbc.url", "jdbc:sqlite:" + dbFile.getAbsolutePath())
                .property("hibernate.dialect", "org.hibernate.community.dialect.SQLiteDialect")
                .property("hibernate.hbm2ddl.auto", "create-drop")
                .createEntityManagerFactory();

        session = sessionFactory.openStatelessSession();
    }

    @AfterEach
    void tearDown() {
        if (session != null && session.isOpen()) {
            session.close();
        }
        if (sessionFactory != null && sessionFactory.isOpen()) {
            sessionFactory.close();
        }
        if (dbFile != null) {
            dbFile.delete();
        }
    }

    @Override
    protected Warehouse getWarehouse() {
        return new HibernateWarehouse(new WarehouseRepository_(new ObjectProvider<>() {
            @Override
            public StatelessSession getObject() { return session; }

            @Override
            public StatelessSession getObject(Object... args) { return session; }

            @Override
            public StatelessSession getIfAvailable() { return session; }

            @Override
            public StatelessSession getIfUnique() { return session; }
        }));
    }
}
