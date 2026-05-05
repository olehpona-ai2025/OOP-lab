package funFarm.infrastructure.ui.web;

import funFarm.core.model.WorkerInfo;
import funFarm.core.workers.profiles.WorkerProfileType;
import funFarm.service.WorkerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WorkerController.class)
class WorkerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WorkerService workerService;

    @Test
    void shouldCreateWorker() throws Exception {
        String body = "{\"profile\": \"HUMAN\"}";

        mockMvc.perform(post("/workers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());

        verify(workerService).createWorker(WorkerProfileType.HUMAN);
    }

    @Test
    void shouldGetWorkers() throws Exception {
        when(workerService.getWorkers()).thenReturn(List.of(new WorkerInfo("worker-1", "HUMAN", "area-1")));

        mockMvc.perform(get("/workers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value("worker-1"))
                .andExpect(jsonPath("$[0].profile").value("HUMAN"))
                .andExpect(jsonPath("$[0].farmArea").value("area-1"));

        verify(workerService).getWorkers();
    }

    @Test
    void shouldAssignWorker() throws Exception {
        String body = "{\"farmAreaId\": \"area-1\"}";

        mockMvc.perform(post("/workers/assign/test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());

        verify(workerService).assignWorker("test", "area-1");
    }

    @Test
    void shouldDeleteWorker() throws Exception {
        mockMvc.perform(delete("/workers/test"))
                .andExpect(status().isOk());

        verify(workerService).deleteWorker("test");
    }

    @Test
    void shouldRunWorkerLoop() throws Exception {
        mockMvc.perform(post("/workers/loop"))
                .andExpect(status().isOk());

        verify(workerService).workerLoop();
    }
}
