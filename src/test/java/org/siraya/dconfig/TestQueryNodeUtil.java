package org.siraya.dconfig;

import org.junit.Before;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

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
}
