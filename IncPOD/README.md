The algorithm IncPOD is to discover PODs on a given dataset plus an incremental dataset.

IncPOD.jar needs five parameters: original data D, incremental data △D, ODs discovered on D, tuple numbers in D, tuple numbers in △D.

For example, the command  "java -jar IncPOD.jar SPS_origin.csv SPS_inc.csv SPS_90k.txt 90000 20000" is to run incremental POD discovery on D + △D, where D is SPS_origin.csv, △D is SPS_inc.csv, ODs discovered on D is in SPS_90k.txt. In this run, we use 90000 tuples in D and 20000 tuples in △D. Before running IncPOD, please run hydra* or finder* to discover PODs on SPS_origin.csv and save results in SPS_90k.txt in advance.

The design here is to easily vary the size of D and the size of △D, which is required in some experimental studies.

In addition, IncPOD.jar can take one more optional parameter: tuples in a single round. This enables us to handle △D as a continuous sets of tuple insertions, which is tested in some experiments.  

For example, the command "java -jar IncPOD.jar SPS_origin.csv SPS_inc.csv SPS_90k.txt 90000 20000 size=5000" is to apply the 20000 tuples in △D in 4 rounds; in each round, 5000 tuples are inserted into D.

For ease of use, the process of index building is integrated into the IncPOD.jar, which requires only D and ODs discovered on D (the first and the third parameter).  
We measure the time for index building and the time for incremental OD discovery respectively, in the output of the program. Index building is conducted only once ever if △D is handled in multiple rounds.

Parameter "l" in paper for building equalilty index, you can change it by "l=XX",  
For example, the command "java -jar IncPOD.jar SPS_origin.csv SPS_inc.csv SPS_90k.txt 90000 20000 size=5000 l=0.65" is to change "l" to "0.65".
