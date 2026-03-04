package funFarm.service;

import funFarm.core.*;
import funFarm.core.model.*;
import funFarm.core.plants.Plant;
import funFarm.core.state.FarmState;
import funFarm.core.state.FarmStateMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("BaseFarmService")
public class BaseFarmService implements FarmService {
    private final PlantRegistry registry;
    private final Warehouse warehouse;
    private final EventNotifier notifier;
    private final Farm farm;
    private final FarmStore farmStore;
    private final Reporter reporter;

    public BaseFarmService(PlantRegistry registry, Warehouse warehouse, EventNotifier notifier, Farm farm, FarmStore farmStore, Reporter reporter) {
        this.registry = registry;
        this.warehouse = warehouse;
        this.notifier = notifier;
        this.farm = farm;
        this.farmStore = farmStore;
        this.reporter = reporter;
    }
    @Override
    public List<String> getPlants() {
        return registry.getNames();
    }

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
    public void createFarmArea(int area) {
        farm.addNewArea(area);
    }

    @Override
    public void removeFarmArea(String area) {
        farm.removeArea(area);
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
            notifier.notifyListeners(FarmEvent.planted(plantName, result.planted()));
        }
        return result;
    }

    @Override
    public HarvestResult harvestFarmArea(String farmArea) {
        var result = farm.harvestArea(farmArea);
        if (result.success()) {
            warehouse.updatePlantCount(result.targetPlant(), result.harvested());
            notifier.notifyListeners(FarmEvent.harvested(result.targetPlant(), result.harvested()));
        }
        return result;
    }

    @Override
    public void growLoop() {
        farm.areaLoop();
    }

    @Override
    public void turboGrow() {
        farm.customAction(area -> {
            Plant plant = area.getCurrentPlant();
            if (plant == null) return;

            while (plant.getState() != PlantGrowState.GREW) {
                plant.grow();
            }
        });
    }

    @Override
    public List<FarmReport> getReport() {
        return reporter.getReport();
    }

    @Override
    public void saveData() {
        FarmState state = FarmStateMapper.getFarmState(farm);
        farmStore.saveFarmState(state);
    }
}
