package com.hartzman.disneyinterview.service

import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.hartzman.disneyinterview.controller.typeReference
import com.hartzman.disneyinterview.model.*
import lombok.extern.slf4j.Slf4j
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.server.ResponseStatusException
import java.math.BigDecimal
import java.math.RoundingMode

@Slf4j
@Service
class WeatherService {
    private val log: Logger = LoggerFactory.getLogger(WeatherService::class.java)

    @Autowired
    lateinit var webClient: WebClient

    suspend fun getCurrentDayForecast(office: String, gridX: Int, gridY: Int): Forecast {
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
                }) { throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR) }
                .onStatus({ responseStatus ->
                    responseStatus == HttpStatus.BAD_REQUEST
                }) { throw ResponseStatusException(HttpStatus.BAD_REQUEST) }
                .awaitBody<String>()
            log.info("Successfully got a response")

        val responseObj = convertStringToResponse(responseBody)
        val dayName = findDayName(responseObj)
        val temperature = responseObj.properties.periods[0].temperature
        val celsius = fahrenheitToCelsius(temperature)
        val blurp = responseObj.properties.periods[0].shortForecast
        val forecastMap = mapOf("day_name" to dayName, "temp_high_celsius" to celsius, "forecast_blurp" to blurp)
        val forecastList = listOf(forecastMap)
        return Forecast(forecastList)
    }

    fun findDayName(response: Response) : String {
        var currentDay = response.properties.periods[0].name
        val isDayOfWeek = DaysOfWeek.contains(currentDay)
        if (!isDayOfWeek) { // get next day's ordinal
            currentDay = DaysOfWeek.previousDay(response.properties.periods[1].name)
        }
        return currentDay
    }
    fun fahrenheitToCelsius(fahrenheit: Int) : BigDecimal {
      return ((fahrenheit - 32.0) * 5/9).toBigDecimal().setScale(1, RoundingMode.HALF_EVEN)
    }

    fun convertStringToResponse(stringBody: String) : Response {
        val mapper = jacksonObjectMapper()
        val module = SimpleModule()
        module.addDeserializer(Properties::class.java, Deserializers.PropertiesDeserializer)
        module.addDeserializer(Period::class.java, Deserializers.PeriodDeserializer)
        module.addDeserializer(Context::class.java, Deserializers.ContextDeserializer)
        mapper.registerModule(module)

        return mapper.readValue<Response>(stringBody, typeReference())

    }
}