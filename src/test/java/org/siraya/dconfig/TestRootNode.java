package org.siraya.dconfig;

import java.io.InputStream;
import java.util.logging.Level;

import junit.framework.TestCase;
import org.siraya.dconfig.RootNode;

public class TestRootNode extends TestCase {
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
		Dimensions dimensions = new Dimensions(in2);

		InputStream in = getClass().getClassLoader().getResourceAsStream(
				"example1.yaml");
		RootNode root = new RootNode(in, dimensions);

	}

	public void testLoad() {

	}
}
