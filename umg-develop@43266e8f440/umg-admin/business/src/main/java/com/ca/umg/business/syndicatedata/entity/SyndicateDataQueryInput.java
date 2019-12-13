package com.ca.umg.business.syndicatedata.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.envers.Audited;
import org.pojomatic.annotations.Property;

import com.ca.framework.core.db.domain.MultiTenantEntity;

@Entity
@Table(name = "SYNDICATE_DATA_QUERY_INPUTS")
@Audited
public class SyndicateDataQueryInput extends MultiTenantEntity {

    private static final long serialVersionUID = 3110958980196192278L;

    @Property
    @Column(name = "NAME")
    private String name;

    @Property
    @Column(name = "DATA_TYPE")
    private String dataType;

    @Property
    @ManyToOne
    @JoinColumn(name = "SYNDICATE_DATA_QUERY_ID")
    private SyndicateDataQuery query;

    @Property
    @Column(name = "SAMPLE_VALUE")
    private String sampleValue;
    
    @Property
    @Column(name = "DATATYPE_FORMAT")
    private String dataTypeFormat;
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public SyndicateDataQuery getQuery() {
        return query;
    }

    public void setQuery(SyndicateDataQuery query) {
        this.query = query;
    }

    public String getSampleValue() {
        return sampleValue;
    }

    public void setSampleValue(String sampleValue) {
        this.sampleValue = sampleValue;
    }

	public String getDataTypeFormat() {
		return dataTypeFormat;
	}

	public void setDataTypeFormat(String dataTypeFormat) {
		this.dataTypeFormat = dataTypeFormat;
	}

}
