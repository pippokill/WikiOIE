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
package di.uniba.it.wikioie.process;

import di.uniba.it.wikioie.data.Triple;
import di.uniba.it.wikioie.data.Span;
import di.uniba.it.wikioie.data.Token;
import di.uniba.it.wikioie.data.TokenIdComparator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.jgrapht.Graph;

/**
 *
 * @author pierpaolo
 */
public class WikiITSimpleDepPNSubjExtractor implements WikiExtractor {

    /**
     *
     */
    public WikiITSimpleDepPNSubjExtractor() {
    }

    private Span getSpan(List<Token> list, int start, int end) {
        float score = 1;
        float l = 0;
        StringBuilder sb = new StringBuilder();
        for (int k = start; k < end; k++) {
            if (list.get(k).getUpostag().equals("PROPN")) {
                score += 2;
            } else if (list.get(k).getUpostag().equals("NOUN")) {
                score += 1;
            }
            sb.append(list.get(k).getForm()).append(" ");
            l++;
        }
        return new Span(sb.toString().trim(), start, end, score * (1 / l));
    }

    private Span checkSubject(List<Token> list, int offset) {
        if (offset < 0) {
            return null;
        }
        int j = offset;
        boolean noun = false;
        boolean pnoun = false;
        int adp_c = 0;
        int det_c = 0;
        while (j >= 0) {
            if (list.get(j).getUpostag().equals("DET")
                    || list.get(j).getUpostag().equals("ADP")
                    || list.get(j).getUpostag().equals("NOUN")
                    || list.get(j).getUpostag().equals("PROPN")
                    || list.get(j).getUpostag().equals("ADJ")
                    || list.get(j).getUpostag().equals("NUM")) {
                if (!noun) {
                    noun = list.get(j).getUpostag().equals("NOUN") || list.get(j).getUpostag().equals("PROPN") || list.get(j).getUpostag().equals("NUM");
                }
                if (!pnoun) {
                    pnoun = list.get(j).getUpostag().equals("PROPN");
                }
                if (list.get(j).getUpostag().equals("ADP")) {
                    adp_c++;
                }
                if (list.get(j).getUpostag().equals("DET")) {
                    det_c++;
                }
            } else {
                break;
            }
            j--;
        }
        if (!pnoun) {
            return null;
        }
        if (adp_c > 1) {
            return null;
        }
        if (list.get(offset).getUpostag().equals("ADP") || list.get(offset).getUpostag().equals("DET")) {
            return null;
        }
        if (noun && j < offset) {
            return getSpan(list, j + 1, offset + 1);
        } else {
            return null;
        }
    }

    private Span checkObject(List<Token> list, int offset) {
        if (offset >= list.size()) {
            return null;
        }
        int j = offset;
        boolean noun = false;
        int adp_c = 0;
        int det_c = 0;
        while (j < list.size()) {
            if (list.get(j).getUpostag().equals("DET")
                    || list.get(j).getUpostag().equals("ADP")
                    || list.get(j).getUpostag().equals("NOUN")
                    || list.get(j).getUpostag().equals("ADJ")
                    || list.get(j).getUpostag().equals("PROPN")
                    || list.get(j).getUpostag().equals("NUM")) {
                if (!noun) {
                    noun = list.get(j).getUpostag().equals("NOUN") || list.get(j).getUpostag().equals("PROPN") || list.get(j).getUpostag().equals("NUM");
                }
                if (list.get(j).getUpostag().equals("ADP")) {
                    adp_c++;
                }
                if (list.get(j).getUpostag().equals("DET")) {
                    det_c++;
                }
            } else {
                break;
            }
            j++;
        }
        if (adp_c > 1) {
            return null;
        }
        if (list.get(j - 1).getUpostag().equals("ADP") || list.get(j - 1).getUpostag().equals("DET")) {
            return null;
        }
        if (noun && j > offset) {
            return getSpan(list, offset, j);
        } else {
            return null;
        }
    }

    private boolean isLinked(Graph<Token, String> graph, List<Token> deps, List<Token> heads) {
        Set<Token> seth = new HashSet<>(heads);
        for (Token t : deps) {
            Set<String> oute = graph.outgoingEdgesOf(t);
            for (String e : oute) {
                Token edgeTarget = graph.getEdgeTarget(e);
                if (seth.contains(edgeTarget)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isValidTriple(List<Token> tokens, Graph<Token, String> graph, Triple triple) {
        List<Token> pl = tokens.subList(triple.getPredicate().getStart(), triple.getPredicate().getEnd());
        List<Token> sl = tokens.subList(triple.getSubject().getStart(), triple.getSubject().getEnd());
        if (!isLinked(graph, sl, pl)) {
            return false;
        } else {
            List<Token> ol = tokens.subList(triple.getObject().getStart(), triple.getObject().getEnd());
            return isLinked(graph, ol, pl);
        }
    }

    /**
     *
     * @param text
     * @return
     */
    @Override
    public List<Triple> extract(List<Graph<Token, String>> text) {
        List<Triple> ts = new ArrayList<>();
        for (Graph<Token, String> g : text) {
            ts.addAll(extract(g));
        }
        return ts;
    }

    /**
     *
     * @param g
     * @return
     */
    @Override
    public List<Triple> extract(Graph<Token, String> g) {
        List<Triple> ts = new ArrayList<>();
        List<Token> list = new ArrayList<>(g.vertexSet());
        Collections.sort(list, new TokenIdComparator());
        int i = 0;
        while (i < list.size()) {
            Token t = list.get(i);
            if (t.getUpostag().equals("AUX")) {
                if (i + 1 < list.size() && list.get(i + 1).getUpostag().equals("VERB")) {
                    if (i + 2 < list.size() && list.get(i + 2).getUpostag().equals("ADP")) {
                        Span subj = checkSubject(list, i - 1);
                        Span obj = checkObject(list, i + 3);
                        if (subj != null && obj != null) {
                            Triple triple = new Triple(subj, getSpan(list, i, i + 3), obj, subj.getScore() + obj.getScore());
                            if (isValidTriple(list, g, triple)) {
                                ts.add(triple);
                            }
                            i = i + 2;
                        }
                    } else {
                        Span subj = checkSubject(list, i - 1);
                        Span obj = checkObject(list, i + 2);
                        if (subj != null && obj != null) {
                            Triple triple = new Triple(subj, getSpan(list, i, i + 2), obj, subj.getScore() + obj.getScore());
                            if (isValidTriple(list, g, triple)) {
                                ts.add(triple);
                            }
                            i = i + 1;
                        }
                    }
                }
            } else if (t.getUpostag().equals("AUX") && t.getLemma().equals("essere")) {
                Span subj = checkSubject(list, i - 1);
                Span obj = checkObject(list, i + 1);
                if (subj != null && obj != null) {
                    Triple triple = new Triple(subj, getSpan(list, i, i + 1), obj, subj.getScore() + obj.getScore());
                    if (isValidTriple(list, g, triple)) {
                        ts.add(triple);
                    }
                }
            } else if (t.getUpostag().equals("VERB")) {
                if (i + 1 < list.size() && list.get(i + 1).getUpostag().equals("ADP")) {
                    Span subj = checkSubject(list, i - 1);
                    Span obj = checkObject(list, i + 2);
                    if (subj != null && obj != null) {
                        Triple triple = new Triple(subj, getSpan(list, i, i + 2), obj, subj.getScore() + obj.getScore());
                        if (isValidTriple(list, g, triple)) {
                            ts.add(triple);
                        }
                        i = i + 1;
                    }
                } else {
                    Span subj = checkSubject(list, i - 1);
                    Span obj = checkObject(list, i + 1);
                    if (subj != null && obj != null) {
                        Triple triple = new Triple(subj, getSpan(list, i, i + 1), obj, subj.getScore() + obj.getScore());
                        if (isValidTriple(list, g, triple)) {
                            ts.add(triple);
                        }
                    }
                }
            }
            i++;
        }
        return ts;
    }

}
