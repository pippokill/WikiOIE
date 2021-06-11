/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.it.wikioie.cmd;

import di.uniba.it.wikioie.Utils;
import di.uniba.it.wikioie.indexing.WikiOIEIndex;
import di.uniba.it.wikioie.indexing.post.PassageProcessor;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;
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
public class RunProcessing {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Options options = new Options();
        options = options.addOption(new Option("i", true, "Input directory"))
                .addOption(new Option("o", true, "Output directory"))
                .addOption(new Option("p", true, "Post processing class (optional)"));
        try {
            DefaultParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption("i") && cmd.hasOption("o")) {
                WikiOIEIndex idx = new WikiOIEIndex();
                PassageProcessor processor = null;
                if (cmd.hasOption("p")) {
                    try {
                        processor = (PassageProcessor) ClassLoader.getSystemClassLoader().loadClass("di.uniba.it.wikioie.indexing.post." + cmd.getOptionValue("p")).getDeclaredConstructor().newInstance();
                    } catch (ClassNotFoundException | NoSuchMethodException ex) {
                        Logger.getLogger(RunProcessing.class.getName()).log(Level.SEVERE, "Not valid processor, use null", ex);
                    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                        Logger.getLogger(RunProcessing.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                idx.process(cmd.getOptionValue("i"), cmd.getOptionValue("o"), processor);
            } else {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("WikiOIE - Run indexing", options);
            }
        } catch (IOException ex) {
            Logger.getLogger(RunProcessing.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("WikiOIE Run indexing", options);
        }
    }

}
