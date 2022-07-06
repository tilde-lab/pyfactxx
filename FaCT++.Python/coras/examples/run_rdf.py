
import sys
from datetime import datetime
from statistics import mean, pstdev

from pyfactxx import coras
from pyfactxx.coras.cli import load_and_parse


if len(sys.argv) < 2:
	print('Usage: run_rdf.py <rdf ontology filename> [<number of iterations>]')
else:
	filename = sys.argv[1]
	iterations = 5

	if len(sys.argv) > 2:
		iterations = int(sys.argv[2])

	times = []

	for i in range(iterations):

		start = datetime.now()

		crs = coras.Coras()
		load_and_parse(crs, filename)
		crs.realise()

		time = (datetime.now() - start).total_seconds()

		print(time)

		times.append(time)

	print(f"min: {min(times)}, max: {max(times)}")
	print(f"average: {mean(times)}, std dev: {pstdev(times)}")

