package funFarm.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class ApiKeyFilterTest {

    @AfterEach
    void tearDown() {
        ActorContext.clear();
    }

    @Test
    void whenNoHeader_thenProceedAndActorNotSet() throws ServletException, IOException {
        ApiKeyFilter filter = new ApiKeyFilter("");

        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        doAnswer(invocation -> {
            assertThat(ActorContext.get()).isNull();
            return null;
        }).when(chain).doFilter(req, resp);

        filter.doFilterInternal(req, resp, chain);

        verify(chain, times(1)).doFilter(req, resp);
        assertThat(ActorContext.get()).isNull();
    }

    @Test
    void whenValidKey_thenSetActorDuringChainAndClearedAfter() throws ServletException, IOException {
        ApiKeyFilter filter = new ApiKeyFilter("secretKey=alice");

        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getHeader(ApiKeyFilter.HEADER)).thenReturn("secretKey");

        HttpServletResponse resp = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        doAnswer(invocation -> {
            assertThat(ActorContext.get()).isEqualTo("alice");
            return null;
        }).when(chain).doFilter(req, resp);

        filter.doFilterInternal(req, resp, chain);

        verify(chain, times(1)).doFilter(req, resp);
        assertThat(ActorContext.get()).isNull();
    }

    @Test
    void whenInvalidKey_thenReturn401AndNoChain() throws ServletException, IOException {
        ApiKeyFilter filter = new ApiKeyFilter("secretKey=alice");

        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getHeader(ApiKeyFilter.HEADER)).thenReturn("badKey");

        HttpServletResponse resp = mock(HttpServletResponse.class);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        when(resp.getWriter()).thenReturn(pw);

        FilterChain chain = mock(FilterChain.class);

        filter.doFilterInternal(req, resp, chain);

        verify(resp).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(resp).setHeader("Content-Type", "application/json");
        pw.flush();
        assertThat(sw.toString()).contains("Unauthorized");
        verify(chain, never()).doFilter(any(), any());
        assertThat(ActorContext.get()).isNull();
    }
}
