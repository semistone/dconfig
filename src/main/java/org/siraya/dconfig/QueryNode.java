package org.siraya.dconfig;

import java.util.*;
public class QueryNode implements Map<String, Object>{
	Node node;
	List<Branch> branches;


	public QueryNode(Node node, List<Branch> branches) {
		this.node = node;
		this.branches = branches;
	}

	private QueryNode getChildNode(String name) {
		return new QueryNode(this._getChildNode(name), branches);
	}

	private Node _getChildNode(String name){
		for (Branch branch : this.branches) {
			Node childNode = node.getChildNode(branch, name);
			if (childNode != null) {
				return childNode;
			}
		}
		throw new NodeException("can't find child node");		
	}


	/**
	 * get node value
	 * 
	 * @return
	 */
	private Object getChildValue(String name) {
		Node childNode = _getChildNode(name);
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
		try {
			return this.node.getChildMap(branches).size();			
		} catch (NodeException e) {
			return 0;
		}
	}

	public boolean isEmpty() {
		try {
			 return this.node.getChildMap(branches).isEmpty();			
		} catch (NodeException e) {
			return true;
		}
	}

	public boolean containsKey(Object key) {
		try {
			 return this.node.getChildMap(branches).containsKey(key);			
		} catch (NodeException e) {
			return false;
		}
	}

	public boolean containsValue(Object value) {
		throw new UnsupportedOperationException("not implement yet");
	}

	public Object get(Object key) {
		if (!(key instanceof String)) {
			throw new NodeException("only support string key");
		}
		String name = (String) key;
		Node childNode = _getChildNode(name);
		if (childNode.isTreeNode()) {
			return this.getChildNode(name);
		} else {
			return this.getChildValue(name);
		}
	}

	public Object put(String key, Object value) {
		throw new NodeException("this is readonly Map");
	}

	public Object remove(Object key) {
		throw new NodeException("this is readonly Map");
	}

	public void putAll(Map<? extends String, ? extends Object> t) {
		throw new NodeException("this is readonly Map");		
	}

	public void clear() {
		throw new NodeException("this is readonly Map");		
	}

	public Set<String> keySet() {
		try {
			return this.node.getChildMap(branches).keySet();			
		} catch (NodeException e) {
			return null;
		}
	}

	public Collection<Object> values() {
		Collection<Object> ret  = new ArrayList<Object>();
		for (String key : this.node.getChildMap(branches).keySet()) {
			Node node = this.node.getChildNode(key);
			if (node.isTreeNode()) {
				ret.add(new QueryNode(node, this.branches));
			} else {
				ret.add(this.get(key));
			}
		}
		return ret;
	}

	/**
	 * quick implement.
	 * 
	 */
	public Set<java.util.Map.Entry<String, Object>> entrySet() {
		Map<String,Object> ret=new HashMap<String, Object>();
		for (String key : this.node.getChildMap(branches).keySet()) {
			Node node = this.node.getChildNode(key);
			if (node.isTreeNode()) {
				ret.put(key, new QueryNode(node, this.branches));
			} else {
				ret.put(key,this.get(key));
			}			
		}
		return ret.entrySet();
	}

}
