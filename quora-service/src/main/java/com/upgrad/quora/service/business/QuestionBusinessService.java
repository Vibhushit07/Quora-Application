package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthenticationTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
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

    public QuestionEntity deleteQuestion(final String questionid, final String authorizationToken) throws AuthorizationFailedException, InvalidQuestionException {
        UserAuthenticationTokenEntity userAuthTokenEntity = questionDao.getUserAuthToken(authorizationToken);
        if(userAuthTokenEntity != null){
            ZonedDateTime logout = userAuthTokenEntity.getLogoutAt();
            if (logout != null) {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to delete a question");
            }

            UserEntity userEntity = questionDao.getUserByUuid(userAuthTokenEntity.getUuid());
            QuestionEntity Entity = questionDao.getQuestionByUuid(questionid);
            if (Entity == null){
                throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
            }

            if (Entity.getUserEntity() == userAuthTokenEntity.getUserEntity() || userEntity.getRole().equals("admin")){
                return questionDao.DeleteQuestion(Entity);
            }
            else
            {
                throw new AuthorizationFailedException("ATHR-003", "Only the question owner or admin can delete the question");
            }
        }

        throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    }


    public List<QuestionEntity> getAllQuestionsByUserId(final String userId, final String authorization) throws AuthorizationFailedException, UserNotFoundException {

        UserAuthenticationTokenEntity userAuthenticationTokenEntity = questionDao.getUserAuthToken(authorization);

        if(userAuthenticationTokenEntity != null){

            if(userAuthenticationTokenEntity.getLogoutAt() != null)
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions posted by a specific user");

            UserEntity userEntity = userDao.getUserByUuid(userId);

            if(userEntity == null)
                throw new UserNotFoundException("USR-001", "User with entered uuid whose question details are to be seen does not exist");

            return questionDao.getAllQuestionsByUserId(userEntity.getId());
        }

        throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    }

}