/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.it.wikioie.udp;

import di.uniba.it.wikioie.data.Token;
import com.google.gson.Gson;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;

/**
 *
 * @author pierpaolo
 */
public class UDPParser {

    private final String address;

    private final String model;

    /**
     *
     * @param address
     * @param model
     */
    public UDPParser(String address, String model) {
        this.address = address;
        this.model = model;
    }

    private UDPResult call(String url, String text) throws IOException {
        url = url + "&data=" + URLEncoder.encode(text, StandardCharsets.UTF_8.toString());
        String json = IOUtils.toString(new URL(url), StandardCharsets.UTF_8.toString());
        Gson gson = new Gson();
        return gson.fromJson(json, UDPResult.class);
    }

    /**
     *
     * @param text
     * @return
     * @throws IOException
     */
    public UDPResult process(String text) throws IOException {
        return call(address + "?tokenizer&tagger&parser&model=" + model, text);
    }

    /**
     *
     * @param doc
     * @return
     * @throws IOException
     */
    public List<UDPSentence> getSentences(String doc) throws IOException {
        UDPResult result = process(doc);
        String[] lines = result.getResult().split("\n");
        List<UDPSentence> r = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        String id = "";
        String text = "";
        for (String line : lines) {
            if (line.startsWith("# sent_id")) {
                id = line.substring(12);
            } else if (line.startsWith("# text")) {
                text = line.substring(9);
            } else if (line.length() == 0) {
                if (sb.length() > 0) {
                    UDPSentence s = new UDPSentence(id, text, sb.toString());
                    r.add(s);
                    sb = new StringBuilder();
                    id = "";
                    text = "";
                }
            } else if (!line.startsWith("#")) {
                sb.append(line);
                sb.append("\n");
            }
        }
        if (sb.length() > 0) {
            UDPSentence s = new UDPSentence(id, text, sb.toString());
            r.add(s);
        }
        for (UDPSentence s : r) {
            s.setTokens(getTokens(s));
            s.setGraph(getGraph(s.getTokens()));
        }
        return r;
    }

    /**
     *
     * @param s
     * @return
     */
    public static List<Token> getTokens(UDPSentence s) {
        int offset = 0;
        List<Token> tokens = new ArrayList<>();
        String[] lines = s.getConll().split("\n");
        for (String line : lines) {
            if (!line.startsWith("#") && !line.isEmpty()) {
                String[] split = line.split("\t");
                if (split[0].contains("-")) {
                    continue;
                }
                Token token = new Token(Integer.parseInt(split[0]));
                int indexOf = s.getText().indexOf(split[1], offset);
                token.setStart(indexOf);
                token.setEnd(indexOf + split[1].length());
                offset += split[1].length();
                token.setForm(split[1]);
                token.setLemma(split[2]);
                token.setUpostag(split[3]);
                token.setXpostag(split[4]);
                token.setFeats(split[5]);
                token.setHead(Integer.parseInt(split[6]));
                token.setDepRel(split[7]);
                token.setDeps(split[8]);
                token.setMisc(split[9]);
                tokens.add(token);
            }
        }
        return tokens;
    }

    /**
     *
     * @param tokens
     * @return
     */
    public static Graph<Token, String> getGraph(List<Token> tokens) {
        Graph<Token, String> graph = new DefaultDirectedGraph(String.class);
        for (Token node : tokens) {
            graph.addVertex(node);
        }
        int id = 0;
        for (Token node : tokens) {
            int head = node.getHead();
            if (head != 0) {
                graph.addEdge(node, tokens.get(head - 1), node.getDepRel() + "-" + id);
                id++;
            }
        }
        return graph;
    }

    private List<List<Token>> parse(String result, String text) {
        List<List<Token>> r = new ArrayList<>();
        int offset = 0;
        List<Token> nodes = new ArrayList<>();
        String[] lines = result.split("\n");
        for (String line : lines) {
            if (!line.startsWith("#") && !line.isEmpty()) {
                String[] split = line.split("\t");
                if (split[0].contains("-")) {
                    continue;
                }
                Token node = new Token(Integer.parseInt(split[0]));
                int indexOf = text.indexOf(split[1], offset);
                node.setStart(indexOf);
                node.setEnd(indexOf + split[1].length());
                offset += split[1].length();
                node.setForm(split[1]);
                node.setLemma(split[2]);
                node.setUpostag(split[3]);
                node.setXpostag(split[4]);
                node.setFeats(split[5]);
                node.setHead(Integer.parseInt(split[6]));
                node.setDepRel(split[7]);
                node.setDeps(split[8]);
                node.setMisc(split[9]);
                nodes.add(node);
            } else if (line.isEmpty()) {
                r.add(nodes);
                nodes = new ArrayList<>();
                offset = 0;
            }
        }
        if (!nodes.isEmpty()) {
            r.add(nodes);
        }
        return r;
    }

    /**
     *
     * @param result
     * @param text
     * @return
     * @throws Exception
     */
    public List<Graph<Token, String>> getGraphs(String result, String text) throws Exception {
        List<Graph<Token, String>> r = new ArrayList<>();
        List<List<Token>> sentences = parse(result, text);
        for (List<Token> nodes : sentences) {
            Graph<Token, String> graph = new DefaultDirectedGraph(String.class);
            for (Token node : nodes) {
                graph.addVertex(node);
            }
            int id = 0;
            for (Token node : nodes) {
                int head = node.getHead();
                if (head != 0) {
                    graph.addEdge(node, nodes.get(head - 1), node.getDepRel() + "-" + id);
                    id++;
                }
            }
            r.add(graph);
        }
        return r;
    }

    /**
     *
     * @param text
     * @return
     * @throws Exception
     */
    public List<Graph<Token, String>> parse(String text) throws Exception {
        UDPResult result = call(address + "?tokenizer&tagger&parser&model=" + model, text);
        return getGraphs(result.getResult(), text);
    }

}
