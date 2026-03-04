package funFarm.infrastructure.ui;

import funFarm.core.model.FarmAreaInfo;
import funFarm.core.model.FarmReport;
import funFarm.core.model.HarvestResult;
import funFarm.core.model.PlantResult;
import funFarm.service.FarmService;
import funFarm.service.View;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component("ConsoleView")
@Primary
public class ConsoleView implements View {
    private final FarmService service;

    @Autowired
    public ConsoleView(FarmService service) {
        this.service = service;
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
                    8. Grow loop
                    9. Turbo grow
                    10. Remove farm area
                    11. Set farm area name
                    12. Save farmState
                    13. Exit
                    """);

                int mode = readInt(scanner, "Enter menu option:");

                switch (mode) {
                    case 1: {
                        int area = readInt(scanner, "Enter area (m^2):");
                        service.createFarmArea(area);
                        break;
                    }
                    case 2:
                        var plantList = service.getPlants();
                        for (int i = 0; i < plantList.size(); i++) {
                            System.out.println(i + " " + plantList.get(i));
                        }
                        break;
                    case 3: {
                        String name = readString(scanner, "Enter plant name:");
                        int count = readInt(scanner, "Enter plant count:");

                        if (!service.buyPlants(name, count)){
                            System.out.println("Failed buying plants");
                        }

                        break;
                    }
                    case 4:
                        var infos = service.getFarmAreas();
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
                            res = service.plantFarmArea(areaId, plantName);
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
                            res = service.harvestFarmArea(areaId);
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
                        for (FarmReport report: service.getReport()) {
                            System.out.println(report.plantName() + " " + report.increasePercentage() + "%");
                        }
                        break;
                    }
                    case 8:
                        service.growLoop();
                        break;
                    case 9:
                        service.turboGrow();
                        break;
                    case 10: {
                        String areaId = readString(scanner, "Enter area id:");

                        service.removeFarmArea(areaId);
                        System.out.println("Area removed successfully.");
                        break;
                    }
                    case 11: {
                        String areaId = readString(scanner, "Enter area id:");
                        String areaName = readString(scanner, "Enter area name:");

                        service.setFarmAreaName(areaId, areaName);
                        System.out.println("Name set successfully.");
                        break;
                    }
                    case 12:
                        service.saveData();
                        break;
                    case 13:
                        running = false;
                        break;
                    default:
                        System.out.println("Unknown option.");
                }
            } catch (funFarm.core.FarmException | funFarm.core.WarehouseException e) {
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
}
