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
 * AnswerEntity class contains all the attributes to be mapped to all the
 * fields in ANSWER table in the database.
 * All the annotations which are used to specify all the constraints
 * to the columns in the database must be correctly implemented.
 */

@Entity
@Table(name = "answer")
@NamedQueries(
        {
                @NamedQuery(name = "getAllAnswer", query = "select u from AnswerEntity u "),
                @NamedQuery(name = "answerByUUID", query = "select u from AnswerEntity u where u.uuid =:UUID"),
                @NamedQuery(name = "getAllAnswerByUser", query = "select u from AnswerEntity u where u.user.id =:UserID"),
                @NamedQuery(name = "getAllAnswerByQuestion", query = "select u from AnswerEntity u where u.question.uuid =:QuestionId")

        }
)
public class AnswerEntity implements Serializable{

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "UUID")
    @NotNull
    private String uuid;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "QUESTION_ID")
    private QuestionEntity question;

    @Column(name = "ANS")
    @NotNull
    private String content;

    @Column(name = "DATE")
    private ZonedDateTime date;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public QuestionEntity getQuestion() {
        return question;
    }

    public void setQuestion(QuestionEntity question) {
        this.question = question;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }
    @Override
    public boolean equals(Object obj) {
        return new EqualsBuilder().append(this, obj).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this).hashCode();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

}
