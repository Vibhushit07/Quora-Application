package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

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

    public QuestionEntity getQuestionById(final String id) {
        try {
            return entityManager.createNamedQuery("getAllQuestionsByUuid", QuestionEntity.class)
                    .setParameter("Uuid", id).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    @Transactional
    public AnswerEntity editAnswer(AnswerEntity answerEntity){
        entityManager.merge(answerEntity);
        return answerEntity;
    }

    public AnswerEntity getAnswerById(final String UUID) {
        try {
            return entityManager.createNamedQuery("answerByUUID", AnswerEntity.class).setParameter("UUID", UUID).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public AnswerEntity deleteAnswer(AnswerEntity answerEntity){
        entityManager.remove(answerEntity);
        return answerEntity;
    }

    public List<AnswerEntity> getAllAnswersByQuestionId(final String questionId){
        return entityManager.createNamedQuery("getAllAnswerByQuestion", AnswerEntity.class)
                .setParameter("QuestionId",questionId).getResultList();
    }


}
