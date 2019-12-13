/**
 * 
 */
package com.ca.umg.business.model.entity;

import java.util.Arrays;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.NotBlank;

import com.ca.framework.core.db.domain.MultiTenantEntity;

/**
 * @author kamathan
 *
 */
@Entity
@Table(name = "MODEL_DEFINITION")
@Audited
public class ModelDefinition extends MultiTenantEntity {

    private static final long serialVersionUID = 2196345324800597758L;

    @NotNull(message = "Model cannot be null")
    @OneToOne(orphanRemoval = true)
    @JoinColumn(name = "MODEL_ID")
    private Model model;

    @NotNull(message = "Type cannot be null")
    @NotBlank(message = "Type cannot be blank.")
    @Column(name = "IO_TYPE")
    private String type;

    @NotNull(message = "IO definition cannot be null.")
    @Column(name = "IO_DEFINITION")
    @Lob
    private byte[] ioDefinition;

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public byte[] getIoDefinition() {
        return ioDefinition != null ? Arrays.copyOf(ioDefinition, ioDefinition.length) : null;
    }

    public void setIoDefinition(byte[] ioDefinition) {
        if (ioDefinition != null) {
            this.ioDefinition = Arrays.copyOf(ioDefinition, ioDefinition.length);
        }
    }
}
