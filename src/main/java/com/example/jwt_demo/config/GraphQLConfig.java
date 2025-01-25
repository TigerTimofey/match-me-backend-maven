package com.example.jwt_demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;
import org.springframework.graphql.server.WebGraphQlInterceptor;
import org.springframework.graphql.server.webmvc.GraphQlHttpHandler;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

@Configuration
public class GraphQLConfig implements WebMvcConfigurer {
    
    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return wiringBuilder -> wiringBuilder
            .scalar(graphql.scalars.ExtendedScalars.DateTime)
            .scalar(graphql.scalars.ExtendedScalars.Object);
    }

    @Bean
    public RouterFunction<ServerResponse> graphQLEndpoint(GraphQlHttpHandler graphQlHttpHandler) {
        return RouterFunctions.route()
            .GET("/graphql", request -> ServerResponse.ok().body("Use POST for GraphQL queries"))
            .POST("/graphql", graphQlHttpHandler::handleRequest)
            .build();
    }

    @Bean
    public WebGraphQlInterceptor explorerInterceptor() {
        return (webInput, interceptorChain) -> {
            if (webInput.getHeaders().getFirst("Apollo-Require-Preflight") != null) {
                webInput.configureExecutionInput((executionInput, builder) ->
                    builder.graphQLContext(builder1 -> builder1.of(executionInput.getGraphQLContext()))
                        .build());
            }
            return interceptorChain.next(webInput);
        };
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer
            .defaultContentType(MediaType.APPLICATION_JSON)
            .mediaType("json", MediaType.APPLICATION_JSON);
    }
} 