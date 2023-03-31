/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.it.wikioie.reasoning;

import di.uniba.it.wikioie.data.Counter;
import di.uniba.it.wikioie.vectors.VectorReader;
import di.uniba.it.wikioie.vectors.lucene.LuceneVectorReader;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.lucene.queryparser.classic.ParseException;

/**
 *
 * @author Alessia
 */
public class TestTripleFullVectorSim {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     * @throws org.apache.lucene.queryparser.classic.ParseException
     */
    public static void main(String[] args) throws IOException, ParseException {

        String vector_dir = "/home/pierpaolo/data/fasttext/cc.it.300.vec.index";
        String triple_index_dir = "/home/pierpaolo/data/siap/oie/OIE_paper/extraction/LR_index/triple_idx";
        //String triple_vector_index_dir = "/home/pierpaolo/data/siap/oie/OIE_paper/extraction/LR_index/triple_vectors_idx";
        String triple_vector_bin = "/home/pierpaolo/data/siap/oie/OIE_paper/extraction/LR_index/triple_vectors.bin";
        double cosine_threshold = 0.85;
        int n = 100;

        VectorReader vr = new LuceneVectorReader(new File(vector_dir));
        vr.init();

        TripleFullVectorSim vectorSim = new TripleFullVectorSim(triple_index_dir, triple_vector_bin, TripleVectorIndex.STORE_TYPE.MEM);
        vectorSim.open();

        System.out.println("Searching...");
        System.out.println("Predicate...");
        List<Counter> l = vectorSim.discoverSimilPred("adotta come", vr, n, cosine_threshold);
        for (Counter c : l) {
            System.out.println(c.getItem() + "\t" + c.getCount());
        }
        /*System.out.println("Subject...");
        l = vectorSim.discoverSimilSubj("bando", vr, n, cosine_threshold);
        for (Counter c : l) {
            System.out.println(c.getItem() + "\t" + c.getCount());
        }
        System.out.println("Object...");
        l = vectorSim.discoverSimilObj("bando", vr, n, cosine_threshold);
        for (Counter c : l) {
            System.out.println(c.getItem() + "\t" + c.getCount());
        }*/
        vr.close();

    }

}