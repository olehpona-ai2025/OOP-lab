package funFarm.infrastructure.storage;

import funFarm.core.farm.YieldCalculator;
import funFarm.core.model.events.FarmEvent;
import funFarm.core.model.events.HarvestEvent;
import funFarm.core.model.events.PlantEvent;
import funFarm.core.model.FarmReport;
import funFarm.service.AnalyticStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component("RamAnalyticStore")
@Primary
@ConditionalOnProperty(name = "storage.analytic", havingValue = "ram")
public class RamAnalyticStore implements AnalyticStore {
    private final Map<String, ReportMap> aggregatedData = new ConcurrentHashMap<>();

    private static class ReportMap {
        public int harvested = 0;
        public int planted = 0;
    }

    private class Reporter implements funFarm.service.Reporter {
        @Override
        public List<FarmReport> getReport() {
            List<FarmReport> reports = new ArrayList<>();

            aggregatedData.forEach((plantName, stats) -> {
                reports.add(new FarmReport(plantName, YieldCalculator.calculatePercentage(stats.planted, stats.harvested)));
            });

            return reports;
        }
    }

    @Override
    public void pushEvent(FarmEvent event) {
        if (event instanceof PlantEvent(String plantName, int count)) {

            aggregatedData.compute(plantName, (key, value) -> {
                if (value == null) value = new ReportMap();
                value.planted += count;
                return value;
            });

        } else if (event instanceof HarvestEvent(String plantName, int count)) {

            aggregatedData.compute(plantName, (key, value) -> {
                if (value == null) value = new ReportMap();
                value.harvested += count;
                return value;
            });

        }
    }

    @Bean
    @Override
    public funFarm.service.Reporter createrReporter() {
        return new Reporter();
    }
}