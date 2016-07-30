== Summary ==================================
NFA and CNF classes work individually, while the NONO class has yet to combine all CNF instances and output a single .cnf file.
Please test NFA and CNF individually to see how they work.

== Compilation ==============================
javac NFA.java
javac CNF.java
javac NONO.java

== Execution ================================
java NFA < 00or11.nfa
java CNF < n (where n is the row/column size)
java NONO < inclass10x10.txt