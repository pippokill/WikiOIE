/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.it.wikioie.cmd;

import di.uniba.it.wikioie.Utils;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pierpaolo
 */
public class RemoveDuplicate {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            Utils.removeDuplicate(new File("C:/Users/angel/Documents/OIE4PA/Dataset/U/u_triples_dd.tsv"),
                     new File("C:/Users/angel/Documents/OIE4PA/Extraction/process/triples.tsv"),
                     new File("C:/Users/angel/Documents/OIE4PA/Extraction/process/triples_dd.tsv"));
        } catch (IOException ex) {
            Logger.getLogger(RemoveDuplicate.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}