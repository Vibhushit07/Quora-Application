package com.upgrad.quora.api.controller;


import com.upgrad.quora.api.model.UserDetailsResponse;
import com.upgrad.quora.service.business.CommonBusinessService;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/")
public class CommonController {

    @Autowired
    private CommonBusinessService commonBusinessService;

    /**
     * A controller method for endpoint /userprofile/{userId}
     * @param userId
     * @param authorization
     * @return all the details of the user from the database in the JSON response
     *         with the corresponding HTTP status
     * @throws AuthorizationFailedException
     * @throws UserNotFoundException
     */

    @RequestMapping(method = RequestMethod.GET, path = "/userprofile/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDetailsResponse> getUser(@PathVariable("userId") final String userId, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, UserNotFoundException
    {
        String [] bearerToken = authorization.split("Bearer ");
        final UserEntity userEntity = commonBusinessService.getUser(userId,bearerToken[0]);

        UserDetailsResponse userDetailsResponse = new UserDetailsResponse();
        userDetailsResponse.firstName(userEntity.getFirstName());
        userDetailsResponse.lastName(userEntity.getLastName());
        userDetailsResponse.userName(userEntity.getUserName());
        userDetailsResponse.emailAddress(userEntity.getEmail());
        userDetailsResponse.country(userEntity.getCountry());
        userDetailsResponse.aboutMe(userEntity.getAboutMe());
        userDetailsResponse.dob(userEntity.getDob());
        userDetailsResponse.contactNumber(userEntity.getContactNumber());


        return new ResponseEntity<UserDetailsResponse>(userDetailsResponse, HttpStatus.OK);
    }
}
