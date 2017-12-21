/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2015, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.as.quickstarts.mdb.status.logging;

import java.lang.management.ManagementFactory;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Formatter;
import java.util.logging.Level;

import javax.annotation.PostConstruct;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.jboss.logmanager.ExtHandler;
import org.jboss.logmanager.ExtLogRecord;

/**
 * @author bmaxwell
 *
 */
public class LogMonitorHandler extends ExtHandler implements LogMonitorMXBean {

    /*
     * 
     <module xmlns="urn:jboss:module:1.1" name="org.jboss.logmanager">
        <resources>
            <resource-root path="jboss-logmanager-1.5.4.Final-redhat-1.jar"/>
            <!-- Insert resources here -->
        </resources>

        <dependencies>
            <module name="javax.api"/>
            <module name="org.jboss.modules"/>
            <module name="org.jboss.as.logging" services="import"/>
        </dependencies>
    </module>
     */
    
    // ADD 
    // module add --name=test --slot=main --dependencies=javax.api,org.jboss.modules,org.jboss.as.logging,org.jboss.logmanager --resources=common.jar
    // /subsystem=logging/custom-handler=LogMonitor:add(module=test, class=com.redhat.middleware.gss.logging.LogMonitorHandler, enabled=true)
    // /subsystem=logging/root-logger=ROOT:add-handler(name=LogMonitor)
    // <custom-handler name="LogMonitor" class="com.redhat.middleware.gss.logging.LogMonitorHandler" module="test"/>

    // REMOVE
    // /subsystem=logging/root-logger=ROOT:remove-handler(name=LogMonitor)
    // /subsystem=logging/custom-handler=LogMonitor:remove()
    // module remove --name=system.layers.base.test --slot=main
    
    public static void main(String [] args) {
        System.out.println("INFO: " + Level.INFO.intValue());
        System.out.println("WARNING: " + Level.WARNING.intValue());
        System.out.println("SEVERE: " + Level.SEVERE.intValue());
    }
    
    
    
    private ObjectName objectName;
    private MBeanServer platformMBeanServer;
    
    private String[] categories = new String[0];
    
    private ExtLogRecord logRecord = null;
    
    private boolean initialized = false;
    
    private Set<String> loggers = new HashSet<>();
    private HornetqServerStatus status = new HornetqServerStatus();
    
    @PostConstruct
    private void init() throws Exception {
    		loggers.add("org.hornetq.core.server");
    		loggers.add("org.jboss.msc");
        platformMBeanServer = ManagementFactory.getPlatformMBeanServer();
        objectName = new ObjectName(LogMonitorMXBean.OBJECT_NAME);
        platformMBeanServer.registerMBean(this, objectName);
        initialized = true;
    }
    
    @Override
    public void setFormatter(Formatter newFormatter) throws SecurityException {
        try {
            if(!initialized)
                init();
        } catch(Exception e) {
            e.printStackTrace();
        }
        super.setFormatter(newFormatter);
    }
    
    
    
    @Override
    protected void doPublish(ExtLogRecord record) {
        
//    	08/22/17 23:02:19,833 INFO  [org.jboss.modules] (main) JBoss Modules version 1.3.8.Final-redhat-1
//    	08/22/17 23:02:20,077 INFO  [org.jboss.msc] (main) JBoss MSC version 1.1.7.SP1-redhat-1
//    	08/22/17 23:02:20,140 INFO  [org.jboss.as] (MSC service thread 1-7) JBAS015899: JBoss EAP 6.4.15.GA (AS 7.5.15.Final-redhat-3) starting    		    
    		if(record.getLevel().intValue() >= Level.INFO.intValue()) {   
	    		if(loggers.contains(record.getLoggerName())) {
	    			switch(record.getMessage()) {
		    			case "org.hornetq.core.server":
		    				if(record.getMessage().contains("HQ221027")) // bridge
			    				status.getBridges().add(record);
		    				else if(record.getMessage().contains("HQ221000")) // live or backup started with config
		    					status.getServerConfigs().add(record);
		    				else if(record.getMessage().contains("HQ221035") || record.getMessage().contains("HQ221010")) // locks obtained
		    					status.getLocksObtained().add(record);
		    			break;
		    			case "org.jboss.as": 
		    				if(record.getMessage().contains("JBAS015899")) // restart
		    					status = new HornetqServerStatus();		
		    			break;
	    			}	    				    				    				
	    		}
    		}
    	
        // check for WARN , ERROR or worse
//        if(failTheTest == false) {
//            if(failOnWarn && record.getLevel().intValue() >= Level.WARNING.intValue()) {
//                failTheTest = true;
//                failedDate = new Date(record.getMillis());
//                failTheTestMessage = "[" + record.getLevel().getName() + "]" + record.getFormattedMessage();
//                this.logRecord = record;
//            }
//            else if(failOnError && record.getLevel().intValue() >= Level.SEVERE.intValue()) {
//                failTheTest = true;
//                failedDate = new Date(record.getMillis());
//                failTheTestMessage = "[" + record.getLevel().getName() + "]" + record.getFormattedMessage();
//                this.logRecord = record;
//            }
//        }
    }
}
