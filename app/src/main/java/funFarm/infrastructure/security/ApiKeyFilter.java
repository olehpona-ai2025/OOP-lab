package funFarm.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ApiKeyFilter extends OncePerRequestFilter {

    public static final String HEADER = "X-API-KEY";

    private final Map<String, String> keyStore;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    private final List<String> excludedPaths = Arrays.asList(
            "/actuator/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html"
    );

    public ApiKeyFilter(@Value("${app.api.secrets:}") String secretsConfig) {
        this.keyStore = Arrays.stream(secretsConfig.split(","))
                .filter(s -> s.contains("="))
                .map(s -> s.split("="))
                .collect(Collectors.toMap(arr -> arr[0].trim(), arr -> arr[1].trim()));
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return excludedPaths.stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String apiKey = request.getHeader(HEADER);

        if (apiKey != null) {
            String actorName = keyStore.get(apiKey);

            if (actorName != null) {
                ActorContext.set(actorName);
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setHeader("Content-Type", "application/json");
                response.getWriter().write("{\"error\": \"Unauthorized\"}");
                return;
            }
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            ActorContext.clear();
        }
    }
}