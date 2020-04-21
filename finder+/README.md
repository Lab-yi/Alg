The Finder+.jar is to discover PODs on a given dataset.

The Finder+.jar need three parameters: the path to dataset; the path you want to save result; data size.

A simple example is given as follows (all files are assumed to be in the current directory):
java -jar Finder+.jar SPS_origin.csv SPS_90k.txt 90000

Please note that algorithm finder* is implemented by adapting dcfinder, so you may increase heap memory for reduce running time or fix Java Heap space error.

A simple example is given as follow:
java -jar -Xms512M -Xmx2g Finder+.jar SPS_origin.csv SPS_90k 90000 
