package com.hartzman.disneyinterview.controller

//import com.hartzman.disneyinterview.model.Coordinate
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.hartzman.disneyinterview.model.*
import com.hartzman.disneyinterview.service.WeatherService
import lombok.extern.slf4j.Slf4j
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono


inline fun <reified T> typeReference() = object : TypeReference<T>() {}

@Slf4j
@RestController
class WeatherController  {
    @Autowired
    lateinit var webClient: WebClient

    @Autowired
    lateinit var weatherService: WeatherService

    private val log: Logger = LoggerFactory.getLogger(WeatherController::class.java)
    @GetMapping("/forecast/{office}/{gridX},{gridY}")
    suspend fun currentDayForecast(@PathVariable office: String,
                           @PathVariable gridX: Int,
                           @PathVariable gridY: Int): String {

        val responseBody =
            webClient.get()
                .uri("/gridpoints/$office/$gridX,$gridY/forecast")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus({ responseStatus ->
                    responseStatus == HttpStatus.NOT_FOUND
                }) { throw ResponseStatusException(HttpStatus.NOT_FOUND) }
                .onStatus({ responseStatus ->
                    responseStatus == HttpStatus.INTERNAL_SERVER_ERROR
                }) { throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR)}
                .awaitBody<String>()
//                .awaitExchange { response -> handleResponse(response) }
//            .exchangeToMono { response -> handleResponse(response) }
//            .doOnError()
//                .log()


        val mapper = jacksonObjectMapper()
        val module = SimpleModule()
        module.addDeserializer(Properties::class.java, Deserializers.PropertiesDeserializer)
        module.addDeserializer(Period::class.java, Deserializers.PeriodDeserializer)
        module.addDeserializer(Context::class.java, Deserializers.ContextDeserializer)
        mapper.registerModule(module)


//        val responseStg: String? = responseBody
        val responeVal = mapper.readValue<Response>(responseBody, typeReference())
//        val monocopy = responseBody
//        val response1 = responseStg?.let { mapper.readValue<Response>(it) }
        return responseBody // as String
    }

    fun handleResponse(response: ClientResponse): Mono<String> {
        if (response.statusCode().is2xxSuccessful()) {
            return response.bodyToMono(String::class.java)
        }
        else if (response.statusCode().is4xxClientError()) {
            // Handle client errors (e.g., 404 Not Found)
            println("error getting response")
            return Mono.error(RuntimeException("Response not found"));
        }
        else if (response.statusCode().is5xxServerError()) {
            // Handle server errors (e.g., 500 Internal Server Error)
            println("server error")
            return Mono.error(RuntimeException("Server error"));
        }
        else {
            // Handle other status codes as needed
            println("unknown error")
            return Mono.error(RuntimeException("Unexpected error"));
        }
    }


}