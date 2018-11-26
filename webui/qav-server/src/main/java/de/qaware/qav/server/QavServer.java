package de.qaware.qav.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Start the QAvalidator WebApplication server.
 *
 * @author QAware GmbH
 */
@SpringBootApplication
public class QavServer {

    /**
     * Entry point.
     *
     * @param args command line
     */
    public static void main(String[] args) {
        SpringApplication.run(QavServer.class, args);
    }

    /**
     * @return the CORS configuration
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**");
            }
        };
    }

}
