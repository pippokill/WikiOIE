# WikiOIE
WikiOIE is a framework for extracting facts (triples) from the Wikipedia dump. WikiOIE relies on UDpipe universal dependency parser, simple rules, and heuristics for automatically extracting facts from the Wikipedia dump. Moreover, WikiOIE can use a supervised approach for classifing relevant and not-relevant triples. In case you have a small number of annotated triples, you can exploit a self-training strategy.

WikiOIE is described in the following paper. Please, cite it if you use our framework.

```
@inproceedings{cassottiIIR2021,
  title = {{Extracting Relations from Italian Wikipedia using Unsupervised Information Extraction}},
  author = {Cassotti, Pierluigi and Siciliani, Lucia and Basile, Pierpaolo and de Gemmis, Marco and Lops, Pasquale},
  editor = {Anelli, Vito Walter and Di Noia, Tommaso and Ferro, Nicola and Narducci, Fedelucio},
  booktitle = {Proceedings of the 11th Italian Information Retrieval Workshop 2021 (IIR 2021)},
  publisher = {CEUR-WS},
  year = {2021},
  note = {http://ceur-ws.org/Vol-2947/paper2.pdf}
}
```

While, the self-training stategy is described in the following paper:

```
@inproceedings{sicilianiCLICit2021,
  title = {{Extracting Relations from Italian Wikipedia using Self-Training}},
  author = {Siciliani, Lucia and Cassotti, Pierluigi and Basile, Pierpaolo and de Gemmis, Marco and Lops, Pasquale and Semeraro, Giovanni},
  booktitle = {Eighth Italian Conference on Computational Linguistics (CLiC-it 2021},
  publisher = {CEUR-WS},
  year = {2021}
}
```

**This is an alternative framework for extracting facts from Italian Public Administration announcements. The main difference is the *di.uniba.it.wikioie.preprocessing* package.**

## Workflow
-----------
### On Debian distributions

1. [Clone](https://github.com/ufal/udpipe) **UDpipe** repository
    ```
    git clone https://github.com/ufal/udpipe
    ```   
2. Compile the REST server by opening a command-line interface at *udpipe/src* path and by typing
    ```
    make server
    ```
    You need to install ```make``` and ```g++``` if not installed by default. Read [here](https://ufal.mff.cuni.cz/udpipe/1/install) for more information on how to compile the server
3. [Download](https://lindat.mff.cuni.cz/repository/xmlui/handle/11234/1-3131#) the Italian language model **italian-isdt-ud-2.5-191206**
4. Run the server by opening a command-line interface at *udpipe/src/rest_server* path and by typing
    ```
    ./udpipe_server port model_name model_name model_path model_desc
    ```
    - ```port``` is the port of your choice
    - ```model_path``` is the path where *italian-isdt-ud-2.5-191206.udpipe* is stored
    - for easy of use, we're using *it* for both ```model_name``` and ```model_desc```

    To test if you correctly started the server, type in your browser
    ```
    http://localhost:port/process
    ```
    You should get this message 
    ```
    Required argument 'data' is missing.
    ```
    More information about the server [here](https://ufal.mff.cuni.cz/udpipe/1/users-manual#udpipe_server)
5.  [Clone](https://github.com/Midorilly/WikiOIE) **WikiOIE** repository
    ```
    git clone https://github.com/Midorilly/WikiOIE
    ```
6.  Update WikiOIE **config.properties** file with the chosen port; for example:
    ```
    #udp.address=http://193.204.187.35:7777/process
    #udp.model=italian-isdt-ud-2.5-191206
    udp.address=http://localhost:yourport/process
    udp.model=it
    wrapper.idx=/media/pierpaolo/fastExt4/wikidump/wikioie/simpledep_idx
    server.address=http://localhost/
    server.port=yourport
    ```
7. Install **Java JDK** (suggested version 11.0.11) by typing in CLI
    ```
    sudo apt install openjdk-11-jre-headless
    ```
8. Install **Tesseract OCR** by typing in CLI
    ```
    sudo apt install tesseract-ocr
    ```
    Find more about Tesseract [here](https://cwiki.apache.org/confluence/display/tika/tikaocr)
9. Download the Tesseract package for Italian language typing in CLI
    ```
    sudo apt-get install tesseract-ocr-ita
    ```
10. Install **p7zip** by typing in CLI
    ```
    sudo apt install p7zip-full
    ```
   
### On Windows distributions
1. [Clone](https://github.com/ufal/udpipe) **UDpipe** repository
    ```
    git clone https://github.com/ufal/udpipe
    ```   
2. Compile the REST server by opening a Cygwin command-line interface at *udpipe/src* path and by typing
    ```
    make server
    ```
     You need to install ```make``` and ```g++``` if not installed by default. Read [here](https://ufal.mff.cuni.cz/udpipe/1/install) for more information on how to compile the server
3.  Run the server by opening a command-line interface at *udpipe/src/rest_server* path and by typing
    ```
    ./udpipe_server port model_name model_name model_path model_desc
    ```
    - ```port``` is the port of your choice
    - ```model_path``` is the path where *italian-isdt-ud-2.5-191206.udpipe* is stored
    - for easy of use, we're using *it* for both ```model_name``` and ```model_desc```

    To test if you correctly started the server, type in your browser
    ```
    http://localhost:port/process
    ```
    You should get this message 
    ```
    Required argument 'data' is missing.
    ```
    More information about the server [here](https://ufal.mff.cuni.cz/udpipe/1/users-manual#udpipe_server)
4.  [Clone](https://github.com/Midorilly/WikiOIE) **WikiOIE** repository
    ```
    git clone https://github.com/Midorilly/WikiOIE
    ```
5. [Download](https://www.oracle.com/java/technologies/downloads/) the appropriate **Java JDK** version for your OS
6. [Install](https://github.com/UB-Mannheim/tesseract/wiki) **Tesseract OCR** and add its path to your system variables. Read more about Tesseract [here](https://cwiki.apache.org/confluence/display/tika/tikaocr)
7. [Download](https://github.com/tesseract-ocr/tessdata) the Tesseract file for Italian language *ita.traineddata* and store it in *Tesseract-OCR/tessdata* folder 
8. [Install](https://www.7-zip.org/download.html) **7-Zip** for Windows and add its path to your system variables
9. [Install](https://wiki.openssl.org/index.php/Binaries) **OpenSSL** for Windows or, if Git is installed, you can already find ```openssl.exe``` at ```C:\Program Files\Git\usr\bin```. Add its path to your system variables

## Usage
-----------
1. Download your dump.
2. **Preprocess the dump**: run the *utils/clean_up* script; remember to specify the dump path. This script extracts possible .7z, .zip and .rar folders and converts .p7m files found in your dump. 
  
3. **Extract raw text**: run the main class *di.uniba.it.wikioie.preprocessing.Preprocess* using the following run configurations
    ```
    -i input directory
    -o output directory
    -t number of threads (optional, default 4)
    -r Tesseract enabled (optional, default disabled)
    ```
    The input directory is the directory where the dump is stored. The output directory is the directory where Preprocess will store its output (in text format). 
    It is recommended to increase the amount of RAM available to the JVM based on the number of threads you decide to run. Multiple instance of Tesseract can quickly cause an OOM error, leading to incomplete text extractions. The suggested value for 4 threads is around 2046MB of initial heap size and 4096MB of maximum heap size. This can be either done by using ```-Xms``` and ```-Xmx``` parameter when running the script in terminal or by modifying your IDE settings in this regard.

4. **Extract triples**: run the main class *di.uniba.it.wikioie.cmd.Pipeline* using the following run configurations
    ```
    -i input directory
    -o output directory
    -p processing class
    -t number of threads (optional, default 4)
    -d training file (optional)
    -s sampling (optional)
    -f use predicate occurrances file (optional)
    -m min predicate occurrances (used with option -f, optional, 5)
    -x print text
    ```
    The input directory is the directory where you stored the output of point 3. The output directory is the directory where WikiIOE will store triples (in JSON format). The processing class is the classname of the class that implements the extraction algorithm. Currently, we provide two extractors for the Italian language: 
    - *WikiITSimplePassageProcessor*: it uses only PoS-tag information. It is fast but less accurate;
    - *WikiITSimpleDepPassageProcessor*: it uses both PoS-tag and syntactic dependencies. It is slow but more accurate;
    - *WikiITSimpleDepSupervisedPassageProcessor*: it uses a supervised approach for classifing relevant and not-relevant triples. This method requires that both the parameters -t and -c must be provided;
    - Indexing
    
