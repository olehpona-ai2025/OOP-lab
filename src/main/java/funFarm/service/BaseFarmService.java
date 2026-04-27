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
    public List<Plant> getPlants() {
        return registry.getPlants();
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
        String id = farm.addNewArea(area);
        farmStore.updateFarmAreaState(FarmStateMapper.getFarmAreaState(farm.getFarmAreaInfo(id)));
    }

    @Override
    public void removeFarmArea(String area) {
        farm.removeArea(area);
        farmStore.removeFarmArea(area);
    }

    @Override
    public void setFarmAreaName(String id, String name) {
        farm.setFarmAreaName(id, name);
        farmStore.updateFarmAreaState(FarmStateMapper.getFarmAreaState(farm.getFarmAreaInfo(id)));
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
            farmStore.updateFarmAreaState(FarmStateMapper.getFarmAreaState(farm.getFarmAreaInfo(areaId)));
        }
        return result;
    }

    @Override
    public HarvestResult harvestFarmArea(String farmArea) {
        var result = farm.harvestArea(farmArea);
        if (result.success()) {
            warehouse.updatePlantCount(result.targetPlant(), result.harvested());
            notifier.notifyListeners(FarmEvent.harvested(result.targetPlant(), result.harvested()));
            farmStore.updateFarmAreaState(FarmStateMapper.getFarmAreaState(farm.getFarmAreaInfo(farmArea)));
        }
        return result;
    }

    @Override
    public void growLoop() {
        farm.areaLoop();
        saveData();
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
        saveData();
    }

    @Override
    public List<FarmReport> getReport() {
        return reporter.getReport();
    }

    @Override
    public List<WarehouseInfo> getWarehouseInfo() {
        return warehouse.getInfo();
    }

    private void saveData() {
        FarmState state = FarmStateMapper.getFarmState(farm);
        farmStore.saveFarmState(state);
    }

    @Override
    public void loadData() {
        FarmStateMapper.setFarmState(farm, farmStore.loadFarmState(), registry);
    }
}
