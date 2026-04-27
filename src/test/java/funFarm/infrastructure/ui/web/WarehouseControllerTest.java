package funFarm.infrastructure.ui.web;

import funFarm.core.model.FarmReport;
import funFarm.core.model.WarehouseInfo;
import funFarm.service.FarmService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WarehouseController.class)
class WarehouseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FarmService farmService;

    @Test
    void getAnalytics() throws Exception {
        FarmReport report = new FarmReport("Wheat", 120);
        when(farmService.getReport()).thenReturn(List.of(report));

        mockMvc.perform(get("/analytics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].plantName").value("Wheat"))
                .andExpect(jsonPath("$[0].increasePercentage").value(120));

        verify(farmService).getReport();
    }

    @Test
    void getWarehouseInfo() throws Exception {
        WarehouseInfo info = new WarehouseInfo("Wheat", 50);
        when(farmService.getWarehouseInfo()).thenReturn(List.of(info));

        mockMvc.perform(get("/warehouse"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].plantName").value("Wheat"))
                .andExpect(jsonPath("$[0].count").value(50));

        verify(farmService).getWarehouseInfo();
    }
}
