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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.DocValuesType;
import org.apache.lucene.index.IndexOptions;

/**
 * Utils method for vectors in Lucene index
 *
 * @author pierpaolo
 */
public class LuceneVectorUtils {

    /**
     *
     * @param vector
     * @return
     * @throws IOException
     */
    public static byte[] encodeVector(Vector vector) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream(vector.getDimension());
        DataOutputStream outstream = new DataOutputStream(byteStream);
        vector.writeToStream(outstream);
        outstream.flush();
        return byteStream.toByteArray();
    }

    /**
     *
     * @param bytes
     * @param dimension
     * @return
     * @throws IOException
     */
    public static Vector decodeVector(byte[] bytes, int dimension) throws IOException {
        ByteArrayInputStream byteStream = new ByteArrayInputStream(bytes);
        DataInputStream instream = new DataInputStream(byteStream);
        float[] v = new float[dimension];
        for (int i = 0; i < v.length; i++) {
            v[i] = Float.intBitsToFloat(instream.readInt());
        }
        byteStream.close();
        return new RealVector(v);
    }

    /**
     *
     * @param name
     * @param bytes
     * @return
     */
    public static Field getBinaryField(String name, byte[] bytes) {
        FieldType binType = new FieldType();
        binType.setDocValuesType(DocValuesType.BINARY);
        binType.setIndexOptions(IndexOptions.NONE);
        binType.setStoreTermVectorOffsets(false);
        binType.setStoreTermVectorPayloads(false);
        binType.setStoreTermVectorPositions(false);
        binType.setStoreTermVectors(false);
        binType.setStored(true);
        binType.setTokenized(false);
        return new Field(name, bytes, binType);
    }

}
