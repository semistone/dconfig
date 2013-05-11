package org.siraya.dconfig;

import org.apache.commons.cli.*;
import org.apache.commons.cli.BasicParser;

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
		Option out = new Option("out", "input file");
		Option query = OptionBuilder.withArgName("query").hasArg()
				.withDescription("query string").isRequired(false)
				.create("query");
		Options options = new Options();

		options.addOption(in);
		options.addOption(out);
		options.addOption(format);
		options.addOption(query);

		try {
			// parse the command line arguments
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp( "dconfig", options );
			CommandLine line = parser.parse(options, args);
			String inString = line.getOptionValue("in");
			System.out.println("parse inString " + inString);
		}catch (ParseException exp) {
			// oops, something went wrong
			System.err.println("Parsing failed.  Reason: " + exp.getMessage());
		}

	}
}
