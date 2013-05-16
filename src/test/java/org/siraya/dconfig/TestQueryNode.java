package org.siraya.dconfig;

import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import java.util.*;

import junit.framework.Assert;
public class TestQueryNode {
	Dimensions dimensions;
	RootNode root;
	
	@Before
	public void setUp() {
		InputStream in2 = getClass().getClassLoader().getResourceAsStream(
				"dimensions.yaml");
		dimensions = new Dimensions(in2);
		InputStream in = getClass().getClassLoader().getResourceAsStream(
				"example1.yaml");
		root = new RootNode(in, dimensions);
		InputStream in1 = getClass().getClassLoader().getResourceAsStream(
				"example2.yaml");
		root.load(in1);

		InputStream in3 = getClass().getClassLoader().getResourceAsStream(
				"example5.yaml");
		root.load(in3);
		
	}

	@Test
	public void testGetValueMatch() {
		Branch developement = dimensions.getBranchMap("environment").get("development");
		ArrayList<Branch> branches = new ArrayList<Branch>();
		branches.add(developement);
		QueryNode node = new QueryNode(root, branches);
		Object obj = node.get("data-url");
		Assert.assertEquals("http://service_dev.yahoo.com", obj);
		
		
		
		
	}
	
	@Test
	public void testGetValueNotMatch() {
		Branch product = dimensions.getBranchMap("environment").get("production");
		ArrayList<Branch> branches = new ArrayList<Branch>();
		branches.add(product);
		QueryNode node = new QueryNode(root, branches);
		Object obj = node.get("data-url");
		Assert.assertEquals("http://service.yahoo.com", obj);
	}
	
	@Test
	public void testGetValueDualMatch() {
		Branch developement = dimensions.getBranchMap("environment").get("development");
		Branch fr_Test = dimensions.getBranchMap("lang").get("fr_Test");
		Branch fr = dimensions.getBranchMap("lang").get("fr");
		ArrayList<Branch> branches = new ArrayList<Branch>();
		branches.add(developement);
		branches.add(fr_Test);
		QueryNode node = new QueryNode(root, branches);
		Object obj = node.get("data-url");
		Assert.assertEquals("http://service_dev.yahoo.com", obj);
		
		branches.clear();
		branches.add(fr_Test);
		branches.add(developement);
		QueryNode node2 = new QueryNode(root, branches);
		Object obj2 = node2.get("data-url");
		Assert.assertEquals("http://service_fr.yahoo.com", obj2);
	
		branches.clear();
		branches.add(fr);
		branches.add(developement);
		QueryNode node3 = new QueryNode(root, branches);
		Object obj3 = node2.get("data-url");
		Assert.assertEquals("http://service_dev.yahoo.com", obj3);
		
		
	}
	@Test
	public void testSize() {
		List<Branch> branches = this.devAndFr();
		QueryNode node = new QueryNode(root, branches);
		Assert.assertEquals(4, node.size());
				
	}
	
	private List<Branch> devAndFr() {
		Branch developement = dimensions.getBranchMap("environment").get("development");
		Branch fr_Test = dimensions.getBranchMap("lang").get("fr_Test");

		ArrayList<Branch> branches = new ArrayList<Branch>();
		branches.add(developement);
		branches.add(fr_Test);
		return branches;
	}
	@Test
	public void testKeySet() {
		List<Branch> branches = this.devAndFr();
		QueryNode node = new QueryNode(root, branches);
		for (String key :node.keySet()) {
			System.out.println("key is "+key);			
		}
		Assert.assertEquals(4, node.keySet().size());
		
	}
	
	@Test
	public void testGetChildNode() {
		List<Branch> branches = this.devAndFr();
		QueryNode node = new QueryNode(root, branches);
		Object child = node.get("links");
		Assert.assertEquals(QueryNode.class, child.getClass());
		Object mail = ((QueryNode)child).get("mail");
		Assert.assertEquals("http://mail_dev.yahoo.com", mail);
	}
	
	@Test
	public void testDump() {
		List<Branch> branches = this.devAndFr();
		QueryNode node = new QueryNode(root, branches);
		Yaml yaml = new Yaml();
		String x = yaml.dump(node);
		System.out.println(x);
		// just check no exception
	}
}
