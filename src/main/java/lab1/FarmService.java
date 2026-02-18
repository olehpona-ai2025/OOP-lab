package lab1;

import java.util.List;

public interface FarmService {
    List<String> getPlants();
    void buyPlants(int plantIndex, int plantCount);

    void createFarmArea(int area);
    List<FarmAreaInfo> getFarmAreas();
    FarmOpResult plantFarmArea(int areaIndex, int plantIndex);
    FarmOpResult harvestFarmArea(int farmArea);

    void growLoop();
    void turboGrow();

    List<FarmReport> getReport();
}
