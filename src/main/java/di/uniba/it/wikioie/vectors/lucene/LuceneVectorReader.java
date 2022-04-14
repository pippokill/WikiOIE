/**
 * Copyright (c) 2020, the UNIBA-MLIA-Task2 AUTHORS.
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
package di.uniba.it.wikioie.vectors.lucene;

import di.uniba.it.wikioie.vectors.ObjectVector;
import di.uniba.it.wikioie.vectors.Vector;
import di.uniba.it.wikioie.vectors.VectorReader;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

/**
 *
 * @author pierpaolo
 */
public class LuceneVectorReader implements VectorReader {

    private final File indexfile;

    private IndexSearcher searcher;

    private int dimension = -1;

    /**
     *
     * @param indexfile
     */
    public LuceneVectorReader(File indexfile) {
        this.indexfile = indexfile;
    }

    /**
     *
     * @throws IOException
     */
    @Override
    public void init() throws IOException {
        DirectoryReader r = DirectoryReader.open(FSDirectory.open(indexfile.toPath()));
        searcher = new IndexSearcher(r);
        TermQuery tq = new TermQuery(new Term("header", "TRUE"));
        TopDocs topDocs = searcher.search(tq, 1);
        if (topDocs.scoreDocs.length > 0) {
            dimension = searcher.doc(topDocs.scoreDocs[0].doc).getField("dim").numericValue().intValue();
        } else {
            throw new IOException("No vector dimension");
        }
    }

    /**
     *
     * @throws IOException
     */
    @Override
    public void close() throws IOException {
        searcher = null;
    }

    /**
     *
     * @param key
     * @return
     * @throws IOException
     */
    @Override
    public Vector getVector(String key) throws IOException {
        TermQuery tq = new TermQuery(new Term("key", key));
        TopDocs topDocs = searcher.search(tq, 1);
        if (topDocs.scoreDocs.length > 0) {
            return LuceneVectorUtils.decodeVector(searcher.doc(topDocs.scoreDocs[0].doc).getField("vector").binaryValue().bytes, dimension);
        } else {
            return null;
        }
    }

    /**
     *
     * @return @throws IOException
     */
    @Override
    public Iterator<String> getKeys() throws IOException {
        return new LuceneKeyIterable(searcher);
    }

    /**
     *
     * @return @throws IOException
     */
    @Override
    public Iterator<ObjectVector> getAllVectors() throws IOException {
        return new LuceneVectorIterable(searcher);
    }

    /**
     *
     * @return
     */
    @Override
    public int getDimension() {
        return this.dimension;
    }

    class LuceneVectorIterable implements Iterator<ObjectVector> {

        private final IndexSearcher searcher;

        private int docid = 0;

        public LuceneVectorIterable(IndexSearcher searcher) {
            this.searcher = searcher;
        }

        @Override
        public boolean hasNext() {
            return docid < searcher.getIndexReader().maxDoc();
        }

        @Override
        public ObjectVector next() {
            try {
                Document doc = searcher.doc(docid);
                if (doc.get("header") != null) {
                    docid++;
                    doc = searcher.doc(docid);
                }
                ObjectVector ov = new ObjectVector(doc.get("key"),
                        LuceneVectorUtils.decodeVector(doc.getField("vector").binaryValue().bytes, dimension));
                docid++;
                return ov;
            } catch (IOException ex) {
                Logger.getLogger(LuceneVectorReader.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        }

    }

    class LuceneKeyIterable implements Iterator<String> {

        private final IndexSearcher searcher;

        private int docid = 0;

        public LuceneKeyIterable(IndexSearcher searcher) {
            this.searcher = searcher;
        }

        @Override
        public boolean hasNext() {
            return docid < searcher.getIndexReader().maxDoc();
        }

        @Override
        public String next() {
            try {
                Document doc = searcher.doc(docid);
                docid++;
                return doc.get("key");
            } catch (IOException ex) {
                Logger.getLogger(LuceneVectorReader.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        }

    }

}
