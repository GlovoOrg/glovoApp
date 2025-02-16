package com.api.glovoCRM.DAOs.QueryDSL;

import com.querydsl.core.types.EntityPath;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

public abstract class BaseDAOQueryDSL<T, Q extends EntityPath<T>> {

    @PersistenceContext
    protected EntityManager entityManager;

    protected abstract Q getQEntity();

    public List<T> findBySimilarNameQueryDSL(String name) {
        if (name == null || name.trim().isEmpty()) {
            return new JPAQuery<>(entityManager)
                    .select(getQEntity())
                    .from(getQEntity())
                    .fetch();
        }
        String searchPattern = name.toLowerCase();
        com.querydsl.core.types.dsl.StringPath namePath;
        try {
            namePath = (com.querydsl.core.types.dsl.StringPath) getQEntity()
                    .getClass().getDeclaredField("name")
                    .get(getQEntity());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Ошибка при доступе к полю name в Q-классе", e);
        }

        return new JPAQuery<>(entityManager)
                .select(getQEntity())
                .from(getQEntity())
                .where(namePath.lower().contains(searchPattern))
                .fetch();
    }
}
