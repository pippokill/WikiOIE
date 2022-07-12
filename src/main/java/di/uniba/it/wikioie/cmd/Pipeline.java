/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package di.uniba.it.wikioie.cmd;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 *
 * @author pierpaolo
 */
public class Pipeline {
    
    private static final Logger LOG = Logger.getLogger(Pipeline.class.getName());

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Options options = new Options();
        options = options.addOption(new Option("i", true, "Input directory"))
                .addOption(new Option("o", true, "Output directory"))
                .addOption(new Option("p", true, "Processor"))
                .addOption(new Option("t", true, "Number of threads (optional, default 4)"))
                .addOption(new Option("d", true, "Training file (optional)"))
                .addOption(new Option("ds", true, "Training supervised approach"))
                .addOption(new Option("dp", true, "Training parameters"))
                .addOption(new Option("s", true, "Sampling (optional)"))
                .addOption(new Option("f", false, "Use predicate occurrances file (optional)"))
                .addOption(new Option("m", true, "Min predicate occurrances (used with option -f, optional, 5)"))
                .addOption(new Option("x", false, "Print text"));
        try {
            DefaultParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption("i") && cmd.hasOption("o") && cmd.hasOption("p")) {
                LOG.log(Level.INFO, "Input dir: {0}", cmd.getOptionValue("i"));
                LOG.log(Level.INFO, "Output dir: {0}", cmd.getOptionValue("o"));
                LOG.log(Level.INFO, "Processor: {0}", cmd.getOptionValue("p"));
                File udpipedir = new File(cmd.getOptionValue("o") + "/udpipe");
                udpipedir.mkdirs();
                File extdir = new File(cmd.getOptionValue("o") + "/extraction");
                extdir.mkdirs();
                File triplesFile = new File(cmd.getOptionValue("o") + "/triples.tsv");
                LOG.info("UDPipe...");
                ProcessUDpipe.main(new String[]{"-i", cmd.getOptionValue("i"), "-o", udpipedir.getAbsolutePath(), "-t", cmd.getOptionValue("t", "4")});
                LOG.info("Processing...");
                if (cmd.hasOption("d")) {
                    Process.main(new String[]{"-i", udpipedir.getAbsolutePath(), "-o", extdir.getAbsolutePath(), "-p", cmd.getOptionValue("p"), "-t", cmd.getOptionValue("d"), "-ts", cmd.getOptionValue("ds"), "-tp", cmd.getOptionValue("dp")});
                } else {
                    Process.main(new String[]{"-i", udpipedir.getAbsolutePath(), "-o", extdir.getAbsolutePath(), "-p", cmd.getOptionValue("p")});
                }
                LOG.info("Counting...");
                Count.count(extdir, cmd.getOptionValue("o"));
                List<String> cal = new ArrayList<>();
                cal.add("-i");
                cal.add(extdir.getAbsolutePath());
                cal.add("-o");
                cal.add(triplesFile.getAbsolutePath());
                if (cmd.hasOption("s")) {
                    cal.add("-s");
                    cal.add(cmd.getOptionValue("s"));
                }
                if (cmd.hasOption("f")) {
                    cal.add("-f");
                    cal.add(cmd.getOptionValue("o") + "/pred.count");
                    cal.add("m");
                    cal.add(cmd.getOptionValue("m", "5"));
                }
                if (cmd.hasOption("x")) {
                    cal.add("-t");
                }
                LOG.info("Create dataset...");
                CreateDataset.main(cal.toArray(new String[cal.size()]));
            } else {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("WikiOIE - Pipeline", options);
            }
        } catch (ParseException ex) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("WikiOIE - Pipeline", options);
        } catch (Exception ex) {
            Logger.getLogger(Process.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
