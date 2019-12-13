package com.ca.umg.business.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.NotBlank;
import org.pojomatic.annotations.Property;

import com.ca.framework.core.db.domain.MultiTenantEntity;

/**
 * @author basanaga
 *
 */
@Entity
@Table(name = "MEDIATE_MODEL_LIBRARY")
@Audited
public class MediateModelLibrary extends MultiTenantEntity {

    @NotNull(message = "tar name cannot be null.")
    @NotBlank(message = "tar name cannot be blank.")
    @Column(name = "TAR_NAME")
    @Property
    private String tarName;

    @NotNull(message = "Checksum value name be null.")
    @NotBlank(message = "Checksum value cannot be blank.")
    @Property
    @Column(name = "CHECKSUM_VALUE")
    private String checksum;

    @NotNull(message = "Checksum type name be null.")
    @NotBlank(message = "Checksum type cannot be blank.")
    @Property
    @Column(name = "CHECKSUM_TYPE")
    private String encodingType;

    @NotNull(message = "Model exec env name can not be null")
    @NotBlank(message = "Model exec env id can not be blank")
    @Column(name = "MODEL_EXEC_ENV_NAME")
    @Property
    private String modelExecEnvName;   

    public String getTarName() {
        return tarName;
    }

    public void setTarName(String tarName) {
        this.tarName = tarName;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public String getEncodingType() {
        return encodingType;
    }

    public void setEncodingType(String encodingType) {
        this.encodingType = encodingType;
    }

    public String getModelExecEnvName() {
        return modelExecEnvName;
    }

    public void setModelExecEnvName(String modelExecEnvName) {
        this.modelExecEnvName = modelExecEnvName;
    }

}
