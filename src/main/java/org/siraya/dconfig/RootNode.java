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
public class RootNode extends Node {
	private static Logger logger = Logger.getLogger(RootNode.class.getName());

	private int minBranchLevel = 0;
	/**
	 * construct of root node.
	 * 
	 * @param input
	 * @param dimensions
	 */
	public RootNode(InputStream input, Dimensions dimensions) {
		this.load(input, dimensions);
	}
	
	public void load(InputStream input, Dimensions dimensions) {
		Yaml yaml = new Yaml();
		logger.info("parsing yaml file and get root node");
		Object root = yaml.load(input);
		if (!(root instanceof List)) {
			throw new NodeException("config must start with list object");
		}

		for (Map<String, Object> setting : (List<Map<String, Object>>) root) {
			String branch = (String) setting.get("settings");
			Branch currentBranch = null;

			if ("master".equals(branch)) {
				//
				// init root node
				//
				currentBranch = Branch.MASTER;

			} else {
				logger.info("load branch " + branch);
				String[] tmp = branch.split("=");
				currentBranch = dimensions.getBranchMap(tmp[0]).get(tmp[1]);
				if (currentBranch == null) {
					throw new NodeException("branch " + branch + " not exist");
				}
			}
			//
			// loading sequence.
			//
			if (currentBranch.getBranchLevel() < this.minBranchLevel) {
				throw new NodeException("can't load branch level " + currentBranch.getId() +
						" min level is "+ this.minBranchLevel + 
						" current level is "+ currentBranch.getBranchLevel());
			} else {
				logger.info("set min level for " + currentBranch.getId());
				this.minBranchLevel = currentBranch.getBranchLevel();
			}
			//
			// init master node.
			//
			Node rootNode = this;
			setting.remove("settings");
			Set<String> keys = ((Map<String, Object>) setting).keySet();
			for (String ikey : keys) {
				initNodeTree(ikey, setting.get(ikey), rootNode, currentBranch);
			}
		}
	}

	private void initNodeTree(String key, Object obj, Node parent,
			Branch currentBranch) {
		Node currentNode = parent.addChildNode(currentBranch, key, null);
		if (obj instanceof Map) {
			Map<String, Object> map = (Map<String, Object>) obj;
			Set<String> keys = ((Map<String, Object>) obj).keySet();
			for (String ikey : keys) {
				Object value = map.get(ikey);
				this.initNodeTree(ikey, value, currentNode, currentBranch);
			}

		} else {
			currentNode.setValue(currentBranch, obj);
		}
	}
	

}
