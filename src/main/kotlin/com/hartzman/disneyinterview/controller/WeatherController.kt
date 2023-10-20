package com.hartzman.disneyinterview.controller

import com.fasterxml.jackson.core.type.TypeReference
import com.hartzman.disneyinterview.model.*
import com.hartzman.disneyinterview.service.WeatherService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController


inline fun <reified T> typeReference() = object : TypeReference<T>() {}


@RestController
class WeatherController  {
    @Autowired
    lateinit var weatherService: WeatherService

    @GetMapping("/forecast/{office}/{gridX},{gridY}")
    suspend fun currentDayForecast(@PathVariable office: String,
                           @PathVariable gridX: Int,
                           @PathVariable gridY: Int): Forecast {
        return weatherService.getCurrentDayForecast(office, gridX, gridY)
    }

}