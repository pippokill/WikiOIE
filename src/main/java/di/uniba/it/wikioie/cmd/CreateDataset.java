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

import com.google.gson.Gson;
import di.uniba.it.wikioie.Utils;
import di.uniba.it.wikioie.data.Passage;
import di.uniba.it.wikioie.data.Triple;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
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
public class CreateDataset {

    private static final Logger LOG = Logger.getLogger(CreateDataset.class.getName());

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Options options = new Options();
        options = options.addOption(new Option("i", true, "Input directory"))
                .addOption(new Option("o", true, "Output file"))
                .addOption(new Option("s", true, "Sampling"))
                .addOption(new Option("f", true, "Predicate occurrances file (optional)"))
                .addOption(new Option("m", true, "Min predicate occurrances (optional, 5)"));
        try {
            DefaultParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption("i") && cmd.hasOption("o") && cmd.hasOption("s")) {
                Set<String> filterSet;
                if (cmd.hasOption("f")) {
                    filterSet = Utils.loadFilterSet(new File(cmd.getOptionValue("f")), Integer.parseInt(cmd.getOptionValue("m", "5")));
                } else {
                    filterSet = new HashSet<>();
                }
                BufferedWriter writer = new BufferedWriter(new FileWriter(cmd.getOptionValue("o")));
                Random rnd = new Random();
                double s = Double.parseDouble(cmd.getOptionValue("s"));
                File inputdir = new File(cmd.getOptionValue("i"));
                if (inputdir.isDirectory()) {
                    File[] listFiles = inputdir.listFiles();
                    for (File file : listFiles) {
                        LOG.log(Level.INFO, "Processing file: {0}", file.getAbsolutePath());
                        Gson gson = new Gson();
                        BufferedReader reader;
                        if (file.getName().endsWith(".gz")) {
                            reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(file))));
                        } else {
                            reader = new BufferedReader(new FileReader(file));
                        }
                        while (reader.ready()) {
                            String line = reader.readLine();
                            Passage p = gson.fromJson(line, Passage.class);
                            for (Triple t : p.getTriples()) {
                                if (t.getSubject().getSpan().length() > 0 && t.getPredicate().getSpan().length() > 0 && t.getObject().getSpan().length() > 0) {
                                    if (!filterSet.contains(t.getPredicate().getSpan().toLowerCase())) {
                                        double r = rnd.nextDouble();
                                        if (r <= s) {
                                            writer.append(p.getId()).append("\t").append(p.getTitle())
                                                    .append("\t").append(t.getSubject().getSpan().replaceAll("\t", " "))
                                                    .append("\t").append(t.getPredicate().getSpan().replaceAll("\t", " "))
                                                    .append("\t").append(t.getObject().getSpan().replaceAll("\t", " "));
                                            writer.newLine();
                                        }
                                    }
                                }
                            }
                        }
                        reader.close();
                    }
                }
                writer.close();
            }
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("WikiOIE Run indexing", options);
        }
    }

}
