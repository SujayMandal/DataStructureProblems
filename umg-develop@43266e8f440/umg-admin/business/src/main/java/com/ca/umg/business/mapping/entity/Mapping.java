package com.ca.umg.business.mapping.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.NotBlank;
import org.pojomatic.annotations.PojomaticPolicy;
import org.pojomatic.annotations.Property;

import com.ca.framework.core.db.domain.MultiTenantEntity;
import com.ca.umg.business.model.entity.Model;

@Entity
@Table(name = "MAPPING")
@Audited
public class Mapping extends MultiTenantEntity {

    private static final long serialVersionUID = 7261007754333803076L;

    @NotNull(message = "Name cannot be null")
    @NotBlank(message = "Name cannot be empty")
    @Column(name = "NAME")
    @Property
    private String name;

    @Column(name = "DESCRIPTION")
    @Property(policy = PojomaticPolicy.TO_STRING)
    private String description;

    @NotNull(message = "Model cannot be null")
    @ManyToOne
    @JoinColumn(name = "MODEL_ID")
    @Property
    private Model model;

    @NotNull(message = "Model IO data cannot be null.")
    @Column(name = "MODEL_IO_DATA")
    @Lob
    private byte[] modelIO;
    
    @Property
    @NotNull(message = "Staus cannot be null.")
    @Column(name = "STATUS")
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public byte[] getModelIO() {
        return modelIO;
    }

    public void setModelIO(byte[] modelIO) {
        this.modelIO = modelIO;
    }
}
