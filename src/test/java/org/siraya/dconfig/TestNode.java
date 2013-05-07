package org.siraya.dconfig;

import java.io.InputStream;
import java.util.logging.Level;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class TestNode {
	Dimensions dimensions;

	@Before
	public void setUp() {
		//
		// init logger for finest.
		//
		// ConsoleHandler ch = new ConsoleHandler();
		// ch.setLevel(Level.FINEST);
		// RootNode.logger.addHandler(ch);
		RootNode.logger.setLevel(Level.ALL);

		InputStream in2 = getClass().getClassLoader().getResourceAsStream(
				"dimensions.yaml");
		dimensions = new Dimensions(in2);
		InputStream in = getClass().getClassLoader().getResourceAsStream(
				"example1.yaml");
		RootNode root = new RootNode(in, dimensions);
		InputStream in1 = getClass().getClassLoader().getResourceAsStream(
				"example2.yaml");
		RootNode root2 = new RootNode(in1, dimensions);
		
		InputStream in3 = getClass().getClassLoader().getResourceAsStream(
				"example5.yaml");
		RootNode root3 = new RootNode(in3, dimensions);
	}
	
	@Test
	public void testGetValue(){
		Object obj = Branch.MASTER.getRoot().getChildNode("data-url").getValue();		
		Assert.assertEquals("http://service.yahoo.com", obj);
	}
	
	@Test
	public void testGetValueOverwriteByBranch(){
		Branch developement = dimensions.getBranchMap("environment").get("development");
		Object obj = developement.getRoot().getChildNode(developement, "data-url").getValue(developement);
		/*
		Node node =  developement.getRoot().getChildNode(developement, "data-url");
		StringBuffer sb = new StringBuffer();
		node.dump(sb);
		System.out.println(sb.toString());
		*/
		Assert.assertEquals("http://service_dev.yahoo.com", obj);
	}
	
	/*
	@Test
	public void testGetValueLower() {
		Object obj = Branch.MASTER.getRoot().getChildNode("data-url").getValue(dimensions.getBranchMap("lang").get("fr_CA"));

		Assert.assertEquals("http://service_fr.yahoo.com", obj);
	}
	*/
}
