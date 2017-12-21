/**
 * 
 */
package org.jboss.as.quickstarts.mdb.status.logging;

import java.util.ArrayList;
import java.util.List;

import org.jboss.logmanager.ExtLogRecord;

/**
 * @author bmaxwell
 *
 */
public class HornetqServerStatus {

	private List<ExtLogRecord> bridges = new ArrayList<>();
	private List<ExtLogRecord> serverConfigs = new ArrayList<>();
	private List<ExtLogRecord> locksObtained = new ArrayList<>();
	
	
	/**
	 * 
	 */
	public HornetqServerStatus() {
	}


	public List<ExtLogRecord> getBridges() {
		return bridges;
	}


	public void setBridges(List<ExtLogRecord> bridges) {
		this.bridges = bridges;
	}


	public List<ExtLogRecord> getServerConfigs() {
		return serverConfigs;
	}


	public void setServerConfigs(List<ExtLogRecord> serverConfigs) {
		this.serverConfigs = serverConfigs;
	}


	public List<ExtLogRecord> getLocksObtained() {
		return locksObtained;
	}


	public void setLocksObtained(List<ExtLogRecord> locksObtained) {
		this.locksObtained = locksObtained;
	}
}