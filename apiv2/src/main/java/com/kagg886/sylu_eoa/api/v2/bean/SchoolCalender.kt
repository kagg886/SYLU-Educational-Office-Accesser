package com.kagg886.sylu_eoa.api.v2.bean

import kotlinx.serialization.Contextual
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@Serializable
data class SchoolCalender(
    @Serializable(with = LocalDateTimeAsLongSerializer::class) val start: LocalDate,
    @Serializable(with = LocalDateTimeAsLongSerializer::class) val end: LocalDate)
{

    fun currentWeek(now: LocalDate = LocalDate.now()): Int {
        if (now < start || now > end) {
            return -1
        }

        var start = start
        var i = 0
        while (start <= now) {
            i++
            start = start.plusWeeks(1)
        }
        return i
    }
}

object LocalDateTimeAsLongSerializer : KSerializer<LocalDate> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("java.util.LocalDate")
    override fun serialize(encoder: Encoder, value: LocalDate) {
        val date = value.atStartOfDay(ZoneOffset.ofHours(0))
        encoder.encodeLong(date.toInstant().toEpochMilli())
    }

    override fun deserialize(decoder: Decoder): LocalDate {
        return LocalDate.ofInstant(Instant.ofEpochMilli(decoder.decodeLong()), ZoneOffset.UTC)
    }
}