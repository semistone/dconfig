package org.siraya.dconfig;

import java.io.InputStream;
import java.util.logging.Level;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class TestBranch {
	private Dimensions dimensions;
	
	@Before
	public void setUp(){
		//
		// init logger for finest.
		//
		//ConsoleHandler ch = new ConsoleHandler();
        //ch.setLevel(Level.FINEST);
		//Dimensions.logger.addHandler(ch);
	    Dimensions.logger.setLevel(Level.ALL);
	    
	    
		InputStream in =
	            getClass().getClassLoader().getResourceAsStream("dimensions.yaml");	
		dimensions = new Dimensions(in);
	}
	
	
	@Test
	public void testMatchLevel(){
		Branch developement = dimensions.getBranchMap("environment").get("development");
		Branch fr = dimensions.getBranchMap("lang").get("fr");
		int match = developement.matchLevel(fr);
		Assert.assertEquals(-3, match);
		match = fr.matchLevel(dimensions.getBranchMap("lang").get("en"));
		Assert.assertEquals(-1, match);
	}
}
