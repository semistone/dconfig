package org.siraya.dconfig;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Set;
import java.util.logging.Logger;
/**
 * 
 * 
 * @author angus_chen
 *
 */
public class Node {
	private String path;
	private Node parentNode;
	private boolean isTreeNode = false;
	//
	// key is branch.
	//
	private Map<Branch, Object> branchValues;
	//
	// value is list , key is level one branch.
	//
	private Map<Branch, List<Branch>> branchGroupByLevelOneBranch = new HashMap<Branch, List<Branch> >();

	private int nodeLevel;
	
	static Logger logger = Logger.getLogger(Node.class.getName());
	public Node() {
		logger.info("create root node");
		this.path = "";
		this.parentNode = null;
		this.nodeLevel = 0;
		this.isTreeNode = true;
	}

	public Node(Node parentNode,String name) {
		this.path = parentNode.path + "/" + name;
		this.parentNode = parentNode;
		this.nodeLevel = parentNode.getNodeLevel() + 1;
		logger.info("new node in " + path + " level:" + this.nodeLevel);
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
		if (this.isTreeNode) {
			throw new NodeException("can't set value into tree node");
		}
		return this.branchValues.get(Branch.MASTER);
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
		if (branchValues == null) {
			branchValues = new 	HashMap<Branch, Object>();
		}
		
		if (branchValues.containsKey(branch)) {
			throw new NodeException("set duplication value in branch:"+branch.getId() + " path:"+ this.path);
		}
		
		this.branchValues.put(branch, value);
		logger.info("set value in:"+this.path+ " value is:"+ value);
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
		if (this.isTreeNode()) {
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
			return this.branchValues.get(Branch.MASTER);
		}
		
		List<Branch> branches = branchGroupByLevelOneBranch.get(branch.getLevelOneBranch());
		//
		// map key is match level, value is branch
		// and sort by match level.
		//
		HashMap<Integer,Branch> map = new HashMap<Integer,Branch>();
		for (Branch localBranch : branches) {
			logger.fine("level " + branch.matchLevel(localBranch) + " local branch id " + localBranch.getId() + " compare to "+branch.getId());
			int matchLevel = branch.matchLevel(localBranch);
			if (matchLevel > 0) {
				map.put(matchLevel, localBranch);				
			}
		}
		//
		// no match 
		//
		if (map.size() == 0) {
			return this.branchValues.get(Branch.MASTER);
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
	 * get child map from branch value
	 * @param branch
	 * @return
	 */
	private Map<String, Node> getChildMap(Branch branch){
		if (!this.isTreeNode && branchValues != null) {
			throw new NodeException("this isn't tree node in path:" + this.path);
		}
		this.isTreeNode = true;
		if (branchValues == null) {
			branchValues = new 	HashMap<Branch, Object>();
		}
		Map<String, Node> children = null;
		//
		// if in the same family, then use it.
		//
		for (Branch currentBranch : this.branchValues.keySet()) {
			if (currentBranch.isSameFamily(branch)) {
				children = (Map<String, Node>)this.branchValues.get(currentBranch);
				break;
			}
		}
		if (children == null) {
			children =  new HashMap<String, Node>();
			this.branchValues.put(branch, children);
		}
		return children;
	}
	
	/**
	 * add child node and return subnode.
	 * if node not exist, then create new node. If exists, then return old node.
	 * @param branch
	 * @param name
	 * @param child
	 * @return
	 * @throws Exception
	 */
	public Node addChildNode(Branch branch, String name, Object child) {

		Node node = null;
		Map<String, Node> children = getChildMap(branch);
		if (children == null) {
			children =  new HashMap<String, Node>();
		}
		
		if (children.containsKey(name)) {
			node = children.get(name);
		} else {
			node = new Node(this,name);
			children.put(name, node);
		}
		if (child != null) {
			node.setValue(branch, child);			
		}
		return node;
	}

	/**
	 * get child node by master branch.
	 * 
	 * @param name
	 * @return
	 */
	public Node getChildNode(Branch branch, String name) {
		if (!this.isTreeNode) {
			throw new NodeException("this is leaf node");
		}
		return getChildMap(branch).get(name);
	}
	
	/**
	 * get master branch's child node.
	 * @param name
	 * @return
	 */
	public Node getChildNode(String name) {
		return this.getChildNode(Branch.MASTER, name);
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
		
		for (Node child: this.getChildMap(Branch.MASTER).values()) {
			if (!child.isTreeNode || (child.getValue() instanceof List)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * get child's key set.
	 * @return
	 */
	public Set<String> keySet(Branch branch){
		return this.getChildMap(branch).keySet();
	}
	
	/**
	 * get child's key set.
	 * @return
	 */
	public Set<String> keySet(){		
		return this.keySet(Branch.MASTER);
	}

	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}


	public int size(Branch branch) {
		return this.getChildMap(branch).size();
	}
	
	public void dump(StringBuffer sb) {
		String prefix = "";
		for (int i = 0 ; i < this.nodeLevel; i++) {
			prefix += "    ";			
		}
		if (this.isTreeNode) {
			for (Branch key : this.branchValues.keySet()){
				sb.append(prefix);
				sb.append("[" + key.getId()+ " tree node]\n");
				Map<String, Node> map = (Map)branchValues.get(key);
				for (String nodeKey : map.keySet()) {
					sb.append(prefix);
					sb.append(nodeKey);
					sb.append(":\n");
					map.get(nodeKey).dump(sb);
					sb.append("\n");
				}
				sb.append("\n");
			}			
		}else{
			for (Branch key : this.branchValues.keySet()){
				sb.append(prefix);
				sb.append("[" + key.getId()+ " leaf node]:");
				sb.append(branchValues.get(key));
				sb.append("\n");
			}
		}
	}
	
	
	public int branchSize() {
		return this.branchValues.size();
	}
}
