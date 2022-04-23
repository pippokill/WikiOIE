package di.uniba.it.wikioie.preprocessing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

/**
 * PreprocessThread class shapes a single thread.
 */
public class PreprocessThread extends Thread {

    private final BlockingQueue<PreFile> in;
    private boolean run = true;
    private final String outputPath;
    private int threadDocCount = 0;
    private final AutoDetectParser autoParser;
    private final Metadata metadata;
    private final ParseContext context;
    private static final Logger LOG = Logger.getLogger(PreprocessThread.class.getName());

    /**
     *
     * @param in
     * @param outputPath
     * @param autoParser
     * @param metadata
     * @param context
     */
    public PreprocessThread(BlockingQueue<PreFile> in, String outputPath, AutoDetectParser autoParser, Metadata metadata, ParseContext context) {
        this.in = in;
        this.outputPath = outputPath;
        this.autoParser = autoParser;
        this.metadata = metadata;
        this.context = context;
    }

    /**
     * Takes a PreFile object from the BlockingQueue and checks if it is a
     * poison object. If not, parses the wrapped file, otherwise, sets run to
     * false, closing the thread.
     */
    @Override
    public void run() {
        while (run) {
            try {
                PreFile prefile = in.take();
                if (!prefile.isPoison()) {
                    File file = prefile.getFile();
                    int id = prefile.getId();
                    String title = file.getAbsolutePath();
                    String text = parse(file);
                    if (!text.isEmpty()) {
                        String folderName = file.getParentFile().getName();
                        File outputDir = new File(outputPath + "/" + folderName);
                        outputDir.mkdirs();
                        writePlainText(id, title, text, outputDir.getAbsolutePath());
                    } else {
                        LOG.log(Level.WARNING, "Unable to correctly parse {0}", title);
                    }
                } else {
                    setRun(false);
                }
            } catch (InterruptedException | IOException | SAXException e) {
                LOG.log(Level.SEVERE, "An error occurred: ", e);
            }
        }
    }

    /**
     * Parses and extracts text from the file wrapped in PreFile object.
     *
     * @param file
     * @return text to be written.
     * @throws IOException
     * @throws SAXException
     * @throws TikaException
     */
    private String parse(File file) throws IOException, SAXException {
        String filePath = file.getAbsolutePath();
        FileInputStream stream = new FileInputStream(file);
        BodyContentHandler handler = new BodyContentHandler(Integer.MAX_VALUE);
        LOG.log(Level.INFO, "[NAME: {0}] Processing {1}", new Object[]{Thread.currentThread().getName(), filePath});
        try {
            autoParser.parse(stream, handler, metadata, context);
        } catch (OutOfMemoryError | TikaException e) {
            LOG.log(Level.WARNING, "[FILE: {0}] caused error: {1}", new Object[]{filePath, e.getStackTrace().toString()});
        }
        String text = handler.toString();
        stream.close();
        return text;
    }

    /**
     * Writes the extracted text in a new file
     *
     * @param id of the file
     * @param title of the file
     * @param text
     * @param outputPath where the new file is stored
     * @throws IOException
     */
    private void writePlainText(int id, String title, String text, String outputPath) throws IOException {
        try {
            FileWriter writer = new FileWriter(new File(outputPath, "plain_" + id));
            writer.write("<doc id=\"" + id + "\" url=\"?curid=" + id + "\" title=\"" + title + "\" >");
            writer.write(text);
            writer.write("</doc>");
            writer.close();
            threadDocCount++;
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "An error occurred ", e);
        }
    }

    private void setRun(boolean set) {
        run = set;
    }

    int getDocCount() {
        return threadDocCount;
    }
}
