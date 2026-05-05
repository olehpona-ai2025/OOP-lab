package funFarm.infrastructure.ui.web;

import funFarm.core.model.*;
import funFarm.service.FarmService;
import funFarm.service.PlantService;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Profile("web")
public class PlantController {
    private final PlantService service;

    PlantController(PlantService service) {
        this.service = service;
    }

    private record PlantInfo(String plantName, int plantingCost, int baseYield){};
    @GetMapping("/plants")
    public List<PlantInfo> getPlants() {
        return service.getPlants().stream().map(plant -> new PlantInfo(plant.getPlantName(), plant.getPlantingCost(), plant.getBaseYield())).toList();
    }
}
