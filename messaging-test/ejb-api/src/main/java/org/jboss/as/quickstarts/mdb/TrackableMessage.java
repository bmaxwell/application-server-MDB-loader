/**
 * 
 */
package org.jboss.as.quickstarts.mdb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author bmaxwell
 *
 */
public class TrackableMessage implements Serializable {
	
	private String producerName;
	private String messageId;
	private byte[] messageContent;
	private List<String> messagePath = new ArrayList<String>();
	private boolean sendToQueue3 = false;
	
	/**
	 * 
	 */
	public TrackableMessage() {
	}
		
	public TrackableMessage(String producerName, String messageId) {
		this.producerName = producerName;
		this.messageId = messageId;
	}
	
	public byte[] getMessageContent() {
		return messageContent;
	}

	public void setMessageContent(byte[] messageContent) {
		this.messageContent = messageContent;
	}

	public List<String> getMessagePath() {
		return messagePath;
	}

	public void setMessagePath(List<String> messagePath) {
		this.messagePath = messagePath;
	}

	public boolean isSendToQueue3() {
		return sendToQueue3;
	}

	public void setSendToQueue3(boolean sendToQueue3) {
		this.sendToQueue3 = sendToQueue3;
	}
	
	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public void addPath(String newLocation) {
		this.messagePath.add(newLocation);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("TrackableMessage producer: %s id: %s messageContextSize: %d\n", getProducerName(), getMessageId(), getMessageContent().length));
		for(String path : getMessagePath())
			sb.append(String.format("%s\n", path));
		return sb.toString(); 
	}

	public String getProducerName() {
		return producerName;
	}

	public void setProducerName(String producerName) {
		this.producerName = producerName;
	}
}