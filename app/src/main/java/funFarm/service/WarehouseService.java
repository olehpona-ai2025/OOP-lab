package funFarm.service;

import funFarm.core.model.FarmReport;
import funFarm.core.model.WarehouseInfo;

import java.util.List;

public interface WarehouseService {
    boolean buyPlants(String plantName, int plantCount);
    List<FarmReport> getReport();
    List<WarehouseInfo> getWarehouseInfo();
}
