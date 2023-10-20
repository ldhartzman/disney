package com.hartzman.disneyinterview.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient




@Configuration
class Beans {
    val API_BASE_URL = "https://api.weather.gov"
    @Bean
    fun webClient(webClientBuilder: WebClient.Builder): WebClient? {
        return webClientBuilder
            .baseUrl(API_BASE_URL)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build()
    }

}