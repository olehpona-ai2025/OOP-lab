package funFarm.infrastructure.storage;

import funFarm.infrastructure.storage.HibernateDB.Entity.FarmAreaEntity;
import funFarm.infrastructure.storage.HibernateDB.Entity.WarehouseEntity;
import funFarm.infrastructure.storage.HibernateDB.FarmStateRepository_;
import funFarm.infrastructure.storage.HibernateDB.HibernateStateStore;
import funFarm.service.FarmStore;
import funFarm.service.FarmStoreTest;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.jpa.HibernatePersistenceConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.ObjectProvider;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class HibernateStateStoreTest extends FarmStoreTest {
    private SessionFactory sessionFactory;
    private StatelessSession session;
    private File dbFile;
    private FarmStore store;

    @BeforeEach
    void setUp() throws IOException {
        dbFile = Files.createTempFile("test-state-", ".db").toFile();
        dbFile.deleteOnExit();

        sessionFactory = new HibernatePersistenceConfiguration("test")
                .managedClasses(FarmAreaEntity.class, WarehouseEntity.class)
                .property("jakarta.persistence.jdbc.url", "jdbc:sqlite:" + dbFile.getAbsolutePath())
                .property("hibernate.dialect", "org.hibernate.community.dialect.SQLiteDialect")
                .property("hibernate.hbm2ddl.auto", "create-drop")
                .createEntityManagerFactory();

        session = sessionFactory.openStatelessSession();
        session.beginTransaction();
        store = new HibernateStateStore(new FarmStateRepository_(new ObjectProvider<>() {
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

    @AfterEach
    void tearDown() {
        if (session != null && session.isOpen()) {
            if (session.getTransaction() != null && session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            session.close();
        }
        if (sessionFactory != null && sessionFactory.isOpen()) sessionFactory.close();
        if (dbFile != null) dbFile.delete();
    }

    @Override
    protected FarmStore getFarmStore() {
        return store;
    }
}
