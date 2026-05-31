package ru.itis.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.itis.dto.MovieNightForm;
import ru.itis.model.MovieNight;

@Component
public class MovieNightToFormConverter implements Converter<MovieNight, MovieNightForm> {

    @Override
    public MovieNightForm convert(MovieNight source) {
        MovieNightForm form = new MovieNightForm();
        form.setTitle(source.getTitle());
        form.setDescription(source.getDescription());
        form.setEventDate(source.getEventDate());
        form.setPlatform(source.getPlatform());
        return form;
    }
}