#
# factpp - Python interface to FaCT++ reasoner
#
# Copyright (C) 2016-2018 by Artur Wroblewski <wrobell@riseup.net>
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.
#

"""
Zebra puzzle as described at

    https://en.wikipedia.org/wiki/Zebra_Puzzle

The example is based on the ontology described at

    http://folk.uio.no/martige/what/20120422/zebra.html

"""

import time
import factpp

CLASSES = ['Color', 'Drink', 'House', 'Person', 'Pet', 'Smoke']

COLORS = ['blue', 'yellow', 'ivory', 'green', 'red']
DRINKS = ['orange juice', 'water', 'tea', 'milk', 'coffee']
PETS = ['horse', 'zebra', 'fox', 'dog', 'snails']
SMOKES = ['kools', 'chesterfield', 'lucky strike', 'old gold', 'parliament']
HOUSES = ['house5', 'house4', 'house2', 'house3', 'house1']
PERSONS = ['norwegian', 'spaniard', 'ukrainian', 'japanese', 'englishman']

def create_cls(cls_name, individuals):
    individuals = {i: reasoner.individual(i) for i in individuals}
    reasoner.different_individuals(*individuals.values())

    cls = reasoner.concept(cls_name)
    an_individual = reasoner.one_of(*individuals.values())
    reasoner.equal_concepts(cls, an_individual)

    return cls

def create_property(name, domain, range):
    r = reasoner.object_role(name)
    reasoner.set_o_functional(r)
    reasoner.set_inverse_functional(r)
    reasoner.set_o_domain(r, domain)
    reasoner.set_o_range(r, range)
    return r

def is_same(s1, s2):
    i1 = reasoner.individual(s1)
    i2 = reasoner.individual(s2)
    same = reasoner.is_same_individuals(i1, i2)
    print('{} is {}: {}'.format(s1, s2, 'yes' if same else 'no'))

def is_related_to(name, property_name, property):
    i = reasoner.individual(name)
    values = reasoner.get_role_fillers(i, property)
    print(name, property_name, [v.name for v in values])

reasoner = factpp.Reasoner()

get_i = reasoner.individual

classes = [reasoner.concept(c) for c in CLASSES]
reasoner.disjoint_concepts(*classes)

color = create_cls('Color', COLORS)
drink = create_cls('Drink', DRINKS)
pet = create_cls('Pet', PETS)
smoke = create_cls('Smoke', SMOKES)
house = create_cls('House', HOUSES)
person = create_cls('Person', PERSONS)

drinks = create_property('drinks', person, drink)
has_color = create_property('has_color', house, color)
has_pet = create_property('has_pet', person, pet)
lives_in = create_property('lives_in', person, house)
smokes = create_property('smokes', person, smoke)

is_next_to = reasoner.object_role('is_next_to')
reasoner.set_symmetric(is_next_to)
reasoner.set_irreflexive(is_next_to)
reasoner.set_o_domain(is_next_to, house)
reasoner.set_o_range(is_next_to, house)

is_left_to = reasoner.object_role('is_left_to')
reasoner.implies_o_roles(is_left_to, is_next_to)

is_right_to = reasoner.inverse(is_left_to)
reasoner.implies_o_roles(is_right_to, is_next_to)
reasoner.set_o_functional(is_right_to)
reasoner.set_inverse_functional(is_right_to)

a_house_color = reasoner.o_exists(has_color, color)
reasoner.implies_concepts(house, a_house_color)
a_house_next = reasoner.max_o_cardinality(2, is_next_to, house)
reasoner.implies_concepts(house, a_house_next)

a_person = reasoner.intersection(
    reasoner.o_exists(drinks, drink),
    reasoner.o_exists(has_pet, pet),
    reasoner.o_exists(lives_in, house),
    reasoner.o_exists(smokes, smoke),
)
reasoner.implies_concepts(person, a_person)

# 1. There are five houses.
reasoner.related_to(get_i('house1'), is_left_to, get_i('house2'))
reasoner.related_to(get_i('house2'), is_left_to, get_i('house3'))
reasoner.related_to(get_i('house3'), is_left_to, get_i('house4'))
reasoner.related_to(get_i('house4'), is_left_to, get_i('house5'))
reasoner.related_to_not(get_i('house1'), is_next_to, get_i('house5'))

# 2. The Englishman lives in the red house.
h = get_i('h1')
reasoner.related_to(h, has_color, get_i('red'))
reasoner.related_to(get_i('englishman'), lives_in, h)

# 3. The Spaniard owns the dog.
reasoner.related_to(get_i('spaniard'), has_pet, get_i('dog'))

# 4. Coffee is drunk in the green house.
x = get_i('x1')
h = get_i('h2')
reasoner.related_to(x, drinks, get_i('coffee'))
reasoner.related_to(h, has_color, get_i('green'))
reasoner.related_to(x, lives_in, h)

# 5. The Ukrainian drinks tea.
reasoner.related_to(get_i('ukrainian'), drinks, get_i('tea'))

# 6. The green house is immediately to the right of the ivory house.
h1 = get_i('h3')
h2 = get_i('h4')
reasoner.related_to(h1, has_color, get_i('green'))
reasoner.related_to(h2, has_color, get_i('ivory'))
reasoner.related_to(h1, is_right_to, h2)

# 7. The Old Gold smoker owns snails.
x = get_i('x2')
reasoner.related_to(x, smokes, get_i('old gold'))
reasoner.related_to(x, has_pet, get_i('snails'))

# 8. Kools are smoked in the yellow house.
x = get_i('x3')
h = get_i('h5')
reasoner.related_to(x, smokes, get_i('kools'))
reasoner.related_to(h, has_color, get_i('yellow'))
reasoner.related_to(x, lives_in, h)

# 9. Milk is drunk in the middle house.
x = get_i('x4')
reasoner.related_to(x, drinks, get_i('milk'))
reasoner.related_to(x, lives_in, get_i('house3'))

# 10. The Norwegian lives in the first house.
reasoner.related_to(get_i('norwegian'), lives_in, get_i('house1'))

# 11. The man who smokes Chesterfields lives in the house next to the man with the fox.
x1 = get_i('x5')
x2 = get_i('x6')
h1 = get_i('h6')
h2 = get_i('h7')
reasoner.related_to(x1, smokes, get_i('chesterfield'))
reasoner.related_to(x1, lives_in, h1)
reasoner.related_to(x2, has_pet, get_i('fox'))
reasoner.related_to(x2, lives_in, h2)
reasoner.related_to(h1, is_next_to, h2)

# 12. Kools are smoked in a house next to the house where the horse is kept.
x1 = get_i('x7')
x2 = get_i('x8')
h1 = get_i('h8')
h2 = get_i('h9')
reasoner.related_to(x1, smokes, get_i('kools'))
reasoner.related_to(x1, lives_in, h1)
reasoner.related_to(x2, has_pet, get_i('horse'))
reasoner.related_to(x2, lives_in, h2)
reasoner.related_to(h1, is_next_to, h2)

# 13. The Lucky Strike smoker drinks orange juice.
x = get_i('x9')
reasoner.related_to(x, smokes, get_i('lucky strike'))
reasoner.related_to(x, drinks, get_i('orange juice'))

# 14. The Japanese smokes Parliaments.
reasoner.related_to(get_i('japanese'), smokes, get_i('parliament'))

# 15. The Norwegian lives next to the blue house.
h1 = get_i('h10')
h2 = get_i('h11')
reasoner.related_to(get_i('norwegian'), lives_in, h1)
reasoner.related_to(h2, has_color, get_i('blue'))
reasoner.related_to(h1, is_next_to, h2)

assert reasoner.is_consistent()

start = time.monotonic()
reasoner.realise()
duration  = time.monotonic() - start

print('debug:')

# 8, 12 and 15: norwegian smokes kools and lives in house1
is_same('x3', 'x7')
is_same('house1', 'h10')

# ... h11 is next to house1, so h11 is house2
is_same('house2', 'h11')

is_related_to('yellow', 'color of', reasoner.inverse(has_color))
is_related_to('blue', 'color of', reasoner.inverse(has_color))
is_related_to('red', 'color of', reasoner.inverse(has_color))
is_related_to('ivory', 'color of', reasoner.inverse(has_color))
is_related_to('green', 'color of', reasoner.inverse(has_color))

print('\nsolution:')
# norwegian expected
is_related_to('water', 'is drunk by', reasoner.inverse(drinks))
# japanese expected
is_related_to('zebra', 'is owned by', reasoner.inverse(has_pet))

print('\nsolved in {:.2f}s'.format(duration))

# vim: sw=4:et:ai
