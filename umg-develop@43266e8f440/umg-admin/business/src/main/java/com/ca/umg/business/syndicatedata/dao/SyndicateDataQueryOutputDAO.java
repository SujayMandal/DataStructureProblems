package com.ca.umg.business.syndicatedata.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ca.umg.business.syndicatedata.entity.SyndicateDataQueryOutput;

@Repository
public interface SyndicateDataQueryOutputDAO extends JpaRepository<SyndicateDataQueryOutput, String> {

    @Query("DELETE FROM SyndicateDataQueryOutput INP where INP.query.id = ?1")
    void deleteByQueryId(String id);

}
