package funFarm.service;

import funFarm.core.model.FarmEvent;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.*;

public abstract class EventNotifierTest {
    protected abstract EventNotifier getNotifier();

    @Test
    void notifiesRegisteredListeners() {
        EventNotifier notifier = getNotifier();

        final FarmEvent[] receivedEvent = {null, null, null, null};

        for (int i =0; i< 4;i ++) {
            int finalI = i;
            notifier.registerListener(event -> receivedEvent[finalI] = event);
        }

        FarmEvent toNotify = FarmEvent.planted("Test", 0);

        notifier.notifyListeners(toNotify);
        for (int i =0; i< 4;i ++) {
            assertThat(receivedEvent[i]).isNotNull().isEqualTo(toNotify);
        }
    }

    @Test
    void shouldNotifyOnlyOnce() {
        EventNotifier notifier = getNotifier();

        AtomicInteger count = new AtomicInteger();

        notifier.registerListener(event -> count.getAndIncrement());

        FarmEvent toNotify = FarmEvent.planted("Test", 0);
        notifier.notifyListeners(toNotify);
    }
}
