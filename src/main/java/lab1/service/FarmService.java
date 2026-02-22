package lab1.service;

import lab1.core.model.FarmAreaInfo;
import lab1.core.model.FarmReport;
import lab1.infrastructure.ui.DisplayableResult;

import java.util.List;

public interface FarmService {
    List<String> getPlants();
    void buyPlants(String plantName, int plantCount);

    void createFarmArea(int area);
    void removeFarmArea(String id);
    void setFarmAreaName(String id, String name);
    List<FarmAreaInfo> getFarmAreas();
    DisplayableResult plantFarmArea(String areaId, String plantName);
    DisplayableResult harvestFarmArea(String farmArea);

    void growLoop();
    void turboGrow();

    List<FarmReport> getReport();

    void saveData();
}
