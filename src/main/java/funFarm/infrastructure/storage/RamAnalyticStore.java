package funFarm.infrastructure.storage;

import funFarm.core.YieldCalculator;
import funFarm.core.model.FarmEvent;
import funFarm.core.model.FarmEventType;
import funFarm.core.model.FarmReport;
import funFarm.service.AnalyticStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

@Component("RamAnalyticStore")
@Primary
public class RamAnalyticStore implements AnalyticStore {
    private final List<FarmEvent> eventPool = new ArrayList<>();

    private class Reporter implements funFarm.service.Reporter {
        private static class ReportMap {
            public int harvested;
            public int planted;

            ReportMap(int harvested, int planted) {
                this.harvested = harvested;
                this.planted = planted;
            }
        }

        @Override
        public List<FarmReport> getReport() {
            HashMap<String, ReportMap> map = new HashMap<>();

            for (FarmEvent event: eventPool) {
                map.compute(event.targetPlant, (key, value) -> {
                    if (value == null) {
                        return event.eventType == FarmEventType.Harvested
                                ? new ReportMap(event.count, 0)
                                : new ReportMap(0, event.count);
                    }

                    switch (event.eventType) {
                        case FarmEventType.Harvested:
                            value.harvested += event.count;
                            break;
                        case FarmEventType.Planted:
                            value.planted += event.count;
                            break;
                    }

                    return value;
                });
            }

            List<FarmReport> reports = new ArrayList<>();
            map.forEach((k,v) -> {
                reports.add(new FarmReport(k, YieldCalculator.calculatePercentage(v.planted, v.harvested)));
            });

            return reports;
        }
    }

    @Override
    public void pushEvent(FarmEvent event) {
        eventPool.add(event);
    }

    @Bean
    @Override
    public funFarm.service.Reporter createrReporter() {
        return new Reporter();
    };
}
