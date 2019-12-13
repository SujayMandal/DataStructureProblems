/**
 * 
 */
package com.ca.umg.business.version.command.info;

/**
 * @author chandrsa
 *
 */
public enum CommandProcess {

    CREATE("CREATE"), EDIT("EDIT");

    private String commmandProcess;

    private CommandProcess(String commmandProcess) {
        this.commmandProcess = commmandProcess;
    }

    public String getCommmandProcess() {
        return commmandProcess;
    }

    public void setCommmandProcess(String commmandProcess) {
        this.commmandProcess = commmandProcess;
    }
}
