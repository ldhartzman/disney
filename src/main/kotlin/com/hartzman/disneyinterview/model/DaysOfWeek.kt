package com.hartzman.disneyinterview.model

enum class DaysOfWeek {
    Sunday,
    Monday,
    Tuesday,
    Wednesday,
    Thursday,
    Friday,
    Saturday;

    companion object {
        fun contains(input: String): Boolean {
            return DaysOfWeek.values().map { it.name }.contains(input)
        }
        fun previousDay(input: String) : String {
            var previous = DaysOfWeek.values().filter { it.name == input }.map { it.ordinal }.get(0)
            previous = if (previous == 0) {
                6
            } else previous - 1
            return DaysOfWeek.values().filter { it.ordinal == previous }.map { it.name }.get(0)
        }
    }
}