package com.ca.umg.sdc.rest.constants;

public final class ModelConstants {
    public static final String MODEL_URL = "/model";
    public static final String LIST_ALL_MODEL_LIBS = "/listAllModelLibs";
    public static final String LIST_ALL_MODEL_LIB_HIERARCHY = "/listAllModelLibHierarchy";
    public static final String DELETE_MODEL_LIBRARY = "/deleteModelLibrary";
    public static final String LIST_ALL = "/listAll";
    public static final String LIST_MGINFO = "/listMGInfo";
    public static final String ADD_MODEL_LIBRARY = "/addModelLibrary";
    public static final String ADD_R_MODEL_LIBRARY = "/rUpload";
    public static final String CREATE_MODEL = "/createModel";
    public static final String DELETE_MODEL = "/deleteModel/{id}";
    public static final String FETCH_MODEL_DETAIL = "/fetchModelDetail/{id}";
    public static final String FETCH_MODEL_LIBRARY_DETAIL = "/fetchModelLibraryDetail";
    public static final String MODEL_DELETE_MESSAGE = "Model has been deleted";
    public static final String MODEL_DETAILS_NOT_FOUND = "Model Details not found.";
    public static final String MODEL_LIB_DETAILS_NOT_FOUND = "Model Library Details not found.";
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    public static final String ALLOWNULL = "allowNull";
    public static final String XMLPATH = "xmlPath";
    public static final String DOCUMENTATION = "documentation";
    public static final String MODEL_DELETION_FAILURE = "Model deletion has failed";
    public static final String MODEL_DOWNLOAD_URL = "/modelDownload";
    public static final String MODEL_LIBRARY_NOT_FOUND = "The required model library doesn't exist in the repository";
    public static final String MODEL_DOC_NOT_FOUND = "The required model documentation doesn't exist in the repository";
    public static final String MODEL_NOT_FOUND = "The required model doesn't exist";
    public static final String MODEL_MANIFEST_NOT_FOUND = "The required manifest doesn't exist in the repository";
    public static final String MODEL_RPT_TMPL_NOT_FOUND = "Model report template doesn't exist in the repository";
    public static final String MODEL_RPT_TMPL_DOES_NOT = "This Model does not have a report";

    public static final String CONTENT_DISPOSITION = "Content-Disposition";

    private ModelConstants() {

    }
}
