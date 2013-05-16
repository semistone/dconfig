package org.siraya.dconfig;

import org.junit.Before;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;
import java.util.*;
import java.io.*;
public class TestQueryNodeUtil {

	@Before
	public void setUp(){
		
	}
	
	@Test
	public void testCreateQueryNode(){
		QueryNode node = QueryNodeUtil.createQueryNode("src/test/resources/example7", "environment=development");
		/*
		Yaml yaml = new Yaml();
		String x = yaml.dump(node);
		System.out.println(x);
		*/
	}
	
	@Test
	public void testSaveToIni()throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		Map<String,Object> sec1 = new HashMap<String,Object>();
		sec1.put("key1", "value1");
		sec1.put("key2", new Integer(12));
		map.put("sec1", sec1);
		OutputStream baos = new ByteArrayOutputStream();
		QueryNodeUtil.saveToIni(map, new BufferedOutputStream(baos));
		//System.out.println(baos.toString());
		
	}
}
