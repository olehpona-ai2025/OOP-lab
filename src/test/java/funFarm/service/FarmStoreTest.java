package funFarm.service;

import funFarm.core.model.FarmAreaInfo;
import funFarm.core.state.FarmState;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

public abstract class FarmStoreTest {
    protected abstract FarmStore getFarmStore();

    @Test
    void shouldPersistDataAfterSave() {
        FarmStore store = getFarmStore();

        FarmState state = new FarmState(List.of(
                new FarmAreaInfo("1", "Test1", 100, "null", "null"),
                new FarmAreaInfo("2", "Test2", 300, "Potato", "GROWING")
        ));

        store.saveFarmState(state);
        FarmState loaded = store.loadFarmState();

        assertThat(loaded).isNotNull();
        assertThat(loaded.farmAreaStateList())
                .isNotNull()
                .isNotEmpty()
                .hasSize(2)
                .containsAll(state.farmAreaStateList());
    }

    @Test
    void shouldThrowErrorWhenStateNull() {
        FarmStore store = getFarmStore();

        assertThatThrownBy(() -> store.saveFarmState(null)).isInstanceOf(FarmStoreException.class);
    }
}
