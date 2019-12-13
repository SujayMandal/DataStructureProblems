/**
 * 
 *//*
package com.ca.umg.file;

import static org.junit.Assert.fail;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.inject.Inject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ca.framework.core.exception.SystemException;

*//**
 * @author kamathan
 *
 *//*
@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class UmgFilePollerTest {

    @Inject
    private UmgFilePoller UmgFilePoller;

    @Value("${sanBase}")
    private String sanBasePath;

    private File bulkDir;

    *//**
     * @throws java.lang.Exception
     *//*
    @Before
    public void setUp() throws Exception {
        File sanBase = new File(sanBasePath);
        if (!sanBase.exists()) {
            sanBase.mkdir();
        }

        bulkDir = new File(sanBase, File.separator + "dummyTenant" + File.separator + "bulk" + File.separator + "input");
        bulkDir.mkdirs();
    }

    @After
    public void tearDown() {
        deleteSanBase(new File(sanBasePath));
    }

    private void deleteSanBase(File sanBase) {
        if (sanBase.isDirectory()) {
            for (File c : sanBase.listFiles())
                deleteSanBase(c);
        }
        sanBase.delete();
    }

    *//**
     * Test method for {@link com.ca.umg.file.UmgFilePoller#registerDirectoriesForPolling()}.
     *//*
    @Test
    public void testRegisterDirectoriesForPolling() {
        try {
            UmgFilePoller.registerDirectoriesForPolling();
        } catch (SystemException e) {
            fail(e.getMessage());
        }
    }

    *//**
     * Test method for {@link com.ca.umg.file.UmgFilePoller#poll()}.
     *//*
    @Test
    public void testPoll() {
        try {
            UmgFilePoller.registerDirectoriesForPolling();

            Runnable runnable = new Runnable() {

                @Override
                public void run() {
                    try {
                        UmgFilePoller.poll();
                    } catch (SystemException e) {
                        fail(e.getMessage());
                    }
                }
            };

            new Thread(runnable).start();
            writeFile();
            Thread.sleep(1000l);
        } catch (SystemException e) {
            fail(e.getMessage());
        } catch (InterruptedException e) {
            fail(e.getMessage());
        }
    }

    private void writeFile() {

        Writer writer = null;

        try {
            writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(bulkDir + File.separator + "sample.json"), "utf-8"));
            writer.write("Sample file");
        } catch (IOException e) {
            fail(e.getMessage());
        } finally {
            try {
                writer.close();
            } catch (Exception ex) {
                 ignore }
        }

    }

}
*/