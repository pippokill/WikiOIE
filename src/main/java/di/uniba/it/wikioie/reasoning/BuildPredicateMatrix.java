/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package di.uniba.it.wikioie.reasoning;

import di.uniba.it.wikioie.vectors.VectorReader;
import di.uniba.it.wikioie.vectors.lucene.LuceneVectorReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

/**
 *
 * @author pierpaolo
 */
public class BuildPredicateMatrix {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Options options = new Options();
        options = options.addOption(new Option("i", true, "Index directory"))
                .addOption(new Option("v", true, "Vectors directory"))
                .addOption(new Option("o", true, "Output file"))
                .addOption(new Option("c", true, "Cosine threshold (optional, default 0.8)"))
                .addOption(new Option("t", true, "Result set size (optional, default 100)"));
        try {
            /*String vector_dir = "/home/pierpaolo/data/fasttext/cc.it.300.vec.index";
            String triple_index_dir = "/home/pierpaolo/data/siap/oie/OIE_paper/extraction/LR_index/triple_idx";
            double cosine_threshold = 0.8;
            int top = 100;*/
            DefaultParser cmdParser = new DefaultParser();
            CommandLine cmd = cmdParser.parse(options, args);
            if (cmd.hasOption("i") && cmd.hasOption("v") && cmd.hasOption("o")) {
                String vector_dir = cmd.getOptionValue("v");
                String triple_index_dir = cmd.getOptionValue("i");
                double cosine_threshold = Double.parseDouble(cmd.getOptionValue("c", "0.8"));
                int top = Integer.parseInt(cmd.getOptionValue("t", "100"));
                VectorReader vr = new LuceneVectorReader(new File(vector_dir));
                vr.init();

                BufferedWriter writer = new BufferedWriter(new FileWriter(cmd.getOptionValue("o")));

                FSDirectory fsdir = FSDirectory.open(new File(triple_index_dir).toPath());
                IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(fsdir));
                int max_doc = searcher.getIndexReader().maxDoc();

                Query allDocs = new MatchAllDocsQuery();
                TopDocs topdocs = searcher.search(allDocs, max_doc);
                for (ScoreDoc sd : topdocs.scoreDocs) {
                    System.out.println(sd.doc);
                    writer.append(String.valueOf(sd.doc));
                    String pred = searcher.doc(sd.doc).get("pred");

                    QueryParser parser = new QueryParser("pred", new StandardAnalyzer(CharArraySet.EMPTY_SET));
                    Query qp = parser.parse(pred);
                    TopDocs topSimDocs = searcher.search(qp, top);
                    for (ScoreDoc sdSim : topSimDocs.scoreDocs) {
                        if (sdSim.doc != sd.doc) {
                            String predSim = searcher.doc(sdSim.doc).get("pred");
                            double sim = CosineSim.sim(pred, predSim, vr);
                            if (sim >= cosine_threshold) {
                                writer.append("\t").append(String.valueOf(sdSim.doc)).append("\t").append(String.valueOf(sim));
                            }
                        }
                    }
                    writer.newLine();
                    writer.flush();
                }
                writer.close();
            } else {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("WikiOIE - Build predicates' similarity matrix", options);
            }
        } catch (IOException ex) {
            Logger.getLogger(BuildPredicateMatrix.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(BuildPredicateMatrix.class.getName()).log(Level.SEVERE, null, ex);
        } catch (org.apache.commons.cli.ParseException ex) {
            Logger.getLogger(BuildPredicateMatrix.class.getName()).log(Level.SEVERE, null, ex);
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("WikiOIE - Build predicates' similarity matrix", options);
        }
    }

}
