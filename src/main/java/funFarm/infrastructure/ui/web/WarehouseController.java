package funFarm.infrastructure.ui.web;

import funFarm.core.model.FarmReport;
import funFarm.core.model.WarehouseInfo;
import funFarm.service.FarmService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class WarehouseController {
    private final FarmService service;
    public WarehouseController(FarmService service) {
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
}
