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
public class WikiUDpipeProcessThread extends Thread {

    private final BlockingQueue<WikiDoc> in;

    private final UDPParser parser;

    private boolean run = true;

    private final BufferedWriter writer;

    private static final Logger LOG = Logger.getLogger(WikiUDpipeProcessThread.class.getName());

    private final Gson gson = new Gson();

    /**
     *
     * @param in
     * @param parser
     * @param writer
     */
    public WikiUDpipeProcessThread(BlockingQueue<WikiDoc> in, UDPParser parser, BufferedWriter writer) {
        this.in = in;
        this.parser = parser;
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
                doc = in.poll(5, TimeUnit.SECONDS);
            } catch (InterruptedException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
            if (doc != null) {
                try {
                    List<UDPSentence> sentences = parser.getSentences(doc.getText());
                    for (UDPSentence s : sentences) {
                        Passage data = new Passage(doc.getId(), doc.getTitle(), s.getText(), s.getConll(), new Triple[0]);
                        try {
                            writer.append(gson.toJson(data, Passage.class));
                            writer.newLine();
                        } catch (IOException ex) {
                            Logger.getLogger(WikiUDpipeProcessThread.class.getName()).log(Level.SEVERE, null, ex);
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
