package funFarm.infrastructure.storage;

import funFarm.core.workers.WorkerDepotStore;
import funFarm.core.workers.WorkerDepotStoreTest;
import funFarm.infrastructure.storage.HibernateDB.Entity.FarmAreaEntity;
import funFarm.infrastructure.storage.HibernateDB.Entity.WarehouseEntity;
import funFarm.infrastructure.storage.HibernateDB.Entity.WorkerEntity;
import funFarm.infrastructure.storage.HibernateDB.FarmStateRepository;
import funFarm.infrastructure.storage.HibernateDB.FarmStateRepository_;
import funFarm.infrastructure.storage.HibernateDB.HibernateWorkerDepotStore;
import funFarm.infrastructure.storage.HibernateDB.WorkerDepotRepository;
import funFarm.infrastructure.storage.HibernateDB.WorkerDepotRepository_;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.jpa.HibernatePersistenceConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.ObjectProvider;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class HibernateWorkerDepotStoreTest extends WorkerDepotStoreTest {
    private SessionFactory sessionFactory;
    private StatelessSession session;
    private File dbFile;
    private FarmStateRepository farmStateRepository;
    private WorkerDepotStore store;

    @BeforeEach
    void setUp() throws IOException {
        dbFile = Files.createTempFile("test-worker-depot-", ".db").toFile();
        dbFile.deleteOnExit();

        sessionFactory = new HibernatePersistenceConfiguration("test")
                .managedClasses(WarehouseEntity.class, FarmAreaEntity.class, WorkerEntity.class)
                .property("jakarta.persistence.jdbc.url", "jdbc:sqlite:" + dbFile.getAbsolutePath())
                .property("hibernate.dialect", "org.hibernate.community.dialect.SQLiteDialect")
                .property("hibernate.hbm2ddl.auto", "create-drop")
                .createEntityManagerFactory();

        session = sessionFactory.openStatelessSession();
        session.beginTransaction();

        ObjectProvider<StatelessSession> provider = new ObjectProvider<>() {
            @Override
            public StatelessSession getObject() { return session; }

            @Override
            public StatelessSession getObject(Object... args) { return session; }

            @Override
            public StatelessSession getIfAvailable() { return session; }

            @Override
            public StatelessSession getIfUnique() { return session; }
        };

        farmStateRepository = new FarmStateRepository_(provider);
        WorkerDepotRepository workerDepotRepository = new WorkerDepotRepository_(provider);
        store = new HibernateWorkerDepotStore(workerDepotRepository, farmStateRepository);
    }

    @AfterEach
    void tearDown() {
        if (session != null && session.isOpen()) {
            if (session.getTransaction() != null && session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
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
    protected WorkerDepotStore getWorkerDepotStore() {
        return store;
    }

    @Override
    protected void seedFarmArea(String farmAreaId) {
        farmStateRepository.save(new FarmAreaEntity(farmAreaId, "TestArea", 10, null, null, 0));
    }
}
