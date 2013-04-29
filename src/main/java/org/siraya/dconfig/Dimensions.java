package org.siraya.dconfig;
import org.yaml.snakeyaml.Yaml;
import java.util.Set;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
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
	private Map<String, Map<String, Branch>> branchGroupByLevelOneBranch = new HashMap<String, Map<String, Branch> >();
	
	public Dimensions(InputStream input) {
		Yaml yaml = new Yaml();
		logger.info("parsing yaml file and get dimensions node");
		Map<String, Object>  data = ((Map<String, Map>)yaml.load(input)).get("dimensions");		
		Branch master = Branch.MASTER;
		initBranchTree(data , master);
	}
	
	
	/**
	 * init branch tree
	 * 
	 * @param obj
	 * @param root
	 */
	private void initBranchTree(Object obj, Branch parent) {
		
		if (obj instanceof Map) {
			Set<String> keys = ((Map<String,Object>)obj).keySet();
			for (String key: keys) {
				Branch current = new Branch();
				current.setId(key);
				parent.addChildBranch(current);
				//
				// root branch need create new map.
				//
				if (parent.getBranchLevel() == 0) {
					logger.info("new root branch " + key + " into "+ current.getLevelOneBranch().getId());
					branchGroupByLevelOneBranch.put(current.getLevelOneBranch().getId(), new HashMap<String, Branch>());
				} else {					
					branchGroupByLevelOneBranch.get(current.getLevelOneBranch().getId()).put(key, current);
				}
		
				initBranchTree(((Map<String,Object>)obj).get(key), current);
			}
		} else if (obj == null) {
			logger.info("end node for " + parent.getId());
		} else {
			logger.warning("something wrong ");
		}
	}
	

	/**
	 * get branch map by level one branch id. 
	 * @param key
	 * @return
	 */
	public  Map<String, Branch> getBranchMap(String key){
		return this.branchGroupByLevelOneBranch.get(key);
	}
	
	
	
}
