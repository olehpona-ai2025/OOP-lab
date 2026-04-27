package funFarm.core.model;

import funFarm.core.Farm;
import funFarm.core.PlantRegistry;
import funFarm.core.plants.Plant;
import funFarm.core.state.FarmAreaState;
import funFarm.core.state.FarmState;
import funFarm.core.state.FarmStateMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FarmStateMapperTest {
    @Test
    void shouldCreateCorrectFarmState(){
        Farm farmMock = mock(Farm.class);
        when(farmMock.getFarmAreaInfo()).thenReturn(List.of(
                new FarmAreaInfo("1", "Test1", 100, "null", "null"),
                new FarmAreaInfo("2", "Test2", 300, "Potato", "GROWING")
        ));

        FarmState state = FarmStateMapper.getFarmState(farmMock);
        assertThat(state).isNotNull();
        assertThat(state.farmAreaStateList()).isNotNull().isNotEmpty().hasSize(2).containsExactlyInAnyOrder(
                new FarmAreaState("1", "Test1", 100, "null", "null"),
                new FarmAreaState("2", "Test2", 300, "Potato", "GROWING")
        );
    }

    @Test
    void shouldThrowErrorOnDependencyNull() {
        Farm farmMock = mock(Farm.class);
        PlantRegistry registryMock = mock(PlantRegistry.class);
        FarmState stateMock =  new FarmState(List.of());

        assertThatThrownBy(() -> FarmStateMapper.getFarmState(null)).isInstanceOf(NullPointerException.class);

        assertThatThrownBy(() -> FarmStateMapper.setFarmState(null,stateMock, registryMock)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> FarmStateMapper.setFarmState(farmMock,null, registryMock)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> FarmStateMapper.setFarmState(farmMock,stateMock, null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldCorrectlySetFarmState() {
        Farm farmMock = mock(Farm.class);
        PlantRegistry registryMock = mock(PlantRegistry.class);
        Plant potatoMock = mock(Plant.class);
        Plant tomatoMock = mock(Plant.class);

        FarmState state = new FarmState(List.of(
                new FarmAreaState("1", "Test1", 100, "Potato", "GROWING"),
                new FarmAreaState("2", "Test2", 500, "Tomato", "GROWING")
        ));

        when(registryMock.createWithState(eq("Potato"), any())).thenReturn(potatoMock);
        when(registryMock.createWithState(eq("Tomato"), any())).thenReturn(tomatoMock);

        FarmStateMapper.setFarmState(farmMock, state, registryMock);

        verify(farmMock).loadNewArea(100, "1");
        verify(farmMock).loadNewArea(500, "2");

        verify(farmMock).setFarmAreaName("1", "Test1");
        verify(farmMock).setFarmAreaName("2", "Test2");

        verify(farmMock).plantArea(eq("1"), eq(potatoMock));
        verify(farmMock).plantArea(eq("2"), eq(tomatoMock));

        verifyNoMoreInteractions(farmMock);
    }
    @Test
    void shouldGetFarmAreaState() {
        FarmAreaInfo info = new FarmAreaInfo("1", "Test1", 100, "Potato", "GROWING");
        FarmAreaState state = FarmStateMapper.getFarmAreaState(info);
        
        assertThat(state).isNotNull();
        assertThat(state.id()).isEqualTo("1");
        assertThat(state.name()).isEqualTo("Test1");
        assertThat(state.area()).isEqualTo(100);
        assertThat(state.plantName()).isEqualTo("Potato");
        assertThat(state.plantState()).isEqualTo("GROWING");
    }

    @Test
    void shouldCorrectlySetFarmStateWithNullPlant() {
        Farm farmMock = mock(Farm.class);
        PlantRegistry registryMock = mock(PlantRegistry.class);

        FarmState state = new FarmState(List.of(
                new FarmAreaState("1", "Test1", 100, null, null)
        ));

        FarmStateMapper.setFarmState(farmMock, state, registryMock);

        verify(farmMock).loadNewArea(100, "1");
        verify(farmMock).setFarmAreaName("1", "Test1");
        verifyNoInteractions(registryMock);
    }
}
