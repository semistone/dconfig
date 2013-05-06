package org.siraya.dconfig;

import java.io.InputStream;
import java.util.logging.Level;
import org.junit.Test;
import org.junit.Before;
import org.siraya.dconfig.RootNode;

public class TestRootNode {
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

	}

	@Test
	public void testLoadExample1() {
		InputStream in = getClass().getClassLoader().getResourceAsStream(
				"example1.yaml");
		RootNode root = new RootNode(in, dimensions);
	}

	@Test
	public void testLoadExample2() {
		InputStream in = getClass().getClassLoader().getResourceAsStream(
				"example2.yaml");
		RootNode root = new RootNode(in, dimensions);
	}

	@Test
	public void testLoadExample1And2() {
		InputStream in = getClass().getClassLoader().getResourceAsStream(
				"example1.yaml");
		RootNode root = new RootNode(in, dimensions);
		InputStream in2 = getClass().getClassLoader().getResourceAsStream(
				"example2.yaml");
		RootNode root2 = new RootNode(in2, dimensions);
	}

	@Test(expected = NodeException.class)
	public void testLoadExample3() {
		InputStream in = getClass().getClassLoader().getResourceAsStream(
				"example3.yaml");
		RootNode root = new RootNode(in, dimensions);

	}

	@Test
	public void testLoadExample4() {
		InputStream in = getClass().getClassLoader().getResourceAsStream(
				"example4.yaml");
		RootNode root = new RootNode(in, dimensions);
	}
}
