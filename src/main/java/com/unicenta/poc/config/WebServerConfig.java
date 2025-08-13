package com.unicenta.poc.config;

import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class to enable both HTTP and HTTPS ports for the embedded Tomcat server.
 * This sets up a custom TomcatServletWebServerFactory with two connectors.
 */
@Configuration
public class WebServerConfig {

    // Inject the HTTP port from application.properties
    @Value("${server.http.port}")
    private int httpPort;

    @Bean
    public ServletWebServerFactory servletContainer() {
        // Create a TomcatServletWebServerFactory instance
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();

        // Add the HTTP connector
        tomcat.addAdditionalTomcatConnectors(createHttpConnector());
        
        return tomcat;
    }

    /**
     * Creates and configures the HTTP connector.
     * This connector will listen on the port defined by `server.http.port`.
     * @return The configured HTTP connector.
     */
    private Connector createHttpConnector() {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setScheme("http");
        connector.setPort(httpPort);
        connector.setSecure(false);
        return connector;
    }
}
