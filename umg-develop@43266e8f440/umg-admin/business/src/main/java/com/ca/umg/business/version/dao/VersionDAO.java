package com.ca.umg.business.version.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ca.umg.business.version.entity.Version;

public interface VersionDAO extends JpaRepository<Version, String>, JpaSpecificationExecutor<Version> {

    static final String TENANT_MODEL_NAME = "tenantModelName";
    static final String TID_NAME = "tidName";

    /**
     * 
     */
    @Override
    List<Version> findAll(Specification<Version> spec);

    @Override
    Page<Version> findAll(Specification<Version> spec, Pageable pageable);

    /**
     * Find all distinct libraries from version table and retrieve in pages
     * 
     * @param pageable
     * @return
     */
    @Query("SELECT DISTINCT V.modelLibrary.name FROM Version V where lower(V.modelLibrary.name) like :libraryNameStr")
    Page<String> findAllLibraries(@Param("libraryNameStr") String libraryNameStr, Pageable pageable);

    /**
     * Find all distinct model names for a given library from version table and retrieve in pages
     * 
     * @param libraryName
     * @param pageable
     * @return
     */
    @Query("SELECT DISTINCT V.mapping.model.name FROM #{#entityName} V where V.modelLibrary.name = :libraryName")
    Page<String> findAllModels(@Param("libraryName") String libraryName, Pageable pageable);

    /**
     * Find all versions for given library name and model name and retrieve in pages
     * 
     * @param libraryName
     * @param modelName
     * @param pageable
     * @return
     */
    @Query("SELECT V FROM Version V where V.modelLibrary.name = :libraryName and V.mapping.model.name = :modelName and (lower(V.name) like %:searchText% OR lower(V.versionDescription) like %:searchText%)")
    Page<Version> findByLibraryNameAndModelNameAndSearchTextInNameOrDescription(@Param("libraryName") String libraryName,
            @Param("modelName") String modelName, @Param("searchText") String searchText, Pageable pageable);

    @Query(value = "SELECT MAX(minorVersion) FROM Version ver WHERE ver.majorVersion = :majorVersion and ver.name = :tenantModelName")
    Integer getMaxMinorVersionForGivenMajorVersionAndTenantModelName(@Param("majorVersion") Integer majorVersion,
            @Param(TENANT_MODEL_NAME) String tenantModelName);

    /**
     * This method will fetch the list of Model libraries associated with the Version and having status pulished or deactivated
     * 
     * @param libraryId
     * @param published
     * @param deactivated
     * @return
     */
    @Query("SELECT V FROM Version V where V.modelLibrary.id = :libraryId and V.status != :deleted")
    List<Version> findVersionsOnModelLibAndStatus(@Param("libraryId") String libraryId, @Param("deleted") String deleted);

    @Query(value = "SELECT MAX(majorVersion) FROM Version ver WHERE ver.name = :tenantModelName")
    Integer getMaxMajorVersionForTenantModelName(@Param(TENANT_MODEL_NAME) String tenantModelName);

    @Query(value = "SELECT distinct(majorVersion) FROM Version ver WHERE ver.name = :tenantModelName")
    List<Integer> getMajorVersionsForTenantModelName(@Param(TENANT_MODEL_NAME) String tenantModelName);

    Version findByNameAndMappingNameAndModelLibraryUmgName(String tenantModelName, String mappingName, String libraryUmgName);

    /**
     * Returns the list of versions for defined TID Name having status as published or deactivated.
     * 
     * @param model
     * @return
     */
    @Query("select u from #{#entityName} u where u.mapping.name = :tidName and (u.status = :published or u.status = :deactivated)")
    List<Version> findByMappingName(@Param(TID_NAME) String tidName, @Param("published") String published,
            @Param("deactivated") String deactivated);

    /**
     * get the version for tidname having status as saved or tested
     * 
     * @param tidName
     * @param saved
     * @param tested
     * @return
     */
    @Query("select u.name from #{#entityName} u where u.mapping.name = :tidNme and (u.status = :saved or u.status = :tested)")
    List<String> getVersionStatus(@Param("tidNme") String tidNme, @Param("saved") String saved, @Param("tested") String tested);
    

    /**
     * Returns the list of versions having status as not deleted for given tid name
     * 
     * @param tidName
     * @param deleted
     * @return
     */
    @Query("select u from #{#entityName} u where u.mapping.name = :tidName and (u.status != :deleted )")
    List<Version> findNotDeletedVersions(@Param(TID_NAME) String tidName, @Param("deleted") String deleted);

    /**
     * Returns list of versions having status as tested for given tid name
     * 
     * @param tidName
     * @param tested
     * @return
     */
    @Query("select u.id from #{#entityName} u where u.mapping.name = :tidName and u.status = :tested ")
    List<String> getTestedVersions(@Param(TID_NAME) String tidName, @Param("tested") String tested);

    @Query("select name, description from Version group by name, description")
    List<Object[]> getNameAndDescriptionOfAllVersions();

    @Query("select distinct(name) from Version")
    List<String> getAllTenantModelNames();

    @Query("select ver.description from Version ver where ver.name = :tenantModelName")
    List<String> getTenantModeldescriptions(@Param(TENANT_MODEL_NAME) String tenantModelName);

    @Query("select distinct(ver.mapping.model.name) from Version ver where ver.modelLibrary.name = :libraryName and (lower(ver.name) like %:searchText% OR lower(ver.versionDescription) like %:searchText%)")
    List<String> getModelNamesForLibraryNameAndCharsInNameOrDescription(@Param("libraryName") String libraryName,
            @Param("searchText") String searchText, Sort sort);

    /**
     * This method will retrieve all major&minor version and return paginated data back with page info
     */
    @Query("SELECT V FROM Version V where V.name = :versionName ")
    Page<Version> findAllversionByVersionName(@Param("versionName") String modelName, Pageable pageable);

    @Query("select distinct(name) from Version V where V.modelLibrary.modelExecEnvName = :executionEnvironment order by LOWER(V.name)")
    List<String> getAllTenantModelNamesByEnv(@Param("executionEnvironment") String executionEnv);
    
    @Query("SELECT V FROM Version V where lower(V.name) = :name and V.majorVersion = :majorVersion and V.minorVersion = :minorVersion")
    Version findByNameAndMajorVersionAndMinorVersion(@Param("name") String name,
            @Param("majorVersion") Integer majorVersion, @Param("minorVersion") Integer minorVersion);
}
