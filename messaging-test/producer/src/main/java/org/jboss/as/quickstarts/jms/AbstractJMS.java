/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.as.quickstarts.jms;

import java.util.Properties;
import java.util.logging.Logger;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.naming.Context;
import javax.naming.InitialContext;

public abstract class AbstractJMS implements Runnable {
	private Logger log;

	// Set up all the default values
	private static final String DEFAULT_CONNECTION_FACTORY = "jms/RemoteConnectionFactory";
	private static final String DEFAULT_USERNAME = "guest";
	private static final String DEFAULT_PASSWORD = "Redhat1!";
	private static final String INITIAL_CONTEXT_FACTORY = "org.jboss.naming.remote.client.InitialContextFactory";
	// private static final String PROVIDER_URL = "remote://127.0.0.1:4447";

	/**
	 * public static void main(String[] args) throws Exception { YourClass
	 * object = new YourClass(); object.doRun(args[0], args[1]); }
	 */

	private ConnectionFactory connectionFactory;
	Connection connection = null;
	Queue queue = null;
	Context context = null;
	private String[] remotes;
	private static int remoteIndex = 0;
	private String destinationString;
	protected boolean sendToQueue3 = true;

	protected AbstractJMS(String remote, String destinationString) {
		this.remotes = remote.split(",");
		this.destinationString = destinationString;		
	}

	public static void createAndRunThreads(AbstractJMS[] abstracJMSs) {
		Thread[] threads = new Thread[abstracJMSs.length];

		for (int i = 0; i < abstracJMSs.length; i++) {
			threads[i] = new Thread(abstracJMSs[i], abstracJMSs[i].getClass().getSimpleName() + ":" + i);
		}

		for (int i = 0; i < abstracJMSs.length; i++) {
			try {
				threads[i].start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// wait for consumers to finish
		for (int i = 0; i < abstracJMSs.length; i++) {
			try {
				threads[i].join();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	protected Logger getLogger() {
		if(log == null)
		 log = Logger.getLogger(AbstractJMS.class.getName());
		return log;		
	}	
	
	public void logThreadMessage(String message) {
		System.out.println(String.format("[%s] %s", Thread.currentThread().getName(), message));
		System.out.flush();
	}

	private synchronized String getNextRemote() {
		String remote = remotes[remoteIndex];
		remoteIndex++;		
		if(remoteIndex >= remotes.length)
			remoteIndex = 0;
		return remote;
	}
	
	public void connect() throws Exception {

		try {
			// Set up the context for the JNDI lookup
			final Properties env = new Properties();
			env.put(Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY);
			String remote = getNextRemote();
			System.out.println("Using Remote: " + remote);
			env.put(Context.PROVIDER_URL, System.getProperty(Context.PROVIDER_URL, remote));
			env.put(Context.SECURITY_PRINCIPAL, System.getProperty("username", DEFAULT_USERNAME));
			env.put(Context.SECURITY_CREDENTIALS, System.getProperty("password", DEFAULT_PASSWORD));
			context = new InitialContext(env);

			// Perform the JNDI lookups
			String connectionFactoryString = System.getProperty("connection.factory", DEFAULT_CONNECTION_FACTORY);
			logThreadMessage("Attempting to acquire connection factory \"" + connectionFactoryString + "\"");
			connectionFactory = (ConnectionFactory) context.lookup(connectionFactoryString);
			logThreadMessage("Found connection factory \"" + connectionFactoryString + "\" in JNDI");

			logThreadMessage("Attempting to acquire destination \"" + destinationString + "\"");
			queue = (Queue) context.lookup(destinationString);
			// large_queue = (Queue) context.lookup(large_destinationString);
			logThreadMessage("Found destination \"" + destinationString + "\" in JNDI");

			// Create the JMS connection, session, producer, and consumer
			connection = connectionFactory.createConnection(System.getProperty("username", DEFAULT_USERNAME),
					System.getProperty("password", DEFAULT_PASSWORD));

		} catch (Exception e) {
			getLogger().severe(e.getMessage());
			throw e;
		}
	}

	public abstract void run();
}