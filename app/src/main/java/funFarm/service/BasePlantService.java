package funFarm.service;

import funFarm.core.plants.PlantRegistry;
import funFarm.core.plants.Plant;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("BasePlantService")
public class BasePlantService implements PlantService{
    private final PlantRegistry registry;

    public BasePlantService(PlantRegistry registry) {
        this.registry = registry;
    }

    @Override
    public List<Plant> getPlants() {
        return registry.getPlants();
    }
}
