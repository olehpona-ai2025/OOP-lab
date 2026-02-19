package lab1;

import java.util.List;

public class Main {
    static void main() {

        AnalyticStore store = new RamAnalyticStore();
        Reporter reporter = store.createrReporter();
        Warehouse warehouse = new RamWarehouse();

        Farm farm = new Farm(warehouse);

        farm.registerListener(store);
        EventListener eventDebugger = (FarmEvent event) -> {
            System.out.println("Event type: " + event.eventType + " count " + event.count);
        };
        farm.registerListener(eventDebugger);

        PlantRegistry registry = new PlantRegistry();
        registry.register("Potato", Plants.Potato::new);
        registry.register("Tomato", Plants.Tomato::new);


        FarmService service = new FarmService() {
            @Override
            public List<String> getPlants() {
                return registry.getNames();
            }

            @Override
            public void buyPlants(int plantIndex, int plantCount) {
                Plant toBuy = registry.create(plantIndex);
                farm.buyPlant(toBuy, plantCount);
            }

            @Override
            public void createFarmArea(int area) {
                farm.addNewArea(area);
            }

            @Override
            public List<FarmAreaInfo> getFarmAreas() {
                return farm.getFarmAreaInfo();
            }

            @Override
            public FarmOpResult plantFarmArea(int areaIndex, int plantIndex) {
                Plant toPlant = registry.create(plantIndex);
                return farm.plantArea(areaIndex, toPlant);
            }

            @Override
            public FarmOpResult harvestFarmArea(int farmArea) {
                return farm.harvestArea(farmArea);
            }

            @Override
            public void growLoop() {
                farm.areaLoop();
            }

            @Override
            public void turboGrow() {
                farm.customAction(area -> {
                    Plant plant = area.getCurrentPlant();
                    if (plant == null) return;

                    while (plant.getState() != PlantGrowState.GREW) {
                        plant.grow();
                    }
                });
            }

            @Override
            public List<FarmReport> getReport() {
                return reporter.getReport();
            }
        };

        View view = new ConsoleView(service);

        view.run();
    }
}
