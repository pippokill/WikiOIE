package di.uniba.it.wikioie.preprocessing;

/**
 * Preprocess class processes files in order to extract text.
 *
 * @author angelica
 */
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.ocr.TesseractOCRConfig;
import org.apache.tika.parser.ocr.TesseractOCRParser;
import org.apache.tika.parser.pdf.PDFParserConfig;
import org.xml.sax.SAXException;

public class Preprocess {

    private static final BlockingQueue<PreFile> in = new ArrayBlockingQueue(10000);
    private static List<PreprocessThread> list;
    private int id = 0;
    private static int inputDocCount = 0;
    private static int outputDocCount;
    private static final Logger LOG = Logger.getLogger(Preprocess.class.getName());

    public Preprocess() {
    }

    /**
     * Creates a PreFile object for each file and adds it to the queue.
     *
     * @param file
     * @throws IOException
     * @throws TikaException
     * @throws SAXException
     */
    private void preprocess(File file) throws IOException, TikaException, SAXException {
        if (file.isDirectory()) {
            File[] listFiles = file.listFiles();
            for (File f : listFiles) {
                preprocess(f);
            }
        } else {
            if (file.isFile()) {
                inputDocCount++;
                if (file.length() != 0) {
                    PreFile prefile = new PreFile(file, id);
                    in.add(prefile);
                    id++;
                } else {
                    LOG.log(Level.INFO, "{0} is empty", file.getName());
                }
            }
        }
    }

    /**
     * Creates nt PreprocessThread object(s) and adds it to a tlist
     *
     * @param nt number of threads
     * @param outputPath
     * @param autoParser
     * @param metadata
     * @param context
     * @return tlist of PreprocessThread object(s)
     */
    private static List<PreprocessThread> initializeThread(int nt, String outputPath, AutoDetectParser autoParser, Metadata metadata, ParseContext context) {
        List<PreprocessThread> tlist = new ArrayList<>();
        for (int i = 0; i < nt; i++) {
            tlist.add(new PreprocessThread(in, outputPath, autoParser, metadata, context));
        }
        return tlist;
    }

    /**
     * Starts thread(s)
     */
    private void startThread() {
        for (Thread t : list) {
            t.start();
        }
    }

    /**
     * Closes thread(s)
     *
     * @throws InterruptedException
     */
    private void closeThread() throws InterruptedException {
        poison();
        for (Thread t : list) {
            t.join();
        }
        LOG.info("Closing...");
    }

    /**
     * Creates a poison PreFile for each thread. A poisoned object is added to
     * the queue at last, to stop the thread taking it.
     */
    private void poison() {
        PreFile poison = new PreFile();
        for (Thread t : list) {
            in.add(poison);
        }
    }

    private void stats() {
        for (PreprocessThread t : list) {
            outputDocCount = outputDocCount + t.getDocCount();
        }
        LOG.log(Level.INFO, "Input file count: {0}", inputDocCount);
        LOG.log(Level.INFO, "Processed file count: {0}", outputDocCount);
    }

    public static void main(String[] args) throws IOException, TikaException, SAXException {
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "%4$s: %5$s [%1$tc]%n");
        Options options = new Options();
        options = options.addOption(new Option("i", true, "Input directory"))
                .addOption(new Option("o", true, "Output directory"))
                .addOption(new Option("t", true, "Number of threads (optional, default 4)"))
                .addOption(new Option("r", false, "Tesseract OCR (optional, default disabled)"));
        try {
            DefaultParser cmdParser = new DefaultParser();
            CommandLine cmd = cmdParser.parse(options, args);
            if (cmd.hasOption("i") && cmd.hasOption("o")) {
                File inputPath = new File(cmd.getOptionValue("i"));
                String outputPath = cmd.getOptionValue("o");
                int nt = Integer.parseInt(cmd.getOptionValue("t", "4"));
                LOG.log(Level.INFO, "Input dir: {0}", cmd.getOptionValue("i"));
                LOG.log(Level.INFO, "Output dir: {0}", cmd.getOptionValue("o"));
                LOG.log(Level.INFO, "Threads: {0}", nt);

                //Initializing parser
                AutoDetectParser autoParser = new AutoDetectParser();
                Metadata metadata = new Metadata();
                PDFParserConfig pdfConfig = new PDFParserConfig();
                pdfConfig.setExtractInlineImages(true);
                TesseractOCRConfig ocrConfig = new TesseractOCRConfig();
                if (cmd.hasOption("r")) {
                    TesseractOCRParser ocrParser = new TesseractOCRParser();
                    if (ocrParser.hasTesseract()) {
                        ocrConfig.setLanguage("ita");
                        LOG.log(Level.INFO, "OCR enabled");
                    } else {
                        ocrConfig.setSkipOcr(true);
                        LOG.log(Level.WARNING, "Tesseract is not installed, can't enable OCR");
                    }
                } else {
                    ocrConfig = new TesseractOCRConfig();
                    ocrConfig.setSkipOcr(true);
                    LOG.log(Level.INFO, "OCR disabled");
                }
                ParseContext context = new ParseContext();
                context.set(PDFParserConfig.class, pdfConfig);
                context.set(AutoDetectParser.class, autoParser);
                context.set(TesseractOCRConfig.class, ocrConfig);

                list = initializeThread(nt, outputPath, autoParser, metadata, context);
                Preprocess pre = new Preprocess();
                pre.startThread();
                LOG.info("Starting preprocessing...");
                pre.preprocess(inputPath);
                pre.closeThread();
                pre.stats();
            }
        } catch (ParseException e) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("Preprocess", options);
        } catch (InterruptedException e) {
            LOG.log(Level.SEVERE, "An error occurred: ", e);
        }

    }

}
