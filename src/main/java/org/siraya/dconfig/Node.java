package org.siraya.dconfig;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
    private Map<String, Node> allChildren = null;
    //
    // key is branch.
    //
    private Map<Branch, Object> branchValues;
    //
    // value is list , key is level one branch.
    //
    private final Map<Branch, List<Branch>> branchGroupByLevelOneBranch = new LinkedHashMap<Branch, List<Branch>>();

    private int nodeLevel;

    static Logger logger = Logger.getLogger(Node.class.getName());

    public Node() {
        Node.logger.info("create root node");
        this.path = "";
        this.parentNode = null;
        this.nodeLevel = 0;
        this.isTreeNode = true;
    }

    public Node(final Node theParentNode, final String name) {
        this.path = this.parentNode.path + "/" + name;
        this.parentNode = theParentNode;
        this.nodeLevel = theParentNode.getNodeLevel() + 1;
        Node.logger.info("new node in " + this.path + " level:"
                + this.nodeLevel);
    }

    public int getNodeLevel() {
        return this.nodeLevel;
    }

    public void setNodeLevel(final int theNodeLevel) {
        this.nodeLevel = theNodeLevel;
    }

    public Node getParentNode() {
        return this.parentNode;
    }

    public boolean isTreeNode() {
        return this.isTreeNode;
    }

    public void setHasSubNode(final boolean hasSubNode) {
        this.isTreeNode = hasSubNode;
    }

    public Object getMasterValue() {
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
    public void setValue(final Branch branch, final Object value) {
        if (this.isTreeNode) {
            throw new NodeException("tree node " + this.path
                    + " can't set value");
        }
        if (value instanceof Map) {
            throw new NodeException("value can't be Map object");
        }
        if (this.branchValues == null) {
            this.branchValues = new LinkedHashMap<Branch, Object>();
        }

        if (this.branchValues.containsKey(branch)) {
            throw new NodeException("set duplication value in branch:"
                    + branch.getId() + " path:" + this.path);
        }

        this.branchValues.put(branch, value);
        Node.logger.info("set value in:" + this.path + " value is:" + value);
        if (branch.getBranchLevel() == 0) {
            //
            // master branch don't beloge to any level one branch.
            //
            return;
        }

        //
        // set into branchGroupByLevelOneBranch
        //
        List<Branch> list = null;
        if (this.branchGroupByLevelOneBranch.containsKey(branch
                .getLevelOneBranch())) {
            list = this.branchGroupByLevelOneBranch.get(branch
                    .getLevelOneBranch());
        } else {
            list = new ArrayList<Branch>();
            this.branchGroupByLevelOneBranch.put(branch.getLevelOneBranch(),
                    list);
        }
        if (list.contains(branch)) {
            throw new NodeException("set duplicat branch into the same node");
        }
        list.add(branch);
    }

    public void setValue(final Object value) {
        this.setValue(Branch.MASTER, value);
    }

    /**
     * get value, if not match, return master branch.
     * 
     * @param branch
     * @return
     */
    public Object getValue(final Branch branch) {
        final Object ret = this._getValue(branch);
        if (ret == null) {
            return this.branchValues.get(Branch.MASTER);
        }
        return ret;
    }

    /**
     * 
     * @param branch
     * @return null, if no match branch exist.
     */
    Object _getValue(final Branch branch) {
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
        if (!this.branchGroupByLevelOneBranch.containsKey(branch
                .getLevelOneBranch())) {
            return null;
        }

        final List<Branch> branches = this.branchGroupByLevelOneBranch
                .get(branch.getLevelOneBranch());
        //
        // map key is match level, value is branch
        // and sort by match level.
        //
        final Map<Integer, Branch> map = new LinkedHashMap<Integer, Branch>();
        for (final Branch localBranch : branches) {
            Node.logger.fine("level " + branch.matchLevel(localBranch)
                    + " local branch id " + localBranch.getId()
                    + " compare to " + branch.getId());
            final int matchLevel = branch.matchLevel(localBranch);
            if (matchLevel > 0) {
                map.put(matchLevel, localBranch);
            }
        }
        //
        // no match
        //
        if (map.size() == 0) {
            return null;
        }
        final Object[] key = map.keySet().toArray();
        java.util.Arrays.sort(key);
        //
        // get hightest match branch and as key to get
        // from branch values
        //
        return this.branchValues.get(map.get(key[key.length - 1]));

    }

    /**
     * get child map from branch value
     * 
     * @param branch
     * @return
     */
    private Map<String, Node> _getChildMapForPut(final Branch branch,
            final String name) {
        if (!this.isTreeNode && (this.branchValues != null)) {
            throw new NodeException("this isn't tree node in path:" + this.path);
        }
        this.isTreeNode = true;
        if (this.branchValues == null) {
            this.branchValues = new LinkedHashMap<Branch, Object>();
            this.allChildren = new LinkedHashMap<String, Node>();
        }

        //
        // master branch
        //
        Map<String, Node> children = null;
        if (branch.equals(Branch.MASTER)) {
            children = (Map<String, Node>) this.branchValues.get(Branch.MASTER);
            if (children == null) {
                children = new LinkedHashMap<String, Node>();
                this.branchValues.put(branch, children);
            }
            return children;
        }

        //
        // master branch exist, always return it.
        //
        if (this.branchValues.containsKey(Branch.MASTER)) {
            final Map<String, Node> masterMap = (Map<String, Node>) this.branchValues
                    .get(Branch.MASTER);

            if (masterMap.containsKey(name)) {
                // if master exist, always use it.
                return masterMap;
            }
        }

        //
        // if in the same family, then use it.
        //
        for (final Branch currentBranch : this.branchValues.keySet()) {
            if (currentBranch.getBranchLevel() == 0) {
                continue;
            }
            if (branch.isSameFamily(currentBranch)) {
                return (Map<String, Node>) this.branchValues.get(currentBranch);
            }
        }

        children = new LinkedHashMap<String, Node>();
        this.branchValues.put(branch, children);

        return children;

    }

    /**
     * Get child node map by branch. 1. check is tree node. </br> 2. get master
     * map <br/>
     * 3. merge match sub map.<br/>
     * 
     * @param branch
     * @return
     */
    public Map<String, Node> getChildMap(final Branch branch) {
        if (!this.isTreeNode && (this.branchValues != null)) {
            throw new NodeException("this isn't tree node in path:" + this.path);
        }
        if (this.branchValues == null) {
            throw new NodeException("branch value is null");
        }
        Map<String, Node> ret = null;
        if (this.branchValues.containsKey(Branch.MASTER)) {
            ret = (Map<String, Node>) this.branchValues.get(Branch.MASTER);
            ret = new LinkedHashMap<String, Node>(ret); // clone master.
        } else {
            ret = new LinkedHashMap<String, Node>();
        }

        Map<String, Node> subMap = null;
        for (final Branch currentBranch : this.branchValues.keySet()) {
            if (branch.matchLevel(currentBranch) > 0) {
                subMap = (Map<String, Node>) this.branchValues
                        .get(currentBranch);
                for (final String key : subMap.keySet()) {
                    ret.put(key, subMap.get(key));
                }
            }
        }

        if (ret.size() == 0) {
            throw new NodeException("no child");
        }
        return ret;
    }

    /**
     * get child map by branches. merge all possible nodes.
     * 
     * @param branches
     * @return
     */
    public Map<String, Node> getChildMap(final List<Branch> branches) {
        if (!this.isTreeNode && (this.branchValues != null)) {
            throw new NodeException("this isn't tree node in path:" + this.path);
        }
        if (this.branchValues == null) {
            throw new NodeException("branch value is null");
        }
        Map<String, Node> ret = null;
        if (this.branchValues.containsKey(Branch.MASTER)) {
            ret = (Map<String, Node>) this.branchValues.get(Branch.MASTER);
            ret = new LinkedHashMap<String, Node>(ret); // clone master.
        } else {
            ret = new LinkedHashMap<String, Node>();
        }

        Map<String, Node> subMap = null;
        for (final Branch branch : branches) {
            for (final Branch currentBranch : this.branchValues.keySet()) {
                if (branch.matchLevel(currentBranch) > 0) {
                    subMap = (Map<String, Node>) this.branchValues
                            .get(currentBranch);
                    for (final String key : subMap.keySet()) {
                        if (!ret.containsKey(key)) {
                            ret.put(key, subMap.get(key));
                        }
                    }
                }
            }
        }

        if (ret.size() == 0) {
            throw new NodeException("no child");
        }
        return ret;
    }

    /**
     * add child node and return subnode. if node not exist, then create new
     * node. If exists, then return old node.
     * 
     * @param branch
     * @param name
     * @param child
     * @return
     * @throws Exception
     */
    public Node addChildNode(final Branch branch, final String name,
            final Object child) {

        Node node = null;
        final Map<String, Node> children = this
                ._getChildMapForPut(branch, name);

        if (children.containsKey(name)) {
            node = children.get(name);
        } else {
            if (this.allChildren.containsKey(name)) {
                node = this.allChildren.get(name);
            } else {
                node = new Node(this, name);
                this.allChildren.put(name, node);
            }
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
    public Node getChildNode(final Branch branch, final String name) {
        if (!this.isTreeNode) {
            throw new NodeException("this is leaf node");
        }
        return this.getChildMap(branch).get(name);
    }

    /**
     * get child node in all branch.
     * 
     * @param name
     * @return
     */
    public Node getChildNode(final String name) {
        if (!this.isTreeNode) {
            throw new NodeException("this is leaf node");
        }
        return this.allChildren.get(name);
    }

    /**
     * get child's key set.
     * 
     * @return
     */
    public Set<String> keySet(final Branch branch) {
        return this.getChildMap(branch).keySet();
    }

    /**
     * get child's key set.
     * 
     * @return
     */
    public Set<String> masterBranchKeySet() {
        return this.keySet(Branch.MASTER);
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(final String thePath) {
        this.path = thePath;
    }

    public int size(final Branch branch) {
        return this.getChildMap(branch).size();
    }

    public void dump(final StringBuffer sb) {
        String prefix = "";
        for (int i = 0; i < this.nodeLevel; i++) {
            prefix += "    ";
        }
        if (this.isTreeNode) {
            for (final Branch key : this.branchValues.keySet()) {
                sb.append(prefix);
                sb.append("[BRANCH " + key.getId() + " tree node]");
                sb.append("\n");
            }
            final Map<String, Node> map = this.allChildren;
            for (final String nodeKey : map.keySet()) {
                sb.append(prefix);
                sb.append(nodeKey);
                sb.append(":\n");
                map.get(nodeKey).dump(sb);
                sb.append("\n");
            }
        } else {
            for (final Branch key : this.branchValues.keySet()) {
                sb.append(prefix);
                sb.append("[BRANCH " + key.getId() + " leaf node]:");
                sb.append(this.branchValues.get(key));
                sb.append("\n");

            }
        }
    }

    public int branchSize() {
        return this.branchValues.size();
    }

    /**
     * gc
     */
    @Override
    public void finalize() throws Throwable {
        if (this.allChildren != null) {

            for (final String key : this.allChildren.keySet()) {
                this.allChildren.get(key).finalize();
            }
            this.allChildren.clear();
        }
        this.parentNode = null;
        if (this.branchValues != null) {
            this.branchValues.clear();
        }
        if (this.branchGroupByLevelOneBranch != null) {
            this.branchGroupByLevelOneBranch.clear();
        }
        super.finalize();
    }
}
