package org.siraya.dconfig;
import org.yaml.snakeyaml.Yaml;
import java.io.InputStream;
import java.util.Map;
public class Dimensions {
	private static Map<String, Object>  data;

	public Dimensions(InputStream input) {
		Yaml yaml = new Yaml();
		data = ((Map<String, Map>)yaml.load(input)).get("dimensions");
		
	}
	
	public Map<String,Object> get(String key){
		return (Map<String,Object>)data.get(key);
	}
}
