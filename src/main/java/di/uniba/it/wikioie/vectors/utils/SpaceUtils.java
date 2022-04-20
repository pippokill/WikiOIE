/**
 * Copyright (c) 2014, the Temporal Random Indexing AUTHORS.
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
package di.uniba.it.wikioie.vectors.utils;

import di.uniba.it.wikioie.vectors.*;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Set;

/**
 *
 * @author pierpaolo
 */
public class SpaceUtils {

    /**
     *
     * @param spaces
     * @return
     */
    public static Map<String, Vector> combineSpaces(Map<String, Vector>... spaces) {
        Map<String, Vector> newSpace = new HashMap<>();
        for (Map<String, Vector> space : spaces) {
            Iterator<String> iterator = space.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                Vector v = space.get(key);
                Vector nw = newSpace.get(key);
                if (nw != null) {
                    nw.superpose(v, 1, null);
                } else {
                    newSpace.put(key, v);
                }
            }
        }
        Iterator<Vector> iterator = newSpace.values().iterator();
        while (iterator.hasNext()) {
            iterator.next().normalize();
        }
        return newSpace;
    }

    /**
     *
     * @param readers
     * @return
     * @throws IOException
     */
    public static Map<String, Vector> combineVectorReader(VectorReader... readers) throws IOException {
        Map<String, Vector> newSpace = new HashMap<>();
        for (VectorReader reader : readers) {
            Iterator<ObjectVector> allVectors = reader.getAllVectors();
            while (allVectors.hasNext()) {
                ObjectVector ov = allVectors.next();
                Vector nw = newSpace.get(ov.getKey());
                if (nw != null) {
                    nw.superpose(ov.getVector(), 1, null);
                } else {
                    newSpace.put(ov.getKey(), ov.getVector());
                }
            }
        }
        Iterator<Vector> iterator = newSpace.values().iterator();
        while (iterator.hasNext()) {
            iterator.next().normalize();
        }
        return newSpace;
    }

    /**
     *
     * @param readers
     * @return
     * @throws IOException
     */
    public static VectorReader combineAndBuildVectorReader(VectorReader... readers) throws IOException {
        Map<String, Vector> newSpace = new HashMap<>();
        System.out.println();
        for (VectorReader reader : readers) {
            System.out.print(".");
            Iterator<ObjectVector> allVectors = reader.getAllVectors();
            while (allVectors.hasNext()) {
                ObjectVector ov = allVectors.next();
                Vector nw = newSpace.get(ov.getKey());
                if (nw != null) {
                    nw.superpose(ov.getVector(), 1, null);
                } else {
                    newSpace.put(ov.getKey(), ov.getVector());
                }
            }
        }
        System.out.println();
        Iterator<Vector> iterator = newSpace.values().iterator();
        while (iterator.hasNext()) {
            iterator.next().normalize();
        }
        return new MapVectorReader(newSpace);
    }

    /**
     *
     * @param outputFile
     * @param readers
     * @throws IOException
     */
    public static void combineAndSaveVectorReader(File outputFile, VectorReader... readers) throws IOException {
        Map<String, Vector> newSpace = new HashMap<>();
        System.out.println();
        for (VectorReader reader : readers) {
            System.out.print(".");
            Iterator<ObjectVector> allVectors = reader.getAllVectors();
            while (allVectors.hasNext()) {
                ObjectVector ov = allVectors.next();
                Vector nw = newSpace.get(ov.getKey());
                if (nw != null) {
                    nw.superpose(ov.getVector(), 1, null);
                } else {
                    newSpace.put(ov.getKey(), ov.getVector());
                }
            }
        }
        System.out.println();
        int dimension = 0;
        Iterator<Vector> iterator = newSpace.values().iterator();
        while (iterator.hasNext()) {
            if (dimension == 0) {
                Vector v = iterator.next();
                dimension = v.getDimension();
                v.normalize();
            } else {
                iterator.next().normalize();
            }
        }
        DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(outputFile)));
        String header = VectorStoreUtils.createHeader(VectorType.REAL, dimension, -1);
        outputStream.writeUTF(header);
        for (Entry<String, Vector> entry : newSpace.entrySet()) {
            outputStream.writeUTF(entry.getKey());
            entry.getValue().writeToStream(outputStream);
        }
        outputStream.close();
        newSpace.clear();
        newSpace = null;
    }

    /**
     *
     * @param store
     * @param word
     * @param n
     * @return
     * @throws IOException
     */
    public static List<ObjectVector> getNearestVectors(VectorReader store, String word, int n) throws IOException {
        Vector vector = store.getVector(word);
        if (vector != null) {
            return getNearestVectors(store, vector, n);
        } else {
            return new ArrayList<>();
        }
    }

    /**
     *
     * @param store
     * @param c
     * @param n
     * @return
     * @throws IOException
     */
    public static List<ObjectVector> getNearestVectorsByCoordinate(VectorReader store, int c, int n) throws IOException {
        if (c > store.getDimension()) {
            throw new IOException("Not valid coordinates");
        } else {
            PriorityQueue<ObjectVector> queue = new PriorityQueue<>();
            Iterator<ObjectVector> allVectors = store.getAllVectors();
            while (allVectors.hasNext()) {
                ObjectVector ov = allVectors.next();
                double overlap = ((RealVector) ov.getVector()).getCoordinates()[c];
                ov.setScore(overlap);
                if (queue.size() <= n) {
                    queue.offer(ov);
                } else {
                    queue.poll();
                    queue.offer(ov);
                }
            }
            queue.poll();
            List<ObjectVector> list = new ArrayList<>(queue);
            Collections.sort(list, new ReverseObjectVectorComparator());
            return list;
        }
    }

    /**
     *
     * @param store
     * @param vector
     * @param n
     * @return
     * @throws IOException
     */
    public static List<ObjectVector> getNearestVectors(VectorReader store, Vector vector, int n) throws IOException {
        PriorityQueue<ObjectVector> queue = new PriorityQueue<>();
        Iterator<ObjectVector> allVectors = store.getAllVectors();
        while (allVectors.hasNext()) {
            ObjectVector ov = allVectors.next();
            if (!ov.getVector().isZeroVector()) {
                double overlap = ov.getVector().measureOverlap(vector);
                ov.setScore(overlap);
                if (queue.size() <= n) {
                    queue.offer(ov);
                } else {
                    queue.poll();
                    queue.offer(ov);
                }
            }
        }
        queue.poll();
        List<ObjectVector> list = new ArrayList<>(queue);
        Collections.sort(list, new ReverseObjectVectorComparator());
        return list;
    }

    /**
     *
     * @param store1
     * @param store2
     * @param n
     * @return
     * @throws IOException
     */
    public static List<ObjectVector> sims(VectorReader store1, VectorReader store2, int n) throws IOException {
        PriorityQueue<ObjectVector> queue = new PriorityQueue<>();
        Iterator<ObjectVector> allVectors = store1.getAllVectors();
        while (allVectors.hasNext()) {
            ObjectVector ov = allVectors.next();
            Vector vector = store2.getVector(ov.getKey());
            if (vector != null) {
                double overlap = 1 - ov.getVector().measureOverlap(vector);
                ov.setScore(overlap);
                if (queue.size() <= n) {
                    queue.offer(ov);
                } else {
                    queue.poll();
                    queue.offer(ov);
                }
            }
        }
        queue.poll();
        List<ObjectVector> list = new ArrayList<>(queue);
        Collections.sort(list, new ReverseObjectVectorComparator());
        return list;
    }

    /**
     *
     * @param reader
     * @return
     * @throws IOException
     */
    public static int countVectors(VectorReader reader) throws IOException {
        Iterator<ObjectVector> allVectors = reader.getAllVectors();
        int counter = 0;
        while (allVectors.hasNext()) {
            allVectors.next();
            counter++;
        }
        return counter;

    }

    /**
     *
     * @param reader
     * @param v1
     * @param v2
     * @param v3
     * @param n
     * @return
     * @throws IOException
     */
    public static List<ObjectVector> analogy(VectorReader reader, Vector v1, Vector v2, Vector v3, int n) throws IOException {
        Vector m1 = v1.copy();
        m1.superpose(v3, -1, null);
        PriorityQueue<ObjectVector> queue = new PriorityQueue<>();
        Iterator<ObjectVector> allVectors = reader.getAllVectors();
        while (allVectors.hasNext()) {
            ObjectVector ov = allVectors.next();
            Vector m2 = v2.copy();
            m2.superpose(ov.getVector(), -1, null);
            ov.setScore(m1.measureOverlap(m2));
            if (queue.size() <= n) {
                queue.offer(ov);
            } else {
                queue.poll();
                queue.offer(ov);
            }
        }
        queue.poll();
        List<ObjectVector> list = new ArrayList<>(queue);
        Collections.sort(list, new ReverseObjectVectorComparator());
        return list;
    }

    /**
     *
     * @param reader
     * @param v1
     * @param v2
     * @param v3
     * @param n
     * @return
     * @throws IOException
     */
    public static List<ObjectVector> analogy2(VectorReader reader, Vector v1, Vector v2, Vector v3, int n) throws IOException {
        List<Vector> l1 = new ArrayList<>();
        l1.add(v3.copy());
        l1.add(v1.copy());
        VectorUtils.orthogonalizeVectors(l1);
        PriorityQueue<ObjectVector> queue = new PriorityQueue<>();
        Iterator<ObjectVector> allVectors = reader.getAllVectors();
        while (allVectors.hasNext()) {
            ObjectVector ov = allVectors.next();
            List<Vector> l2 = new ArrayList<>();
            l2.add(ov.getVector().copy());
            l2.add(v2.copy());
            VectorUtils.orthogonalizeVectors(l2);
            ov.setScore(l1.get(l1.size() - 1).measureOverlap(l2.get(l2.size() - 1)));
            if (queue.size() <= n) {
                queue.offer(ov);
            } else {
                queue.poll();
                queue.offer(ov);
            }
        }
        queue.poll();
        List<ObjectVector> list = new ArrayList<>(queue);
        Collections.sort(list, new ReverseObjectVectorComparator());
        return list;
    }

    /**
     *
     * @param vr
     * @param keys
     * @return
     * @throws IOException
     */
    public static Vector superposeVectors(VectorReader vr, String... keys) throws IOException {
        List<Vector> l = new ArrayList<>(keys.length);
        for (String k : keys) {
            Vector v = vr.getVector(k);
            if (v != null) {
                l.add(v);
            }
        }
        return superposeVectors(vr, l.toArray(Vector[]::new));
    }

    /**
     *
     * @param vr
     * @param vectors
     * @return
     */
    public static Vector superposeVectors(VectorReader vr, Vector... vectors) {
        if (vectors.length == 0) {
            return VectorFactory.createZeroVector(VectorType.REAL, vr.getDimension());
        } else {
            Vector s = VectorFactory.createZeroVector(VectorType.REAL, vr.getDimension());
            for (Vector v : vectors) {
                s.superpose(v, 1, null);
            }
            Vector r = VectorFactory.createZeroVector(VectorType.REAL, vr.getDimension());
            r.superpose(s, 1 / (double) vectors.length, null);
            return r;
        }
    }

    /**
     *
     * @param vr
     * @param dict
     * @return
     * @throws IOException
     */
    public static Map<String, Vector> cacheVectors(VectorReader vr, Set<String> dict) throws IOException {
        Map<String, Vector> map = new HashMap<>();
        for (String k : dict) {
            Vector vector = vr.getVector(k);
            if (vector != null) {
                map.put(k, vector);
            }
        }
        return map;
    }

}
