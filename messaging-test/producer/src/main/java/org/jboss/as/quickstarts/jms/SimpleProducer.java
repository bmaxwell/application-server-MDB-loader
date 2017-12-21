/*
 * Copyright 2005-2014 Red Hat, Inc.
 * Red Hat licenses this file to you under the Apache License, version
 * 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */

package org.jboss.as.quickstarts.jms;

import javax.jms.MessageProducer;
import javax.jms.Session;

import org.jboss.as.quickstarts.mdb.TrackableMessage;

/**
 * @author Clebert Suconic
 */

public class SimpleProducer extends AbstractJMS implements Runnable {

	private int sleepTime;
	
	public SimpleProducer(String remote, String destinationString, int sleepTime, boolean sendToQueue3) {
		super(remote, destinationString);
		this.sleepTime = sleepTime;
		super.sendToQueue3 = sendToQueue3;
	}

	public static void main(String args[]) {
		
		if(args.length < 4) {
			System.out.println("Usage: [remote://IP:4447,remote://IP2:4447] [destination] [sleepTime] [numberOfThreads] [sendToQueue3 default true]");
			System.out.println("To configure username/password: -Dusername=username -Dpassword=password");
			System.exit(0);
		}
		
		String remote = args[0]; 
		String destinationString = args[1];		
		int sleepTime = Integer.parseInt(args[2]);
		int numberOfThreads = Integer.parseInt(args[3]);
		Boolean sendToQueue3 = true;
		if(args.length > 4)
			sendToQueue3 = Boolean.valueOf(args[4]);

		System.out.println(String.format("remote=%s destination=%s sleepTime=%d ms numberOfThreads=%d", remote, destinationString, sleepTime, numberOfThreads));
		
		SimpleProducer[] producers = new SimpleProducer[numberOfThreads];
		for (int i = 0; i < producers.length; i++)
			producers[i] = new SimpleProducer(remote, destinationString, sleepTime, sendToQueue3);

		createAndRunThreads(producers);

		// SimpleProducer producer = new SimpleProducer();
		// try {
		// producer.connect(arg[0], arg[1]);
		// producer.run(Integer.parseInt(arg[2]));
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
	}

	public void run() {
		String threadName = Thread.currentThread().getName();
		try {
			connect();
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			MessageProducer producer = session.createProducer(queue);
			long thread_id = System.currentTimeMillis();
			logThreadMessage("sleepTime " + sleepTime);
			long i = 0;
			while (true) {
                String message_id = (Long.toString(thread_id) + "-" + i);
				TrackableMessage tmsg = new TrackableMessage(threadName, message_id);				
//				producer.send(session.createTextMessage("Message " + (i++)));
				producer.send(session.createObjectMessage(tmsg));
				if (sleepTime > 0) {
					Thread.sleep(sleepTime);
				}
				if (i % 100 == 0) {
					logThreadMessage("Sent " + i + " messages on " + queue);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}