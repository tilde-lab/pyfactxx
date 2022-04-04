
import sys

import coras
from coras.cli import load_and_parse


if len(sys.argv) < 3:
	print("Usage: rdf_to_lisp.py <rdf ontology filename> <lisp ontology filename>")
else:
	rdf_filename = sys.argv[1]
	lisp_filename = sys.argv[2]

	crs = coras.Coras()
	load_and_parse(crs, rdf_filename)
	#crs.realise()

	crs.reasoner.dump(lisp_filename)
