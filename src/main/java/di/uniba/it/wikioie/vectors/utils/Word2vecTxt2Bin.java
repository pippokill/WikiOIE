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
package di.uniba.it.wikioie.vectors.utils;

import di.uniba.it.wikioie.vectors.VectorStoreUtils;
import di.uniba.it.wikioie.vectors.VectorType;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Convert word2vec text output in binary format
 *
 * @author pierpaolo
 */
public class Word2vecTxt2Bin {

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        Options options = new Options();
        options = options.addOption(new Option("i", true, "Input file"))
                .addOption(new Option("o", true, "Output file"));
        try {
            DefaultParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption("i") && cmd.hasOption("o")) {
                File file = new File(cmd.getOptionValue("i"));
                try {
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
                    DataOutputStream outStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(cmd.getOptionValue("o"))));
                    outStream.writeUTF(VectorStoreUtils.createHeader(VectorType.REAL, dim, -1));
                    while (reader.ready()) {
                        split = reader.readLine().split("\\s+");
                        outStream.writeUTF(split[0]);
                        for (int i = 1; i < split.length; i++) {
                            outStream.writeFloat(Float.parseFloat(split[i].replace(",", ".")));
                        }
                    }
                    reader.close();
                    outStream.close();
                } catch (IOException ex) {
                    Logger.getLogger(Word2vecTxt2Bin.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("WikiOIE - Run word2vec txt2bin converter", options);
            }
        } catch (ParseException ex) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("WikiOIE - Run word2vec txt2bin converter", options);
        }
    }

}
