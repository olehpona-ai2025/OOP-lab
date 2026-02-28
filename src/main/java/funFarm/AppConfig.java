package funFarm;

import funFarm.core.PlantRegistry;
import funFarm.core.plants.Beetroot;
import funFarm.core.plants.Potato;
import funFarm.core.plants.Tomato;
import funFarm.service.AnalyticStore;
import funFarm.service.EventNotifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Configuration
@ComponentScan(basePackages = "funFarm")
public class AppConfig {
    @Bean(destroyMethod = "close")
    public Connection sqlConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:db.db");
    }

    @Autowired
    public void bindListeners(EventNotifier notifier, AnalyticStore store) {
        notifier.registerListener(store);
    }

    @Autowired
    public void registerPlants(PlantRegistry registry) {
        registry.register(Potato::new);
        registry.register(Tomato::new);
        registry.register(Beetroot::new);
    }
}
