package org.siraya.dconfig;

import java.io.FileOutputStream;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.yaml.snakeyaml.Yaml;

/**
 * -in <input dir> -out <output_file> -format <output format> -query <query
 * string>
 * 
 */
public class App {
    public static void main(final String[] args) throws Exception {
        final CommandLineParser parser = new BasicParser();
        OptionBuilder.withArgName("in");
        OptionBuilder.hasArg();
        OptionBuilder.withDescription("input file");
        OptionBuilder.isRequired(true);
        final Option in = OptionBuilder.create("in");
        OptionBuilder.withArgName("fotmat");
        OptionBuilder.hasArg();
        OptionBuilder.withDescription("output format");
        OptionBuilder.isRequired(false);
        final Option format = OptionBuilder.create("format");
        OptionBuilder.withArgName("out");
        OptionBuilder.hasArg();
        OptionBuilder.withDescription("output file");
        OptionBuilder.isRequired(true);
        final Option out = OptionBuilder.create("out");
        OptionBuilder.withArgName("query");
        OptionBuilder.hasArg();
        OptionBuilder.withDescription("query string");
        OptionBuilder.isRequired(false);
        final Option query = OptionBuilder.create("query");
        OptionBuilder.withArgName("dump");
        OptionBuilder.hasArg();
        OptionBuilder.withDescription("dump internal structure");
        OptionBuilder.isRequired(false);
        final Option dump = OptionBuilder.create("dump");
        final Options options = new Options();

        options.addOption(in);
        options.addOption(out);
        options.addOption(format);
        options.addOption(query);
        options.addOption(dump);
        int formatType = 0;
        try {
            // parse the command line arguments
            final HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("dconfig", options);
            final CommandLine line = parser.parse(options, args);
            final String inString = line.getOptionValue("in");
            final String queryString = line.getOptionValue("query");
            final String outString = line.getOptionValue("out");
            final String formatString = line.getOptionValue("format");
            if (formatString == null) {
                formatType = 0;
            } else if ("ini".equals(formatString)) {
                formatType = 1;
            }
            final FileOutputStream os = new java.io.FileOutputStream(outString);
            final QueryNode node = QueryNodeUtil.createQueryNode(inString,
                    queryString);
            String outputString = null;
            switch (formatType) {
            case 1: // ini
                QueryNodeUtil.saveToIni(node, os);
                break;
            case 0: // yaml
                final Yaml yaml = new Yaml();
                outputString = yaml.dump(node);
                os.write(outputString.getBytes());
            default:
                break;
            }

            os.flush();
            os.close();
            if (line.hasOption("dump")) {
                final String dumpString = line.getOptionValue("dump");
                final FileOutputStream dos = new java.io.FileOutputStream(
                        dumpString);
                final StringBuffer sb = new StringBuffer();
                node.node.dump(sb);
                dos.write(sb.toString().getBytes());
                dos.flush();
                dos.close();
            }
        } catch (final ParseException exp) {
            // oops, something went wrong
            System.err.println("Parsing failed.  Reason: " + exp.getMessage());
        }

    }
}
