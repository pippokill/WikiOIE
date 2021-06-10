/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.it.wikioie.cmd;

import di.uniba.it.wikioie.indexing.SearchDoc;
import di.uniba.it.wikioie.indexing.SearchTriple;
import di.uniba.it.wikioie.indexing.WikiOIEIndex;
import di.uniba.it.wikioie.indexing.post.Count;
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
public class RunSearch {

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
            Logger.getLogger(RunSearch.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
