package com.upgrad.quora.service.dao;


import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthenticationTokenEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;


@Repository
public class QuestionDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public QuestionEntity createQuestion(QuestionEntity questionEntity) {
        entityManager.persist(questionEntity);
        return questionEntity;
    }

    public UserAuthenticationTokenEntity getUserAuthToken(final String accessToken) {
        try {
            return entityManager.createNamedQuery("userAuthToken", UserAuthenticationTokenEntity.class)
                    .setParameter("accessToken", accessToken).getSingleResult();
        } catch (NoResultException nre) {

            return null;
        }
    }

    public List<QuestionEntity> getAllQuestions(){
        try{
            return entityManager.createNamedQuery("getAllQuestions", QuestionEntity.class).getResultList();
        }catch (NoResultException nre){
            return null;
        }
    }

    public QuestionEntity getQuestionByUuid(final String UUID) {
        try {
            return entityManager.createNamedQuery("getAllQuestionsByUuid", QuestionEntity.class).setParameter("Uuid", UUID).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    @Transactional
    public QuestionEntity editQuestionContent(QuestionEntity questionEntity){
        entityManager.merge(questionEntity);
        return questionEntity;
    }

}