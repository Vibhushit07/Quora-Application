package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthenticationTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.UUID;

@Service
public class UserBusinessService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordCryptographyProvider passwordCryptographyProvider;

    /**
     * The method implements the business logic for /user/signup endpoint.
     * @param  userEntity
     *  @return uuid of registered user
     */

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity signup(final UserEntity userEntity) throws SignUpRestrictedException {

        UserEntity userEntity1 = userDao.getUserByUserName(userEntity.getUserName());
        UserEntity userEntity2 = userDao.getUserByEmail(userEntity.getEmail());

        String[] encryptedText = passwordCryptographyProvider.encrypt(userEntity.getPassword());
        userEntity.setSalt(encryptedText[0]);
        userEntity.setPassword(encryptedText[1]);

        if (userEntity1 != null)
            throw new SignUpRestrictedException("SGR-001", "Username already exist");

        if (userEntity2 != null)
            throw new SignUpRestrictedException("SGR-002", "Email already exist");


        return userDao.createUser(userEntity);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthenticationTokenEntity authenticate(final String username, final String password) throws AuthenticationFailedException {

        UserEntity userEntity = userDao.getUserByUserName(username);

        if (userEntity == null)
            throw new AuthenticationFailedException("ATH-001", "User doesn't exist");

        final String encryptedPassword = passwordCryptographyProvider.encrypt(password, userEntity.getSalt());

        if (encryptedPassword.equals(userEntity.getPassword())) {

            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(password);
            UserAuthenticationTokenEntity userAuthenticationTokenEntity = new UserAuthenticationTokenEntity();
            userAuthenticationTokenEntity.setUserEntity(userEntity);
            final ZonedDateTime loginAt = ZonedDateTime.now();
            final ZonedDateTime expiresAt = loginAt.plusHours(10);
            userAuthenticationTokenEntity.setAccessToken(jwtTokenProvider.generateToken(userEntity.getUuid(), loginAt, expiresAt));
            userAuthenticationTokenEntity.setLoginAt(loginAt);
            userAuthenticationTokenEntity.setExpiresAt(expiresAt);
            userAuthenticationTokenEntity.setLogoutAt(null);
            userAuthenticationTokenEntity.setUuid(UUID.randomUUID().toString());

            userDao.createAuthToken(userAuthenticationTokenEntity);

            return userAuthenticationTokenEntity;

        } else {
            throw new AuthenticationFailedException("ATH-002", "Incorrect Password");
        }
    }

    /**
     * The method implements the business logic for /user/signout endpoint.
     * @param authorizationToken
     * @return uuid of signed out user
     */

    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthenticationTokenEntity signout(final String authorizationToken) throws SignOutRestrictedException {

        UserAuthenticationTokenEntity userAuthenticationTokenEntity = userDao.getUserAuthToken(authorizationToken);

            if(userAuthenticationTokenEntity == null)
                throw new SignOutRestrictedException("SGR-001", "User not signed in");

            return userDao.signOut(userAuthenticationTokenEntity);
        }
    }
