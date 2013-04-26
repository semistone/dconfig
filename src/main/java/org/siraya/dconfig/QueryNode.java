package org.siraya.dconfig;

import java.util.List;
import java.util.HashMap;

public class QueryNode {
	Node node;
	List<Branch> branches;
	boolean isTreeNode;

	public QueryNode(Node node, List<Branch> branches) {
		this.node = node;
		this.branches = branches;
		this.isTreeNode = !node.isEndPoint();
	}

	private QueryNode getChildNode(String name) {
		return new QueryNode(node.getChildNode(name), branches);
	}

	public boolean isTreeNode() {
		return isTreeNode;
	}

	public Object get(String name) {
		if (!node.getChildNode(name).isEndPoint()) {
			return this.getChildNode(name);
		} else {
			return this.getValue();
		}
	}

	/**
	 * get node value
	 * 
	 * @return
	 */
	public Object getValue() {

		if (this.isTreeNode) {
			throw new NodeException("tree node can't get value");
		}

		if (!node.isTreeNode()) {
			//
			// map be list or normal value
			//
			return this.getNodeValueByNode(node);
		} else {
			//
			// put into hashMap
			//
			HashMap<String, Object> map = new HashMap<String, Object>();
			for (String key : node.keySet()) {
				Node childNode = node.getChildNode(key);
				Object value = getNodeValueByNode(childNode);
				map.put(key, value);
			}
			return map;
		}
	}

	/**
	 * get node value by branch.
	 * 
	 * @param node
	 * @return
	 */
	private Object getNodeValueByNode(Node node) {
		if (node.isTreeNode()) {
			throw new NodeException("tree node can't get value");
		}
		Object value = null;
		for (Branch branch : branches) {
			value = node.getValue(branch);
			if (value != null) {
				return value;
			}
		}
		if (value == null) { // get default master value.
			return node.getValue();
		} else {
			throw new NodeException("impossible here.");
		}
	}

}
