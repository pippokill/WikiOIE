WikiOIE - Wikipedia Open Information Extractor
=================================================

WikiOIE is a framework for extracting facts (triples) from the Wikipedia dump.
WikiOIE relies on UDpipe universal dependency parser, simple rules, and heuristics for automatically extracting facts from the Wikipedia dump.
Moreover, WikiOIE can use a supervised approach for classifing relevant and not-relevant triples.
In case you have a small number of annotated triples, you can exploit a self-training strategy.

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

Workflow
-----------

1. Download a Wikipedia articles dump
2. Process the dump by using WikiExtractor: https://github.com/attardi/wikiextractor
3. Process the WikiExtractor output by using UDpipe **di.uniba.it.wikioie.cmd.ProcessUDpipe**
4. Process the output of point 3 by using an extractor and **di.uniba.it.wikioie.cmd.Process**
5. (optional) You can index the output of point 4 by using Lucene and **di.uniba.it.wikioie.cmd.Indexing**
6. (optional) You can extract the TSV dataset containing triples by using ****di.uniba.it.wikioie.cmd.CreateDataset**

For training/self-training, you must specify the training dataset during step 4.
The self-training procedure can be performed using the class **di.uniba.it.wikioie.training.CoTraining**.
See the main method for more details.

Usage
--------

1. Download a Wikipedia articles dump
----------------------------------------

See https://dumps.wikimedia.org/. You must donwload XML dump with articles.

2. Process the dump with WikiExtractor
-----------------------------------------

See https://github.com/attardi/wikiextractor.

3. UDpipe
------------

You need to setup the *config.properties* file with information about the UDpipe server, see https://ufal.mff.cuni.cz/udpipe/1#online.

Then, run the main class **di.uniba.it.wikioie.cmd.ProcessUDpipe**.

usage: WikiOIE - Process Wikipedia using UDpipe<br>
 -i <arg>   Input directory<br>
 -o <arg>   Output directory<br>
 -t <arg>   Number of threads (optional, default 4)<br>

The input directory is the directory where WikiExtractor stores its output.
The output directory is the directory where WikiOIE will store its output (in JSON format).

4. Extract triples
---------------------

Run the main class **di.uniba.it.wikioie.cmd.Process**.

usage: WikiOIE - Run processing<br>
 -i <arg>   Input directory<br>
 -o <arg>   Output directory<br>
 -p <arg>   Processing class<br>
 -t <arg>   Training file (optional)<br>
 -c <arg>   C value (optional, default=1)<br>

 The input directory is the directory where you stored the output of UDpipe.
 The output directory is the directory where WikiIOE will store triples (in JSON format).
 The processing class is the classname of the class that implements the extraction algorithm. Currently, we provided two extractors for the Italian language:
 1. *WikiITSimplePassageProcessor*: it uses only PoS-tag information. It is fast but less accurate.
 2. *WikiITSimpleDepPassageProcessor*: it uses both PoS-tag and syntactic dependencies. It is slow but more accurate.
 3. *WikiITSimpleDepSupervisedPassageProcessor*: it uses a supervised approach for classifing relevant and not-relevant triples. This method requires that both the parameters -t and -c must be provided. 

5. Indexing
--------------

(TO DO)

6. TSV dataset
-----------------

(TO DO)
