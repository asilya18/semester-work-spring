package ru.itis.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import ru.itis.model.Movie;
import ru.itis.repository.MovieRepositoryCustom;

import java.util.ArrayList;
import java.util.List;

public class MovieRepositoryCustomImpl implements MovieRepositoryCustom {
    @PersistenceContext // аннотация специально для em
    // autowired не подходит, тк это не обычный спринг бин
    private EntityManager entityManager;

    @Override
    public List<Movie> searchMovies(String title, Integer year) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        // cb - фабрика условий, создает кусочками условия запроса
        CriteriaQuery<Movie> query = cb.createQuery(Movie.class);
        // cq - контейнер для частей запроса
        Root<Movie> root = query.from(Movie.class);
        // root - точка входа в сущность
        // аналог from movies в sql
        List<Predicate> predicates = new ArrayList<>();
        // predicate - одно условие - один кусок фильтра
        if (title != null && !title.isBlank()) {
            predicates.add(cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
        }
        if (year != null) {
            predicates.add(cb.equal(root.get("year"), year));
        }
        query.where(predicates.toArray(new Predicate[0]));
        // toArray(new Predicate[0]) - конвертация List в массив Predicate[]
        query.orderBy(cb.asc(root.get("title")));

        return entityManager.createQuery(query).getResultList();
    }
}