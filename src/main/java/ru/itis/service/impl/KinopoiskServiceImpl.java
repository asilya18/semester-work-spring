package ru.itis.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.itis.dto.KinopoiskFilmDto;
import ru.itis.dto.KinopoiskSearchResponseDto;
import ru.itis.exception.NotFoundException;
import ru.itis.service.KinopoiskService;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class KinopoiskServiceImpl implements KinopoiskService {
    @Value("${kinopoisk.api.key}")
    private String apiKey;
    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;

    public KinopoiskServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.httpClient = new OkHttpClient();
    }

    @Override
    public List<KinopoiskFilmDto> search(String query) {
        String url = "https://kinopoiskapiunofficial.tech/api/v2.1/films/search-by-keyword?keyword="
                + URLEncoder.encode(query, StandardCharsets.UTF_8) + "&page=1";
        Request request = new Request.Builder()
                .url(url)
                .header("X-API-KEY", apiKey) // как требует кинопоиск
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                log.error("kinopoisk API returned error status: {}", response.code());
                return Collections.emptyList();
            }
            KinopoiskSearchResponseDto dto = objectMapper.readValue(
                    response.body().string(), KinopoiskSearchResponseDto.class);
            return dto.getFilms() != null ? dto.getFilms() : Collections.emptyList();
        } catch (IOException e) {
            log.error("error calling Kinopoisk search API: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public KinopoiskFilmDto findById(String kinopoiskId) {
        // идет за полными данными для сохранения в бд
        String url = "https://kinopoiskapiunofficial.tech/api/v2.2/films/" + kinopoiskId;
        Request request = new Request.Builder()
                .url(url)
                .header("X-API-KEY", apiKey)
                .build();
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                log.error("kinopoisk API returned error for id {}: {}", kinopoiskId, response.code());
            }
            return objectMapper.readValue(response.body().string(), KinopoiskFilmDto.class);
        } catch (IOException e) {
            log.error("error calling Kinopoisk film API for id {}: {}", kinopoiskId, e.getMessage(), e);
            throw new NotFoundException("ошибка при получении фильма из Кинопоиска");
        }
    }
}