# Alg
This directory provides the required source code and datasets for incremental POD discovery.

## paper.pdf
the full paper with two additional experimental evaluations in the appendix.

## Hydra* and Finder*
Algorithms for batch POD discovery. We implement Hydra* (resp. Finder*) by adapting hydra (resp. DCFinder): [metanome-algorithms](https://github.com/HPI-Information-Systems/metanome-algorithms).
Hydra (resp. DCFinder) has been implemented by student(s) of the information systems group at the Hasso-Plattner-Institut (HPI) in the context of the Metanome project. 
The original algorithms are modified to consider only predicates allowed in PODs so as to traverse the serach space of PODs. The algorithms are provided to conduct batch POD discovery, and to compare against our incremental approach. We are really grateful for the open source implementaion of Hydra and DCFinder. The adaptions of them are for academic purpose only.

# IncPOD
The incremental POD discovery algorithm, developed in this submission.

## Dataset
Example datasets.

## To run incremental OD discovery:
(1) run Hydra* or Finder* to discovery ODs on a given data set D;

(2) run IncPOD to incrementally discovery ODs on D and an incremental data set △D:

A simple example is given as follows (all files are assumed to be in the current directory):

(1) java -jar Hydra+.jar SPS_origin.csv SPS_90k.txt 90000

(2) java -jar IncPOD.jar SPS_origin.csv SPS_inc.csv SPS_90k.txt 90000 20000

Note that (1) is to conduct POD discovery on SPS_origin.csv, and to save the discovered PODs in SPS_90k.txt. (2) is to conduct incremental POD discovery on SPS_origin.csv + SPS_inc.csv, leveraging known PODs in SPS_90k.txt. Numbers such as 90000, 20000 are used to set the number of tuples in SPS_origin.csv and SPS_inc.csv, respectively.

For more detailed parameters of different algorithms, please refer to the readme file in separate directories.

Please feel free to conduct Ai Ran(aran17@fudan.edu.cn) for problems in running the algorithms.
