package com.ca.framework.core.db.domain;

import org.hibernate.envers.RevisionListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Revision Listener.
 * 
 * @author mandavak
 *
 */
public class RevListener implements RevisionListener {
    public void newRevision(Object revisionEntity) {
        RevEntity exampleRevEntity = (RevEntity) revisionEntity;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        exampleRevEntity.setUsername(auth==null?"SYSTEM":auth.getName());
    }
}