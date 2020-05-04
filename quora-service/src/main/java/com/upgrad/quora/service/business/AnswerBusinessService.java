package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthenticationTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class AnswerBusinessService {
    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private AnswerDao answerDao;

    @Autowired
    private UserDao userDao;

    /**
     * The method implements the business logic for /question/{questionId}/answer/create endpoint. 
     * @param answerEntity
     * @param authorization
     * @return uuid of the created answer
     * @throws AuthorizationFailedException
     * @throws InvalidQuestionException
     */

    public AnswerEntity createAnswer(final AnswerEntity answerEntity, final String authorization) throws AuthorizationFailedException, InvalidQuestionException {

        UserAuthenticationTokenEntity userAuthenticationTokenEntity = userDao.getUserAuthToken(authorization);

        if (userAuthenticationTokenEntity == null)
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");

        if (userAuthenticationTokenEntity.getLogoutAt() != null)
            throw new AuthorizationFailedException("ATHR-002", "User is signed out. Sign in first to post an answer");

        answerEntity.setUser(userAuthenticationTokenEntity.getUserEntity());
        return answerDao.createAnswer(answerEntity);
    }

    public QuestionEntity getQuestionByUuid(final String uuid) throws InvalidQuestionException {
        QuestionEntity questionEntity = answerDao.getQuestionById(uuid);

        if (questionEntity == null)
            throw new InvalidQuestionException("QUES-001", "The question entered is invalid");

        return questionEntity;
    }

    /**
     * The method implements the business logic for /answer/edit/{answerId} endpoint. 
     * @param answerContent
     * @param answerId
     * @param accessToken
     * @return uuid of the edited answer
     * @throws AuthorizationFailedException
     * @throws AnswerNotFoundException
     */

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity editAnswer(final String answerContent, final String answerId, final String accessToken) throws AuthorizationFailedException, AnswerNotFoundException {

        UserAuthenticationTokenEntity userAuthenticationTokenEntity = userDao.getUserAuthToken(accessToken);

        if (userAuthenticationTokenEntity == null)
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");

        if (userAuthenticationTokenEntity.getLogoutAt() != null)
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to edit an answer");

        AnswerEntity answerEntity = answerDao.getAnswerById(answerId);

        if (answerEntity == null)
            throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");

        if (userAuthenticationTokenEntity.getUserEntity() == answerEntity.getUser()) {
            answerEntity.setContent(answerContent);

            answerDao.editAnswer(answerEntity);
            return answerEntity;
        } else {
            throw new AuthorizationFailedException("ATHR-003", "Only the answer owner can edit the answer");
        }
    }

    /**
     * The method implements the business logic for /answer/delete/{answerId} endpoint. 
     * @param answerId
     * @param accessToken
     * @return uuid of the deleted answer
     * @throws AuthorizationFailedException
     * @throws AnswerNotFoundException
     */

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity deleteAnswer(final String answerId,final String accessToken) throws AuthorizationFailedException, AnswerNotFoundException {

        UserAuthenticationTokenEntity userAuthenticationTokenEntity = userDao.getUserAuthToken(accessToken);

        if(userAuthenticationTokenEntity == null)
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");

        if(userAuthenticationTokenEntity.getLogoutAt() != null)
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to delete an answer");

        AnswerEntity answerEntity = answerDao.getAnswerById(answerId);
        if(answerEntity == null)
            throw new AnswerNotFoundException("ANS-001","Entered answer uuid does not exist");


        UserEntity userEntity = userDao.getUserByUuid(userAuthenticationTokenEntity.getUuid());
        if(userAuthenticationTokenEntity.getUserEntity() == answerEntity.getUser() || (userEntity.getRole()).equals("admin")){
            answerDao.deleteAnswer(answerEntity);
            return answerEntity;
        }
        else{
            throw new AuthorizationFailedException("ATHR-003","Only the answer owner or admin can delete the answer");
        }

    }

    /**
     * The method implements the business logic for answer/all/{questionId} endpoint. 
     * @param questionId
     * @param accessToken
     * @return uuid of the answer, "content" of the question and "content"
     *         of all the answers posted for that particular question 
     * @throws AuthorizationFailedException
     * @throws InvalidQuestionException
     */

    @Transactional(propagation = Propagation.REQUIRED)
    public List<AnswerEntity> getAllAnswersByQuestionId(final String questionId, final String accessToken) throws AuthorizationFailedException, InvalidQuestionException {

        UserAuthenticationTokenEntity userAuthenticationTokenEntity = userDao.getUserAuthToken(accessToken);

        if(userAuthenticationTokenEntity == null)
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");

        if(userAuthenticationTokenEntity.getLogoutAt() != null)
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to get the answers");

        QuestionEntity question = questionDao.getQuestionByUuid(questionId);

        if(question == null)
            throw new InvalidQuestionException("QUES-001","The question with entered uuid whose details are to be seen does not exist");

        List<AnswerEntity> allAnswersToQuestion = answerDao.getAllAnswersByQuestionId(question.getUuid());
        return allAnswersToQuestion;
    }

}