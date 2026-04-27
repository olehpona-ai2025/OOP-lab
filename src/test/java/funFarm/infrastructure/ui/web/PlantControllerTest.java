package funFarm.infrastructure.ui.web;

import funFarm.core.plants.Plant;
import funFarm.service.FarmService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PlantController.class)
class PlantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FarmService farmService;

    @Test
    void getPlants() throws Exception {
        Plant mockPlant = mock(Plant.class);
        when(mockPlant.getPlantName()).thenReturn("Wheat");
        when(mockPlant.getPlantingCost()).thenReturn(10);
        when(mockPlant.getBaseYield()).thenReturn(20);

        when(farmService.getPlants()).thenReturn(List.of(mockPlant));

        mockMvc.perform(get("/plants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].plantName").value("Wheat"))
                .andExpect(jsonPath("$[0].plantingCost").value(10))
                .andExpect(jsonPath("$[0].baseYield").value(20));

        verify(farmService).getPlants();
    }

    @Test
    void buyPlantSuccess() throws Exception {
        when(farmService.buyPlants("Wheat", 5)).thenReturn(true);

        String jsonRequest = "{\"plantName\":\"Wheat\",\"count\":5}";

        mockMvc.perform(post("/plants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(farmService).buyPlants("Wheat", 5);
    }

    @Test
    void buyPlantFailure() throws Exception {
        when(farmService.buyPlants("Unknown", 10)).thenReturn(false);

        String jsonRequest = "{\"plantName\":\"Unknown\",\"count\":10}";

        mockMvc.perform(post("/plants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));

        verify(farmService).buyPlants("Unknown", 10);
    }
}
