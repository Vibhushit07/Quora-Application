package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserBusinessService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordCryptographyProvider passwordCryptographyProvider;

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
}