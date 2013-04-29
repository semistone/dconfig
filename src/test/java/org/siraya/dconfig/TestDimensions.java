package org.siraya.dconfig;
import junit.framework.*;
import java.io.InputStream;
import java.util.logging.*;
import org.siraya.dconfig.Dimensions;
import java.util.Map;
public class TestDimensions extends TestCase{
	private Dimensions dimensions;
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
	
	public void testGet(){
		Assert.assertEquals(4, dimensions.getBranchMap("environment").size());
	}
}
