package lab1;

import java.util.Scanner;

public class Main {
    static void main() {

        AnalyticStore store = new RamAnalyticStore();
        Reporter reporter = store.createrReporter();
        Warehouse warehouse = new RamWarehouse();

        Farm farm = new Farm(warehouse);
        farm.registerListener(store);
        farm.registerListener((FarmEvent event) -> {
            System.out.println("Event type: " + event.eventType + " count " + event.count);
        });

        PlantRegistry registry = new PlantRegistry();
        registry.register("Potato", Plants.Potato::new);
        registry.register("Tomato", Plants.Tomato::new);

        Scanner scanner = new Scanner(System.in);

        while (true) {
            try {
                System.out.println("""
                    Farm menu
                    1. Add Farm area
                    2. Plant list
                    3. Buy plant
                    4. Farm areas list
                    5. Plant farm area
                    6. Harvest farm area
                    7. Get report
                    8. Grow loop
                    9. Turbo grow
                    10. Exit
                    """);

                int mode = scanner.nextInt();
                scanner.nextLine();

                switch (mode) {
                    case 1: {
                        System.out.println("Enter area (m^2)");
                        int area = scanner.nextInt();
                        scanner.nextLine();
                        farm.addNewArea(area);
                        break;
                    }
                    case 2:
                        var plantList = registry.getNames();
                        for (int i = 0; i < plantList.size(); i++) {
                            System.out.println(i + " " + plantList.get(i));
                        }
                        break;
                    case 3: {
                        System.out.println("Enter plant name");
                        int idx = scanner.nextInt();
                        scanner.nextLine();
                        System.out.println("Enter plant count");
                        int count = scanner.nextInt();
                        scanner.nextLine();
                        Plant toBuy = registry.create(idx);
                        if (toBuy == null) {
                            System.out.println("Plant not found");
                            break;
                        }
                        farm.buyPlant(toBuy, count);
                        break;
                    }
                    case 4:
                        var infos = farm.getFarmAreaInfo();
                        System.out.println("Index, ID, Area");
                        for (int i = 0; i< infos.size(); i++) {
                            System.out.println(i + " " + infos.get(i).id() + " " + infos.get(i).area());
                        }
                        break;
                    case 5: {
                        System.out.println("Enter area index");
                        int area_idx = scanner.nextInt();
                        scanner.nextLine();
                        System.out.println("Enter plant index");
                        int plant_idx = scanner.nextInt();
                        scanner.nextLine();
                        Plant toPlant = registry.create(plant_idx);
                        if (toPlant == null) {
                            System.out.println("Plant not found");
                            break;
                        }
                        var res = farm.plantArea(area_idx, toPlant);
                        if (!res.success()) {
                            System.out.println(res.msg());
                        } else {
                            System.out.println("Success " + res.count());
                        }
                        break;
                    }
                    case 6: {
                        System.out.println("Enter area index");
                        int area_idx = scanner.nextInt();
                        scanner.nextLine();
                        var res = farm.harvestArea(area_idx);
                        if (!res.success()) {
                            System.out.println(res.msg());
                        } else {
                            System.out.println("Success " + res.count());
                        }
                        break;
                    }
                    case 7: {
                        for (FarmReport report: reporter.getReport()) {
                            System.out.println(report.plantName() + " " + report.increasePercentage() + "x");
                        }
                        break;
                    }
                    case 8:
                        farm.areaLoop();
                        break;
                    case 9:
                        farm.customAction(new AreaFunction() {
                            @Override
                            public void inspect(FarmArea area) {
                                Plant plant = area.getCurrentPlant();
                                if (plant == null) return;

                                while (plant.getState() != PlantGrowState.GREW) {
                                    plant.grow();
                                }
                            }
                        });
                        break;
                    case 10:
                        return;
                }
            } catch (RuntimeException e) {
                System.out.println("Runtime error, try again " + e );
                scanner.nextLine();
            }

        }
    }
}
