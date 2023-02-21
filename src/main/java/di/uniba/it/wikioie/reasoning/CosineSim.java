/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.it.wikioie.reasoning;

import di.uniba.it.wikioie.Utils;
import di.uniba.it.wikioie.vectors.Vector;
import di.uniba.it.wikioie.vectors.VectorReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.it.ItalianAnalyzer;

/**
 *
 * @author Alessia
 */
public class CosineSim {

    private static CharArraySet ss = ItalianAnalyzer.getDefaultStopSet();

    private static List<String> parse(String line) {
        String ln = line.toLowerCase().trim();
        List<String> l = new ArrayList<>();
        String[] split = ln.split("\\s+");
        for (String t : split) {
            if (!ss.contains(t)) {
                l.add(t);
            }
        }
        return l;
    }

    public static double sim(String s1, String s2, VectorReader vr) throws IOException {
        Vector v1 = Utils.getTextVector(parse(s1), vr);
        Vector v2 = Utils.getTextVector(parse(s2), vr);
        return v1.measureOverlap(v2);
    }

}
