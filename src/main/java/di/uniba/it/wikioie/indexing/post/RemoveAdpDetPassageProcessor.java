/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.it.wikioie.indexing.post;

import di.uniba.it.wikioie.data.Passage;
import di.uniba.it.wikioie.data.Span;
import di.uniba.it.wikioie.data.Token;
import di.uniba.it.wikioie.data.Triple;
import di.uniba.it.wikioie.udp.UDPParser;
import di.uniba.it.wikioie.udp.UDPSentence;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author pierpaolo
 */
public class RemoveAdpDetPassageProcessor implements PassageProcessor {

    /**
     *
     */
    public RemoveAdpDetPassageProcessor() {
    }

    /**
     *
     * @param passage
     * @return
     */
    @Override
    public Passage process(Passage passage) {
        UDPSentence sentence = new UDPSentence(passage.getId(), passage.getText(), passage.getConll());
        List<Token> tokens = UDPParser.getTokens(sentence);
        List<Triple> tl = new ArrayList<>();
        for (Triple t : passage.getTriples()) {
            boolean remove = false;
            Span subject = t.getSubject();
            if (tokens.get(subject.getEnd() - 1).getUpostag().endsWith("ADP")
                    || tokens.get(subject.getEnd() - 1).getUpostag().endsWith("DET")) {
                remove = true;
            }
            Span object = t.getObject();
            if (tokens.get(object.getEnd() - 1).getUpostag().endsWith("ADP")
                    || tokens.get(object.getEnd() - 1).getUpostag().endsWith("DET")) {
                remove = true;
            }
            if (!remove) {
                tl.add(t);
            }
        }
        Passage r = new Passage(passage.getId(), passage.getTitle(), passage.getText(), passage.getConll(),
                 tl.toArray(new Triple[tl.size()]));
        return r;
    }

}
