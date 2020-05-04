package com.upgrad.quora.service.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * Question Entity class contains all the attributes to be mapped to
 * all the fields in QUESTION table in the database.
 * All the annotations which are used to specify all the
 * constraints to the columns in the database must be correctly implemented.
 */
@Entity
@Table(name = "question")
@NamedQueries(
        {
                @NamedQuery(name = "getAllQuestionsById", query = "select qe from QuestionEntity qe where qe.id = :questionId"),
                @NamedQuery(name = "getAllQuestionsByUuid", query = "select qe from QuestionEntity qe where qe.uuid = :Uuid"),
                @NamedQuery(name = "getAllQuestionsByUser", query = "select qe from QuestionEntity qe where qe.userEntity.id = :userId"),
                @NamedQuery(name = "getAllQuestions", query = "select qe from QuestionEntity qe")
        }
)
public class QuestionEntity implements Serializable {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "UUID")
    @NotNull
    private String uuid;

    @Column(name = "CONTENT")
    @NotNull
    private String content;

    @Column(name = "DATE")
    @NotNull
    private ZonedDateTime date;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private UserEntity userEntity;

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public String getUuid() { return uuid; }

    public void setUuid(String uuid) { this.uuid = uuid; }

    public String getContent() { return content; }

    public void setContent(String content) { this.content = content; }

    public ZonedDateTime getDate() { return date; }

    public void setDate(ZonedDateTime date) { this.date = date; }

    public UserEntity getUserEntity() { return userEntity; }

    public void setUserEntity(UserEntity userEntity) { this.userEntity = userEntity; }

    @Override
    public int hashCode() { return new HashCodeBuilder().append(this).hashCode(); }

    @Override
    public boolean equals(Object obj) { return new EqualsBuilder().append(this, obj).isEquals(); }

    @Override
    public String toString() { return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE); }
}
