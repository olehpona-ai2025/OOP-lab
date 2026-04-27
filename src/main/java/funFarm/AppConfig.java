package funFarm;

import funFarm.core.PlantRegistry;
import funFarm.core.plants.Plant;
import funFarm.core.plants.strategies.FastStrategy;
import funFarm.core.plants.strategies.StepStrategy;
import funFarm.infrastructure.storage.HibernateDB.Entity.FarmAreaEntity;
import funFarm.infrastructure.storage.HibernateDB.Entity.WarehouseEntity;
import funFarm.infrastructure.storage.HibernateDB.FarmStateRepository;
import funFarm.infrastructure.storage.HibernateDB.FarmStateRepository_;
import funFarm.infrastructure.storage.HibernateDB.WarehouseRepository;
import funFarm.infrastructure.storage.HibernateDB.WarehouseRepository_;
import funFarm.service.AnalyticStore;
import funFarm.service.EventNotifier;
import funFarm.service.FarmService;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.jpa.HibernatePersistenceConfiguration;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.hibernate.HibernateTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.sql.Connection;

@Configuration
@ComponentScan(basePackages = "funFarm")
public class AppConfig {

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.sqlite.JDBC");
        dataSource.setUrl("jdbc:sqlite:db.db");
        return dataSource;
    }

    @Bean
    public SessionFactory sessionFactory() {
        return new HibernatePersistenceConfiguration("Jakarta Data Example").managedClasses(FarmAreaEntity.class, WarehouseEntity.class).createEntityManagerFactory();
    }

    @Bean
    public PlatformTransactionManager transactionManager(SessionFactory sessionFactory, DataSource dataSource) {
        HibernateTransactionManager txManager = new HibernateTransactionManager();
        txManager.setDataSource(dataSource);
        txManager.setSessionFactory(sessionFactory);

        return txManager;
    }

    @Bean
    @Primary
    public FarmStateRepository farmStateRepository(SessionFactory sessionFactory, javax.sql.DataSource dataSource) {
        return new FarmStateRepository_(buildSessionProvider(sessionFactory, dataSource, "FARM_SESSION"));
    }

    @Bean
    @Primary
    public WarehouseRepository warehouseRepository(SessionFactory sessionFactory, javax.sql.DataSource dataSource) {
        return new WarehouseRepository_(buildSessionProvider(sessionFactory, dataSource, "WAREHOUSE_SESSION"));
    }

    @Bean
    @Scope("prototype")
    public StatelessSession statelessSession(SessionFactory factory) {
        return factory.openStatelessSession();
    }

    @Autowired
    public void bindListeners(EventNotifier notifier, AnalyticStore store) {
        notifier.registerListener(store);
    }

    @Autowired
    public void registerPlants(PlantRegistry registry) {
        registry.register(new Plant("Potato", 2, 10, new FastStrategy()));
        registry.register(new Plant("Tomato", 1, 2, new StepStrategy(10)));
        registry.register(new Plant("Beetroot", 3, 25, new StepStrategy(5)));
    }

    @Bean
    public CommandLineRunner loadDataFromDb(FarmService service) {
        return args -> service.loadData();
    }

    private ObjectProvider<StatelessSession> buildSessionProvider(SessionFactory sessionFactory, javax.sql.DataSource dataSource, String sessionKey) {
        return new ObjectProvider<>() {
            @Override
            public StatelessSession getObject() {
                if (TransactionSynchronizationManager.hasResource(sessionKey)) {
                    return (StatelessSession) TransactionSynchronizationManager.getResource(sessionKey);
                }

                Connection conn = DataSourceUtils.getConnection(dataSource);

                StatelessSession session = sessionFactory.withStatelessOptions().connection(conn).openStatelessSession();

                if (TransactionSynchronizationManager.isSynchronizationActive()) {
                    TransactionSynchronizationManager.bindResource(sessionKey, session);
                    TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                        @Override
                        public void beforeCompletion() {
                            TransactionSynchronizationManager.unbindResource(sessionKey);
                            session.close();
                        }
                    });
                }
                return session;
            }

            @Override
            public StatelessSession getObject(Object... args) {
                return getObject();
            }

            @Override
            public StatelessSession getIfAvailable() {
                return getObject();
            }

            @Override
            public StatelessSession getIfUnique() {
                return getObject();
            }
        };
    }
}
