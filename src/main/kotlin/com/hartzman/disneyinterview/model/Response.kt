package com.hartzman.disneyinterview.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

data class Context (@JsonProperty("@context") val context: List<Any>)

data class Geometry (val type: String,
                     val coordinates: List<List<List<Int>>>)
data class Elevation (val unitCode: String, val value: Double)
data class PrecipitationProbability (val unitCode: String, val value: Int?)
data class DewPoint (val unitCode: String, val value: Double)
data class RelativeHumidity (val unitCode: String, val value: Int?)
data class Period (val number: Int,
                   val name: String,
                   val startTime: String,
                   val endTime: String,
                   val isDayTime: Boolean,
                   val temperature: Int,
                   val temperatureUnit: String,
                   val temperatureTrend: String?,
                   val probabilityOfPrecipitation: PrecipitationProbability,
                   val dewPoint: DewPoint,
                   val relativeHumidity: RelativeHumidity,
                   val windSpeed: String,
                   val windDirection: String,
                   val icon: String,
                   val shortForecast: String,
                   val detailedForecast: String)
data class Properties (val updated: String,
                               val units: String,
                               val forecastGenerator: String,
                               val generatedAt: String,
                               val updateTime: String,
                               val validTimes: String,
                               val elevation: Elevation,
                               @JsonDeserialize(contentUsing = Deserializers.PeriodDeserializer::class)
                               val periods: List<Period>)
data class Response(@JsonDeserialize(using = Deserializers.ContextDeserializer::class)
                    @JsonProperty("@context") val context: Context,
                    val type: String,
                    val geometry: Geometry,
                    @JsonDeserialize(contentUsing = Deserializers.PropertiesDeserializer::class)
                    val properties: Properties)

object Deserializers {
    object ContextDeserializer : JsonDeserializer<Context>() {
        override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): Context {
            val node = p?.readValueAsTree<JsonNode>()

            val anyList = ArrayList<Any>()
            val stg = node?.get(0)?.asText()!!
            anyList.add(stg)
            val map = node.get(1)
            val map1: MutableMap<*, *>? = jacksonObjectMapper().convertValue(map, MutableMap::class.java)
            anyList.add(map1 as Any)
            return Context(anyList)
        }

    }

    object PropertiesDeserializer : JsonDeserializer<Properties>() {
        override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): Properties {
            val node = p?.readValueAsTree<JsonNode>()

            val updated = node?.get("updated")?.asText()!!
            val units = node.get("units").asText()!!
            val forecastGenerator = node.get("forecastGenerator").asText()!!
            val generatedAt = node.get("generatedAt").asText()!!
            val updateTime = node.get("updateTime").asText()!!
            val validTimes = node.get("validTimes").asText()!!
            val elevationNode = node.get("elevation")
            val elevation = elevationDeserializer(elevationNode)
            val periodsNode = node.get("periods").toList()
            val periodList = ArrayList<Period>()
            for (periodNode in periodsNode) {
                val period = periodDeserializer(periodNode)
                periodList.add(period)
            }

            return Properties(
                updated,
                units,
                forecastGenerator,
                generatedAt,
                updateTime,
                validTimes,
                elevation,
                periodList
            )
        }

    }

    object PeriodDeserializer : JsonDeserializer<Period>() {
        override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): Period {
            val node = p?.readValueAsTree<JsonNode>()

            val number = node?.get("number")?.asInt()!!
            val name = node.get("name")?.asText()!!
            val startTime = node.get("startTime").asText()!!
            val endTime = node.get("endTime").asText()!!
            val isDayTime = node.get("isDayTime").asBoolean()
            val temperature = node.get("temperature").asInt()
            val temperatureUnit = node.get("temperatureUnit").asText()!!
            val temperatureTrend = node.get("temperatureTrend").asText()
            val probabilityOfPrecipitationNode = node.get("probabilityOfPrecipitation")
            val probabilityOfPrecipitation = precipitationDeserializer(probabilityOfPrecipitationNode)
            val dewPointNode = node.get("dewPoint")
            val dewPoint = dewPointDeserializer(dewPointNode)
            val humidityNode = node.get("relativeHumidity")
            val relativeHumidity = humidityDeserializer(humidityNode)
            val windSpeed = node.get("windSpeed").asText()!!
            val windDirection = node.get("windDirection").asText()!!
            val icon = node.get("icon").asText()!!
            val shortForecast = node.get("shortForecast").asText()!!
            val detailedForecast = node.get("detailedForecast").asText()!!

            return Period(
                number,
                name,
                startTime,
                endTime,
                isDayTime,
                temperature,
                temperatureUnit,
                temperatureTrend,
                probabilityOfPrecipitation,
                dewPoint,
                relativeHumidity,
                windSpeed,
                windDirection,
                icon,
                shortForecast,
                detailedForecast
            )
        }
    }
}
        fun periodDeserializer(jsonNode: JsonNode) : Period {
            val number = jsonNode.get("number")?.asInt()!!
            val name = jsonNode.get("name")?.asText()!!
            val startTime = jsonNode.get("startTime").asText()!!
            val endTime = jsonNode.get("endTime").asText()!!
            val isDayTime = jsonNode.get("isDaytime").asBoolean()
            val temperature = jsonNode.get("temperature").asInt()
            val temperatureUnit = jsonNode.get("temperatureUnit").asText()!!
            val temperatureTrend = jsonNode.get("temperatureTrend").asText()
            val probabilityOfPrecipitationNode = jsonNode.get("probabilityOfPrecipitation")
            val probabilityOfPrecipitation = precipitationDeserializer(probabilityOfPrecipitationNode)
            val dewpointNode = jsonNode.get("dewpoint")
            val dewpoint = dewPointDeserializer(dewpointNode)
            val humidityNode = jsonNode.get("relativeHumidity")
            val relativeHumidity = humidityDeserializer(humidityNode)
            val windSpeed = jsonNode.get("windSpeed").asText()!!
            val windDirection = jsonNode.get("windDirection").asText()!!
            val icon = jsonNode.get("icon").asText()!!
            val shortForecast = jsonNode.get("shortForecast").asText()!!
            val detailedForecast = jsonNode.get("detailedForecast").asText()!!

            return Period(
                number,
                name,
                startTime,
                endTime,
                isDayTime,
                temperature,
                temperatureUnit,
                temperatureTrend,
                probabilityOfPrecipitation,
                dewpoint,
                relativeHumidity,
                windSpeed,
                windDirection,
                icon,
                shortForecast,
                detailedForecast
            )
        }


        fun precipitationDeserializer(jsonNode: JsonNode): PrecipitationProbability {
            val unitCode = jsonNode.get("unitCode").asText()
            val value = jsonNode.get("value").asInt()
            return PrecipitationProbability(unitCode, value)
        }

        fun dewPointDeserializer(jsonNode: JsonNode): DewPoint {
            val unitCode = jsonNode.get("unitCode").asText()
            val value = jsonNode.get("value").asDouble()
            return DewPoint(unitCode, value)
        }

        fun elevationDeserializer(jsonNode: JsonNode): Elevation {
            val unitCode = jsonNode.get("unitCode").asText()
            val value = jsonNode.get("value").asDouble()
            return Elevation(unitCode, value)
        }

        fun humidityDeserializer(jsonNode: JsonNode): RelativeHumidity {
                val unitCode = jsonNode.get("unitCode").asText()
                val value = jsonNode.get("value").asInt()
                return RelativeHumidity(unitCode, value)
            }
