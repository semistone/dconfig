package org.siraya.dconfig;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import java.io.*;
import org.yaml.snakeyaml.Yaml;

public class QueryNodeUtil {

	private static Logger logger = Logger.getLogger(QueryNodeUtil.class.getName());




	/**
	 * create query node
	 * @param path
	 * @param queryString
	 * @return
	 */
	public static QueryNode createQueryNode(String path, String queryString){
		RootNode root = null;
		try{
			root = new RootNode(new File(path));
		}catch (IOException e){
			throw new NodeException("parse node fail " + e.getMessage());
		}
		Dimensions dimensions = root.getDimensions();
		List<Branch> branches = new ArrayList<Branch>();
		for (String condition: queryString.split(";")){
			String[] tmp = condition.split("=");
			Branch currentBranch = dimensions.getBranchMap(tmp[0]).get(tmp[1]);
			branches.add(currentBranch);
		}
		QueryNode node = new QueryNode(root, branches);
		return node;		
	}
}
