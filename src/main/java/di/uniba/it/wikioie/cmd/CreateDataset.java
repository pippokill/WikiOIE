/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.it.wikioie.cmd;

import com.google.gson.Gson;
import di.uniba.it.wikioie.data.Counter;
import di.uniba.it.wikioie.data.Passage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
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
                .addOption(new Option("o", true, "Output directory"))
                .addOption(new Option("s", true, "Size"));
        try {
            DefaultParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption("i") && cmd.hasOption("o")) {
                File inputdir = new File(cmd.getOptionValue("i"));
                if (inputdir.isDirectory()) {
                    Map<String, Counter<String>> map = new HashMap<>();
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
                            Counter<String> count = map.get(p.getTitle());
                            if (count == null) {
                                map.put(p.getTitle(), new Counter<>(p.getTitle()));
                            } else {
                                count.increment(p.getTriples().length);
                            }
                        }
                        reader.close();
                    }
                    List<Counter<String>> pages = new ArrayList<>(map.values());
                    Collections.sort(pages, Collections.reverseOrder());
                }
            }
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("WikiOIE Run indexing", options);
        }
    }

}
