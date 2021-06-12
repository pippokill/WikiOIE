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

package di.uniba.it.wikioie.test;

import di.uniba.it.wikioie.Utils;
import di.uniba.it.wikioie.data.Config;
import di.uniba.it.wikioie.data.Token;
import di.uniba.it.wikioie.data.Triple;
import di.uniba.it.wikioie.process.WikiExtractor;
import di.uniba.it.wikioie.process.WikiITSimpleDepExtractor;
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
            wie = new WikiITSimpleDepExtractor();
            ts = wie.extract(ud);
            for (Triple t : ts) {
                System.out.println(t.toSimpleString());
            }
        } catch (Exception ex) {
            Logger.getLogger(TestDepExtractor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
