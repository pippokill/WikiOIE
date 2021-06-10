/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.it.wikioie.test;

import di.uniba.it.wikioie.Utils;
import di.uniba.it.wikioie.data.Config;
import di.uniba.it.wikioie.data.Token;
import di.uniba.it.wikioie.data.Triple;
import di.uniba.it.wikioie.process.WikiExtractor;
import di.uniba.it.wikioie.process.WikiITDepExtractor;
import di.uniba.it.wikioie.process.WikiITSimpleExtractor;
import di.uniba.it.wikioie.udp.UDPParser;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jgrapht.Graph;

/**
 *
 * @author pierpaolo
 */
public class TestDepExtractor {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            String text = Utils.readText("resources/test_01.txt");
            //String text = "Durante il Risorgimento gli italiani lottarono per l'indipendenza nazionale, finché, dopo la Seconda guerra di indipendenza e la Spedizione dei Mille, nel 1861 nacque il Regno d'Italia, che ottenne la vittoria nella Prima guerra mondiale (1918).";
            UDPParser parser = new UDPParser(Config.getInstance().getValue("udp.address"), Config.getInstance().getValue("udp.model"));
            List<Graph<Token, String>> ud = parser.parse(text);
            WikiExtractor wie = new WikiITSimpleExtractor();
            List<Triple> ts = wie.extract(ud);
            for (Triple t : ts) {
                System.out.println(t.toSimpleString());
            }
            System.out.println("*** ================= ***");
            wie = new WikiITDepExtractor();
            ts = wie.extract(ud);
            for (Triple t : ts) {
                System.out.println(t.toSimpleString());
            }
        } catch (Exception ex) {
            Logger.getLogger(TestDepExtractor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
