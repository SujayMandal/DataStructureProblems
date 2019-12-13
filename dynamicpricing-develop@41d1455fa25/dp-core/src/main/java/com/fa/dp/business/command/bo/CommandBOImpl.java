/**
 * 
 */
package com.fa.dp.business.command.bo;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import com.fa.dp.business.command.dao.CommandDAO;
import com.fa.dp.business.command.entity.Command;
import com.fa.dp.core.exception.SystemException;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

/**
 * @author mandasuj
 *
 */
@Named
public class CommandBOImpl implements CommandBO {

    @Inject
    private CommandDAO commandDAO;

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.business.version.command.bo.CommandSequenceBO#getAllCommandsByExecutionSequenceForProcess(java.lang.String)
     */
    @Override
    public List<String> getAllCommandsByExecutionSequenceForProcess(String process) throws SystemException {
        List<String> commandNames = new LinkedList<String>();
        List<Command> commands = commandDAO.findByProcessOrderByExecutionSequence(process);
        if (CollectionUtils.isNotEmpty(commands)) {
            for (Command command : commands) {
            	if(command.isActive()){
            		commandNames.add(command.getName());
            	}
            }
        }
        return commandNames;
    }

}
