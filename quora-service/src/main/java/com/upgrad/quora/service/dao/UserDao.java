package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 * UserDao class provides the database access for all the endpoints in user controller.
 */

@Repository
public class UserDao {

    @PersistenceContext
    private EntityManager entityManager;

    /**This method provides the database access to
     /user/signup endpoint
     */

    public UserEntity createUser(UserEntity userEntity){
        entityManager.persist(userEntity);
        return userEntity;
    }
    
    public UserEntity getUserByPassword(final String password){
        try {
            return entityManager.createNamedQuery("userByPassword", UserEntity.class).setParameter("password", password).getSingleResult();
        }catch(NoResultException nrex){
            return null;
        }
    }

    public UserEntity getUserByUserName(final String userName){
        try{
            return entityManager.createNamedQuery("userByUsername", UserEntity.class).setParameter("userName", userName).getSingleResult();
        }catch (NoResultException nrex){
            return null;
        }
    }

    public UserEntity getUserById(final String userId){
        try{
            return entityManager.createNamedQuery("userById", UserEntity.class).setParameter("id", userId).getSingleResult();
        }catch (NoResultException nrex){
            return null;
        }
    }

    public UserEntity getUserByUuid(final String uuid){
        try{
            return entityManager.createNamedQuery("userByUuid", UserEntity.class).setParameter("uuid", uuid).getSingleResult();
        }catch (NoResultException nrex){
            return null;
        }
    }

    public UserEntity getUserByEmail(final String email){
        try{
            return entityManager.createNamedQuery("userByEmail", UserEntity.class).setParameter("email", email).getSingleResult();
        }catch (NoResultException nrex){
            return null;
        }
    }
}
