package lab1;

import lab1.core.*;
import lab1.core.plants.Potato;
import lab1.core.plants.Tomato;
import lab1.core.state.FarmStateMapper;
import lab1.infrastructure.storage.*;
import lab1.infrastructure.ui.ConsoleView;
import lab1.service.FarmService;
import lab1.service.View;
import lab1.core.model.*;
import lab1.service.*;

import java.sql.SQLException;

public class Main {
    static void main() {

        AnalyticStore store = new RamAnalyticStore();
        Reporter reporter = store.createrReporter();
        Warehouse warehouse = new RamWarehouse();

        Farm farm = new Farm();
        EventNotifier eventNotifier = new ListEventNotifier();

        eventNotifier.registerListener(store);
        EventListener eventDebugger = (FarmEvent event) -> {
            System.out.println("Event type: " + event.eventType + " count " + event.count + " plant name " + event.targetPlant);
        };
        eventNotifier.registerListener(eventDebugger);

        PlantRegistry registry = new PlantRegistry();
        registry.register(Potato::new);
        registry.register(Tomato::new);
        FarmStore farmStore;
        try {
             farmStore = new SqliteStateStore("db.db");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            FarmStateMapper.setFarmState(farm, farmStore.loadFarmState(), registry);
        } catch (RuntimeException e) {
            System.out.println("Failed loading data");
        }

        FarmService service = new BaseService(registry, warehouse, eventNotifier, farm, farmStore, reporter);

        View view = new ConsoleView(service);

        view.run();
    }
}
