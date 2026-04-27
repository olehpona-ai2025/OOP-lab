package funFarm.infrastructure.ui.web;

import funFarm.core.model.FarmAreaInfo;
import funFarm.core.model.HarvestResult;
import funFarm.core.model.PlantResult;
import funFarm.service.FarmService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class FarmAreaController {
    private final FarmService service;
    public FarmAreaController(FarmService service) {
        this.service = service;
    }

    private record CreateFarmAreaBody(int area){};
    @PostMapping("/farmArea")
    public void createFarmArea(@RequestBody CreateFarmAreaBody body) {
        service.createFarmArea(body.area());
    }

    @GetMapping("/farmArea")
    public List<FarmAreaInfo> getFarmAreas() {
        return service.getFarmAreas();
    }

    @DeleteMapping("/farmArea/{id}")
    public void removeFarmArea(@PathVariable String id) {
        service.removeFarmArea(id);
    }

    private record UpdateFarmAreaBody(String name){};
    @PutMapping("/farmArea/{id}")
    public void updateFarmArea(@PathVariable String id, @RequestBody UpdateFarmAreaBody body) {
        service.setFarmAreaName(id, body.name());
    }

    private record PlantFarmAreaBody(String plantName){};
    @PostMapping("/farmArea/plant/{id}")
    public PlantResult plantFarmArea(@PathVariable String id, @RequestBody PlantFarmAreaBody body) {
        return service.plantFarmArea(id, body.plantName());
    }

    @PostMapping("/farmArea/harvest/{id}")
    public HarvestResult harvestFarmArea(@PathVariable String id) {
        return service.harvestFarmArea(id);
    }

    @PostMapping("/growLoop")
    public void growLoop() {
        service.growLoop();
    }

    @PostMapping("/turboGrow")
    public void turboGrow() {
        service.turboGrow();
    }
}
