package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthenticationTokenEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class QuestionBusinessService {

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private UserDao userDao;

    public QuestionEntity createQuestion(final QuestionEntity questionEntity, final String authorizationToken) throws AuthorizationFailedException {

        UserAuthenticationTokenEntity userAuthenticationTokenEntity = questionDao.getUserAuthToken(authorizationToken);

        if (userAuthenticationTokenEntity == null)
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");

        if (userAuthenticationTokenEntity.getLogoutAt() != null)
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post a question");

        questionEntity.setUserEntity(userAuthenticationTokenEntity.getUserEntity());
        return questionDao.createQuestion(questionEntity);
    }

    public List<QuestionEntity> getAllQuestions(final String authorizationToken) throws AuthorizationFailedException {

        UserAuthenticationTokenEntity userAuthenticationTokenEntity = userDao.getUserAuthToken(authorizationToken);

        if(userAuthenticationTokenEntity != null){
            if(userAuthenticationTokenEntity.getLogoutAt() != null)
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions");

            return questionDao.getAllQuestions();
        }

        throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    }

    public QuestionEntity editQuestionContent(final QuestionEntity questionEntity, final String questionUuid, final String authorizationToken) throws AuthorizationFailedException, InvalidQuestionException {

        UserAuthenticationTokenEntity userAuthenticationTokenEntity = questionDao.getUserAuthToken(authorizationToken);

        if(userAuthenticationTokenEntity != null){
            if(userAuthenticationTokenEntity.getLogoutAt() != null)
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to edit the question");

            QuestionEntity questionEntity1 = questionDao.getQuestionByUuid(questionUuid);

            if(questionEntity1 == null)
                throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");

            if(questionEntity1.getUserEntity() != userAuthenticationTokenEntity.getUserEntity())
                throw new AuthorizationFailedException("ATHR-003", "Only the question owner can edit the question");

            questionEntity.setDate(questionEntity1.getDate());
            questionEntity.setUserEntity(questionEntity1.getUserEntity());
            questionEntity.setId(questionEntity1.getId());
            questionEntity.setUuid(questionEntity1.getUuid());

            return questionDao.editQuestionContent(questionEntity);
        }

        throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    }
    
}