package org.siraya.dconfig;
import junit.framework.*;
import java.io.InputStream;
import org.siraya.dconfig.Dimensions;
public class TestDimensions extends TestCase{
	private Dimensions dimensions;
	public void setUp(){
		InputStream in =
	            getClass().getClassLoader().getResourceAsStream("dimensions.yaml");	
		dimensions = new Dimensions(in);
	}
	
	public void testGet(){
		Object obj = dimensions.get("lang");
		Assert.assertNotNull(obj);
	}
}
