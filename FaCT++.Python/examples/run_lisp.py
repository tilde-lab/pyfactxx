from factpp import Reasoner
from datetime import datetime
import random
from statistics import mean, pstdev
import sys

if len(sys.argv) < 2:
	print("Usage: run_lisp.py <lisp ontology filename>")
else:
	filename = sys.argv[1]
	
	start = datetime.now()
	reasoner = Reasoner()
	reasoner.parse_lisp(filename)

	reasoner.realise()
	
	time = (datetime.now() - start).total_seconds()
	
	print(time)
		
