package funFarm.infrastructure.ui.web;

import funFarm.core.model.*;
import funFarm.service.FarmService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PlantController {
    private final FarmService service;

    PlantController(FarmService service) {
        this.service = service;
    }

    private record PlantInfo(String plantName, int plantingCost, int baseYield){};
    @GetMapping("/plants")
    public List<PlantInfo> getPlants() {
        return service.getPlants().stream().map(plant -> new PlantInfo(plant.getPlantName(), plant.getPlantingCost(), plant.getBaseYield())).toList();
    }

    private record PlantBuyRequest(String plantName, int count){};
    private record PlantBuyResponse(boolean success){};
    @PostMapping("/plants")
    public PlantBuyResponse buyPlant(@RequestBody PlantBuyRequest body) {
        return new PlantBuyResponse(service.buyPlants(body.plantName(), body.count()));
    }
}
