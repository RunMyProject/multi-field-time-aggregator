/**
 * GlobalCorsConfig.java
 * Author: Edoardo Sabatini
 * Date: 2026-05-20
 * Description: Servlet-level CORS filter configuration for the Spring backend.
 *
 * WHY THIS APPROACH:
 *   The previous WebMvcConfigurer.addCorsMappings() approach registers CORS handling
 *   as a Spring MVC HandlerInterceptor. The custom API-version dispatcher runs before
 *   MVC interceptors and returns HTTP 400 on OPTIONS preflight requests (no matching
 *   versioned handler for OPTIONS), so CORS headers are never written and the browser
 *   blocks the actual GET request.
 *
 *   A CorsFilter @Bean with @Order(Ordered.HIGHEST_PRECEDENCE) runs at the Servlet
 *   filter level — always before Spring MVC processes any request — so OPTIONS
 *   preflights receive a proper 200 + CORS headers and the version dispatcher
 *   never sees them.
 *
 * WHAT TRIGGERS A PREFLIGHT:
 *   Any request that carries a non-simple header (e.g. X-API-Version) causes the
 *   browser to issue an OPTIONS preflight before the real GET. This config explicitly
 *   allows that header so the preflight succeeds and the versioned GET follows.
 */

package com.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class GlobalCorsConfig {

    /**
     * Registers a CorsFilter at the highest possible precedence so that every
     * OPTIONS preflight is handled at the servlet-filter stage, well before
     * Spring MVC dispatch (and the custom API-version handler) can interfere.
     *
     * @return a fully configured CorsFilter bean
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public CorsFilter corsFilter() {

        CorsConfiguration config = new CorsConfiguration();

        // Allow credentials (cookies, Authorization headers) if needed in the future.
        // When allowCredentials is true, the origin must be explicit — NOT "*".
        config.setAllowCredentials(true);

        // Explicit Angular dev-server origin — matches the @CrossOrigin on the controller.
        config.addAllowedOrigin("http://localhost:4200");

        // Allow any request header, including the custom X-API-Version header that
        // triggers the preflight in the first place.
        config.addAllowedHeader("*");

        // Allow all HTTP methods so the same config covers future POST/PUT endpoints.
        config.addAllowedMethod("*");

        // Expose the version header in responses so frontend code can read it if needed.
        config.addExposedHeader("X-API-Version");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
