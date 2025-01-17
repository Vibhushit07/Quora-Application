package com.upgrad.quora.service.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * User Authentication Token Entity class contains all the attributes to be mapped
 * to all the fields in USER_AUTH table in the database.
 * All the annotations which are used to specify all
 * the constraints to the columns in the database must be correctly implemented.
 */

@Entity
@Table(name = "user_auth")
@NamedQueries(
        {
                @NamedQuery(name = "userAuthToken", query = "select uate from UserAuthenticationTokenEntity uate where uate.accessToken = :accessToken")
        }
)
public class UserAuthenticationTokenEntity implements Serializable {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "UUID")
    @NotNull
    @Size(max = 200)
    private String uuid;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private UserEntity userEntity;

    @Column(name = "ACCESS_TOKEN")
    @NotNull
    @Size(max = 500)
    private String accessToken;

    @Column(name = "EXPIRES_AT")
    @NotNull
    private ZonedDateTime expiresAt;

    @Column(name = "LOGIN_AT")
    @NotNull
    private ZonedDateTime loginAt;

    @Column(name = "LOGOUT_AT")
    private ZonedDateTime logoutAt;

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public String getUuid() { return uuid; }

    public void setUuid(String uuid) { this.uuid = uuid; }

    public UserEntity getUserEntity() { return userEntity; }

    public void setUserEntity(UserEntity userEntity) { this.userEntity = userEntity; }

    public String getAccessToken() { return accessToken; }

    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public ZonedDateTime getExpiresAt() { return expiresAt; }

    public void setExpiresAt(ZonedDateTime expiresAt) { this.expiresAt = expiresAt; }

    public ZonedDateTime getLoginAt() { return loginAt; }

    public void setLoginAt(ZonedDateTime loginAt) { this.loginAt = loginAt; }

    public ZonedDateTime getLogoutAt() { return logoutAt; }

    public void setLogoutAt(ZonedDateTime logoutAt) { this.logoutAt = logoutAt; }

    @Override
    public int hashCode() { return new HashCodeBuilder().append(this).hashCode(); }

    @Override
    public boolean equals(Object obj) { return new EqualsBuilder().append(this, obj).isEquals(); }

    @Override
    public String toString() { return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE); }
}
