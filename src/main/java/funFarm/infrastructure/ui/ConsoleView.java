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

    @Override
    public void run() {
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
                    10. Remove farm area
                    11. Set farm area name
                    12. Save farmState
                    13. Exit
                    """);

                int mode = scanner.nextInt();
                scanner.nextLine();

                switch (mode) {
                    case 1: {
                        System.out.println("Enter area (m^2)");
                        int area = scanner.nextInt();
                        scanner.nextLine();
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
                        System.out.println("Enter plant name");
                        String name = scanner.nextLine();
                        System.out.println("Enter plant count");
                        int count = scanner.nextInt();
                        scanner.nextLine();
                        try {
                            service.buyPlants(name, count);
                        } catch (RuntimeException e) {
                            System.out.println("Exception received, msg: " + e.getMessage());
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
                        System.out.println("Enter area id");
                        String areaId = scanner.nextLine();
                        System.out.println("Enter plant name");
                        String plantName = scanner.nextLine();

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
                        System.out.println("Enter area index");
                        String areaId = scanner.nextLine();
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
                        System.out.println("Enter area id");
                        String areaId = scanner.nextLine();
                        try {
                            service.removeFarmArea(areaId);
                        } catch (RuntimeException e) {
                            System.out.println("Exception received, msg: " + e.getMessage());
                            break;
                        }
                        break;
                    }
                    case 11: {
                        System.out.println("Enter area id");
                        String areaId = scanner.nextLine();
                        System.out.println("Enter area name");
                        String areaName = scanner.nextLine();
                        try {
                            service.setFarmAreaName(areaId, areaName);
                        } catch (RuntimeException e) {
                            System.out.println("Exception received, msg: " + e.getMessage());
                            break;
                        }
                        break;
                    }
                    case 12:
                        service.saveData();
                        break;
                    case 13:
                        return;
                }
            } catch (RuntimeException e) {
                System.out.println("Runtime error, try again " + e );
                scanner.nextLine();
            }

        }
    }
}
