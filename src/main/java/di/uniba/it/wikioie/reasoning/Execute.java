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
public class Execute {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     * @throws org.apache.lucene.queryparser.classic.ParseException
     */
    public static void main(String[] args) throws IOException, ParseException {

        String vector_dir = "/home/pierpaolo/data/fasttext/cc.it.300.vec.index";
        String triple_index_dir = "/home/pierpaolo/data/siap/oie/OIE_paper/extraction/LR_index/triple_idx";
        double cosine_threshold = 0.8;

        VectorReader vr = new LuceneVectorReader(new File(vector_dir));
        vr.init();

        IndexUtils index = new IndexUtils(triple_index_dir);
        index.open();
        
        Map<String, Integer> m = index.discoverSimilPred("rientra in", vr, 1000, cosine_threshold);
        System.out.println(m);

        List<Triple> st = index.searchTriple("subj:metri", 5);
        if (!st.isEmpty()) {
            System.out.println(st.get(0));
            List<Triple> discover = index.discover(st.get(0).getDocid(), vr, 1000, cosine_threshold);
            for (Triple t : discover) {
                System.out.println(t);
            }
        }
        System.exit(0);

        int index_dim = index.indexDimension();
        System.out.println("Index dim: " + index_dim);
        Triple ran_tr1 = index.randomTriple(index_dim);
        Triple ran_tr2 = index.searchSecondTriple(ran_tr1, vr, 1000, cosine_threshold);

        if (!(ran_tr2.getSub().isBlank())) {
            System.out.println("Random triples:");
            ran_tr1.printTriple();
            ran_tr2.printTriple();

            //CALCOLO PER LA PRIMA TRIPLA
            System.out.println("--First triple:\nSubject = " + ran_tr1.getSub() + "\tPredicate = " + ran_tr1.getPred() + "\tObject = " + ran_tr1.getObj());

            //Numeratore
            System.out.println("*********Simil triple(s) found searching by subject, predicate and object:*********");
            List<Triple> simil1_SubPredObjs = index.similSPO(ran_tr1.getSub(), ran_tr1.getPred(), ran_tr1.getObj(), vr, 1000, cosine_threshold);
            for (Triple tr : simil1_SubPredObjs) {
                tr.printTriple();
            }

            double prob_num1 = Probability.fraction(simil1_SubPredObjs.size(), index_dim);

            //Denominatore
            System.out.println("*********Simil triple(s) found searching by subject and predicate:*********");
            List<Triple> simil1_SubPreds = index.similSP(ran_tr1.getSub(), ran_tr1.getPred(), vr, 1000, cosine_threshold);
            for (Triple tr : simil1_SubPreds) {
                tr.printTriple();
            }
            double prob_denom1 = Probability.fraction(simil1_SubPreds.size(), index_dim);

            //Probabilità condizionata
            double probCond1 = Probability.probCond(prob_num1, prob_denom1);
            System.out.println("*********Conditional probability of the first triple: " + probCond1 + "*********");

            //CALCOLO PER LA SECONDA TRIPLA
            System.out.println("\n--Second triple:\nSubject = " + ran_tr2.getSub() + "\tPredicate = " + ran_tr2.getPred() + "\tObject = " + ran_tr2.getObj());

            //Numeratore
            System.out.println("*********Simil triple(s) found searching by subject, predicate and object:*********");
            List<Triple> simil2_SubPredObjs = index.similSPO(ran_tr2.getSub(), ran_tr2.getPred(), ran_tr2.getObj(), vr, 1000, cosine_threshold);
            for (Triple tr : simil2_SubPredObjs) {
                tr.printTriple();
            }
            double prob_num2 = Probability.fraction(simil2_SubPredObjs.size(), index_dim);

            //Denominatore
            System.out.println("*********Simil triple(s) found searching by subject and predicate:*********");
            List<Triple> simil2_SubPreds = index.similSP(ran_tr2.getSub(), ran_tr2.getPred(), vr, 1000, cosine_threshold);
            for (Triple tr : simil2_SubPreds) {
                tr.printTriple();
            }
            double prob_denom2 = Probability.fraction(simil2_SubPreds.size(), index_dim);

            //Probabilità condizionata
            double probCond2 = Probability.probCond(prob_num2, prob_denom2);
            System.out.println("*********Conditional probability of the second triple: " + probCond2 + "*********");

            //PROBABILITA' NUOVA RELAZIONE
            double new_rel_prob = Probability.finalProduct(probCond1, probCond2);
            System.out.println("\nThe probability of a new relation between '" + ran_tr1.getSub() + "' and '" + ran_tr2.getObj() + "' is: " + new_rel_prob);
        }
        vr.close();
    }

}
