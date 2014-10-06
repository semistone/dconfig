package org.siraya.dconfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class QueryNode implements Map<String, Object> {
    Node node;
    List<Branch> branches;

    public QueryNode(final Node theNode, final List<Branch> theBranches) {
        this.node = theNode;
        this.branches = theBranches;
    }

    private QueryNode getChildNode(final String name) {
        return new QueryNode(this._getChildNode(name), this.branches);
    }

    private Node _getChildNode(final String name) {
        for (final Branch branch : this.branches) {
            final Node childNode = this.node.getChildNode(branch, name);
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
    private Object getChildValue(final String name) {
        final Node childNode = this._getChildNode(name);
        if (childNode.isTreeNode()) {
            throw new NodeException("tree node can't get value");
        }

        Object value = null;
        for (final Branch branch : this.branches) {
            value = childNode._getValue(branch);
            if (value != null) {
                return value;
            }
        }

        return childNode.getMasterValue();

    }

    public int size() {
        try {
            return this.node.getChildMap(this.branches).size();
        } catch (final NodeException e) {
            return 0;
        }
    }

    public boolean isEmpty() {
        try {
            return this.node.getChildMap(this.branches).isEmpty();
        } catch (final NodeException e) {
            return true;
        }
    }

    public boolean containsKey(final Object key) {
        try {
            return this.node.getChildMap(this.branches).containsKey(key);
        } catch (final NodeException e) {
            return false;
        }
    }

    public boolean containsValue(final Object value) {
        throw new UnsupportedOperationException("not implement yet");
    }

    public Object get(final Object key) {
        if (!(key instanceof String)) {
            throw new NodeException("only support string key");
        }
        final String name = (String) key;
        final Node childNode = this._getChildNode(name);
        if (childNode.isTreeNode()) {
            return this.getChildNode(name);
        }
        return this.getChildValue(name);

    }

    public Object put(final String key, final Object value) {
        throw new NodeException("this is readonly Map");
    }

    public Object remove(final Object key) {
        throw new NodeException("this is readonly Map");
    }

    public void putAll(final Map<? extends String, ? extends Object> t) {
        throw new NodeException("this is readonly Map");
    }

    public void clear() {
        throw new NodeException("this is readonly Map");
    }

    public Set<String> keySet() {
        try {
            return this.node.getChildMap(this.branches).keySet();
        } catch (final NodeException e) {
            return null;
        }
    }

    public Collection<Object> values() {
        final Collection<Object> ret = new ArrayList<Object>();
        for (final String key : this.node.getChildMap(this.branches).keySet()) {
            final Node theNode = this.node.getChildNode(key);
            if (theNode.isTreeNode()) {
                ret.add(new QueryNode(theNode, this.branches));
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
        final Map<String, Object> ret = new LinkedHashMap<String, Object>();
        for (final String key : this.node.getChildMap(this.branches).keySet()) {
            final Node theNode = this.node.getChildNode(key);
            if (theNode.isTreeNode()) {
                ret.put(key, new QueryNode(theNode, this.branches));
            } else {
                ret.put(key, this.get(key));
            }
        }
        return ret.entrySet();
    }

}
