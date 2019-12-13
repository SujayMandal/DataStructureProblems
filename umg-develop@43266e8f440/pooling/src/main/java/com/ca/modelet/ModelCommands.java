package com.ca.modelet;

@SuppressWarnings("PMD")
public enum ModelCommands {

    EXECUTE("EXECUTE"),

    LOAD_MODEL("LOAD_MODEL"),

    UNLOAD_MODEL("UNLOAD_MODEL"),

    DESTROY_SERVER("DESTROY_SERVER"),

    START_RSERVE("START_RSERVE"),

    STOP_RSERVE("STOP_RSERVE"),
    
    GET_STATUS("GET_STATUS");

    private final String commandName;

    private ModelCommands(final String commandName) {
        this.commandName = commandName;
    }

    public String getCommandName() {
        return commandName;
    }

    public static ModelCommands getModelCommands(final String commandName) {
        ModelCommands modelCommand = null;
        switch (valueOf(commandName)) {
        case EXECUTE:
            modelCommand = EXECUTE;
            break;
        case LOAD_MODEL:
            modelCommand = LOAD_MODEL;
            break;
        case UNLOAD_MODEL:
            modelCommand = UNLOAD_MODEL;
            break;
        case DESTROY_SERVER:
            modelCommand = DESTROY_SERVER;
            break;
        case STOP_RSERVE:
            modelCommand = STOP_RSERVE;
            break;
        case START_RSERVE:
            modelCommand = START_RSERVE;
            break;
        case GET_STATUS:
        	modelCommand = GET_STATUS;
        	break;
        default:
            break;
        }
        return modelCommand;
    }
}