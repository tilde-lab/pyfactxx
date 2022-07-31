
import sys
from datetime import datetime

from pyfactxx import coras
from pyfactxx.coras.cli import load_and_parse


if len(sys.argv) < 2:
	print('Usage: test_sparql.py <rdf ontology filename> <SPARQL query>')
else:
	filename = sys.argv[1]
	query = sys.argv[2]

	crs = coras.Coras()
	load_and_parse(crs, filename)
	crs.realise()

	print(*list(crs.query(query)), sep='\n')
