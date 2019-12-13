/**
 * 
 */
package com.fa.dp.business.command.executor;

import java.util.List;

import com.fa.dp.business.command.Command;
import com.fa.dp.core.exception.SystemException;

/**
 * @author mandasuj
 *
 */
public interface CommandExecutor {

    /**
     * Executes the commands submitted to it in the sequence. The first command in the list is executed first and so on.
     * 
     * @param commands
     *            , the list of sequenced commands.
     * @param data
     *            , the data on which the commands will act upon.
     * @return
     * @throws SystemException
     */
    void execute(List<Command> commands, Object data) throws SystemException;
}
