package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthenticationTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;


@Service
public class CommonBusinessService {

    @Autowired
    private UserDao userDao;

    /**
     * The method implements the business logic for /userprofile/{userId}  endpoint.
     * @param userUuid
     * @param authorizationToken
     * @return All the details of the user
     * @throws AuthorizationFailedException
     * @throws UserNotFoundException
     */

    public UserEntity getUser(final String userUuid, final String authorizationToken) throws AuthorizationFailedException, UserNotFoundException {
        UserAuthenticationTokenEntity userAuthenticationTokenEntity = userDao.getUserAuthToken(authorizationToken);
        if(userAuthenticationTokenEntity != null){
            ZonedDateTime logout = userAuthenticationTokenEntity.getLogoutAt();
            if (logout != null) {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get user details");
            }

            UserEntity userEntity = userDao.getUserByUuid(userUuid);
            if (userEntity == null){
                throw new UserNotFoundException("USR-001", "User with entered uuid does not exist");
            }
            return userEntity;
        }

        throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    }
}
