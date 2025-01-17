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

    /**
     * The method implements the business logic for /question/create endpoint.
     * @param questionEntity
     * @param authorizationToken
     * @return uuid of created question
     * @throws AuthorizationFailedException
     */

    public QuestionEntity createQuestion(final QuestionEntity questionEntity, final String authorizationToken) throws AuthorizationFailedException {

        UserAuthenticationTokenEntity userAuthenticationTokenEntity = questionDao.getUserAuthToken(authorizationToken);

        if (userAuthenticationTokenEntity == null)
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");

        if (userAuthenticationTokenEntity.getLogoutAt() != null)
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post a question");

        questionEntity.setUserEntity(userAuthenticationTokenEntity.getUserEntity());
        return questionDao.createQuestion(questionEntity);
    }

    /**
     * The method implements the business logic for /question/all endpoint.
     * @param authorizationToken
     * @return uuid and content of all the questions
     * @throws AuthorizationFailedException
     */
    public List<QuestionEntity> getAllQuestions(final String authorizationToken) throws AuthorizationFailedException {

        UserAuthenticationTokenEntity userAuthenticationTokenEntity = userDao.getUserAuthToken(authorizationToken);

        if(userAuthenticationTokenEntity != null){
            if(userAuthenticationTokenEntity.getLogoutAt() != null)
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions");

            return questionDao.getAllQuestions();
        }

        throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    }

    /**
     * The method implements the business logic for /question/edit/{questionId} endpoint.
     * @param questionEntity
     * @param questionUuid
     * @param authorizationToken
     * @return uuid of the edited question
     * @throws AuthorizationFailedException
     * @throws InvalidQuestionException
     */

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

    /**
     *  The method implements the business logic for /question/delete/{questionId} endpoint.
     * @param questionid
     * @param authorizationToken
     * @return uuid of the deleted question
     * @throws AuthorizationFailedException
     * @throws InvalidQuestionException
     */

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

    /**
     *  The method implements the business logic for /question/all/{userId} endpoint.
     * @param userId
     * @param authorization
     * @return uuid and content of all the questions posed by the corresponding user
     * @throws AuthorizationFailedException
     * @throws UserNotFoundException
     */

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