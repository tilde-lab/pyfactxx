
import sys
from datetime import datetime
import random
from statistics import mean, pstdev

from factpp import Reasoner


if len(sys.argv) < 2:
	print("Usage: run_lisp.py <lisp ontology filename>")
else:
	filename = sys.argv[1]

	start = datetime.now()
	reasoner = Reasoner()
	reasoner.parse_lisp(filename)

	reasoner.realise()

	time = (datetime.now() - start).total_seconds()

	print("%2.2f sec" % time)
