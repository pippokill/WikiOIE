/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.it.wikioie.process;

import di.uniba.it.wikioie.data.Triple;
import di.uniba.it.wikioie.data.Span;
import di.uniba.it.wikioie.data.Token;
import di.uniba.it.wikioie.data.TokenIdComparator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jgrapht.Graph;

/**
 *
 * @author pierpaolo
 */
public class WikiITSimpleExtractor implements WikiExtractor {

    /**
     *
     */
    public WikiITSimpleExtractor() {
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

    private Span checkSubject(List<Token> list, int offset) {
        int j = offset;
        boolean noun = false;
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
                    noun = list.get(j).getUpostag().equals("NOUN") || list.get(j).getUpostag().equals("PROPN");
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
        if (adp_c > 1) {
            return null;
        }
        if (noun && j < offset) {
            return getSpan(list, j + 1, offset + 1);
        } else {
            return null;
        }
    }

    private Span checkObject(List<Token> list, int offset) {
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
                    || list.get(j).getUpostag().equals("NUM")
                    || list.get(j).getUpostag().equals("PRON")
                    || list.get(j).getUpostag().equals("X")) {
                if (!noun) {
                    noun = list.get(j).getUpostag().equals("NOUN") || list.get(j).getUpostag().equals("PROPN");
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
        if (list.get(j - 1).getUpostag().equals("PRON")) {
            return null;
        }
        if (noun && j > offset) {
            return getSpan(list, offset, j);
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
                            ts.add(new Triple(subj, getSpan(list, i, i + 3), obj, subj.getScore() + 0.5f + obj.getScore()));
                            i = i + 2;
                        }
                    } else {
                        Span subj = checkSubject(list, i - 1);
                        Span obj = checkObject(list, i + 2);
                        if (subj != null && obj != null) {
                            ts.add(new Triple(subj, getSpan(list, i, i + 2), obj, subj.getScore() + 0.5f + obj.getScore()));
                            i = i + 1;
                        }
                    }
                }
            } else if (t.getUpostag().equals("AUX") && t.getLemma().equals("essere")) {
                Span subj = checkSubject(list, i - 1);
                Span obj = checkObject(list, i + 1);
                if (subj != null && obj != null) {
                    ts.add(new Triple(subj, getSpan(list, i, i + 1), obj, subj.getScore() + 0.5f + obj.getScore()));
                }
            } else if (t.getUpostag().equals("VERB")) {
                if (i + 1 < list.size() && list.get(i + 1).getUpostag().equals("ADP")) {
                    Span subj = checkSubject(list, i - 1);
                    Span obj = checkObject(list, i + 2);
                    if (subj != null && obj != null) {
                        ts.add(new Triple(subj, getSpan(list, i, i + 2), obj, subj.getScore() + 0.75f + obj.getScore()));
                        i = i + 1;
                    }
                } else {
                    Span subj = checkSubject(list, i - 1);
                    Span obj = checkObject(list, i + 1);
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
