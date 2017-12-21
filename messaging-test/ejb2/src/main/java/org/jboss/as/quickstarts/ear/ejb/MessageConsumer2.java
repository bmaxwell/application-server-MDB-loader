/**
 * 
 */
package org.jboss.as.quickstarts.ear.ejb;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.MessageListener;

import org.jboss.as.quickstarts.mdb.AbstractMessageConsumer;
import org.jboss.as.quickstarts.mdb.MDBStats;

/**
 * @author bmaxwell
 *
 */
@MessageDriven(name = "MessageConsumer2", activationConfig = {			
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "jms/queue/messageQueue2"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
		@ActivationConfigProperty(propertyName = "subscriptionDurability", propertyValue = "Durable"),
		@ActivationConfigProperty(propertyName = "clientId", propertyValue = "MessageConsumer1MessageListener"),
		// @ActivationConfigProperty(propertyName = "maxSession", propertyValue = "1")
		@ActivationConfigProperty(propertyName = "useDLQ", propertyValue = "false"), 
		@ActivationConfigProperty(propertyName = "hA", propertyValue = "true"),
		})		
public class MessageConsumer2 extends AbstractMessageConsumer implements MessageListener {

	public static MDBStats mdbStats = new MDBStats(MessageConsumer2.class.getSimpleName() + ":MessageConsumer2");

	/**
	 * 
	 */
	public MessageConsumer2() {
		super(mdbStats, false);
	}
}