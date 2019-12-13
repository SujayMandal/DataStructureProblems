package com.ca.umg.business.syndicatedata.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ca.umg.business.syndicatedata.entity.SyndicateDataQueryInput;

@Repository
public interface SyndicateDataQueryInputDAO extends JpaRepository<SyndicateDataQueryInput, String> {

    @Query("DELETE FROM SyndicateDataQueryInput INP where INP.query.id = ?1")
    void deleteByQueryId(String id);

}
