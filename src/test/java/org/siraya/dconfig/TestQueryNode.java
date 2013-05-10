package org.siraya.dconfig;

import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;

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
		root.load(in1, dimensions);

		InputStream in3 = getClass().getClassLoader().getResourceAsStream(
				"example5.yaml");
		root.load(in3, dimensions);
		
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
	
	public void testSize() {
		
	}
}
