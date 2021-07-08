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
            Utils.removeDuplicate(new File("resources/bootstrapping/simpledep/bootstrapping_20210706.csv"),
                     new File("resources/bootstrapping/simpledep/triple_simpledep_text_20_01.tsv"),
                     new File("resources/bootstrapping/simpledep/triple_simpledep_text_20_01_dd.tsv"));
        } catch (IOException ex) {
            Logger.getLogger(RemoveDuplicate.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
