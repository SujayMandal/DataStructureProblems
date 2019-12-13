package com.ca.umg.business.syndicatedata.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.envers.Audited;
import org.pojomatic.annotations.Property;
import org.springframework.beans.BeanUtils;

import com.ca.framework.core.db.domain.MultiTenantEntity;
import com.ca.umg.business.mapping.entity.Mapping;

@Entity
@Table(name = "SYNDICATE_DATA_QUERY")
@Audited
public class SyndicateDataQuery extends MultiTenantEntity {

    private static final long serialVersionUID = 1L;

    @Property
    @Column(name = "NAME")
    private String name;

    @Property
    @Column(name = "DESCRIPTION")
    private String description;

    @Property
    @Column(name = "EXEC_SEQUENCE")
    private Integer execSequence;

    @Property
    @ManyToOne
    @JoinColumn(name = "MAPPING_ID", referencedColumnName = "ID")
    private Mapping mapping;

    @Property
    @Column(name = "MAPPING_TYPE")
    private String mappingType;

    @Property
    @Embedded
    private SyndicateDataQueryObject queryObject;

    @OneToMany(mappedBy = "query", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<SyndicateDataQueryInput> inputParameters;

    @OneToMany(mappedBy = "query", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<SyndicateDataQueryOutput> outputParameters;

    @Property
    @Column(name = "ROW_TYPE")
    private String rowType;

    @Property
    @Column(name = "DATA_TYPE")
    private String dataType;

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

    public Integer getExecSequence() {
        return execSequence;
    }

    public void setExecSequence(Integer execSequence) {
        this.execSequence = execSequence;
    }

    public Set<SyndicateDataQueryInput> getInputParameters() {
        return inputParameters;
    }

    public void setInputParameters(Set<SyndicateDataQueryInput> inputParameters) {
        this.inputParameters = inputParameters;
    }

    public Set<SyndicateDataQueryOutput> getOutputParameters() {
        return outputParameters;
    }

    public void setOutputParameters(Set<SyndicateDataQueryOutput> outputParameters) {
        this.outputParameters = outputParameters;
    }

    public SyndicateDataQueryObject getQueryObject() {
        return queryObject;
    }

    public void setQueryObject(SyndicateDataQueryObject queryObject) {
        this.queryObject = queryObject;
    }

    public Mapping getMapping() {
        return mapping;
    }

    public void setMapping(Mapping mapping) {
        this.mapping = mapping;
    }

    public String getMappingType() {
        return mappingType;
    }

    public void setMappingType(String mappingType) {
        this.mappingType = mappingType;
    }

    public String getRowType() {
        return rowType;
    }

    public void setRowType(String rowType) {
        this.rowType = rowType;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
    
    public SyndicateDataQuery copyAsNonEntity() {
        SyndicateDataQuery targetQuery = new SyndicateDataQuery();
        BeanUtils.copyProperties(this, targetQuery, "id", "inputParameters", "outputParameters");
        if(CollectionUtils.isNotEmpty(this.getInputParameters())) {
            Set<SyndicateDataQueryInput> inputs = new HashSet<>();
            SyndicateDataQueryInput targetInput = null;
            for(SyndicateDataQueryInput input: this.getInputParameters()) {
                targetInput = new SyndicateDataQueryInput();
                BeanUtils.copyProperties(input, targetInput, "id");
                targetInput.setQuery(targetQuery);
                inputs.add(targetInput);
            }
            targetQuery.setInputParameters(inputs);
        }
        if(CollectionUtils.isNotEmpty(this.outputParameters)) {
            Set<SyndicateDataQueryOutput> outputs = new HashSet<>();
            SyndicateDataQueryOutput targetOutput = null;
            for(SyndicateDataQueryOutput output: this.getOutputParameters()) {
                targetOutput = new SyndicateDataQueryOutput();
                BeanUtils.copyProperties(output, targetOutput, "id");
                targetOutput.setQuery(targetQuery);
                outputs.add(targetOutput);
            }
            targetQuery.setOutputParameters(outputs);
        }
        return targetQuery;
    }

}
