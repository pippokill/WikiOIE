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

import di.uniba.it.wikioie.vectors.RealVector;
import di.uniba.it.wikioie.vectors.Vector;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;

/**
 *
 * @author pierpaolo
 */
public class LuceneVectorStorage {

    private IndexWriter writer;

    /**
     *
     * @param indexdir
     * @throws IOException
     */
    public void open(File indexdir) throws IOException {
        IndexWriterConfig config = new IndexWriterConfig(new KeywordAnalyzer());
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        writer = new IndexWriter(FSDirectory.open(indexdir.toPath()), config);
    }

    /**
     *
     * @throws IOException
     */
    public void close() throws IOException {
        if (writer != null) {
            writer.close();
        }
    }

    /**
     *
     * @param key
     * @param vector
     * @throws IOException
     */
    public void addVector(String key, Vector vector) throws IOException {
        Document doc = new Document();
        doc.add(new StringField("key", key, Field.Store.YES));
        byte[] encodeVector = LuceneVectorUtils.encodeVector(vector);
        doc.add(LuceneVectorUtils.getBinaryField("vector", encodeVector));
        writer.addDocument(doc);
    }

    /**
     *
     * @param dimension
     * @throws IOException
     */
    public void storeDimension(int dimension) throws IOException {
        Document doc = new Document();
        doc.add(new StringField("header", "TRUE", Field.Store.YES));
        doc.add(new StoredField("dim", dimension));
        writer.addDocument(doc);
    }

    /**
     *
     * @param file
     * @throws IOException
     */
    public void storeFile(File file) throws IOException {
        BufferedReader reader = null;
        if (file.getName().endsWith(".gz")) {
            reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(file))));
        } else {
            reader = new BufferedReader(new FileReader(file));
        }
        String[] split;
        int dim = 0;
        if (reader.ready()) {
            split = reader.readLine().split("\\s+");
            dim = Integer.parseInt(split[1]);
        }
        storeDimension(dim);
        System.out.println("Store vectors into index...");
        int c = 0;
        while (reader.ready()) {
            split = reader.readLine().split("\\s+");
            float[] v = new float[split.length - 1];
            for (int i = 1; i < split.length; i++) {
                v[i - 1] = Float.parseFloat(split[i].replace(",", "."));
            }
            addVector(split[0], new RealVector(v));
            c++;
            if (c % 1000 == 0) {
                System.out.print(".");
                if (c % 100000 == 0) {
                    System.out.println(c);
                }
            }
        }
        reader.close();
    }

}
