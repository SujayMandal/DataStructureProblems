/**
 *
 */
package com.fa.dp.business.command;

import com.fa.dp.core.exception.SystemException;

/**
 * @author mandasuj
 *
 */
public interface CommandPreparator {

	/**
	 * This method would prepare the command bean for execution using the command name. The beans are of type prototype. Be sure
	 * to mark the command implementations are prototypes.
	 *
	 * @param commandName
	 *            , the name of the command to be created.
	 *
	 * @return {@link Command} prepared and ready for use.
	 * @throws SystemException
	 */
	Command prepareCommand(String commandName) throws SystemException;

}
