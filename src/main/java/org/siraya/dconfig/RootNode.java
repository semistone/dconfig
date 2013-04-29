package org.siraya.dconfig;
import java.io.InputStream;
import java.util.logging.Logger;
import java.util.*;

import org.yaml.snakeyaml.Yaml;
/**
 * 
 * 
 * 
 * @author angus_chen
 *
 */
public class RootNode extends Node{
	static Logger logger = Logger.getLogger(RootNode.class.getName());
	
	/**
	 * construct of root node.
	 * 
	 * @param input
	 * @param dimensions
	 */
	public RootNode(InputStream input, Dimensions dimensions) {
		Yaml yaml = new Yaml();
		logger.info("parsing yaml file and get root node");	
		Object root = yaml.load(input);
		if (!(root instanceof List)) {
			throw new NodeException("config must start with list object");
		}
		//
		// init master node.
		//
		Node masterNode	= Branch.MASTER.getRoot();
		if (masterNode == null) {
			masterNode = new Node();
			Branch.MASTER.setRoot(masterNode);
		}
		
		for (Map<String,Object> setting : (List<Map<String,Object>>)root ) {
			String branch = (String)setting.get("settings");
			Branch currentBranch = null;
			logger.info("load branch " + branch);
			if ("master".equals(branch)) {
				//
				// init root node
				//
				currentBranch = Branch.MASTER;
				
			} else {
				logger.warning("not implement yet");
			}
						
			setting.remove("settings");
			Set<String> keys = ((Map<String,Object>)setting).keySet();
			for (String ikey: keys) {
				initNodeTree(ikey, setting.get(ikey), masterNode, currentBranch);				
			}
		}
	}
	
	private void initNodeTree(String key, Object obj, Node parent, Branch currentBranch) {
		Node currentNode = parent.addChildNode(Branch.MASTER, key, null);
		if (obj instanceof Map) {			
			Map<String,Object> map = (Map<String,Object>)obj;
			Set<String> keys = ((Map<String,Object>)obj).keySet();
			for (String ikey: keys) {
				Object value = map.get(ikey);
				this.initNodeTree(ikey, value, currentNode , currentBranch);
			}
			
		} else {
			currentNode.setValue(currentBranch, obj);
		}
	}
}
