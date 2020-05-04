package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *  AnswerDao class provides the database access
 *  for all the endpoints in user controller.
 */

@Repository
public class AnswerDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public AnswerEntity createAnswer(AnswerEntity answerEntity) {
        entityManager.persist(answerEntity);
        return answerEntity;
    }
}
