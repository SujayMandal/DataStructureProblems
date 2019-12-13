/**
 * 
 */
package com.ca.umg.business.version.command.bo;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.umg.business.version.command.dao.CommandDAO;
import com.ca.umg.business.version.command.entity.Command;

/**
 * @author kamathan
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
    public List<String> getAllCommandsByExecutionSequenceForProcess(String process) throws BusinessException, SystemException {
        List<String> commandNames = new LinkedList<String>();
        boolean adminAware = RequestContext.getRequestContext().isAdminAware();
        RequestContext.getRequestContext().setAdminAware(true);
        Order order = new Order(Direction.ASC, "executionSequence");
        List<Command> commands = commandDAO.findByProcess(process, new Sort(order));
        if (CollectionUtils.isNotEmpty(commands)) {
            for (Command command : commands) {
                commandNames.add(command.getName());
            }
        }
        RequestContext.getRequestContext().setAdminAware(adminAware);
        return commandNames;
    }

}
