package org.siraya.dconfig;

import java.util.Collection;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
public class QueryNode implements Map<String, Object>{
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

	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean containsKey(Object key) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean containsValue(Object value) {
		// TODO Auto-generated method stub
		return false;
	}

	public Object get(Object key) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object put(String key, Object value) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object remove(Object key) {
		// TODO Auto-generated method stub
		return null;
	}

	public void putAll(Map<? extends String, ? extends Object> t) {
		// TODO Auto-generated method stub
		
	}

	public void clear() {
		// TODO Auto-generated method stub
		
	}

	public Set<String> keySet() {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<Object> values() {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<java.util.Map.Entry<String, Object>> entrySet() {
		// TODO Auto-generated method stub
		return null;
	}
	

}
