The algorithm IncPOD is to discover PODs on a given dataset and an incremental dataset.

The IncPOD.jar need five parameters and two optional parameters(The square brackets enclose optional parameters):  
original data D, incremental data △D, ODs discovered on D, tuple numbers in D, tuple numbers in △D, \[tuples in a single round\]

For example, the command  "java -jar incpod.jar SPS_origin.csv SPS_inc.csv SPS_90k.txt 90000 20000 5000"
 is to run incremental POD discovery on D+△D, where D is SPS_origin.csv, △D is SPS_inc.csv, 
ODs discovered on D is in SPS_90k.txt. In this run, we use 90000 tuples in D and 20000 tuples in △D. Moreover, the application of  △D to D is run in 4 rounds and 5000 tuples are used in each round.

The design here is to easily vary the size of D, to vary the size of △D, and to apply △D in several rounds. These settings are tested in several experimental studies.

A simplified and more commonly-used version "java -jar incpod.jar SPS_origin.csv SPS_inc.csv SPS_90k.txt 90000 20000" is to apply all tuples in △D in a single round; that is, to insert all of the 20000 tuples in △D into D together.

For ease of use, the process of index building is integrated into the program, which requires only D and ODs discovered on D (the first and the third parameter).  
We measure the time for index building and the time for incremental OD discovery respectively, in the output of the program. Index building is conducted only once ever if △D is handled in multiple rounds.
