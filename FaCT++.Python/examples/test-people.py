from pyfactxx import coras
from pyfactxx.coras.cli import load_and_parse
import rdflib

crs = coras.Coras()
load_and_parse(crs, 'people.rdf')
crs.realise()

def test(scope):
	cat_owners = crs.query('ask where {<http://cohse.semanticweb.org/ontologies/people#cat_owner> <http://www.w3.org/2000/01/rdf-schema#subClassOf> <http://cohse.semanticweb.org/ontologies/people#cat_liker> }', scope=scope)
	print("Cat owners like cats", list(cat_owners))

	old_ladies = crs.query('ask where {<http://cohse.semanticweb.org/ontologies/people#old_lady> <http://www.w3.org/2000/01/rdf-schema#subClassOf> <http://cohse.semanticweb.org/ontologies/people#cat_owner> }', scope=scope)
	print("Old ladies own cats", list(old_ladies))

	mad_cow = crs.query('ask where {<http://cohse.semanticweb.org/ontologies/people#mad_cow> <http://www.w3.org/2000/01/rdf-schema#subClassOf> <http://www.w3.org/2002/07/owl#Nothing> }', scope=scope)
	print("Mad cow does not exist", list(mad_cow))

	pete = crs.query('ask where {<http://cohse.semanticweb.org/ontologies/people#Pete> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://cohse.semanticweb.org/ontologies/people#person> }', scope=scope)
	print("Pete is a person", list(pete))

	spike = crs.query('ask where {<http://cohse.semanticweb.org/ontologies/people#Spike> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://cohse.semanticweb.org/ontologies/people#animal> }', scope=scope)
	print("Spike is an animal", list(spike))

print('\nWithout inferences:')
test('asserted')

print('\nWith inferences:')
test('inferred')
