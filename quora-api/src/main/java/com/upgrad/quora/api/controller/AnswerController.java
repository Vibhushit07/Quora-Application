package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.AnswerBusinessService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
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
public class AnswerController {
    @Autowired
    private AnswerBusinessService answerBusinessService;

    /**
     * Controller method for /question/{questionId}/answer/create endpoint
     *
     * @param answerRequest
     * @param authorization
     * @return uuid of answer
     * @throws AuthorizationFailedException
     */

    @RequestMapping(method = RequestMethod.POST, path = "/question/{questionId}/answer/create", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> createAnswer(@RequestBody final AnswerRequest answerRequest, @PathVariable("questionId") final String questionId, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, InvalidQuestionException {

        final AnswerEntity answerEntity = new AnswerEntity();

        answerEntity.setContent(answerRequest.getAnswer());
        answerEntity.setDate(ZonedDateTime.now());
        answerEntity.setUuid(UUID.randomUUID().toString());
        QuestionEntity questionEntity = answerBusinessService.getQuestionByUuid(questionId);
        answerEntity.setQuestion(questionEntity);

        final AnswerEntity answerEntity1 = answerBusinessService.createAnswer(answerEntity, authorization);
        AnswerResponse answerResponse = new AnswerResponse().id(answerEntity1.getUuid()).status("ANSWER CREATED");

        return new ResponseEntity<AnswerResponse>(answerResponse, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/answer/edit/{answerId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerEditResponse> editAnswerContent(final AnswerEditRequest answerEditRequest, @PathVariable("answerId") final String answerId, @RequestHeader("authorization") final String accessToken) throws AuthorizationFailedException, AnswerNotFoundException {

        answerBusinessService.editAnswer(answerEditRequest.getContent(), answerId, accessToken);
        AnswerEditResponse answerEditResponse = new AnswerEditResponse().id(answerId).status("ANSWER EDITED");

        return new ResponseEntity<>(answerEditResponse, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE,path = "/answer/delete/{answerId}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerDeleteResponse> deleteAnswer(@PathVariable("answerId") final String answerId, @RequestHeader("authorization") final String accessToken) throws AuthorizationFailedException, AnswerNotFoundException {

        answerBusinessService.deleteAnswer(answerId, accessToken);
        AnswerDeleteResponse answerDeleteResponse = new AnswerDeleteResponse().id(answerId).status("ANSWER DELETED");

        return new ResponseEntity<>(answerDeleteResponse,HttpStatus.OK);
    }

    @GetMapping("answer/all/{questionId}")
    public ResponseEntity<List<AnswerDetailsResponse>> getAllAnswersToQuestion(@PathVariable("questionId") final String questionId, @RequestHeader("authorization") final String accessToken) throws AuthorizationFailedException, InvalidQuestionException {

        List<AnswerEntity> answerEntities =  answerBusinessService.getAllAnswersByQuestionId(questionId, accessToken);

        List<AnswerDetailsResponse> answerDetailsResponseList = answerEntities.stream()
                .map(answer -> {
                    AnswerDetailsResponse answerDetailsResponse = new AnswerDetailsResponse();
                    answerDetailsResponse.setId(answer.getUuid());
                    answerDetailsResponse.setAnswerContent(answer.getContent());
                    answerDetailsResponse.setQuestionContent(answer.getQuestion().getContent());
                    return answerDetailsResponse;
                }).collect(Collectors.toList());

        return new ResponseEntity<>(answerDetailsResponseList, HttpStatus.OK);
    }

}


