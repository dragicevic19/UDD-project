package com.example.ddmdemo.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class GeoService {
    private final RestTemplate restTemplate;

    public List<Double> extractCoordinates(String govAddress) {
        String url = "https://us1.locationiq.com/v1/search?key=pk.8ac23eb98d0f5f73ce05b1e595e4f99b&q=" + govAddress + "&format=json";
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
        String response = restTemplate.getForObject(builder.encode().build().toUri(), String.class);

        String longitude = "";
        String latitude = "";

        Pattern pattern = Pattern.compile("\"lon\":\"([\\d.]+)\"");
        Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            longitude = matcher.group(1);
        }

        pattern = Pattern.compile("\"lat\":\"([\\d.]+)\"");
        matcher = pattern.matcher(response);
        if (matcher.find()) {
            latitude = matcher.group(1);
        }

        return List.of(Double.parseDouble(longitude), Double.parseDouble(latitude));
    }

}
