/**
 * 
 */
package com.ca.umg.business.tenant.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.pojomatic.annotations.Property;

import com.ca.framework.core.db.domain.AbstractAuditable;

/**
 * Holds the system key information.
 * 
 * @author kamathan
 * @version 1.0
 */
@Entity
@Table(name = "SYSTEM_KEY")
public class SystemKey extends AbstractAuditable {

    private static final long serialVersionUID = -1422190455156025962L;

    /**
     * holds key
     */
    @Column(name = "SYSTEM_KEY")
    @Property
    private String key;

    /**
     * holds type
     */
    @Column(name = "KEY_TYPE")
    @Property
    private String type;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
