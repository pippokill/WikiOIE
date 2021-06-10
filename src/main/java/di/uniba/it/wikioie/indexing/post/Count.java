/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.it.wikioie.indexing.post;

import di.uniba.it.wikioie.data.Counter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.store.FSDirectory;

/**
 *
 * @author pierpaolo
 */
public class Count {

    private static final String[] countFieldLabel = new String[]{"subj", "pred", "obj"};

    /**
     *
     * @param indexDir
     * @throws IOException
     */
    public static void countPredicate(File indexDir) throws IOException {
        DirectoryReader reader = DirectoryReader.open(FSDirectory.open(indexDir.toPath()));
        Map<String, Counter<String>>[] map = new Map[countFieldLabel.length];
        for (int f = 0; f < countFieldLabel.length; f++) {
            map[f] = new HashMap<>();
        }
        for (int i = 0; i < reader.maxDoc(); i++) {
            Document document = reader.document(i);
            if (document != null) {
                for (int f = 0; f < countFieldLabel.length; f++) {
                    String k = document.get(countFieldLabel[f]).toLowerCase();
                    Counter<String> c = map[f].get(k);
                    if (c == null) {
                        map[f].put(k, new Counter<>(k));
                    } else {
                        c.increment();
                    }
                }
            }
        }
        reader.close();
        for (int f = 0; f < countFieldLabel.length; f++) {
            List<Counter<String>> l = new ArrayList<>();
            l.addAll(map[f].values());
            Collections.sort(l, Collections.reverseOrder());
            BufferedWriter writer = new BufferedWriter(new FileWriter(indexDir.getParent() + "/" + indexDir.getName() + "_" + countFieldLabel[f] + ".count"));
            for (Counter<String> c : l) {
                writer.append(c.getItem()).append("\t").append(String.valueOf(c.getCount()));
                writer.newLine();
            }
            writer.close();
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length > 0) {
            try {
                countPredicate(new File(args[0]));
            } catch (IOException ex) {
                Logger.getLogger(Count.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            Logger.getLogger(Count.class.getName()).log(Level.SEVERE, "Not valid arguments, input directory is necessary.");
        }
    }

}
