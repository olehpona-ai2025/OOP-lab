package funFarm.infrastructure.ui;

import funFarm.core.farm.FarmException;
import funFarm.core.model.FarmAreaInfo;
import funFarm.core.model.FarmReport;
import funFarm.core.model.HarvestResult;
import funFarm.core.model.PlantResult;
import funFarm.core.model.WorkerInfo;
import funFarm.core.model.WarehouseInfo;
import funFarm.core.plants.Plant;
import funFarm.core.warehouse.WarehouseException;
import funFarm.core.workers.profiles.WorkerProfileType;
import funFarm.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component("ConsoleView")
@Primary
@Profile("console")
public class ConsoleView implements View {
    private final FarmService farmService;
    private final PlantService plantService;
    private final WarehouseService warehouseService;
    private final WorkerService workerService;

    @Autowired
    public ConsoleView(FarmService farmService, PlantService plantService, WarehouseService warehouseService, WorkerService workerService) {
        this.farmService = farmService;
        this.plantService = plantService;
        this.workerService = workerService;
        this.warehouseService = warehouseService;
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);

        boolean running = true;
        while (running) {
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
                    8. Worker loop
                    9. Turbo grow
                    10. Remove farm area
                    11. Set farm area name
                    12. Load farmState
                    13. Warehouse info
                    14. Workers list
                    15. Create worker
                    16. Assign worker
                    17. Delete worker
                    18. Exit
                    """);

                int mode = readInt(scanner, "Enter menu option:");

                switch (mode) {
                    case 1: {
                        int area = readInt(scanner, "Enter area (m^2):");
                        farmService.createFarmArea(area);
                        break;
                    }
                    case 2:
                        var plantList = plantService.getPlants();

                        System.out.println("Plant name, Planting cost, Base Yield");

                        for (Plant plant : plantList) {
                            System.out.println(plant.getPlantName() + " " + plant.getPlantingCost() + " " + plant.getBaseYield());
                        }
                        break;
                    case 3: {
                        String name = readString(scanner, "Enter plant name:");
                        int count = readInt(scanner, "Enter plant count:");

                        if (!warehouseService.buyPlants(name, count)){
                            System.out.println("Failed buying plants");
                        }

                        break;
                    }
                    case 4:
                        var infos = farmService.getFarmAreas();
                        System.out.println("ID, Area, Name, Plant, Plant State");
                        for (FarmAreaInfo info : infos) {
                            System.out.println(info.id() + ", " + info.area() + ", " + info.name()+ ", " + info.plantName() + ", " + info.plantState());
                        }
                        break;
                    case 5: {
                        String areaId = readString(scanner, "Enter area id:");
                        String plantName = readString(scanner, "Enter plant name:");

                        PlantResult res;
                        try {
                            res = farmService.plantFarmArea(areaId, plantName);
                        } catch (RuntimeException e) {
                            System.out.println("Exception received, msg: " + e.getMessage());
                            break;
                        }

                        if (!res.success()) {
                            System.out.println("Failed " + res.msg());
                        } else {
                            System.out.println("Success");
                        }
                        break;
                    }
                    case 6: {
                        String areaId = readString(scanner, "Enter area index:");
                        HarvestResult res;

                        try {
                            res = farmService.harvestFarmArea(areaId);
                        } catch (RuntimeException e) {
                            System.out.println("Exception received, msg: " + e.getMessage());
                            break;
                        }

                        if (!res.success()) {
                            System.out.println(res.msg());
                        } else {
                            System.out.println("Success");
                        }
                        break;
                    }
                    case 7: {
                        for (FarmReport report: warehouseService.getReport()) {
                            System.out.println(report.plantName() + " " + report.increasePercentage() + "%");
                        }
                        break;
                    }
                    case 8:
                        workerService.workerLoop();
                        break;
                    case 9: {
                        String areaId = readString(scanner, "Enter area id:");

                        farmService.removeFarmArea(areaId);
                        System.out.println("Area removed successfully.");
                        break;
                    }
                    case 10: {
                        String areaId = readString(scanner, "Enter area id:");
                        String areaName = readString(scanner, "Enter area name:");

                        farmService.setFarmAreaName(areaId, areaName);
                        System.out.println("Name set successfully.");
                        break;
                    }
                    case 11: {
                        var warehouseItems = warehouseService.getWarehouseInfo();
                        if (warehouseItems.isEmpty()) {
                            System.out.println("Warehouse is empty.");
                        } else {
                            System.out.println("Plant Name, Count");
                            for (WarehouseInfo item : warehouseItems) {
                                System.out.println(item.plantName() + ", " + item.count());
                            }
                        }
                        break;
                    }
                    case 12: {
                        var workers = workerService.getWorkers();
                        if (workers.isEmpty()) {
                            System.out.println("Workers list is empty.");
                        } else {
                            System.out.println("Worker ID, Profile, Farm Area");
                            for (WorkerInfo worker : workers) {
                                System.out.println(worker.id() + ", " + worker.profile() + ", " + worker.farmArea());
                            }
                        }
                        break;
                    }
                    case 13: {
                        WorkerProfileType type = readWorkerProfileType(scanner);
                        workerService.createWorker(type);
                        System.out.println("Worker created successfully.");
                        break;
                    }
                    case 14: {
                        String workerId = readString(scanner, "Enter worker id:");
                        String farmAreaId = readString(scanner, "Enter farm area id:");

                        workerService.assignWorker(workerId, farmAreaId);
                        System.out.println("Worker assigned successfully.");
                        break;
                    }
                    case 15: {
                        String workerId = readString(scanner, "Enter worker id:");

                        workerService.deleteWorker(workerId);
                        System.out.println("Worker deleted successfully.");
                        break;
                    }
                    case 16:
                        running = false;
                        break;
                    default:
                        System.out.println("Unknown option.");
                }
            } catch (FarmException | WarehouseException e) {
                System.out.println("Farm logic error: " + e.getMessage());
            } catch (RuntimeException e) {
                System.out.println("Unknown system error: " + e);
            }
        }
    }

    private int readInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.println(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Error: Please enter a valid number.");
            }
        }
    }

    private String readString(Scanner scanner, String prompt) {
        while (true) {
            System.out.println(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("Error: String cannot be empty. Try again.");
        }
    }

    private WorkerProfileType readWorkerProfileType(Scanner scanner) {
        while (true) {
            String rawType = readString(scanner, "Enter worker profile type (HUMAN, ROBOT):");
            try {
                return WorkerProfileType.valueOf(rawType.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Error: Please enter a valid worker profile type.");
            }
        }
    }
}
