package funFarm.infrastructure.ui.web;

import funFarm.core.model.FarmReport;
import funFarm.core.model.WarehouseInfo;
import funFarm.service.WarehouseService;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Profile("web")
public class WarehouseController {
    private final WarehouseService service;
    public WarehouseController(WarehouseService service) {
        this.service = service;
    }

    @GetMapping("/analytics")
    public List<FarmReport> getAnalytics() {
        return service.getReport();
    }

    @GetMapping("/warehouse")
    public List<WarehouseInfo> getWarehouseInfo() {
        return service.getWarehouseInfo();
    }

    private record PlantBuyRequest(String plantName, int count){};
    private record PlantBuyResponse(boolean success){};
    @PostMapping("/warehouse")
    public PlantBuyResponse buyPlant(@RequestBody PlantBuyRequest body) {
        return new PlantBuyResponse(service.buyPlants(body.plantName(), body.count()));
    }
}
