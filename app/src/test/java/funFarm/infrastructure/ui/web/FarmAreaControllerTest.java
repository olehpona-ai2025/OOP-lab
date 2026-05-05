package funFarm.infrastructure.ui.web;

import funFarm.core.model.HarvestResult;
import funFarm.core.model.PlantResult;
import funFarm.service.FarmService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FarmAreaController.class)
class FarmAreaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FarmService farmService;

    @Test
    void shouldCreateFarmArea() throws Exception {
        String jsonBody = "{\"area\": 100}";
        mockMvc.perform(post("/farmArea").contentType(MediaType.APPLICATION_JSON).content(jsonBody))
                .andExpect(status().isOk());

        verify(farmService).createFarmArea(100);
    }

    @Test
    void shouldRetrieveFarmAreas() throws Exception {
        when(farmService.getFarmAreas()).thenReturn(List.of());

        mockMvc.perform(get("/farmArea"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());

        verify(farmService).getFarmAreas();
    }

    @Test
    void shouldDeleteFarmArea() throws Exception {
        mockMvc.perform(delete(("/farmArea/test")))
                .andExpect(status().isOk());
        verify(farmService).removeFarmArea("test");
    }

    @Test
    void shouldUpdateFarmAreaName() throws Exception {
        String body = "{\"name\": \"New name\"}";


        mockMvc.perform(put("/farmArea/test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());

        verify(farmService).setFarmAreaName("test", "New name");
    }

    @Test
    void shouldPlantFarmArea() throws Exception {
        String body = "{\"plantName\": \"plant\"}";

        when(farmService.plantFarmArea(anyString(), anyString())).thenReturn(new PlantResult(true, "test-msg", 10));

        mockMvc.perform(post("/farmArea/plant/test")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.planted").value(10))
                .andExpect(jsonPath("$.msg").value("test-msg"));

        verify(farmService).plantFarmArea("test", "plant");
    }

    @Test
    void shouldHarvestFarmArea() throws Exception {
        when(farmService.harvestFarmArea(anyString())).thenReturn(new HarvestResult(true, "test-msg", 10, "plant"));

        mockMvc.perform(post("/farmArea/harvest/test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.harvested").value(10))
                .andExpect(jsonPath("$.msg").value("test-msg"))
                .andExpect(jsonPath("$.targetPlant").value("plant"));

        verify(farmService).harvestFarmArea("test");
    }

}