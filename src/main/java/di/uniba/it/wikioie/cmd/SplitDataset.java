package di.uniba.it.wikioie.cmd;

import org.apache.commons.cli.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * SplitDataset class split a starting dataset in training and test set,
 * respecting given sampling and relevant triples percentage.
 *
 * @author angel
 */
public class SplitDataset {

    private final Set<String> relevant;
    private final Set<String> notRelevant;
    private double relevantPercentage = 0.0;
    private String outputPath = "";
    private int triplesCount = 0;
    private static final Logger LOG = Logger.getLogger(CreateDataset.class.getName());
    private static final String TR_LOG = "[TRAINING SET]";
    private static final String TS_LOG = "[TEST SET]";

    /**
     *
     * @param percentage
     * @param path
     */
    public SplitDataset(double percentage, String path) {
        relevant = new HashSet<>();
        notRelevant = new HashSet<>();
        relevantPercentage = percentage;
        outputPath = path;
    }

    /**
     * Splits evaluated triples in sets of relevant and non-relevant triples.
     * HashSet structure assures the absence of duplicate triples.
     *
     * @param triples .tsv file containing evaluated triples.
     * @throws IOException
     */
    private void splitTriples(File triples) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(triples));
        String r = "1";
        String nr = "0";
        String line;
        while (reader.ready()) {
            line = reader.readLine();
            if (line.endsWith(r)) {
                relevant.add(line);
            } else if (line.endsWith(nr)) {
                notRelevant.add(line);
            }
        }
        reader.close();
        triplesCount = relevant.size() + notRelevant.size();
        LOG.log(Level.INFO, "Starting with {0} triples", triplesCount);
        LOG.log(Level.INFO, "{0} relevant triples", relevant.size());
        LOG.log(Level.INFO, "{0} non-relevant triples", notRelevant.size());
        removeDuplicates(triples);
    }

    /**
     * Removes possible duplicate triples from the initial dataset.
     *
     * @param triples
     * @throws IOException
     */
    private void removeDuplicates(File triples) throws IOException {
        File triples_dd = new File(triples.getAbsolutePath() + "/l_triples_dd.tsv");
        FileWriter writer = new FileWriter(triples_dd);
        BufferedWriter buff = new BufferedWriter(writer);
        CSVPrinter csv = CSVFormat.TDF.withHeader("title", "text", "subject", "predicate", "object", "label").print(buff);
        for (String line : relevant) {
            buff.write(line);
            buff.newLine();
        }
        for (String line : notRelevant) {
            buff.write(line);
            buff.newLine();
        }
        csv.close();
        buff.close();
        writer.close();
    }

    /**
     * Creates a set with given sampling percentage. Every taken triple is
     * removed from its set.
     *
     * @param sampling
     * @throws IOException
     */
    private void createSet(double sampling, String log, String dirName, String fileName) throws IOException {
        double size = triplesCount * sampling; //size of entire set
        int rSize = (int) (size * relevantPercentage); //number of relevant triples in set
        int nrSize = (int) size - rSize; //number of non-relevant triples in set
        LOG.log(Level.INFO, "{0} {1} relevant triples, {2} non-relevant triples", new Object[]{log, rSize, nrSize});

        File dir = new File(outputPath + dirName);
        if (dir.mkdirs()) {
            File r = new File(dir.getAbsolutePath() + fileName);
            FileWriter writer = new FileWriter(r);
            BufferedWriter buff = new BufferedWriter(writer);
            CSVPrinter csv = CSVFormat.TDF.withHeader("title", "text", "subject", "predicate", "object", "label").print(buff);

            Iterator<String> rIter = relevant.iterator();
            for (int i = 0; i < rSize; i++) {
                if (rIter.hasNext()) {
                    String line = rIter.next();
                    buff.write(line);
                    buff.newLine();
                    rIter.remove();
                } else {
                    LOG.log(Level.WARNING, "{0} Relevant set is empty", log);
                }
            }

            Iterator<String> nrIter = notRelevant.iterator();
            for (int i = 0; i < nrSize; i++) {
                if (nrIter.hasNext()) {
                    String line = nrIter.next();
                    buff.write(line);
                    buff.newLine();
                    nrIter.remove();
                } else {
                    LOG.log(Level.WARNING, "{0} Non-relevant set is empty", log);
                }
            }
            csv.close();
            buff.close();
            writer.close();
            LOG.log(Level.INFO, "{0} relevant triples remaining", relevant.size());
            LOG.log(Level.INFO, "{0} non-relevant triples remaining", notRelevant.size());
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Options options = new Options();
        options = options.addOption(new Option("i", true, "Input file"))
                .addOption(new Option("o", true, "Output directory"))
                .addOption(new Option("a", true, "Training sampling"))
                .addOption(new Option("e", true, "Test sampling"))
                .addOption(new Option("r", true, "Relevant triples percentage"));
        try {
            DefaultParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption("i") && cmd.hasOption("o")) {
                LOG.log(Level.INFO, "Input file: {0}", cmd.getOptionValue("i"));
                LOG.log(Level.INFO, "Output dir: {0}", cmd.getOptionValue("o"));
                File inputFile = new File(cmd.getOptionValue("i"));
                String outputPath = cmd.getOptionValue("o");
                double trainingSampling = Double.parseDouble(cmd.getOptionValue("a"));
                double testSampling = Double.parseDouble(cmd.getOptionValue("e"));
                double relevantPercentage = Double.parseDouble(cmd.getOptionValue("r"));

                SplitDataset splitter = new SplitDataset(relevantPercentage, outputPath);
                LOG.log(Level.INFO, "Splitting dataset...");
                splitter.splitTriples(inputFile);
                LOG.log(Level.INFO, "Creating training set...");
                String dirName = "/training";
                String fileName = "/training_set.tsv";
                splitter.createSet(trainingSampling, TR_LOG, dirName, fileName);
                LOG.log(Level.INFO, "Creating test set...");
                dirName = "/test";
                fileName = "/test_set.tsv";
                splitter.createSet(testSampling, TS_LOG, dirName, fileName);
                LOG.log(Level.INFO, "Closing...");
            }
        } catch (ParseException | IOException e) {
            LOG.log(Level.WARNING, e.getMessage());
        }
    }

}
