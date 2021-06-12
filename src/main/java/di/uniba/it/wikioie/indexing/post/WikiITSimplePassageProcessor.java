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

package di.uniba.it.wikioie.indexing.post;

import di.uniba.it.wikioie.data.Passage;
import di.uniba.it.wikioie.data.Token;
import di.uniba.it.wikioie.data.Triple;
import di.uniba.it.wikioie.process.WikiITSimpleExtractor;
import di.uniba.it.wikioie.udp.UDPParser;
import di.uniba.it.wikioie.udp.UDPSentence;
import java.util.List;
import org.jgrapht.Graph;

/**
 *
 * @author pierpaolo
 */
public class WikiITSimplePassageProcessor implements PassageProcessor {

    private final WikiITSimpleExtractor wie;

    /**
     *
     */
    public WikiITSimplePassageProcessor() {
        this.wie = new WikiITSimpleExtractor();
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
        Graph<Token, String> graph = UDPParser.getGraph(tokens);
        List<Triple> triples = wie.extract(graph);
        Passage r = new Passage(passage.getId(), passage.getTitle(), passage.getText(), passage.getConll(),
                triples.toArray(new Triple[triples.size()]));
        return r;
    }

}
