/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.it.wikioie.indexing.service;

import di.uniba.it.wikioie.data.Config;
import di.uniba.it.wikioie.indexing.WikiOIEIndex;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pierpaolo
 */
public class IndexWrapper {

    private final WikiOIEIndex idx;

    private static IndexWrapper instance;

    private IndexWrapper() {
        idx = new WikiOIEIndex();
        try {
            idx.open(Config.getInstance().getValue("wrapper.idx"));
        } catch (IOException ex) {
            Logger.getLogger(IndexWrapper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @return
     */
    public static synchronized IndexWrapper getInstance() {
        if (instance == null) {
            instance = new IndexWrapper();
        }
        return instance;
    }

    /**
     *
     * @return
     */
    public WikiOIEIndex getIdx() {
        return idx;
    }
    
    

}
