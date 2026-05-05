package funFarm;

import funFarm.core.farm.Farm;
import funFarm.core.farm.FarmStore;
import funFarm.core.plants.PlantRegistry;
import funFarm.core.plants.Plant;
import funFarm.core.plants.strategies.FastStrategy;
import funFarm.core.plants.strategies.StepStrategy;
import funFarm.core.workers.WorkerDepot;
import funFarm.core.workers.WorkerDepotStore;
import funFarm.infrastructure.storage.HibernateDB.*;
import funFarm.service.AnalyticStore;
import funFarm.service.EventNotifier;
import funFarm.service.View;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.hibernate.HibernateTransactionManager;
import org.springframework.orm.jpa.hibernate.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.sql.Connection;

@Configuration
@ComponentScan(basePackages = "funFarm")
public class AppConfig {

    @Bean
    public DataSource dataSource(DatabaseProperties dbProps) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(dbProps.getDriverClassName());
        dataSource.setUrl(dbProps.getUrl());
        dataSource.setUsername(dbProps.getUsername());
        dataSource.setPassword(dbProps.getPassword());
        return dataSource;
    }

    @Bean
    @ConditionalOnExpression("'${storage.state}' == 'hibernate' || '${storage.warehouse}' == 'hibernate' || '${storage.workerDepot}' == 'hibernate'")
    public LocalSessionFactoryBean sessionFactory(DataSource dataSource) {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setPackagesToScan(
                "funFarm.infrastructure.storage.HibernateDB.Entity");

        return sessionFactory;
    }

    @Bean
    @ConditionalOnExpression("'${storage.state}' == 'hibernate' || '${storage.warehouse}' == 'hibernate' || '${storage.workerDepot}' == 'hibernate'")
    public PlatformTransactionManager transactionManager(SessionFactory sessionFactory, DataSource dataSource) {
        HibernateTransactionManager txManager = new HibernateTransactionManager();
        txManager.setDataSource(dataSource);
        txManager.setSessionFactory(sessionFactory);

        return txManager;
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = "storage.state", havingValue = "hibernate")
    public FarmStateRepository farmStateRepository(SessionFactory sessionFactory, javax.sql.DataSource dataSource) {
        return new FarmStateRepository_(buildSessionProvider(sessionFactory, dataSource, "FARM_SESSION"));
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = "storage.warehouse", havingValue = "hibernate")
    public WarehouseRepository warehouseRepository(SessionFactory sessionFactory, javax.sql.DataSource dataSource) {
        return new WarehouseRepository_(buildSessionProvider(sessionFactory, dataSource, "WAREHOUSE_SESSION"));
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = "storage.workerDepot", havingValue = "hibernate")
    public WorkerDepotRepository workerDepotRepository(SessionFactory sessionFactory, javax.sql.DataSource dataSource) {
        return new WorkerDepotRepository_(buildSessionProvider(sessionFactory, dataSource, "WORKER_DEPOT_SESSION"));
    }

    @Bean
    @Scope("prototype")
    @ConditionalOnExpression("'${storage.state}' == 'hibernate' || '${storage.warehouse}' == 'hibernate' || '${storage.workerDepot}' == 'hibernate'")
    public StatelessSession statelessSession(SessionFactory factory) {
        return factory.openStatelessSession();
    }

    @Bean
    public Farm getFarm(FarmStore store) {
        return new Farm(store);
    }

    @Bean
    public PlantRegistry getPlantRegistry() {
        PlantRegistry registry = new PlantRegistry();
        registry.register(new Plant("Potato", 2, 10, new FastStrategy()));
        registry.register(new Plant("Tomato", 1, 2, new StepStrategy(10)));
        registry.register(new Plant("Beetroot", 3, 25, new StepStrategy(5)));
        return registry;
    }

    @Bean
    public WorkerDepot getWorkerDepot(WorkerDepotStore store) {
        return new WorkerDepot(store);
    }

    @Autowired
    public void bindListeners(EventNotifier notifier, AnalyticStore store) {
        notifier.registerListener(store);
    }

    @Bean
    @Profile("console")
    public CommandLineRunner startView(View view) {
        return args -> view.run();
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
