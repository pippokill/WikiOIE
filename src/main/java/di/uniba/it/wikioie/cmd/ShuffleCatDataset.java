/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.it.wikioie.cmd;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

/**
 *
 * @author pierpaolo
 */
public class ShuffleCatDataset {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length > 2) {
            try {
                int c = 0;
                FileReader reader = new FileReader(args[0]);
                CSVParser csvin = CSVFormat.TDF.withFirstRecordAsHeader().parse(reader);
                Iterator<CSVRecord> it = csvin.iterator();
                while (it.hasNext()) {
                    it.next();
                    c++;
                }
                csvin.close();
                reader.close();
                Random rnd = new Random();
                int size = Integer.parseInt(args[2]);
                Set<Integer> idx = new HashSet<>();
                for (int i = 0; i < size; i++) {
                    idx.add(rnd.nextInt(size));
                }

                reader = new FileReader(args[0]);
                csvin = CSVFormat.TDF.withFirstRecordAsHeader().parse(reader);
                FileWriter writer = new FileWriter(args[1]);
                CSVPrinter csvout = CSVFormat.TDF.withHeader(csvin.getHeaderNames().toArray(new String[csvin.getHeaderNames().size()])).print(writer);
                it = csvin.iterator();
                int i = 0;
                c = 0;
                while (it.hasNext()) {
                    it.next();
                    if (idx.contains(c)) {
                        Iterator<String> itv = it.next().iterator();
                        while (itv.hasNext()) {
                            csvout.print(itv.next());
                        }
                        csvout.println();
                        i++;
                    }
                    c++;
                }
                csvin.close();
                reader.close();
                csvout.close();
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(ShuffleCatDataset.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
