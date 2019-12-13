package com.ca.umg.business.model.entity;

import java.util.Arrays;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;
import org.pojomatic.annotations.Property;

import com.ca.framework.core.db.domain.AbstractAuditable;

@Entity
@Table(name = "MODEL_IMPLEMENTATION_TYPE")
public class ModelImplementationType extends AbstractAuditable {

    /**
	 * 
	 */
    private static final long serialVersionUID = 6640180439344616851L;

    @NotNull(message = "Implementation cannot be null")
    @NotBlank(message = "Implementation cannot be blank")
    @Column(name = "IMPLEMENTATION")
    @Property
    private String implementation;

    @Column(name = "TYPE_XSD")
    @Lob
    private byte[] typeXSD;

    public String getImplementation() {
        return implementation;
    }

    public void setImplementation(String implementation) {
        this.implementation = implementation;
    }

    public byte[] getTypeXSD() {
        return typeXSD != null ? Arrays.copyOf(typeXSD, typeXSD.length) : null;
    }

    public void setTypeXSD(byte[] typeXSD) {
        if (typeXSD != null) {
            this.typeXSD = Arrays.copyOf(typeXSD, typeXSD.length);
        }
    }

}
