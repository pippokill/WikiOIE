/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.it.wikioie.process;

import di.uniba.it.wikioie.data.WikiDoc;
import di.uniba.it.wikioie.data.Triple;
import di.uniba.it.wikioie.data.Passage;
import com.google.gson.Gson;
import di.uniba.it.wikioie.udp.UDPParser;
import di.uniba.it.wikioie.udp.UDPSentence;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pierpaolo
 */
public class WikiProcessThread extends Thread {

    private final BlockingQueue<WikiDoc> in;

    private final UDPParser parser;

    private final WikiExtractor ie;

    private boolean run = true;

    private final BufferedWriter writer;

    private static final Logger LOG = Logger.getLogger(WikiProcessThread.class.getName());

    private final Gson gson = new Gson();

    /**
     *
     * @param in
     * @param parser
     * @param ie
     * @param writer
     */
    public WikiProcessThread(BlockingQueue<WikiDoc> in, UDPParser parser, WikiExtractor ie, BufferedWriter writer) {
        this.in = in;
        this.parser = parser;
        this.ie = ie;
        this.writer = writer;
    }

    /**
     *
     */
    @Override
    public void run() {
        while (run) {
            WikiDoc doc = null;
            try {
                doc = in.poll(60, TimeUnit.SECONDS);
            } catch (InterruptedException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
            if (doc != null) {
                try {
                    List<UDPSentence> sentences = parser.getSentences(doc.getText());
                    for (UDPSentence s : sentences) {
                        List<Triple> ts = ie.extract(s.getGraph());
                        Passage data = new Passage(doc.getId(), doc.getTitle(), s.getText(), s.getConll(), ts.toArray(new Triple[ts.size()]));
                        try {
                            writer.append(gson.toJson(data, Passage.class));
                            writer.newLine();
                        } catch (IOException ex) {
                            Logger.getLogger(WikiProcessThread.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } catch (Exception ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    /**
     *
     * @return
     */
    public boolean isRun() {
        return run;
    }

    /**
     *
     * @param run
     */
    public void setRun(boolean run) {
        this.run = run;
    }

}
