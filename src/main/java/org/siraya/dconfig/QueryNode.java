package org.siraya.dconfig;

import java.util.Collection;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
public class QueryNode implements Map<String, Object>{
	Node node;
	List<Branch> branches;


	public QueryNode(Node node, List<Branch> branches) {
		this.node = node;
		this.branches = branches;
	}

	private QueryNode getChildNode(String name) {
		return new QueryNode(node.getChildNode(name), branches);
	}


	public Object get(String name) {
		Node childNode = node.getChildNode(name);
		if (childNode.isTreeNode()) {
			return this.getChildNode(name);
		} else {
			return this.getChildValue(name);
		}
	}

	/**
	 * get node value
	 * 
	 * @return
	 */
	private Object getChildValue(String name) {
		Node childNode = this.node.getChildNode(name);
		if (childNode.isTreeNode()) {
			throw new NodeException("tree node can't get value");
		}

		Object value = null;
		for (Branch branch : this.branches) {
			value = childNode._getValue(branch);
			if (value != null) {
				return value;
			}
		}
		
		if (value == null) { // get default master value.
			return childNode.getMasterValue();
		} else {
			throw new NodeException("impossible here.");
		}
		
	}


	public int size() {
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
