/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package di.uniba.it.wikioie.indexing;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 * @author pierpaolo
 */
public class TripleResultSet {

    private final List<SearchTriple> triples;

    public TripleResultSet(List<SearchTriple> triples) {
        this.triples = triples;
    }

    public List<SearchTriple> getTriples() {
        return triples;
    }

    public int getSize() {
        return this.triples.size();
    }

    public Map<String, List<SearchTriple>> collapse(Function<SearchTriple, String> function) {
        Map<String, List<SearchTriple>> map = triples.stream().collect(Collectors.groupingBy(function));
        map.values().stream().forEach(v -> Collections.sort(v, Collections.reverseOrder()));
        return map;
    }

}
