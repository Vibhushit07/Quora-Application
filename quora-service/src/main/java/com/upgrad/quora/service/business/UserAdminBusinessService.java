package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AdminDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthenticationTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
public class UserAdminBusinessService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private AdminDao adminDao;

    /**
     * The method implements the business logic for /admin/user/{userId} endpoint.
     * @param userid
     * @param authorizationToken
     * @return Uuid of the deleted user
     * @throws AuthorizationFailedException
     * @throws UserNotFoundException
     */

    public UserEntity DeleteUser(final String userid, final String authorizationToken) throws AuthorizationFailedException, UserNotFoundException {
        UserAuthenticationTokenEntity userAuthenticationTokenEntity = userDao.getUserAuthToken(authorizationToken);
        if (userAuthenticationTokenEntity != null) {
            ZonedDateTime logout = userAuthenticationTokenEntity.getLogoutAt();
            if (logout != null) {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out");
            }

            UserEntity userEntity = userDao.getUserByUuid(userid);

            if (userEntity == null) {
                throw new UserNotFoundException("USR-001", "User with entered uuid to be deleted does not exist");
            } else {
                String RoleName = userEntity.getRole();
                if (!RoleName.equals("admin")) {
                    throw new AuthorizationFailedException("ATHR-003", "Unauthorized Access,Entered user is not an admin");
                }

            }

            return adminDao.deleteUser(userAuthenticationTokenEntity.getUserEntity());
        }

        throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    }

}