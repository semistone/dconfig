package org.siraya.dconfig;

import java.io.InputStream;
import java.util.logging.Level;

import junit.framework.Assert;

import org.junit.*;

public class TestNode {
	Dimensions dimensions;
	RootNode root;
	@Before
	public void setUp() {
		//
		// init logger for finest.
		//
		// ConsoleHandler ch = new ConsoleHandler();
		// ch.setLevel(Level.FINEST);
		// RootNode.logger.addHandler(ch);
		//RootNode.logger.setLevel(Level.ALL);

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
	
	@After
	public void tearDown(){
		
	}
	
	@Test
	public void testGetValueInMaster(){
		Object obj = root.getChildNode("data-url").getMasterValue();		
		Assert.assertEquals("http://service.yahoo.com", obj);
	}
	
	@Test
	public void testGetValueOverwriteByBranch(){
		Branch developement = dimensions.getBranchMap("environment").get("development");
		Object obj = root.getChildNode(developement, "data-url").getValue(developement);
		/*
		Node node =  developement.getRoot().getChildNode(developement, "data-url");
		StringBuffer sb = new StringBuffer();
		node.dump(sb);
		System.out.println(sb.toString());
		*/
		Assert.assertEquals("http://service_dev.yahoo.com", obj);
	}
	
	
	@Test
	public void testGetValueInBranch() {
		Branch developement = dimensions.getBranchMap("environment").get("development");
		Node node  = root.getChildNode("data-url");

		Assert.assertEquals(3, node.branchSize());
		Assert.assertEquals("http://service_dev.yahoo.com", node.getValue(developement));
		
		Branch fr = dimensions.getBranchMap("lang").get("fr");
		Assert.assertEquals("http://service.yahoo.com", node.getValue(fr));
		Branch fr_Test = dimensions.getBranchMap("lang").get("fr_Test");
		Assert.assertEquals("http://service_fr.yahoo.com", node.getValue(fr_Test));		
		
		Branch us = dimensions.getBranchMap("region").get("us");
		Assert.assertEquals("http://service.yahoo.com", node.getValue(us));		
	}
	
	@Test(expected = NodeException.class)
	public void testDuplicateLoad(){
		InputStream in4 = getClass().getClassLoader().getResourceAsStream(
				"example5.yaml");
		root.load(in4, dimensions);
	}
	
	@Test
	public void testDump(){
		Branch developement = dimensions.getBranchMap("environment").get("development");
		Node node  = root.getChildNode("data-url");
		StringBuffer sb = new StringBuffer();
		node.dump(sb);
		//System.out.println(sb.toString());
		
		StringBuffer sb2 = new StringBuffer();		
		root.dump(sb2);
		System.out.println(sb2.toString());
	}
	
}
