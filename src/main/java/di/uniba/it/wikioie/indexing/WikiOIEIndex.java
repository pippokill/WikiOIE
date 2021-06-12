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

package di.uniba.it.wikioie.indexing;

import com.google.gson.Gson;
import di.uniba.it.wikioie.data.Span;
import di.uniba.it.wikioie.data.Passage;
import di.uniba.it.wikioie.data.Triple;
import di.uniba.it.wikioie.indexing.post.PassageProcessor;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FloatDocValuesField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

/**
 *
 * @author pierpaolo
 */
public class WikiOIEIndex {

    private static final Logger LOG = Logger.getLogger(WikiOIEIndex.class.getName());

    private IndexSearcher docS;

    private IndexSearcher tripleS;

    /**
     *
     * @param indexdirname
     * @throws IOException
     */
    public void open(String indexdirname) throws IOException {
        open(new File(indexdirname));
    }

    /**
     *
     * @param indexdir
     * @throws IOException
     */
    public void open(File indexdir) throws IOException {
        docS = new IndexSearcher(DirectoryReader.open(FSDirectory.open(new File(indexdir.getAbsolutePath() + "/doc_idx").toPath())));
        tripleS = new IndexSearcher(DirectoryReader.open(FSDirectory.open(new File(indexdir.getAbsolutePath() + "/triple_idx").toPath())));
    }

    /**
     *
     * @param inputdirname
     * @param outputdirname
     * @param filterPredicate
     * @param processor
     * @throws IOException
     */
    public void index(String inputdirname, String outputdirname, Set<String> filterPredicate, PassageProcessor processor) throws IOException {
        index(new File(inputdirname), new File(outputdirname), filterPredicate, processor);
    }

    /**
     *
     * @param inputdir
     * @param outputdir
     * @param filterPredicate
     * @param processor
     * @throws IOException
     */
    public void index(File inputdir, File outputdir, Set<String> filterPredicate, PassageProcessor processor) throws IOException {
        if (inputdir.isDirectory()) {
            IndexWriterConfig iwcd = new IndexWriterConfig(new StandardAnalyzer(CharArraySet.EMPTY_SET));
            iwcd.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            IndexWriter docWriter = new IndexWriter(FSDirectory.open(new File(outputdir.getAbsolutePath() + "/doc_idx").toPath()), iwcd);
            IndexWriterConfig iwct = new IndexWriterConfig(new StandardAnalyzer(CharArraySet.EMPTY_SET));
            iwct.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            IndexWriter tripleWriter = new IndexWriter(FSDirectory.open(new File(outputdir.getAbsolutePath() + "/triple_idx").toPath()), iwct);
            File[] listFiles = inputdir.listFiles();
            int tc = 0;
            int dc = 0;
            for (File file : listFiles) {
                LOG.log(Level.INFO, "Indexing file: {0}", file.getAbsolutePath());
                Gson gson = new Gson();
                BufferedReader reader;
                if (file.getName().endsWith(".gz")) {
                    reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(file))));
                } else {
                    reader = new BufferedReader(new FileReader(file));
                }
                while (reader.ready()) {
                    String line = reader.readLine();
                    Passage data = gson.fromJson(line, Passage.class);
                    if (processor != null) {
                        data = processor.process(data);
                    }
                    if (data.getTriples().length > 0) {
                        boolean added = false;
                        for (Triple t : data.getTriples()) {
                            if (!filterPredicate.contains(t.getPredicate().toString().toLowerCase())) {
                                Document triple = new Document();
                                triple.add(new StringField("doc_id", String.valueOf(dc), Field.Store.YES));
                                triple.add(new TextField("subj", t.getSubject().getSpan(), Field.Store.YES));
                                triple.add(new StoredField("subj_start", t.getSubject().getStart()));
                                triple.add(new StoredField("subj_end", t.getSubject().getEnd()));
                                triple.add(new StoredField("subj_score", t.getSubject().getScore()));
                                triple.add(new TextField("pred", t.getPredicate().getSpan(), Field.Store.YES));
                                triple.add(new StoredField("pred_start", t.getPredicate().getStart()));
                                triple.add(new StoredField("pred_end", t.getPredicate().getEnd()));
                                triple.add(new StoredField("pred_score", t.getPredicate().getScore()));
                                triple.add(new TextField("obj", t.getObject().getSpan(), Field.Store.YES));
                                triple.add(new StoredField("obj_start", t.getObject().getStart()));
                                triple.add(new StoredField("obj_end", t.getObject().getEnd()));
                                triple.add(new StoredField("obj_score", t.getObject().getScore()));
                                triple.add(new FloatDocValuesField("score", t.getScore()));
                                triple.add(new StoredField("score_value", t.getScore()));
                                tripleWriter.addDocument(triple);
                                tc++;
                                added = true;
                            }
                        }
                        if (added) {
                            Document doc = new Document();
                            doc.add(new StringField("id", String.valueOf(dc), Field.Store.YES));
                            doc.add(new StringField("wiki_id", data.getId(), Field.Store.YES));
                            doc.add(new TextField("title", data.getTitle(), Field.Store.YES));
                            doc.add(new StoredField("text", data.getText()));
                            docWriter.addDocument(doc);
                            dc++;
                        }
                    }
                }
                reader.close();
            }
            docWriter.close();
            tripleWriter.close();
            LOG.log(Level.INFO, "Indexed docs {0}, indexed triples {1}", new Object[]{dc, tc});
        }
    }

    public void process(String inputdirname, String outputdirname, PassageProcessor processor) throws IOException {
        process(new File(inputdirname), new File(outputdirname), processor);
    }

    public void process(File inputdir, File outputdir, PassageProcessor processor) throws IOException {
        if (inputdir.isDirectory()) {
            File[] listFiles = inputdir.listFiles();
            int pc = 0;
            for (File file : listFiles) {
                LOG.log(Level.INFO, "Processing file: {0}", file.getAbsolutePath());
                Gson gson = new Gson();
                BufferedReader reader;
                if (file.getName().endsWith(".gz")) {
                    reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(file))));
                } else {
                    reader = new BufferedReader(new FileReader(file));
                }
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(outputdir.getAbsolutePath() + "/" + file.getName().replace(".gz", "_proc.gz")))));
                while (reader.ready()) {
                    String line = reader.readLine();
                    Passage data = gson.fromJson(line, Passage.class);
                    if (processor != null) {
                        data = processor.process(data);
                    }
                    writer.append(gson.toJson(data, Passage.class));
                    writer.newLine();
                }
                reader.close();
                writer.close();
            }
            LOG.log(Level.INFO, "Processed passages {0}", new Object[]{pc});
        }
    }

    private SearchTriple doc2triple(Document doc) {
        Span subjSpan = new Span(doc.get("subj"),
                doc.getField("subj_start").numericValue().intValue(),
                doc.getField("subj_end").numericValue().intValue(),
                doc.getField("subj_score").numericValue().floatValue());
        Span predSpan = new Span(doc.get("pred"),
                doc.getField("pred_start").numericValue().intValue(),
                doc.getField("pred_end").numericValue().intValue(),
                doc.getField("pred_score").numericValue().floatValue());
        Span objSpan = new Span(doc.get("obj"),
                doc.getField("obj_start").numericValue().intValue(),
                doc.getField("obj_end").numericValue().intValue(),
                doc.getField("obj_score").numericValue().floatValue());
        return new SearchTriple(doc.get("doc_id"), subjSpan, predSpan, objSpan, doc.getField("score_value").numericValue().floatValue());
    }

    /**
     *
     * @param query
     * @param n
     * @return
     * @throws IOException
     * @throws ParseException
     */
    public List<SearchTriple> searchTriple(String query, int n) throws IOException, ParseException {
        MultiFieldQueryParser qp = new MultiFieldQueryParser(new String[]{"subj", "pred", "obj"}, new StandardAnalyzer(CharArraySet.EMPTY_SET));
        Query q = qp.parse(query);
        TopDocs topdocs = tripleS.search(q, n);
        List<SearchTriple> rs = new ArrayList<>();
        for (ScoreDoc sd : topdocs.scoreDocs) {
            SearchTriple st = doc2triple(tripleS.doc(sd.doc));
            st.setSearchScore(sd.score * st.getScore());
            st.setId(sd.doc);
            rs.add(st);
        }
        Collections.sort(rs, Collections.reverseOrder());
        return rs;
    }

    /**
     *
     * @param docid
     * @return
     * @throws IOException
     */
    public List<SearchTriple> getTriplesByDocid(String docid) throws IOException {
        Query q = new TermQuery(new Term("doc_id", docid));
        TopDocs topdocs = tripleS.search(q, 10000);
        List<SearchTriple> rs = new ArrayList<>();
        for (ScoreDoc sd : topdocs.scoreDocs) {
            SearchTriple st = doc2triple(tripleS.doc(sd.doc));
            st.setSearchScore(sd.score * st.getScore());
            st.setId(sd.doc);
            rs.add(st);
        }
        Collections.sort(rs, Collections.reverseOrder());
        return rs;
    }

    /**
     *
     * @param docid
     * @return
     * @throws IOException
     */
    public SearchDoc getDocById(String docid) throws IOException {
        Query q = new TermQuery(new Term("id", docid));
        TopDocs td = docS.search(q, 1);
        if (td.scoreDocs.length > 0) {
            Document ld = docS.doc(td.scoreDocs[0].doc);
            SearchDoc doc = new SearchDoc(ld.get("id"), ld.get("wiki_id"), ld.get("title"), ld.get("text"));
            return doc;
        } else {
            return null;
        }
    }

    /**
     *
     * @param wikiId
     * @return
     * @throws IOException
     */
    public List<SearchDoc> getDocByWikiId(String wikiId) throws IOException {
        Query q = new TermQuery(new Term("wiki_id", wikiId));
        TopDocs td = docS.search(q, 10000);
        List<SearchDoc> rs = new ArrayList<>();
        for (ScoreDoc sd : td.scoreDocs) {
            Document ld = docS.doc(sd.doc);
            SearchDoc doc = new SearchDoc(ld.get("id"), ld.get("wiki_id"), ld.get("title"), ld.get("text"));
            rs.add(doc);
        }
        return rs;
    }

    /**
     *
     * @param query
     * @param n
     * @return
     * @throws ParseException
     * @throws IOException
     */
    public List<SearchDoc> searchDocByTitle(String query, int n) throws ParseException, IOException {
        QueryParser qp = new QueryParser("title", new StandardAnalyzer(CharArraySet.EMPTY_SET));
        Query q = qp.parse(query);
        TopDocs topdocs = tripleS.search(q, n);
        List<SearchDoc> rs = new ArrayList<>();
        for (ScoreDoc sd : topdocs.scoreDocs) {
            Document ld = docS.doc(sd.doc);
            SearchDoc doc = new SearchDoc(ld.get("id"), ld.get("wiki_id"), ld.get("title"), ld.get("text"), sd.score);
            rs.add(doc);
        }
        return rs;
    }

}
