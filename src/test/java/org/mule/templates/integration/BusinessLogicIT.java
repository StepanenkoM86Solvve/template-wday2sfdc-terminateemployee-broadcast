/**
 * Mule Anypoint Template
 *
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 */

package org.mule.templates.integration;

import java.util.Calendar;
import java.util.Date;

import com.mulesoft.module.batch.BatchTestHelper;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mule.context.notification.NotificationException;

/**
 * The objective of this class is to validate the correct behavior of the flows
 * for this Anypoint Template that make calls to external systems.
 */
public class BusinessLogicIT extends AbstractTemplateTestCase {

    private static final long TIMEOUT_MILLIS = 180 *1000;
    private static final long DELAY_MILLIS = 500;
    private BatchTestHelper helper;

    @BeforeClass
    public static void beforeTestClass() {
        System.setProperty("poll.startDelayMillis", "8000");
        System.setProperty("poll.frequencyMillis", "30000");
        Date initialDate = new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 100);
        Calendar cal = Calendar.getInstance();
        cal.setTime(initialDate);
        System.setProperty(
        		"watermark.default.expression", 
        		"#[groovy: new GregorianCalendar("
        				+ cal.get(Calendar.YEAR) + ","
        				+ cal.get(Calendar.MONTH) + ","
        				+ cal.get(Calendar.DAY_OF_MONTH) + ","
        				+ cal.get(Calendar.HOUR) + ","
        				+ cal.get(Calendar.MINUTE) + ","
        				+ cal.get(Calendar.SECOND) + ") ]");
    }

    @Before
    public void setUp() throws Exception {
        stopFlowSchedulers(POLL_FLOW_NAME);

        helper = new BatchTestHelper(muleContext);
        registerListeners();
    }

    private void registerListeners() throws NotificationException {
        muleContext.registerListener(pipelineListener);
    }

    @Test
    public void testMainFlow() throws Exception {
        runSchedulersOnce(POLL_FLOW_NAME);
        waitForPollToRun();
        helper.awaitJobTermination(TIMEOUT_MILLIS, DELAY_MILLIS);
        helper.assertJobWasSuccessful();
    }
}
