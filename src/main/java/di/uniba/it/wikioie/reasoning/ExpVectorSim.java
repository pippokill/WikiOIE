/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package di.uniba.it.wikioie.reasoning;

import di.uniba.it.wikioie.data.Counter;
import di.uniba.it.wikioie.vectors.VectorReader;
import di.uniba.it.wikioie.vectors.lucene.LuceneVectorReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.lucene.queryparser.classic.ParseException;

/**
 *
 * @author pierpaolo
 */
public class ExpVectorSim {

    private static final String[] preds = new String[]{"procede a", "adotta come", "indica", "prevede", "partecipa a",
        "è subordinata a", "deve possedere", "deve fornire", "costituisce", "comporta"};

    private static final String[] subjs = new String[]{"il concorrente", "il sistema", "la stazione appaltante", "l' operatore economico", "l' appalto",
        "la piattaforma", "la commissione", "il mandato", "la rete", "l' impresa"};

    private static final String[] objs = new String[]{"le offerte telematiche", "la gara", "i requisiti", "informazioni", "portale",
        "il consorzio", "il contratto", "il campo", "un progetto", "lotti"};

    public static void main(String[] args) {
        try {
            String vector_dir = "/home/pierpaolo/data/fasttext/cc.it.300.vec.index";
            String triple_index_dir = "/home/pierpaolo/data/siap/oie/OIE_paper/extraction/LR_index/triple_idx";
            //String triple_vector_index_dir = "/home/pierpaolo/data/siap/oie/OIE_paper/extraction/LR_index/triple_vectors_idx";
            String triple_vector_bin = "/home/pierpaolo/data/siap/oie/OIE_paper/extraction/LR_index/triple_vectors.bin";
            double cosine_threshold = 0.85;
            int n = 100;

            VectorReader vr = new LuceneVectorReader(new File(vector_dir));
            vr.init();

            TripleVectorSim vectorSim = new TripleVectorSim(triple_index_dir, triple_vector_bin, TripleVectorIndex.STORE_TYPE.MEM);
            vectorSim.open();

            int topn = 25;

            System.out.println("Predicates...");
            Appendable out = new FileWriter("./preds_vec_sim.csv");
            CSVPrinter printer = CSVFormat.DEFAULT.print(out);
            for (String q : preds) {
                printer.printRecord(q, "", "");
                List<Counter> l = vectorSim.discoverSimilPred(q, vr, n, cosine_threshold);
                for (int i = 0; i < l.size() && i < topn; i++) {
                    printer.printRecord("", l.get(i).getItem().toString(), l.get(i).getCount());
                }
                printer.flush();
            }
            printer.close();

            System.out.println("Subjects...");
            out = new FileWriter("./subjs_vec_sim.csv");
            printer = CSVFormat.DEFAULT.print(out);
            for (String q : subjs) {
                printer.printRecord(q, "", "");
                List<Counter> l = vectorSim.discoverSimilSubj(q, vr, n, cosine_threshold);
                for (int i = 0; i < l.size() && i < topn; i++) {
                    printer.printRecord("", l.get(i).getItem().toString(), l.get(i).getCount());
                }
                printer.flush();
            }
            printer.close();

            System.out.println("Objects...");
            out = new FileWriter("./objs_vec_sim.csv");
            printer = CSVFormat.DEFAULT.print(out);
            for (String q : objs) {
                printer.printRecord(q, "", "");
                List<Counter> l = vectorSim.discoverSimilObj(q, vr, n, cosine_threshold);
                for (int i = 0; i < l.size() && i < topn; i++) {
                    printer.printRecord("", l.get(i).getItem().toString(), l.get(i).getCount());
                }
                printer.flush();
            }
            printer.close();
        } catch (IOException | ParseException ex) {
            Logger.getLogger(ExpVectorSim.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
