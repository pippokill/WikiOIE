/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.it.wikioie.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author pierpaolo
 */
public class CountTriples {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        BufferedReader reader=new BufferedReader(new FileReader("/media/pierpaolo/fastExt4/wikidump/wikioie/simpledep_count/subj.count"));
        long c=0;
        while (reader.ready()) {
            String[] v=reader.readLine().split("\t");
            c+=Long.parseLong(v[1]);
        }
        reader.close();
        System.out.println(c);
    }
    
}
