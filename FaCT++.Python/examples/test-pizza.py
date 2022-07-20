from pyfactxx import coras
from pyfactxx.coras.cli import load_and_parse
import rdflib

def extract_name (node):
	node = str(node)
	return node[node.find('#') + 1:]

crs = coras.Coras()
load_and_parse(crs, 'pizza.owl')
crs.realise()


print('\nNamed pizzas:')
named_pizzas = crs.query('select ?p where {	?p <http://www.w3.org/2000/01/rdf-schema#subClassOf> <http://www.co-ode.org/ontologies/pizza/pizza.owl#NamedPizza> . } order by ?p', scope='asserted')

for pizza_row in named_pizzas:
	print (extract_name(pizza_row[0]), 'is', end=' ')
	
	pizza_types = crs.query('select	?c where {	' + pizza_row[0].n3() + ' <http://www.w3.org/2000/01/rdf-schema#subClassOf> ?c . ?c <http://www.w3.org/2000/01/rdf-schema#subClassOf> <http://www.co-ode.org/ontologies/pizza/pizza.owl#Pizza> . } order by ?c',	scope='inferred')

	pizza_types = ', '.join(extract_name(type_row[0]) for type_row in pizza_types if not isinstance(type_row[0], rdflib.BNode) and str(type_row[0]).endswith('Pizza') and not str(type_row[0]).endswith('NamedPizza'))
	
	if pizza_types == '':
		print('special')
	else:
		print(pizza_types)

print('\nUnsatisfiable (inconsistent) classes:')
nothing = crs.query('select ?n where {	?n <http://www.w3.org/2000/01/rdf-schema#subClassOf> <http://www.w3.org/2002/07/owl#Nothing> . } order by ?p', scope='both')
for row in nothing:
	print(extract_name(row[0]))

