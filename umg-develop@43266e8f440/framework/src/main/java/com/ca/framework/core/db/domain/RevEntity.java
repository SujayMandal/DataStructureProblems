package com.ca.framework.core.db.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

/**
 * Revision Entity.
 * 
 * @author mandavak
 *
 */
@Entity
@RevisionEntity(RevListener.class)
@Table(name="REVINFO")
public class RevEntity {
    @Id
    @GeneratedValue
    @RevisionNumber
    @Column(name="REV")
    private int rev;

    @RevisionTimestamp
    @Column(name="REVTSTMP")
    private long timeStamp;

    @Column(name="REVBY")
    private String username;

    /**
     * @return the rev
     */
    public int getRev() {
        return rev;
    }

    /**
     * @param rev the rev to set
     */
    public void setRev(int rev) {
        this.rev = rev;
    }

    /**
     * @return the timeStamp
     */
    public long getTimeStamp() {
        return timeStamp;
    }

    /**
     * @param timeStamp the timeStamp to set
     */
    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }
}