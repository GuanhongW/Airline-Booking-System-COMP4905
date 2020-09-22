package com.guanhong.airlinebookingsystem.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Transactional
@Service
public class BatchService {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Batch insert
     * without check if entity is in the DB
     * @param list list of entity
     * @param <T>  entity
     */
    public <T> void batchInsert(List<T> list) {
        if (!ObjectUtils.isEmpty(list)){
            for (int i = 0; i < list.size(); i++) {
                entityManager.persist(list.get(i));
                if (i % 500 == 0) {
                    entityManager.flush();
                    entityManager.clear();
                }
            }
            entityManager.flush();
            entityManager.clear();
        }
    }

    /**
     * batch update
     * without check if entity is in the DB
     * @param list list of entity
     * @param <T>  entity
     */
    public <T> void batchUpdate(List<T> list) {
        if (!ObjectUtils.isEmpty(list)){
            for (int i = 0; i < list.size(); i++) {
                entityManager.merge(list.get(i));
                if (i % 500 == 0) {
                    entityManager.flush();
                    entityManager.clear();
                }
            }
            entityManager.flush();
            entityManager.clear();
        }
    }
}