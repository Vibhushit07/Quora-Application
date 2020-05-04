package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthenticationTokenEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
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
}