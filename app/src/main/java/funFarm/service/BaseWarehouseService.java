package funFarm.service;

import funFarm.core.farm.FarmException;
import funFarm.core.plants.PlantRegistry;
import funFarm.core.warehouse.Warehouse;
import funFarm.core.warehouse.WarehouseException;
import funFarm.core.model.FarmReport;
import funFarm.core.model.WarehouseInfo;
import funFarm.core.plants.Plant;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component("BaseWarehouseService")
public class BaseWarehouseService implements WarehouseService {
    private final Warehouse warehouse;
    private final Reporter reporter;
    private final PlantRegistry registry;

    public BaseWarehouseService(Warehouse warehouse, Reporter reporter, PlantRegistry registry) {
        this.warehouse = warehouse;
        this.reporter = reporter;
        this.registry = registry;
    }

    @Transactional
    @Override
    public boolean buyPlants(String plantName, int plantCount) {
        Plant toBuy = registry.create(plantName);
        if (toBuy == null) throw new FarmException("Plant '" + plantName + "' not found in registry!");
        try {
            warehouse.updatePlantCount(toBuy.getPlantName(), plantCount);
        } catch (WarehouseException e) {
            return false;
        }
        return true;
    }

    @Override
    public List<FarmReport> getReport() {
        return reporter.getReport();
    }

    @Override
    public List<WarehouseInfo> getWarehouseInfo() {
        return warehouse.getInfo();
    }
}
