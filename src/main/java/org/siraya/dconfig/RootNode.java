package org.siraya.dconfig;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.yaml.snakeyaml.Yaml;

/**
 * 
 * 
 * 
 * @author angus_chen
 * 
 */
public class RootNode extends Node {
    final private static Logger logger = Logger.getLogger(RootNode.class
            .getName());
    private final Yaml yaml = new Yaml();
    private int minBranchLevel = 0;
    private Dimensions dimensions;

    /**
     * load RootNode by directory
     * 
     * @param path
     * @throws IOException
     */
    public RootNode(final File path) throws IOException {

        if (!path.isDirectory()) {
            throw new NodeException("path is not directory");
        }

        final File[] files = path.listFiles(new FilenameFilter() {
            public boolean accept(final File dir, final String name) {
                return name.toLowerCase().endsWith(".yaml");
            }
        });

        this.dimensions = null;
        //
        // load dimension
        //
        for (final File file : files) {
            if (file.getName().equals("dimensions.yaml")) {
                this.dimensions = new Dimensions(new java.io.FileInputStream(
                        file));
            }

        }
        if (this.dimensions == null) {
            throw new NodeException("dimensions.yaml not found");
        }
        final Yaml theYaml = new Yaml();
        final Map<Branch, List<Map<String, Object>>> map = new LinkedHashMap<Branch, List<Map<String, Object>>>();
        //
        //
        //
        for (final File file : files) {
            if (file.getName().equals("dimensions.yaml")) {
                continue;
            }

            RootNode.logger.info("parsing yaml file and get root node");
            final Object root = theYaml.load(new java.io.FileInputStream(file));
            if (!(root instanceof List)) {
                throw new NodeException("config must start with list object");
            }

            for (final Map<String, Object> setting : (List<Map<String, Object>>) root) {
                final Branch currentBranch = RootNode.getCurrentBranch(
                        this.dimensions, setting);
                if (!map.containsKey(currentBranch)) {
                    final List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
                    list.add(setting);
                    map.put(currentBranch, list);
                } else {
                    final List<Map<String, Object>> list = map
                            .get(currentBranch);
                    list.add(setting);
                }
            }
        }

        final Map<Branch, List<Map<String, Object>>> result = RootNode
                .sortByKeys(map);
        for (final Branch currentBranch : result.keySet()) {
            RootNode.logger.info("start parsing branch "
                    + currentBranch.getId());
            final List<Map<String, Object>> list = map.get(currentBranch);
            for (final Map<String, Object> setting : list) {
                this.parse(currentBranch, setting);
            }
        }
    }

    /*
     * from
     * http://javarevisited.blogspot.tw/2012/12/how-to-sort-hashmap-java-by-
     * key-and-value.html
     * 
     * Paramterized method to sort Map e.g. HashMap or Hashtable in Java throw
     * NullPointerException if Map contains null key
     */
    public static <K extends Comparable, V> Map<K, V> sortByKeys(
            final Map<K, V> map) {
        final List<K> keys = new LinkedList<K>(map.keySet());
        Collections.sort(keys);

        // LinkedHashMap will keep the keys in the order they are inserted
        // which is currently sorted on natural ordering
        final Map<K, V> sortedMap = new LinkedHashMap<K, V>();
        for (final K key : keys) {
            sortedMap.put(key, map.get(key));
        }

        return sortedMap;
    }

    /**
     * construct of root node.
     * 
     * @param input
     * @param dimensions
     */
    public RootNode(final InputStream input, final Dimensions theDimensions) {
        this.dimensions = theDimensions;
        this.load(input);
    }

    public void load(final InputStream input) {
        RootNode.logger.info("parsing yaml file and get root node");
        final Object root = this.yaml.load(input);
        if (!(root instanceof List)) {
            throw new NodeException("config must start with list object");
        }
        //
        // loop settings
        //
        for (final Map<String, Object> setting : (List<Map<String, Object>>) root) {
            final Branch currentBranch = RootNode.getCurrentBranch(
                    this.dimensions, setting);
            this.parse(currentBranch, setting);
        }
    }

    /**
     * get current branch in setting
     * 
     * @param dimensions
     * @param setting
     * @return
     */
    public static Branch getCurrentBranch(final Dimensions dimensions,
            final Map<String, Object> setting) {
        final String branch = (String) setting.get("settings");
        Branch currentBranch = null;
        if ("master".equals(branch)) {
            //
            // init root node
            //
            currentBranch = Branch.MASTER;

        } else {
            RootNode.logger.info("load branch " + branch);
            final String[] tmp = branch.split("=");
            if (tmp.length != 2) {
                throw new NodeException(branch
                        + " format error , branch format must be %s=%s");
            }
            final Map<String, Branch> map = dimensions.getBranchMap(tmp[0]);
            if (map == null) {
                throw new NodeException("key " + tmp[0] + " not exist ");
            }
            currentBranch = map.get(tmp[1]);
            if (currentBranch == null) {
                throw new NodeException("branch " + branch + " not exist");
            }
        }
        return currentBranch;
    }

    public void parse(final Branch currentBranch,
            final Map<String, Object> setting) {
        //
        // loading sequence.
        //
        if (currentBranch.getBranchLevel() < this.minBranchLevel) {
            throw new NodeException("can't load branch level "
                    + currentBranch.getId() + " min level is "
                    + this.minBranchLevel + " current level is "
                    + currentBranch.getBranchLevel());
        }
        RootNode.logger.info("set min level for " + currentBranch.getId());
        this.minBranchLevel = currentBranch.getBranchLevel();

        //
        // init master node.
        //
        final Node rootNode = this;
        setting.remove("settings");
        final Set<String> keys = setting.keySet();
        for (final String ikey : keys) {
            this.initNodeTree(ikey, setting.get(ikey), rootNode, currentBranch);
        }
    }

    private void initNodeTree(final String key, final Object obj,
            final Node parent, final Branch currentBranch) {
        final Node currentNode = parent.addChildNode(currentBranch, key, null);
        if (obj instanceof Map) {
            final Map<String, Object> map = (Map<String, Object>) obj;
            final Set<String> keys = ((Map<String, Object>) obj).keySet();
            for (final String ikey : keys) {
                final Object value = map.get(ikey);
                this.initNodeTree(ikey, value, currentNode, currentBranch);
            }

        } else {
            currentNode.setValue(currentBranch, obj);
        }
    }

    public Dimensions getDimensions() {
        return this.dimensions;
    }

}
