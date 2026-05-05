package funFarm.service;

import funFarm.core.farm.Farm;
import funFarm.core.farm.FarmException;
import funFarm.core.farm.FarmStore;
import funFarm.core.model.*;
import funFarm.core.model.events.FarmAreaDeletedEvent;
import funFarm.core.model.events.HarvestEvent;
import funFarm.core.model.events.PlantEvent;
import funFarm.core.plants.Plant;
import funFarm.core.plants.PlantRegistry;
import funFarm.core.warehouse.Warehouse;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component("BaseFarmService")
public class BaseFarmService implements FarmService {
    private final PlantRegistry registry;
    private final Warehouse warehouse;
    private final EventNotifier notifier;
    private final Farm farm;
    private final FarmStore farmStore;

    public BaseFarmService(PlantRegistry registry, Warehouse warehouse, EventNotifier notifier, Farm farm, FarmStore farmStore) {
        this.registry = registry;
        this.warehouse = warehouse;
        this.notifier = notifier;
        this.farm = farm;
        this.farmStore = farmStore;
    }

    @Override
    public void createFarmArea(int area) {
        farm.addNewArea(area);
    }

    @Override
    @Transactional
    public void removeFarmArea(String area) {
        farm.removeArea(area);
        notifier.notifyListeners(new FarmAreaDeletedEvent(area));
    }

    @Override
    public void setFarmAreaName(String id, String name) {
        farm.setFarmAreaName(id, name);
    }

    @Override
    public List<FarmAreaInfo> getFarmAreas() {
        return farm.getFarmAreaInfo();
    }

    @Override
    @Transactional
    public PlantResult plantFarmArea(String areaId, String plantName) {
        Plant toPlant = registry.create(plantName);
        if (toPlant == null) return new PlantResult(false, "Plant '" + plantName + "' not found in registry!", 0);

        int neededForPlant;
        try {
            neededForPlant = farm.getNeededToPlant(areaId, toPlant);
        } catch (FarmException e) {
            return new PlantResult(false, e.getMessage(), 0);
        }

        if (warehouse.getPlantCount(plantName) < neededForPlant) {
            return new PlantResult(false, "Not enough in warehouse", 0);
        }
        var result = farm.plantArea(areaId, toPlant);
        if (result.success()) {
            warehouse.updatePlantCount(toPlant.getPlantName(), -result.planted());
            notifier.notifyListeners(new PlantEvent(plantName, result.planted()));
        }
        return result;
    }

    @Override
    @Transactional
    public HarvestResult harvestFarmArea(String farmArea) {
        var result = farm.harvestArea(farmArea);
        if (result.success()) {
            warehouse.updatePlantCount(result.targetPlant(), result.harvested());
            notifier.notifyListeners(new HarvestEvent(result.targetPlant(), result.harvested()));
        }
        return result;
    }
}
