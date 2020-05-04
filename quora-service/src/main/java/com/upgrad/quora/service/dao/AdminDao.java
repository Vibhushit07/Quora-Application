package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class AdminDao {
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public UserEntity deleteUser(UserEntity userEntity){
        entityManager.remove(userEntity);
        return userEntity;
    }

}
