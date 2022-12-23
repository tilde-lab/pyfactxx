# Python bindings for FaCT++ reasoner

[![PyPI](https://img.shields.io/pypi/v/pyfactxx.svg?style=flat)](https://pypi.org/project/pyfactxx)
[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Ftilde-lab%2Fpyfactxx.svg?type=shield)](https://app.fossa.com/projects/git%2Bgithub.com%2Ftilde-lab%2Fpyfactxx?ref=badge_shield)

FaCT++ is a well-optimized [open-source](https://bitbucket.org/dtsarkov/factplusplus) reasoner for **_SROIQ(D)_** description logic with simple datatypes (OWL 2), written in C++. FaCT++ was created in 2003-2015 by [Dmitry Tsarkov](https://scholar.google.com/citations?user=jDcQ7vQAAAAJ) and [Ian Horrocks](https://scholar.google.com/citations?user=0ypdmcYAAAAJ) in the University of Manchester, UK.

The `pyfactxx` links the FaCT++ reasoner to the Python's [RDFLib](https://rdflib.dev) package. The code is based on the works of Artur Wroblewski: [factpp](https://bitbucket.org/wrobell/factplusplus/src/factpp/factpp) and [coras](https://bitbucket.org/wrobell/coras) interfaces.


## Reasoner details

The FaCT++ implements the [atomic decomposition algorithms](http://ceur-ws.org/Vol-1080/owled2013_13.pdf) (_i.e._ represents the ontologies as terse directed acyclic graphs). A [tableaux decision procedure](http://www.cs.ox.ac.uk/ian.horrocks/Publications/download/2007/HoSa07a.pdf) is applied for **_SROIQ(D)_** together with the set of [optimisation heuristics](https://doi.org/10.1007/11814771_26), such as:

- lexical normalisation and simplification,
- synonym replacement,
- rewriting absorption,
- told cycle elimination,
- dependency-directed backtracking (backjumping),
- boolean constant propagation,
- semantic branching,
- ordering heuristics,
- model merging,
- completely defined concepts,
- clustering for wide and shallow taxonomies.

To tackle the OWL 2 computational complexity (double exponential in time for the worst case), the FaCT++ presents [persistent and incremental reasoning](http://ceur-ws.org/Vol-1207/paper_7.pdf). In the persistent mode, FaCT++ saves the inferred information together with its internal state into a file, which can be reloaded later with much less computational effort than reasoning would require. In the incremental mode, FaCT++ determines which parts of the precomputed inferences may be affected by an incoming change and only recomputes a subset of the inferences.

The mentioned above allows to achieve a very good performance on such known ontologies as **FHKB**, **SNOMED CT**, and **Thesaurus**.

Apart of our present work, the FaCT++ supports [Java OWL-API](https://github.com/owlcs/owlapi), Lisp API, and [DIG interface](http://dl.kr.org/dig/interface.html). It can also be [used in C](https://bitbucket.org/dtsarkov/factplusplus/src/master/FaCT++.C/test.c). There is also a [work of Levin and Cowell](https://doi.org/10.1186/s13326-015-0035-z) on C++ usage (unmaintained).


## Reasoner optimizations for RDFLib

The `pyfactxx` presents the following updates to FaCT++:

- drastically improved individuals support (`precacheIndividuals`)
- unified access point allowing arbitrary SPARQL queries
- exposing all the required C++ interfaces to RDFLib via the `coras` interface


## Installation

`pip install pyfactxx`

NB the PyPI releases plus wheels are done via GitHub action.


## Usage

See `examples` folder. In essense:

```
from pyfactxx import coras

crs = coras.Coras()
crs.load(ontology_file, format='turtle')

crs.parse()
crs.realise()

result = crs.query('SELECT ?a ?b ?c WHERE {?a ?b ?c}')
```


## Authors of Python part

- Artur Wroblewski
- Evgeny Blokhin
- Andrey Sobolev
- Ivan Rygaev


## Authors of C++ part

- Dmitry Tsarkov
- Ian Horrocks
- Ivan Rygaev


## License

- Kernel reasoner code: GNU LGPL 2.1
- Coras Python interface: GNU GPL 3.0


[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Ftilde-lab%2Fpyfactxx.svg?type=large)](https://app.fossa.com/projects/git%2Bgithub.com%2Ftilde-lab%2Fpyfactxx?ref=badge_large)
