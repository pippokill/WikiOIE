/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.it.wikioie.cmd;

import di.uniba.it.wikioie.data.WikiDoc;
import di.uniba.it.wikioie.data.Config;
import di.uniba.it.wikioie.process.WikiExtractor;
import di.uniba.it.wikioie.process.WikiProcessThread;
import di.uniba.it.wikioie.udp.UDPParser;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
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
public class ProcessWiki {

    private static BlockingQueue<WikiDoc> in = new ArrayBlockingQueue(1000);

    private static int pc = 0;

    private static final Logger LOG = Logger.getLogger(ProcessWiki.class.getName());

    private static void process(File file) throws Exception {
        if (file.isDirectory()) {
            File[] listFiles = file.listFiles();
            for (File f : listFiles) {
                process(f);
            }
        } else {
            if (file.isFile() && file.getName().startsWith("wiki_")) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String id = "";
                String title = "";
                while (reader.ready()) {
                    String text = reader.readLine();
                    if (text.startsWith("<doc") || text.startsWith("</doc")) {
                        if (text.startsWith("<doc")) {
                            int s = text.indexOf("title=\"");
                            int e = text.lastIndexOf('"');
                            if (s >= 0 && e > s) {
                                title = text.substring(s + 7, e);
                            }
                            s = text.indexOf("id=\"");
                            e = text.indexOf('"', s + 4);
                            if (s >= 0 && e > s) {
                                id = text.substring(s + 4, e);
                            }
                        }
                        pc++;
                        if (pc % 100 == 0) {
                            System.out.println("Pages: " + pc);
                        }
                    } else {
                        try {
                            in.offer(new WikiDoc(id, title, text), 60, TimeUnit.SECONDS);
                        } catch (InterruptedException ex) {
                            LOG.log(Level.SEVERE, null, ex);
                        }
                    }
                }
                reader.close();
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Options options = new Options();
        options = options.addOption(new Option("i", true, "Input directory"))
                .addOption(new Option("o", true, "Output directory"))
                .addOption(new Option("p", true, "Processor"))
                .addOption(new Option("t", true, "Number of threads (optional, default 4)"));
        try {
            DefaultParser cmdparser = new DefaultParser();
            CommandLine cmd = cmdparser.parse(options, args);
            if (cmd.hasOption("i") && cmd.hasOption("o") && cmd.hasOption("p")) {
                try {
                    int nt = Integer.parseInt(cmd.getOptionValue("t", "4"));
                    LOG.log(Level.INFO, "Threads: {0}", nt);
                    List<WikiProcessThread> list = new ArrayList<>();
                    List<BufferedWriter> buffs = new ArrayList<>();
                    LOG.log(Level.INFO, "Output dir: {0}", cmd.getOptionValue("o"));
                    for (int i = 0; i < nt; i++) {
                        UDPParser parser = new UDPParser(Config.getInstance().getValue("udp.address"), Config.getInstance().getValue("udp.model"));
                        BufferedWriter writer = new BufferedWriter(new FileWriter(cmd.getOptionValue("o") + "/wikiext_" + i));
                        WikiExtractor ie=(WikiExtractor) ClassLoader.getSystemClassLoader().loadClass("di.uniba.it.wikioie.process."+cmd.getOptionValue("p"))
                                .getDeclaredConstructor().newInstance();
                        list.add(new WikiProcessThread(in, parser, ie, writer));
                        buffs.add(writer);
                    }
                    for (Thread t : list) {
                        t.start();
                    }
                    LOG.log(Level.INFO, "Input dump: {0}", cmd.getOptionValue("i"));
                    process(new File(cmd.getOptionValue("o")));
                    LOG.info("Processing...");
                    for (WikiProcessThread t : list) {
                        t.setRun(false);
                    }
                    LOG.info("Waiting for threads...");
                    for (Thread t : list) {
                        t.join();
                    }
                    LOG.info("Closing...");
                    for (BufferedWriter w : buffs) {
                        w.close();
                    }
                    LOG.log(Level.INFO, "Processed: {0}", pc);
                } catch (Exception ex) {
                    Logger.getLogger(ProcessWiki.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("WikiOIE - Process Wikipedia", options);
            }
        } catch (ParseException ex) {
            Logger.getLogger(ProcessWiki.class.getName()).log(Level.SEVERE, null, ex);
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("WikiOIE - Process Wikipedia", options);
        }
    }

}
