/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.it.wikioie.reasoning;

import di.uniba.it.wikioie.vectors.VectorReader;
import di.uniba.it.wikioie.vectors.lucene.LuceneVectorReader;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.apache.lucene.queryparser.classic.ParseException;

/**
 *
 * @author Alessia
 */
public class TestTripleSim {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     * @throws org.apache.lucene.queryparser.classic.ParseException
     */
    public static void main(String[] args) throws IOException, ParseException {

        String vector_dir = "/home/pierpaolo/data/fasttext/cc.it.300.vec.index";
        String triple_index_dir = "/home/pierpaolo/data/siap/oie/OIE_paper/extraction/LR_index/triple_idx";
        double cosine_threshold = 0.85;
        int n=100;

        VectorReader vr = new LuceneVectorReader(new File(vector_dir));
        vr.init();

        TripleSim sim = new TripleSim(triple_index_dir);
        sim.open();

        Map<String, Integer> m = sim.discoverSimilPred("rientra in", vr, n, cosine_threshold);
        System.out.println(m);
        
        m = sim.discoverSimilSubj("bando", vr, n, cosine_threshold);
        System.out.println(m);
        
        m = sim.discoverSimilObj("regione", vr, n, cosine_threshold);
        System.out.println(m);
        vr.close();
    }

}
