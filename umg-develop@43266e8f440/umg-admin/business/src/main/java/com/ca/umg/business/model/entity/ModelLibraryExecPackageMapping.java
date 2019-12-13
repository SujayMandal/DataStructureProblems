/**
 * 
 */
package com.ca.umg.business.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.NotBlank;
import org.pojomatic.annotations.Property;

import com.ca.framework.core.db.domain.MultiTenantEntity;

/**
 * @author kamathan
 *
 */
@Entity
@Table(name = "MODEL_LIB_EXEC_PKG_MAPPING")
@Audited
public class ModelLibraryExecPackageMapping extends MultiTenantEntity {

	private static final long serialVersionUID = -98092984707447798L;

	@ManyToOne
	@JoinColumn(name = "MODEL_LIBRARY_ID", nullable = false)
	@Property
	private ModelLibrary modelLibrary;

    @NotNull(message = "Model exec pk id cannot be null")
    @NotBlank(message = "Model exec pk id cannot be blank")
    @Column(name = "MODEL_EXEC_PKG_ID", nullable = false)
	@Property
    private String modelExecPackageId;


    @NotNull(message = "exec sequence cannot be null")
    @NotBlank(message = "exec sequence cannot be blank")
    @Column(name = "EXEC_SEQUENCE")
    @Property
    private String execSequence;

    public ModelLibrary getModelLibrary() {
		return modelLibrary;
	}

	public void setModelLibrary(ModelLibrary modelLibrary) {
		this.modelLibrary = modelLibrary;
	}


    public String getExecSequence() {
        return execSequence;
    }

    public void setExecSequence(String execSequence) {
        this.execSequence = execSequence;
    }

    public String getModelExecPackageId() {
        return modelExecPackageId;
    }

    public void setModelExecPackageId(String modelExecPackageId) {
        this.modelExecPackageId = modelExecPackageId;
    }
}
