package com.upgrad.quora.api.exception;

import com.upgrad.quora.service.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import com.upgrad.quora.api.model.ErrorResponse;

/**
 * Exception Handler for handling all the exceptions
 */

@ControllerAdvice
public class RestExceptionHandler {

    /**
     * This method implements exceptions of SignUpRestrictedException class
     * @param exe
     * @param request
     * @return the corresponding exception code and message
     */

    @ExceptionHandler(SignUpRestrictedException.class)
    public ResponseEntity<ErrorResponse> resourceNotFoundException(SignUpRestrictedException exe, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(exe.getCode()).message(exe.getErrorMessage()), HttpStatus.CONFLICT

        );
    }

    /**
     * This method implements exceptions of SigninException class
     * @param exe
     * @param request
     * @return the corresponding exception code and message
     */

    @ExceptionHandler(SigninException.class)
    public ResponseEntity<ErrorResponse> resourceNotFoundException(SigninException exe, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(exe.getCode()).message(exe.getErrorMessage()), HttpStatus.NOT_FOUND
        );
    }

    /**
     * This method implements exceptions of SignOutRestrictedException class
     * @param exe
     * @param request
     * @return the corresponding exception code and message
     */

    @ExceptionHandler(SignOutRestrictedException.class)
    public ResponseEntity<ErrorResponse> SignOutRestrictedException(SignOutRestrictedException exe, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(exe.getCode()).message(exe.getErrorMessage()), HttpStatus.UNAUTHORIZED
        );
    }

    /**
     * This method implements exceptions of AuthenticationFailedException class
     * @param exe
     * @param request
     * @return the corresponding exception code and message
     */

    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<ErrorResponse> authenticationFailedException(AuthenticationFailedException exe, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(exe.getCode()).message(exe.getErrorMessage()), HttpStatus.UNAUTHORIZED
        );
    }

    /**
     * This method implements exceptions of AuthorizationFailedException class
     * @param exe
     * @param request
     * @return the corresponding exception code and message
     */

    @ExceptionHandler(AuthorizationFailedException.class)
    public ResponseEntity<ErrorResponse> authorizationFailedException(AuthorizationFailedException exe, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(exe.getCode()).message(exe.getErrorMessage()), HttpStatus.FORBIDDEN
        );
    }

    /**
     * This method implements exceptions of UserNotFoundException class
     * @param exe
     * @param request
     * @return the corresponding exception code and message
     */

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> UserNotFoundException(UserNotFoundException exe, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(exe.getCode()).message(exe.getErrorMessage()), HttpStatus.NOT_FOUND
        );
    }

    /**
     * This method implements exceptions of AnswerNotFoundException class
     * @param exe
     * @param request
     * @return the corresponding exception code and message
     */

    @ExceptionHandler(AnswerNotFoundException.class)
    public ResponseEntity<ErrorResponse> AnswerNotFoundException(AnswerNotFoundException exe, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(exe.getCode()).message(exe.getErrorMessage()), HttpStatus.NOT_FOUND
        );
    }

    /**
     * This method implements exceptions of InvalidQuestionException class
     * @param exe
     * @param request
     * @return the corresponding exception code and message
     */

    @ExceptionHandler(InvalidQuestionException.class)
    public ResponseEntity<ErrorResponse> InvalidQuestionException(InvalidQuestionException exe, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(exe.getCode()).message(exe.getErrorMessage()), HttpStatus.NOT_FOUND
        );
    }

}
