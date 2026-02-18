package lab1;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class RamAnalyticStore implements AnalyticStore {
    private final List<FarmEvent> eventPool = new ArrayList<>();

    private class Reporter implements lab1.Reporter {
        private static class ReportMap {
            public int harvested;
            public int planted;

            ReportMap(int harvested, int planted) {
                this.harvested = harvested;
                this.planted = planted;
            }

            public int getPercentage() {
                return harvested > planted? harvested/planted: - planted/harvested;
            }
        }

        @Override
        public List<FarmReport> getReport() {
            HashMap<String, ReportMap> map = new HashMap<>();

            for (FarmEvent event: eventPool) {
                map.compute(event.targetPlant, (key, value) -> {
                    if (value == null) {
                        return event.eventType == FarmEventType.Harvested
                                ? new ReportMap(event.count, 1)
                                : new ReportMap(1, event.count);
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
                reports.add(new FarmReport(k, v.getPercentage()));
            });

            return reports;
        }
    }

    @Override
    public void pushEvent(FarmEvent event) {
        eventPool.add(event);
    }

    @Override
    public lab1.Reporter createrReporter() {
        return new Reporter();
    };
}
