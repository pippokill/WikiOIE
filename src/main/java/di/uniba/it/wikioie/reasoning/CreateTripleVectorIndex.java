/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package di.uniba.it.wikioie.reasoning;

import di.uniba.it.wikioie.Utils;
import static di.uniba.it.wikioie.reasoning.CosineSim.parse;
import di.uniba.it.wikioie.vectors.VectorReader;
import di.uniba.it.wikioie.vectors.lucene.LuceneVectorReader;
import di.uniba.it.wikioie.vectors.lucene.LuceneVectorStorage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.lucene.index.DirectoryReader;
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
public class CreateTripleVectorIndex {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Options options = new Options();
        options = options.addOption(new Option("i", true, "Triple index directory"))
                .addOption(new Option("o", true, "Vectors index directory"))
                .addOption(new Option("v", true, "Pre-trained vectors index"));
        try {
            DefaultParser cmdParser = new DefaultParser();
            CommandLine cmd = cmdParser.parse(options, args);
            if (cmd.hasOption("i") && cmd.hasOption("v") && cmd.hasOption("o")) {
                String vector_dir = cmd.getOptionValue("v");
                String triple_index_dir = cmd.getOptionValue("i");

                VectorReader vr = new LuceneVectorReader(new File(vector_dir));
                vr.init();

                LuceneVectorStorage storage = new LuceneVectorStorage();
                storage.open(new File(cmd.getOptionValue("o")));

                FSDirectory fsdir = FSDirectory.open(new File(triple_index_dir).toPath());
                IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(fsdir));
                int max_doc = searcher.getIndexReader().maxDoc();

                Query allDocs = new MatchAllDocsQuery();
                TopDocs topdocs = searcher.search(allDocs, max_doc);
                int i = 0;
                for (ScoreDoc sd : topdocs.scoreDocs) {
                    String subj = searcher.doc(sd.doc).get("subj");
                    storage.addVector(sd.doc + "_subj", Utils.getTextVector(parse(subj), vr));
                    String pred = searcher.doc(sd.doc).get("pred");
                    storage.addVector(sd.doc + "_pred", Utils.getTextVector(parse(pred), vr));
                    String obj = searcher.doc(sd.doc).get("obj");
                    storage.addVector(sd.doc + "_obj", Utils.getTextVector(parse(obj), vr));
                    i++;
                    if (i % 100 == 0) {
                        System.out.println(i + "...");
                    }
                }
                System.out.println("Closing...");
                fsdir.close();
                vr.close();
                storage.close();
            } else {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("WikiOIE - Build triples' vectors index", options);
            }
        } catch (IOException ex) {
            Logger.getLogger(CreateTripleVectorIndex.class.getName()).log(Level.SEVERE, null, ex);
        } catch (org.apache.commons.cli.ParseException ex) {
            Logger.getLogger(CreateTripleVectorIndex.class.getName()).log(Level.SEVERE, null, ex);
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("WikiOIE - Build triples' vectors index", options);
        }
    }

}
