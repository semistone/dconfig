package org.siraya.dconfig;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.ini4j.Ini;

public class QueryNodeUtil {

    private static Logger logger = Logger.getLogger(QueryNodeUtil.class
            .getName());

    /**
     * save setting to ini file
     * 
     * @param input
     * @param output
     * @throws IOException
     */
    public static void saveToIni(final Map<String, Object> input,
            final OutputStream output) throws IOException {
        final Ini iniFile = new Ini();
        for (final String key : input.keySet()) {
            final Ini.Section section = iniFile.add(key);
            final Object tmp = input.get(key);
            if (tmp == null) {
                continue;
            }
            if (!(tmp instanceof Map)) {
                throw new NodeException("section must be map");
            }

            final Map<String, Object> sectionData = (Map<String, Object>) tmp;
            for (final String key2 : sectionData.keySet()) {
                Object obj = sectionData.get(key2);
                if (obj == null) {
                    obj = "";
                }
                section.put(key2, obj.toString());
            }
        }
        iniFile.store(output);
    }

    /**
     * create query node
     * 
     * @param path
     * @param queryString
     * @return
     */
    public static QueryNode createQueryNode(final String path,
            final String queryString) {
        RootNode root = null;
        try {
            root = new RootNode(new File(path));
        } catch (final IOException e) {
            throw new NodeException("parse node fail " + e.getMessage());
        }
        final Dimensions dimensions = root.getDimensions();
        final List<Branch> branches = new ArrayList<Branch>();
        for (final String condition : queryString.split(";")) {
            final String[] tmp = condition.split("=");
            final Map<String, Branch> map = dimensions.getBranchMap(tmp[0]);
            if (map == null) {
                throw new NodeException("branch for " + tmp[0] + " not exist");
            }
            final Branch currentBranch = map.get(tmp[1]);
            if (currentBranch == null) {
                throw new NodeException("branch for " + tmp[1] + " not exist");
            }
            branches.add(currentBranch);
        }
        final QueryNode node = new QueryNode(root, branches);
        return node;
    }
}
