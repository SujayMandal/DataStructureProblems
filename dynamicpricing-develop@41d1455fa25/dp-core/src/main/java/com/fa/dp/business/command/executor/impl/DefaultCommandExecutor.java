/**
 *
 */
package com.fa.dp.business.command.executor.impl;

import com.fa.dp.business.command.Command;
import com.fa.dp.business.command.executor.CommandExecutor;
import com.fa.dp.core.exception.SystemException;
import org.apache.commons.collections4.CollectionUtils;

import javax.inject.Named;
import java.util.List;

/**
 * @author mandasuj
 */
@Named
public class DefaultCommandExecutor implements CommandExecutor {

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.ca.umg.business.version.command.executor.CommandExecutor#execute(com.ca.umg.business.version.command.error.ErrorController
	 * , java.util.List)
	 */
	@Override
	public void execute(List<Command> commands, Object data) throws SystemException {
		if (CollectionUtils.isNotEmpty(commands)) {
			for (Command command : commands) {
				command.execute(data);
			}
		}
	}

}
