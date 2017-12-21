/**
 * 
 */
package org.jboss.as.quickstarts.mdb.stats;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import org.jboss.dmr.ModelNode;

/**
 * @author bmaxwell
 *
 */
public class EJBStats {

	/**
	 * 
	 */
	public EJBStats() {
	}

	public static void main(String[] args) {
		try {
			String content = new String(Files.readAllBytes(Paths.get(args[0])));
			content = content.substring(content.indexOf("{"));
			// System.out.println(content);
			ModelNode serverEJBStats = ModelNode.fromString(content);
			List<ModelNode> deployments = serverEJBStats.get("result").asList();

			for (ModelNode deployment : deployments) {
				System.out.println(String.format("Deployment: %s", deployment.get("address")));
				List<ModelNode> subdeployments = deployment.get("result").get("subdeployment").asList();
				for (ModelNode subdeployment : subdeployments) {
					for(String key : subdeployment.keys()) {					
						System.out.println(String.format("SubDeployment: %s MDB: %s", key, isMDB(subdeployment.get(key))));						
	//					//System.out.println(String.format("SubDeployment: %s IsMDB: %s", subdeployment, isMDB(subdeployment)));					
						if(isMDB(subdeployment.get(key))) {
							MDB mdb = new MDB(key, subdeployment.get(key));							
							System.out.println(mdb);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	private static boolean isMDB(ModelNode deployment) {
//		System.out.println(deployment);
		if(!deployment.has("subsystem"))
			return false;
		List<ModelNode> subsystems = deployment.get("subsystem").asList();
		if(subsystems.size() < 1)
			return false;
		
		for(ModelNode subsystem : subsystems) {	
			if(!subsystem.has("ejb3"))
				continue;
			List<ModelNode> ejb3s = subsystem.get("ejb3").asList();
			if(ejb3s.size() < 1)
				return false;
			for(ModelNode ejb3 : ejb3s) {
//				System.out.println(ejb3);
//				System.out.println("m-d-b " + ejb3.keys().contains("message-driven-bean"));
				if(ejb3.has("message-driven-bean"))
					return true;
			}
		}
		return false;
	}
	
	private static class MDB {
		private String subDeploymentName;
		private ModelNode modelNode = null;
		private ModelNode method = null;
		public MDB(String subDeploymentName, ModelNode subdeployment) {
			this.subDeploymentName = subDeploymentName;
			Set<String> keys = subdeployment.get("subsystem").get("ejb3").get("message-driven-bean").keys();
			ModelNode key = subdeployment.get("subsystem").	get("ejb3").get("message-driven-bean").get(keys.iterator().next());
			this.modelNode = key; //subdeployment.get("subsystem").get("ejb3").get("message-driven-bean");

//			System.out.println(key);
//			this.method = modelNode.get("subsystem").get("ejb3").get("message-driven-bean").get(key);
			this.method = key;
//			System.out.println(method);
		}

		// method stats
		public long getWaitTime() {
			return method.get("wait-time").asLong();
		}
		public long getExecutionTime() {
//			System.out.println(method);
			return method.get("execution-time").asLong();
		}
		public long getInvocations() {
			return method.get("invocations").asLong();
		}
		
		// Pool stats
		public long getPeakConcurrentInvocations() {
			return modelNode.get("peak-concurrent-invocations").asLong();
		}
		public int getPoolAvailableCount() {
			return modelNode.get("pool-available-count").asInt();
		}
		public int getPoolCreateCount() {
			return modelNode.get("pool-create-count").asInt();
		}
		public int getPoolCurrentSize() {
			return modelNode.get("pool-current-size").asInt();
		}
		public int getPoolMaxSize() {
			return modelNode.get("pool-max-size").asInt();
		}
		
		public String toString() {
			StringBuffer sb = new StringBuffer();			
			sb.append(String.format("Invocations: %d ", getInvocations()));
			sb.append(String.format("PeakConcurrentInvocations: %d ", getPeakConcurrentInvocations()));
			sb.append(String.format("ExecutionTime: %d ", getExecutionTime()));
			sb.append(String.format("Average Execution Time: %f ", (getExecutionTime()/(float)getInvocations())));			
			return sb.toString();
		}
	}	
}
