
from factpp import Reasoner
from datetime import datetime


def is_related_to(reasoner, name, property_name, property):
    i = reasoner.individual(name)
    values = reasoner.get_role_fillers(i, property)
    print(name, property_name, [v.name for v in values])


start = datetime.now()
reasoner = Reasoner()
reasoner.parse_lisp('t-fast.txt')
reasoner.realise()


is_related_to(reasoner, 'water', 'is drunk by', reasoner.inverse('drinks'))
print(f'Time elapsed: {datetime.now() - start}')
