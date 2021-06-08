/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.it.wikioie;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pierpaolo
 */
public class Utils {

    private static final Logger LOG = Logger.getLogger(Utils.class.getName());

    /**
     *
     * @param is
     * @return
     * @throws IOException
     */
    public static String readText(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        while (reader.ready()) {
            sb.append(reader.readLine());
            sb.append("\n");
        }
        reader.close();
        return sb.toString();
    }

    /**
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static String readText(File file) throws IOException {
        return readText(new FileInputStream(file));
    }

    /**
     *
     * @param filename
     * @return
     * @throws IOException
     */
    public static String readText(String filename) throws IOException {
        return readText(new FileInputStream(filename));
    }

    /**
     *
     * @param file
     * @param minOcc
     * @return
     * @throws IOException
     */
    public static Set<String> loadFilterSet(File file, int minOcc) throws IOException {
        Set<String> set = new HashSet<>();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        while (reader.ready()) {
            String[] v = reader.readLine().split("\t");
            if (Integer.parseInt(v[1]) < minOcc) {
                set.add(v[0]);
            }
        }
        reader.close();
        LOG.log(Level.INFO, "Filtered {0} items from {1}", new Object[]{set.size(), file.getName()});
        return set;
    }

}
