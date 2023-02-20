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

/**
 *
 * @author Alessia
 */
public class CosineSim {

    private static String[] parse(String line) {
        String ln = line.toLowerCase().trim();
        return ln.split("\\s+");
    }

    private static Vector fromStringToVector(String string, VectorReader vr) throws IOException {
        Vector vector = Utils.getTextVector(parse(string), vr);
        return vector;
    }

    public static double sim(String s1, String s2, VectorReader vr) throws IOException {
        Vector v1 = CosineSim.fromStringToVector(s1, vr);
        Vector v2 = CosineSim.fromStringToVector(s2, vr);
        return v1.measureOverlap(v2);
    }

}
