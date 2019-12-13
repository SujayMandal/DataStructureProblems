/**
 * 
 */
package com.ca.umg;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.ca.framework.core.exception.SystemException;
import com.ca.umg.config.UmgSchedulerConfig;
import com.ca.umg.file.UmgFileProcessor;

/**
 * 
 * @author kamathan
 *
 */
public class UmgSchedulerLauncher {

    private static final Logger LOGGER = LoggerFactory.getLogger(UmgSchedulerLauncher.class);

    /**
     * 
     * @param args
     * @throws SystemException
     * @throws InterruptedException
     * @throws IOException
     */
    public static void main(String[] args) {

        final AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(UmgSchedulerConfig.class);

        final UmgFileProcessor umgFileProcessor = ctx.getBean(UmgFileProcessor.class);

        try {
            umgFileProcessor.initialize();
        } catch (SystemException e) {
            LOGGER.error("An error occured while launching UMG Scheduler.");
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                ctx.close();
            }
        });
    }

}
