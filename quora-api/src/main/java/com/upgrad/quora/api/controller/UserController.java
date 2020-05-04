package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.SigninResponse;
import com.upgrad.quora.api.model.SignoutResponse;
import com.upgrad.quora.api.model.SignupUserRequest;
import com.upgrad.quora.api.model.SignupUserResponse;
import com.upgrad.quora.service.business.UserBusinessService;
import com.upgrad.quora.service.entity.UserAuthenticationTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class UserController {

    @Autowired
    private UserBusinessService userBusinessService;

    /**
     * A controller method for endpoint /user/signup.
     *
     * @param signupUserRequest - This argument contains all the attributes required to store user details in the database.
     * @return - ResponseEntity<SignupUserResponse> type object along with Http status CREATED.
     * @throws SignUpRestrictedException
     */

    @RequestMapping(method = RequestMethod.POST, path = "/user/signup", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupUserResponse> signup(final SignupUserRequest signupUserRequest, final SignupUserResponse signupUserResponse) throws SignUpRestrictedException {

        final UserEntity userEntity = new UserEntity();

        userEntity.setUuid(UUID.randomUUID().toString());
        userEntity.setFirstName(signupUserRequest.getFirstName());
        userEntity.setLastName(signupUserRequest.getLastName());
        userEntity.setUserName(signupUserRequest.getUserName());
        userEntity.setEmail(signupUserRequest.getEmailAddress());
        userEntity.setPassword(signupUserRequest.getPassword());
        userEntity.setCountry(signupUserRequest.getCountry());
        userEntity.setAboutMe(signupUserRequest.getAboutMe());
        userEntity.setDob(signupUserRequest.getDob());
        userEntity.setContactNumber(signupUserRequest.getContactNumber());
        userEntity.setRole("nonadmin");

        final UserEntity createdUserEntity = userBusinessService.signup(userEntity);

        SignupUserResponse userResponse = new SignupUserResponse().id(createdUserEntity.getUuid()).status("USER SUCCESSFULLY REGISTERED");
        return new ResponseEntity<SignupUserResponse>(userResponse, HttpStatus.CREATED);
    }

    /**
     * A controller method for endpoint /user/signin.
     *
     * @param authorization - A field in the request header which contains the user credentials as Basic authentication.
     * @return - ResponseEntity<SigninResponse> type object along with Http status OK.
     * @throws AuthenticationFailedException
     */

    @RequestMapping(method = RequestMethod.POST, path = "/user/signin", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SigninResponse> userSignIn(@RequestHeader("authorization") final String authorization) throws AuthenticationFailedException {

        String s = authorization.split("Basic ")[1];
        byte[] decode = Base64.getDecoder().decode(s);
        String decodedText = new String(decode);
        String[] decodedArray = decodedText.split(":");

        UserAuthenticationTokenEntity userAuthenticationTokenEntity = userBusinessService.authenticate(decodedArray[0], decodedArray[1]);

        UserEntity user = userAuthenticationTokenEntity.getUserEntity();

        String sendMessage = "SIGNED IN SUCCESSFULLY";
        SigninResponse signinResponse = new SigninResponse();
        signinResponse.setId(userAuthenticationTokenEntity.getUserEntity().getUuid());
        signinResponse.setMessage(sendMessage);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("access_token", userAuthenticationTokenEntity.getAccessToken());

        return new ResponseEntity<SigninResponse>(signinResponse, httpHeaders, HttpStatus.OK);
    }

    /**
     * A controller method for endpoint /user/signout.
     *
     * @param authorization - A field in the request header which contains the user credentials as Basic authentication.
     * @return - uuid of the signed out user from 'users' table and message 'SIGNED OUT SUCCESSFULLY'
     * in the JSON response with the corresponding HTTP status 
     * * @throws SignOutRestrictedException
     */

    @RequestMapping(method = RequestMethod.POST, path = "/user/signout", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignoutResponse> userSignOut(@RequestHeader("authorization") final String authorization) throws SignOutRestrictedException {

        String [] bearerToken = authorization.split("Bearer ");

        UserAuthenticationTokenEntity userAuthenticationTokenEntity = userBusinessService.signout(bearerToken[0]);

        SignoutResponse authorizedUserResponse = new SignoutResponse().id(userAuthenticationTokenEntity.getUuid()).message("SIGNED OUT SUCCESSFULLY");

        return new ResponseEntity<SignoutResponse>(authorizedUserResponse,HttpStatus.OK);
    }

}