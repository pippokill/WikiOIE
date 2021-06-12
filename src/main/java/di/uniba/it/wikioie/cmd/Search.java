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

import di.uniba.it.wikioie.indexing.SearchDoc;
import di.uniba.it.wikioie.indexing.SearchTriple;
import di.uniba.it.wikioie.indexing.WikiOIEIndex;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.queryparser.classic.ParseException;

/**
 *
 * @author pierpaolo
 */
public class Search {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            if (args.length > 0) {
                WikiOIEIndex idx = new WikiOIEIndex();
                idx.open(args[0]);
                Scanner scanner = new Scanner(System.in);
                scanner.useDelimiter("\n");
                while (scanner.hasNext()) {
                    String cmd = scanner.next();
                    if (cmd.startsWith("st")) {
                        List<SearchTriple> ts = idx.searchTriple(cmd.substring(3).trim(), 100);
                        for (SearchTriple t : ts) {
                            System.out.println(t.getDocid() + "\t" + t.getSearchScore() + "\t" + t.getSubject().toString() + " | " + t.getPredicate().toString() + " | " + t.getObject().toString() + "\t" + t.getScore());
                        }
                    } else if (cmd.startsWith("sd")) {
                        List<SearchDoc> ds = idx.searchDocByTitle(cmd.substring(3).trim(), 25);
                        for (SearchDoc d : ds) {
                            System.out.println(d.getId() + "\t" + d.getSearchScore() + "\t" + d.getText() + "\t" + d.getText());
                        }
                    } else if (cmd.startsWith("doc")) {
                        SearchDoc doc = idx.getDocById(cmd.substring(4).trim());
                        System.out.println(doc.getId() + "\t" + doc.getText() + "\t" + doc.getText());
                    } else if (cmd.equals("help")) {
                        System.out.println("st <query>: search triples");
                        System.out.println("sd <query>: search documents by title");
                        System.out.println("doc <doc id>: get doc by id");
                        System.out.println("!x: exit");
                        System.out.println("help: help");
                    } else if (cmd.equals("!x")) {
                        break;
                    } else {
                        System.out.println("command not found");
                    }
                }
            } else {
                Logger.getLogger(Count.class.getName()).log(Level.SEVERE, "Not valid arguments, input directory is necessary.");
            }
        } catch (IOException | ParseException ex) {
            Logger.getLogger(Search.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
