Assignment problem
There are currently 12 data files.

These data files are taken from J.E.Beasley
"Linear programming on Cray supercomputers" Journal 
of the Operational Research Society 41 (1990) 133-139.

Eight problems are from Table 2 of that paper.  

The following table gives the relationship between test 
problem set in Table 2 and the appropriate files:

Problem set        Files
100                assign100
200                assign200
300                assign300
400                assign400
500                assign500
600                assign600
700                assign700
800                assign800

The format of these data files is:
number of items to be assigned (n)
for each item i (i=1,...,n):
the cost of allocating item i to item j (c(i,j),j=1,...,n)

Four problems are from Table 3 of that paper.

The following table gives the relationship between test
problem set in Table 3 and the appropriate files:

Problem set        Files
800                assignp800
1500               assignp1500
3000               assignp3000
5000               assignp5000
 
The format of these data files is:
number of items to be assigned (n)
for each valid assignment:
i, j, c(i,j) the cost of allocating item i to item j

The value of the optimal solution for each of these data 
files is given in the file assignopt.

The largest file is assign800 of size 2000Kb (approximately).
The entire set of files is of size 12000Kb (approximately).