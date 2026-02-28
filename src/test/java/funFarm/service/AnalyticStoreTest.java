package funFarm.service;

import funFarm.core.YieldCalculator;
import funFarm.core.model.FarmEvent;
import funFarm.core.model.FarmReport;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public abstract class AnalyticStoreTest {
    protected abstract AnalyticStore getAnalyticStore();

    @Test
    void shouldAggregateDataIntoValidReport() {
        AnalyticStore store = getAnalyticStore();
        Reporter reporter = store.createrReporter();

        store.pushEvent(FarmEvent.planted("Test", 100));
        store.pushEvent(FarmEvent.harvested("Test", 500));

        int percentage1 = YieldCalculator.calculatePercentage(100, 500);

        store.pushEvent(FarmEvent.harvested("Test2", 100));
        store.pushEvent(FarmEvent.planted("Test2", 500));

        int percentage2 = YieldCalculator.calculatePercentage(500, 100);

        assertThat(reporter.getReport())
                .isNotNull()
                .isNotEmpty()
                .hasSize(2)
                .containsExactlyInAnyOrder(
                        new FarmReport("Test", percentage1),
                        new FarmReport("Test2", percentage2));
    }
}
