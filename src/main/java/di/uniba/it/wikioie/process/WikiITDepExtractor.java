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
@Deprecated
public class WikiITDepExtractor implements WikiExtractor {

    /**
     *
     */
    public WikiITDepExtractor() {
    }

    private Span getSpan(List<Token> list, int start, int end) {
        float score = 1;
        StringBuilder sb = new StringBuilder();
        for (int k = start; k < end; k++) {
            if (list.get(k).getUpostag().equals("PROPN")) {
                score = 2;
            }
            sb.append(list.get(k).getForm()).append(" ");
        }
        return new Span(sb.toString().trim(), start, end, score);
    }

    private Span getSpan(List<Token> list) {
        float score = 1;
        StringBuilder sb = new StringBuilder();
        for (int k = 0; k < list.size(); k++) {
            if (list.get(k).getUpostag().equals("PROPN")) {
                score = 2;
            }
            sb.append(list.get(k).getForm()).append(" ");
        }
        return new Span(sb.toString().trim(), list.get(0).getId() - 1, list.get(list.size() - 1).getId(), score);
    }

    private Span checkSubject(List<Token> list, int offset, int min) {
        if (offset >= list.size() || offset < 0) {
            return null;
        }
        int start = offset;
        boolean noun = false;
        int adp_c = 0;
        int det_c = 0;
        while (start >= 0) {
            if (list.get(start).getUpostag().equals("DET")
                    || list.get(start).getUpostag().equals("ADP")
                    || list.get(start).getUpostag().equals("NOUN")
                    || list.get(start).getUpostag().equals("PROPN")
                    || list.get(start).getUpostag().equals("ADJ")
                    || list.get(start).getUpostag().equals("NUM")
                    || list.get(start).getUpostag().equals("ADV")) {
                if (!noun) {
                    noun = list.get(start).getUpostag().equals("NOUN") || list.get(start).getUpostag().equals("PROPN") || list.get(start).getUpostag().equals("NUM");
                }
                if (list.get(start).getUpostag().equals("ADP")) {
                    adp_c++;
                }
                if (list.get(start).getUpostag().equals("DET")) {
                    det_c++;
                }
            } else {
                break;
            }
            start--;
        }
        start++;
        int end = offset + 1;
        while (end < list.size()) {
            if (list.get(end).getUpostag().equals("DET")
                    || list.get(end).getUpostag().equals("ADP")
                    || list.get(end).getUpostag().equals("NOUN")
                    || list.get(end).getUpostag().equals("PROPN")
                    || list.get(end).getUpostag().equals("ADJ")
                    || list.get(end).getUpostag().equals("NUM")
                    || list.get(start).getUpostag().equals("ADV")) {
                if (!noun) {
                    noun = list.get(end).getUpostag().equals("NOUN") || list.get(end).getUpostag().equals("PROPN") || list.get(end).getUpostag().equals("NUM");
                }
                if (list.get(end).getUpostag().equals("ADP")) {
                    adp_c++;
                }
                if (list.get(end).getUpostag().equals("DET")) {
                    det_c++;
                }
            } else {
                break;
            }
            end++;
        }
        if (adp_c > 1) {
            return null;
        }
        if (end<list.size() && (list.get(end - 1).getUpostag().equals("ADP") || list.get(end - 1).getUpostag().equals("DET"))) {
            return null;
        }
        if (noun && start < end) {
            if (end <= min) {
                return getSpan(list, start, end);
            } else {
                return getSpan(list, start, min);
            }
        } else {
            return null;
        }
    }

    private Span checkObject(List<Token> list, int offset, int max) {
        if (offset >= list.size() || offset < 0) {
            return null;
        }
        int start = offset;
        boolean noun = false;
        int adp_c = 0;
        int det_c = 0;
        while (start >= 0) {
            if (list.get(start).getUpostag().equals("DET")
                    || list.get(start).getUpostag().equals("ADP")
                    || list.get(start).getUpostag().equals("NOUN")
                    || list.get(start).getUpostag().equals("PROPN")
                    || list.get(start).getUpostag().equals("ADJ")
                    || list.get(start).getUpostag().equals("NUM")
                    || list.get(start).getUpostag().equals("ADV")) {
                if (!noun) {
                    noun = list.get(start).getUpostag().equals("NOUN") || list.get(start).getUpostag().equals("PROPN") || list.get(start).getUpostag().equals("NUM");
                }
                if (list.get(start).getUpostag().equals("ADP")) {
                    adp_c++;
                }
                if (list.get(start).getUpostag().equals("DET")) {
                    det_c++;
                }
            } else {
                break;
            }
            start--;
        }
        start++;
        int end = offset + 1;
        while (end < list.size()) {
            if (list.get(end).getUpostag().equals("DET")
                    || list.get(end).getUpostag().equals("ADP")
                    || list.get(end).getUpostag().equals("NOUN")
                    || list.get(end).getUpostag().equals("PROPN")
                    || list.get(end).getUpostag().equals("ADJ")
                    || list.get(end).getUpostag().equals("NUM")
                    || list.get(start).getUpostag().equals("ADV")) {
                if (!noun) {
                    noun = list.get(end).getUpostag().equals("NOUN") || list.get(end).getUpostag().equals("PROPN") || list.get(end).getUpostag().equals("NUM");
                }
                if (list.get(end).getUpostag().equals("ADP")) {
                    adp_c++;
                }
                if (list.get(end).getUpostag().equals("DET")) {
                    det_c++;
                }
            } else {
                break;
            }
            end++;
        }
        if (adp_c > 1) {
            return null;
        }
        if (start<list.size() && list.get(start).getUpostag().equals("PRON")) {
            return null;
        }
        if (end<list.size() && (list.get(end - 1).getUpostag().equals("ADP") || list.get(end - 1).getUpostag().equals("DET"))) {
            return null;
        }
        if (noun && start < end) {
            if (start >= max) {
                return getSpan(list, start, end);
            } else {
                return getSpan(list, max, end);
            }
        } else {
            return null;
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

    private Span merge(List<Token> list, Span s1, Span s2) {
        if (s1 == null || s2 == null) {
            return null;
        }
        Set<Token> set = new HashSet<>();
        set.addAll(list.subList(s1.getStart(), s1.getEnd()));
        set.addAll(list.subList(s2.getStart(), s2.getEnd()));
        List<Token> m = new ArrayList<>(set);
        Collections.sort(m, new TokenIdComparator());
        return getSpan(m);
    }

    private Span checkValidSpan(List<Token> list, Span s) {
        if (s == null) {
            return null;
        }
        List<Token> sl = list.subList(s.getStart(), s.getEnd());
        for (int i = 0; i < sl.size() - 1; i++) {
            if ((sl.get(i + 1).getId() - sl.get(i).getId()) != 1) {
                return null;
            }
        }
        return s;
    }

    private int[] searchIdx(List<Token> tokens, Graph<Token, String> graph, int start, int end) {
        List<Token> pl = tokens.subList(start, end);
        int left = 0;
        int right = tokens.size();
        for (Token p : pl) {
            Set<String> rels = graph.incomingEdgesOf(p);
            for (String rel : rels) {
                Token source = graph.getEdgeSource(rel);
                int sourceIdx = source.getId() - 1;
                if (sourceIdx < start) {
                    if ((start - left) >= (start - sourceIdx)) {
                        left = sourceIdx;
                    }
                } else if (sourceIdx >= end) {
                    if ((right - end) >= (sourceIdx - end)) {
                        right = sourceIdx;
                    }
                }
            }
        }
        return new int[]{left, right};
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
                        int[] idx = searchIdx(list, g, i, i + 3);
                        Span subj = checkSubject(list, idx[0], i);
                        Span obj = checkObject(list, idx[1], i + 3);
                        if (subj != null && obj != null) {
                            ts.add(new Triple(subj, getSpan(list, i, i + 3), obj, subj.getScore() + 0.5f + obj.getScore()));
                            i = i + 2;
                        }
                    } else {
                        int[] idx = searchIdx(list, g, i, i + 2);
                        Span subj = checkSubject(list, idx[0], i);
                        Span obj = checkObject(list, idx[1], i + 2);
                        if (subj != null && obj != null) {
                            ts.add(new Triple(subj, getSpan(list, i, i + 2), obj, subj.getScore() + 0.5f + obj.getScore()));
                            i = i + 1;
                        }
                    }
                }
            } else if (t.getUpostag().equals("AUX") && t.getLemma().equals("essere")) {
                int[] idx = searchIdx(list, g, i, i + 1);
                Span subj = checkSubject(list, idx[0], i);
                Span obj = checkObject(list, idx[1], i + 1);
                if (subj != null && obj != null) {
                    ts.add(new Triple(subj, getSpan(list, i, i + 1), obj, subj.getScore() + 0.5f + obj.getScore()));
                }
            } else if (t.getUpostag().equals("VERB")) {
                if (i + 1 < list.size() && list.get(i + 1).getUpostag().equals("ADP")) {
                    int[] idx = searchIdx(list, g, i, i + 2);
                    Span subj = checkSubject(list, idx[0], i);
                    Span obj = checkObject(list, idx[1], i + 2);
                    if (subj != null && obj != null) {
                        ts.add(new Triple(subj, getSpan(list, i, i + 2), obj, subj.getScore() + 0.75f + obj.getScore()));
                        i = i + 1;
                    }
                } else {
                    int[] idx = searchIdx(list, g, i, i + 1);
                    Span subj = checkSubject(list, idx[0], i);
                    Span obj = checkObject(list, idx[1], i + 1);
                    if (subj != null && obj != null) {
                        ts.add(new Triple(subj, getSpan(list, i, i + 1), obj, subj.getScore() + 1 + obj.getScore()));
                    }
                }
            }
            i++;
        }
        return ts;
    }

}
