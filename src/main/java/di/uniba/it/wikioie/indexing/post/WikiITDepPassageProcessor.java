/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.it.wikioie.indexing.post;

import di.uniba.it.wikioie.data.Passage;
import di.uniba.it.wikioie.data.Token;
import di.uniba.it.wikioie.data.Triple;
import di.uniba.it.wikioie.process.WikiITDepExtractor;
import di.uniba.it.wikioie.udp.UDPParser;
import di.uniba.it.wikioie.udp.UDPSentence;
import java.util.List;
import org.jgrapht.Graph;

/**
 *
 * @author pierpaolo
 */
public class WikiITDepPassageProcessor implements PassageProcessor {

    private final WikiITDepExtractor wie;

    public WikiITDepPassageProcessor() {
        this.wie = new WikiITDepExtractor();
    }

    @Override
    public Passage process(Passage passage) {
        UDPSentence sentence = new UDPSentence(passage.getId(), passage.getText(), passage.getConll());
        List<Token> tokens = UDPParser.getTokens(sentence);
        Graph<Token, String> graph = UDPParser.getGraph(tokens);
        List<Triple> triples = wie.extract(graph);
        Passage r = new Passage(passage.getId(), passage.getTitle(), passage.getText(), passage.getConll(),
                triples.toArray(new Triple[triples.size()]));
        return r;
    }

}
