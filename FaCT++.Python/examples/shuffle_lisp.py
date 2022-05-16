
import sys
from datetime import datetime
import random
from statistics import mean, pstdev

from pyfactxx import Reasoner


if len(sys.argv) < 2:
	print('Usage: shuffle_lisp.py <lisp ontology filename> [<number of iterations>]')
else:
	filename = sys.argv[1]
	iterations = 20

	if len(sys.argv) > 2:
		iterations = int(sys.argv[2])

	times = []

	for i in range(iterations):
		with open(filename) as file:
			lines = file.readlines()

		random.shuffle(lines)

		with open('test.txt', 'w') as file:
			file.writelines(lines)

		start = datetime.now()
		reasoner = Reasoner()
		reasoner.parse_lisp('test.txt')

		reasoner.realise()

		time = (datetime.now() - start).total_seconds()

		print(time)

		times.append(time)

	print(f"min: {min(times)}, max: {max(times)}")
	print(f"average: {mean(times)}, std dev: {pstdev(times)}")
