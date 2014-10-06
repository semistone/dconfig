package org.siraya.dconfig;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.yaml.snakeyaml.Yaml;

/**
 * load from dimensions.yaml and initialize branch settings.
 * 
 * @author angus_chen
 * 
 */
public class Dimensions {
    static Logger logger = Logger.getLogger(Dimensions.class.getName());
    //
    // value is Map<String, Branch> , key is level one branch
    //
    //
    private final Map<String, Map<String, Branch>> branchGroupByLevelOneBranch = new HashMap<String, Map<String, Branch>>();

    public Dimensions(final InputStream input) {
        final Yaml yaml = new Yaml();
        Dimensions.logger.info("parsing yaml file and get dimensions node");
        final Map<String, Object> data = ((Map<String, Map>) yaml.load(input))
                .get("dimensions");
        final Branch master = Branch.MASTER;
        this.initBranchTree(data, master);
    }

    /**
     * init branch tree
     * 
     * @param obj
     * @param root
     */
    private void initBranchTree(final Object obj, final Branch parent) {

        if (obj instanceof Map) {
            final Set<String> keys = ((Map<String, Object>) obj).keySet();
            for (final String key : keys) {
                final Branch current = new Branch();
                current.setId(key);
                parent.addChildBranch(current);
                //
                // root branch need create new map.
                //
                if (parent.getBranchLevel() == 0) {
                    Dimensions.logger.info("new root branch " + key + " into "
                            + current.getLevelOneBranch().getId());
                    this.branchGroupByLevelOneBranch.put(current
                            .getLevelOneBranch().getId(),
                            new HashMap<String, Branch>());
                } else {
                    this.branchGroupByLevelOneBranch.get(
                            current.getLevelOneBranch().getId()).put(key,
                            current);
                }

                this.initBranchTree(((Map<String, Object>) obj).get(key),
                        current);
            }
        } else if (obj == null) {
            Dimensions.logger.info("end node for " + parent.getId());
        } else {
            Dimensions.logger.warning("something wrong ");
        }
    }

    /**
     * get branch map by level one branch id.
     * 
     * @param key
     * @return
     */
    public Map<String, Branch> getBranchMap(final String key) {
        return this.branchGroupByLevelOneBranch.get(key);
    }

}
