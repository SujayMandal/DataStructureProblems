package com.ca.umg.business.version.command.dao;

import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ca.umg.business.version.command.entity.Command;

@ContextHierarchy({ @ContextConfiguration("classpath:root-db-context.xml"), @ContextConfiguration })
@RunWith(SpringJUnit4ClassRunner.class)
public class CommandDAOTest {

    @Inject
    private CommandDAO commandDao;

    @Test
    public void testRepository() {
        Command commandSequence = new Command();
        commandSequence.setExecutionSequence(1);
        commandSequence = commandDao.save(commandSequence);
        assertNotNull(commandSequence);
        assertNotNull(commandSequence.getId());
    }
}
