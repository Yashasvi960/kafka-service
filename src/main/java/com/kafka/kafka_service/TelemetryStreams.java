package com.kafka.kafka_service;

import org.apache.kafka.streams.KeyValue;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Component
public class TelemetryStreams {

    private static final String SCHEMA_REGISTRY_URL = "http://localhost:8081";

    @Bean
    public KStream<String, Telemetry> kStream(StreamsBuilder builder) {

        // JSON Serde for Telemetry (value)
        Map<String, Object> serdeProps = new HashMap<>();
        serdeProps.put("json.trusted.packages", "com.example.telemetry");

        JsonSerializer<Telemetry> telemetrySerializer = new JsonSerializer<>();
        JsonDeserializer<Telemetry> telemetryDeserializer = new JsonDeserializer<>(Telemetry.class);
        telemetryDeserializer.configure(serdeProps, false);

        Serde<Telemetry> telemetrySerde = Serdes.serdeFrom(telemetrySerializer, telemetryDeserializer);

        // JSON Serde for AvgSpeed (state store value)
        JsonSerializer<AvgSpeed> avgSerializer = new JsonSerializer<>();
        JsonDeserializer<AvgSpeed> avgDeserializer = new JsonDeserializer<>(AvgSpeed.class);
        avgDeserializer.configure(serdeProps, false);

        Serde<AvgSpeed> avgSerde = Serdes.serdeFrom(avgSerializer, avgDeserializer);

        // Build stream
        KStream<String, Telemetry> stream = builder.stream(
                "telemetry",
                Consumed.with(Serdes.String(), telemetrySerde)
        );

        stream.groupByKey(Grouped.with(Serdes.String(), telemetrySerde))
                .windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofSeconds(30)))
                .aggregate(
                        AvgSpeed::new,
                        (key, value, aggregate) -> {
                            if (value != null) aggregate.add(value.getSpeed());
                            return aggregate;
                        },
                        Materialized.with(Serdes.String(), avgSerde)
                )
                .toStream()
                .map((windowedKey, avg) -> KeyValue.pair(windowedKey.key(), avg.getAverage()))
                .to("vehicle.avg.speed", Produced.with(Serdes.String(), Serdes.Double()));

        return stream;
    }
}