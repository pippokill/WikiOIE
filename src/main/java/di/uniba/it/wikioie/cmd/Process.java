/**
 * Copyright (c) 2021, the WikiOIE AUTHORS.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of the University of Bari nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * GNU GENERAL PUBLIC LICENSE - Version 3, 29 June 2007
 *
 */
package di.uniba.it.wikioie.cmd;

import di.uniba.it.wikioie.Utils;
import di.uniba.it.wikioie.indexing.WikiOIEIndex;
import di.uniba.it.wikioie.indexing.post.PassageProcessor;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

import di.uniba.it.wikioie.vectors.VectorReader;
import di.uniba.it.wikioie.vectors.lucene.LuceneVectorReader;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * This class processes a JSON dump of Wikipedia with UDPipe annotations and
 * extracts triples using a WikiExtractor class. The output is stored in JSON
 * format.
 *
 * @author pierpaolo
 */
public class Process {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Options options = new Options();
        options = options.addOption(new Option("i", true, "Input directory"))
                .addOption(new Option("o", true, "Output directory"))
                .addOption(new Option("p", true, "Processing class"))
                .addOption(new Option("t", true, "Training file (optional)"))
                .addOption(new Option("ts", true, "Supervised algorithm (SVC or XGboost, default SVC"))
                // for SVC solver=L2R;C=1 or solver=SVC;C=1
                .addOption(new Option("tp", true, "Supervised parameter (format key1=value1;key2=value2;...;keyN=valueN"))
                .addOption(new Option("v", true, "Vector embedding (optional, default=null)"));
        try {
            DefaultParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption("i") && cmd.hasOption("o") && cmd.hasOption("p")) {
                WikiOIEIndex idx = new WikiOIEIndex();
                PassageProcessor processor = null;
                try {
                    if (cmd.hasOption("t")) {
                        if (cmd.hasOption("ts")) {
                            if (cmd.getOptionValue("ts").equalsIgnoreCase("SVC")) {
                                Map<String, String> params = Utils.getOptionParams(cmd.getOptionValue("tp", "solver=L2R;C=1"));
                                processor = (PassageProcessor) ClassLoader.getSystemClassLoader().loadClass("di.uniba.it.wikioie.indexing.post." + cmd.getOptionValue("p"))
                                        .getDeclaredConstructor(File.class, Double.class, String.class, VectorReader.class).newInstance(new File(cmd.getOptionValue("t")),
                                        Double.parseDouble(params.get("C")), params.get("solver"),
                                        new LuceneVectorReader(new File(cmd.getOptionValue("v", null))));
                            } else if (cmd.getOptionValue("ts").equalsIgnoreCase("XGboost")) {
                                Map<String, String> params = Utils.getOptionParams(cmd.getOptionValue("tp", "eta=0.4;max_depth=12;verbosity=1;seed=42;objective=binary:logistic;round=80"));
                                Map<String, Object> pm = new HashMap<>();
                                pm.put("eta", Double.parseDouble(params.get("eta")));
                                pm.put("max_depth", Integer.parseInt(params.get("max_depth")));
                                pm.put("verbosity", Integer.parseInt(params.get("verbosity")));
                                pm.put("seed", Integer.parseInt(params.get("seed")));
                                pm.put("objective", params.get("objective"));
                                processor = (PassageProcessor) ClassLoader.getSystemClassLoader().loadClass("di.uniba.it.wikioie.indexing.post." + cmd.getOptionValue("p"))
                                        .getDeclaredConstructor(File.class, VectorReader.class, Map.class, Integer.TYPE).newInstance(new File(cmd.getOptionValue("t")),
                                        new LuceneVectorReader(new File(cmd.getOptionValue("v", null))), pm, Integer.parseInt(params.get("round")));
                            } else {
                                throw new IllegalArgumentException("Supervised algorithm " + cmd.getOptionValue("ts") + " is not supported.");
                            }
                        } else {
                            throw new IllegalArgumentException("Supervised algorithm is missing");
                        }
                    } else {
                        processor = (PassageProcessor) ClassLoader.getSystemClassLoader().loadClass("di.uniba.it.wikioie.indexing.post." + cmd.getOptionValue("p")).getDeclaredConstructor().newInstance();
                    }
                } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                    Logger.getLogger(Indexing.class.getName()).log(Level.SEVERE, "Not valid processor...exit", ex);
                    System.exit(1);
                }
                idx.process(cmd.getOptionValue("i"), cmd.getOptionValue("o"), processor);
            } else {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("WikiOIE - Run processing", options);
            }
        } catch (IOException ex) {
            Logger.getLogger(Process.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("WikiOIE Run indexing", options);
        }
    }

}
