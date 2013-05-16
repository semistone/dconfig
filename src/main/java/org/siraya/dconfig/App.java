package org.siraya.dconfig;
import org.yaml.snakeyaml.Yaml;
import org.apache.commons.cli.*;
import java.io.File;
import java.util.*;
import java.io.*;

	
/**
 * -in <input dir> -out <output_file> -format <output format> -query <query
 * string>
 * 
 */
public class App {
	public static void main(String[] args) throws Exception {
		CommandLineParser parser = new BasicParser();
		Option in = OptionBuilder.withArgName("in").hasArg()
				.withDescription("input file").isRequired(true).create("in");
		Option format = OptionBuilder.withArgName("fotmat").hasArg()
				.withDescription("output format").isRequired(false)
				.create("format");
		Option out = OptionBuilder.withArgName("out").hasArg()
				.withDescription("output file").isRequired(true)
				.create("out");
		Option query = OptionBuilder.withArgName("query").hasArg()
				.withDescription("query string").isRequired(false)
				.create("query");
		Options options = new Options();

		options.addOption(in);
		options.addOption(out);
		options.addOption(format);
		options.addOption(query);
		int formatType = 0;
		try {
			// parse the command line arguments
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp( "dconfig", options );
			CommandLine line = parser.parse(options, args);
			String inString = line.getOptionValue("in");
			String queryString = line.getOptionValue("query");
			String outString = line.getOptionValue("out");
			String formatString = line.getOptionValue("format");
			if (formatString == null) {
				formatType = 0;
			} else if ("ini".equals(formatString)) {
				formatType = 1;
			}
			FileOutputStream os  = new java.io.FileOutputStream(outString);
			QueryNode node = QueryNodeUtil.createQueryNode(inString, queryString);
			String outputString = null;
			switch(formatType){
			case 1: //ini 				
				QueryNodeUtil.saveToIni(node, os);
				break;
			case 0: //yaml
				Yaml yaml = new Yaml();
				outputString = yaml.dump(node);	
				os.write(outputString.getBytes());
			default:
				break;
			}
		
			os.flush();
			os.close();
		}catch (ParseException exp) {
			// oops, something went wrong
			System.err.println("Parsing failed.  Reason: " + exp.getMessage());
		}

	}
}
