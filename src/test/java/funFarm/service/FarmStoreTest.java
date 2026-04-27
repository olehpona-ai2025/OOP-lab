package funFarm.service;

import funFarm.core.state.FarmAreaState;
import funFarm.core.state.FarmState;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.UUID;

public abstract class FarmStoreTest {
    protected abstract FarmStore getFarmStore();

    private static final String ID_1 = UUID.randomUUID().toString();
    private static final String ID_2 = UUID.randomUUID().toString();

    @Test
    void shouldPersistDataAfterSave() {
        FarmStore store = getFarmStore();

        FarmState state = new FarmState(List.of(
                new FarmAreaState(ID_1, "Test1", 100, "null", "null"),
                new FarmAreaState(ID_2, "Test2", 300, "Potato", "GROWING")
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

    @Test
    void shouldUpdateFarmAreaState() {
        FarmStore store = getFarmStore();
        FarmState state = new FarmState(List.of(
                new FarmAreaState(ID_1, "Test1", 100, "null", "null")
        ));
        store.saveFarmState(state);

        FarmAreaState updatedInfo = new FarmAreaState(ID_1, "UpdatedTest1", 200, "Potato", "GROWING");
        store.updateFarmAreaState(updatedInfo);

        FarmState loaded = store.loadFarmState();
        assertThat(loaded.farmAreaStateList())
                .hasSize(1)
                .contains(updatedInfo);
    }

    @Test
    void shouldRemoveFarmArea() {
        FarmStore store = getFarmStore();
        FarmState state = new FarmState(List.of(
                new FarmAreaState(ID_1, "Test1", 100, "null", "null"),
                new FarmAreaState(ID_2, "Test2", 300, "Potato", "GROWING")
        ));
        store.saveFarmState(state);

        store.removeFarmArea(ID_1);

        FarmState loaded = store.loadFarmState();
        assertThat(loaded.farmAreaStateList())
                .hasSize(1)
                .contains(new FarmAreaState(ID_2, "Test2", 300, "Potato", "GROWING"));
    }

    @Test
    void shouldThrowErrorWhenUpdateStateNull() {
        FarmStore store = getFarmStore();
        assertThatThrownBy(() -> store.updateFarmAreaState(null)).isInstanceOf(FarmStoreException.class);
    }

    @Test
    void shouldThrowErrorWhenRemoveIdNull() {
        FarmStore store = getFarmStore();
        assertThatThrownBy(() -> store.removeFarmArea(null)).isInstanceOf(FarmStoreException.class);
    }
}
