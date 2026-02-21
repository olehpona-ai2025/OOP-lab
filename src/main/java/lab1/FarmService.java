package lab1;

import java.util.List;

public interface FarmService {
    List<String> getPlants();
    void buyPlants(int plantIndex, int plantCount);

    void createFarmArea(int area);
    void removeFarmArea(String id);
    void setFarmAreaName(String id, String name);
    List<FarmAreaInfo> getFarmAreas();
    FarmOpResult plantFarmArea(String areaId, int plantIndex);
    FarmOpResult harvestFarmArea(String farmArea);

    void growLoop();
    void turboGrow();

    List<FarmReport> getReport();
}
