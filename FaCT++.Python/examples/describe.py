
import sys

from pyfactxx import coras
from pyfactxx.coras.cli import load_and_parse
import rdflib


def describe(crs, node, nsm, visited, with_inferences=False, level=1):

	if node in visited:
		return

	a_po = list(crs._graph.predicate_objects(node))
	b_po = list(crs._query_graph.predicate_objects(node)) if with_inferences else []

	a_set = set(a_po)
	predicate_objects = list(a_po) + [po for po in b_po if po not in a_set]

	for row in predicate_objects:

		if level == 1 or row[0] not in (rdflib.URIRef('http://www.w3.org/2000/01/rdf-schema#subClassOf'), rdflib.URIRef('http://www.w3.org/2002/07/owl#equivalentClass')):
			print('\t'*level, row[0].n3(nsm), end='')

			if isinstance(row[1], rdflib.BNode):
				print(' ', row[1].n3(nsm))
				describe(crs, row[1], nsm, visited | {node}, with_inferences, level + 1)
			else:
				print(' ', row[1].n3(nsm))

if len(sys.argv) < 3:
	print("Usage: describe.py <ontology filename> <object URI> [<with_inferences? (0/1)>]")
else:

	filename = sys.argv[1]
	URI = sys.argv[2]

	if len(sys.argv) > 3:
		with_inferences = (sys.argv[3] == '1')
	else:
		with_inferences = False

	crs = coras.Coras()
	load_and_parse(crs, filename)

	if with_inferences:
		crs.realise()

	nsm = crs._graph.namespace_manager
	node = rdflib.URIRef(URI)
	#node = rdflib.URIRef('http://emmo.info/emmo#EMMO_d67ee67e_4fac_4676_82c9_aec361dba698') # Property
	#node = rdflib.URIRef('http://emmo.info/emmo#EMMO_5b2222df_4da6_442f_8244_96e9e45887d1') # Matter
	print(node.n3(nsm))
	describe(crs, node, nsm, set(), with_inferences)
