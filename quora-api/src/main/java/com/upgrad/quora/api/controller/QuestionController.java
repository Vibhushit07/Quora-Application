package com.upgrad.quora.api.controller;


import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/")
public class QuestionController {

    @Autowired
    private QuestionBusinessService questionBusinessService;

    /**
     * A controller method for endpoint /question/create.
     * @param questionRequest
     * @param authorization
     * @return uuid of the question and message 'QUESTION CREATED' in the JSON response with the corresponding HTTP status
     * @throws AuthorizationFailedException
     */

    @RequestMapping(method = RequestMethod.POST, path = "/question/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(final QuestionRequest questionRequest, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {

        String[] bearerToken = authorization.split("Bearer ");
        final QuestionEntity questionEntity = new QuestionEntity();

        questionEntity.setContent(questionRequest.getContent());
        questionEntity.setDate(ZonedDateTime.now());
        questionEntity.setUuid(UUID.randomUUID().toString());

        final QuestionEntity createdquestionEntity = questionBusinessService.createQuestion(questionEntity, bearerToken[0]);
        QuestionResponse questionResponse = new QuestionResponse().id(createdquestionEntity.getUuid()).status("QUESTION CREATED");

        return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.CREATED);
    }

    /**
     * A controller method for endpoint /question/all.
     * @param authorization
     * @return uuid and content of all the questions from the database in the JSON response with the corresponding HTTP status
     * @throws AuthorizationFailedException
     */

    @GetMapping("/question/all")
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestions(@RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {

        List<QuestionEntity> list = questionBusinessService.getAllQuestions(authorization);

        List<QuestionDetailsResponse> questionDetailsResponseList = list.stream()
                .map(questions -> {
                    QuestionDetailsResponse q = new QuestionDetailsResponse();
                    q.setContent(questions.getContent());
                    q.setId(questions.getUuid());
                    return q;
                }).collect(Collectors.toList());

        return new ResponseEntity<>(questionDetailsResponseList, HttpStatus.OK);

    }

    /**
     * A controller method for endpoint /question/edit/{questionId}.
     * @param questionEditRequest
     * @param questionId
     * @param authorization
     * @return uuid of the edited question and message 'QUESTION EDITED' in the JSON response with the corresponding HTTP status
     * @throws AuthorizationFailedException
     * @throws InvalidQuestionException
     */

    @RequestMapping(method = RequestMethod.PUT, path = "/question/edit/{questionId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionEditResponse> editQuestionContent(final QuestionEditRequest questionEditRequest, @PathVariable("questionId") final String questionId, @RequestHeader("authorization") final String authorization) throws  AuthorizationFailedException, InvalidQuestionException {

        final QuestionEntity questionEntity = new QuestionEntity();

        questionEntity.setContent(questionEditRequest.getContent());

        final QuestionEntity editQuestionEntity = questionBusinessService.editQuestionContent(questionEntity, questionId, authorization);

        QuestionEditResponse questionEditResponse = new QuestionEditResponse();
        questionEditResponse.id(editQuestionEntity.getUuid());
        questionEditResponse.status("QUESTION EDITED SUCCESSFULLY");

        return new ResponseEntity<QuestionEditResponse>(questionEditResponse, HttpStatus.OK);
    }

    /**
     * A controller method for endpoint /question/delete/{questionId}.
     * @param questionId
     * @param authorization
     * @return uuid of the deleted question and message -'QUESTION DELETED' in the JSON response with the corresponding HTTP status
     * @throws AuthorizationFailedException
     * @throws InvalidQuestionException
     */

    @RequestMapping(method = RequestMethod.DELETE, path = "/question/delete/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDeleteResponse> DeleteQuestion(@PathVariable("questionId") final String questionId, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, InvalidQuestionException
    {
        String [] bearerToken = authorization.split("Bearer ");
        final QuestionEntity questionEntity = questionBusinessService.deleteQuestion(questionId,bearerToken[0]);

        QuestionDeleteResponse questionDeleteResponse = new QuestionDeleteResponse().id(questionEntity.getUuid()).status("QUESTION DELETED");

        return new ResponseEntity<QuestionDeleteResponse>(questionDeleteResponse, HttpStatus.OK);
    }

    /**
     * A controller method for endpoint /question/all/{userId}.
     * @param userId
     * @param authorization
     * @return uuid and content of all the questions posed by the corresponding user from the database in the JSON response
     *            with the corresponding HTTP status
     * @throws AuthorizationFailedException
     * @throws UserNotFoundException
     */

    @GetMapping("/question/all/{userId}")
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestionsByUserId(@PathVariable final String userId, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, UserNotFoundException {

        List<QuestionEntity> questionEntityList = questionBusinessService.getAllQuestionsByUserId(userId, authorization);

        List<QuestionDetailsResponse> questionDetailsResponseList = questionEntityList.stream()
                .map(questions -> {

                    QuestionDetailsResponse questionDetailsResponse = new QuestionDetailsResponse();

                    questionDetailsResponse.setContent(questions.getContent());
                    questionDetailsResponse.setId(questions.getUuid());
                    return questionDetailsResponse;
                }).collect(Collectors.toList());

        return new ResponseEntity<>(questionDetailsResponseList, HttpStatus.OK);
    }

}