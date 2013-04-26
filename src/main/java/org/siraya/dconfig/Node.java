package org.siraya.dconfig;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Set;
/**
 * 
 * 
 * @author angus_chen
 *
 */
public class Node {

	private Node parentNode;
	private boolean isTreeNode = false;
	//
	// key is branch.
	//
	private Map<Branch, Object> branchValues = new HashMap<Branch, Object>();
	//
	// value is list , key is level one branch.
	//
	private Map<Branch, List<Branch>> branchGroupByLevelOneBranch = new HashMap<Branch, List<Branch> >();

	private int nodeLevel;
	private Map<String, Node> children;

	public Node() {
		this.parentNode = null;
		this.nodeLevel = 0;
		this.isTreeNode = true;
	}

	public Node(Node parentNode) {
		this.parentNode = parentNode;
		this.nodeLevel = parentNode.getNodeLevel() + 1;
	}

	public int getNodeLevel() {
		return nodeLevel;
	}

	public void setNodeLevel(int nodeLevel) {
		this.nodeLevel = nodeLevel;
	}

	public Node getParentNode() {
		return parentNode;
	}


	public boolean isTreeNode() {
		return isTreeNode;
	}

	public void setHasSubNode(boolean hasSubNode) {
		this.isTreeNode = hasSubNode;
	}

	public Object getValue() {
		return this.branchValues.get("master");
	}

	/**
	 * set value by branch.
	 * 
	 * @param branch
	 * @param value
	 * @throws Exception
	 */
	public void setValue(Branch branch, Object value) {
		if (this.isTreeNode) {
			throw new NodeException("tree node can't set value");
		}
		if (value instanceof Map) {
			throw new NodeException("value can't be Map object");
		}
		this.branchValues.put(branch, value);
		
		if (branch.getBranchLevel() == 0 ) {
			//
			// master branch don't beloge to any level one branch.
			//
			return;
		}
		
		//
		// set into branchGroupByLevelOneBranch
		//
		List<Branch> list = null;
		if (branchGroupByLevelOneBranch.containsKey(branch.getLevelOneBranch())) {
			list = branchGroupByLevelOneBranch.get(branch.getLevelOneBranch());
		} else {
			list = new ArrayList<Branch>();
			branchGroupByLevelOneBranch.put(branch.getLevelOneBranch(), list);
		}
		if (list.contains(branch)) {
			throw new NodeException("set duplicat branch into the same node");
		}
		list.add(branch);
	}

	public void setValue(Object value) {
		this.setValue(Branch.MASTER, value);
	}

	/**
	 * 
	 * @param branch
	 * @return null, if no match branch exist.
	 */
	public Object getValue(Branch branch) {
		if (!this.isTreeNode()) {
			throw new NodeException("only leaf node can get value by branch");
		}
		//
		// get master branch
		//
		if (branch.getBranchLevel() == 0) {
			return this.branchValues.get(branch);
		}
		
		//
		// no the same level one branch exist.
		//
		if (!this.branchGroupByLevelOneBranch.containsKey(branch.getLevelOneBranch())) {
			return null;
		}
		List<Branch> branches = branchGroupByLevelOneBranch.get(branch.getLevelOneBranch());
		//
		// map key is match level, value is branch
		// and sort by match level.
		//
		HashMap<Integer,Branch> map = new HashMap<Integer,Branch>();
		for (Branch localBranch : branches) {
			map.put(branch.matchLevel(localBranch), localBranch);
		}
		Object[] key = map.keySet().toArray();
		java.util.Arrays.sort(key);
		//
		// get hightest match branch and as key to get 
		// from branch values
		//
		return this.branchValues.get(map.get(key[key.length -1]));

	}
	
	/**
	 * add child node
	 * 
	 * @param branch
	 * @param name
	 * @param child
	 * @return
	 * @throws Exception
	 */
	public Node addChildNode(Branch branch, String name, Object child) {
		if (!this.isTreeNode || branchValues != null) {
			throw new NodeException("this is leaf node");
		}
		this.isTreeNode = true;
		Node node = null;
		if (this.children.containsKey(name)) {
			node = this.children.get(name);
		} else {
			if (!Branch.MASTER.equals(branch)) {
				throw new NodeException("only master branch can add new node");
			}
			node = new Node(this);
			this.children.put(name, node);
		}
		node.setValue(branch, child);
		return node;
	}

	/**
	 * get child node.
	 * 
	 * @param name
	 * @return
	 */
	public Node getChildNode(String name) {
		if (!this.isTreeNode) {
			throw new NodeException("this is leaf node");
		}
		return this.children.get(name);
	}

	/**
	 * If child has tree node, then it's not end point.
	 * If one child is list, then it's not end point
	 * @return
	 */
	public boolean isEndPoint() {
		if (!this.isTreeNode) {
			return true;
		}
		
		for (Node child: this.children.values()) {
			if (!child.isTreeNode || (child.getValue() instanceof List)) {
				return false;
			}
		}
		return true;
	}
	
	public Set<String> keySet(){
		return this.children.keySet();
	}
}
