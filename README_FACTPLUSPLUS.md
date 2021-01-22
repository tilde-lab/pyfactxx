# FaCT++ description logic reasoner

[FaCT++ logo](https://bitbucket.org/evgeny-blokhin/factplusplus-6/raw/master/fact_plus_plus_logo.png)

FaCT++ is a well-optimized open-source C++-based reasoner for **_SROIQ_** description logic with simple datatypes (OWL 2). FaCT++ was created in 2003-2015 by [Dmitry Tsarkov](https://scholar.google.com/citations?user=jDcQ7vQAAAAJ) and [Ian Horrocks](https://scholar.google.com/citations?user=0ypdmcYAAAAJ) in the University of Manchester, UK.

## Theoretical details

FaCT++ implements the [atomic decomposition algorithms](http://ceur-ws.org/Vol-1080/owled2013_13.pdf) (_i.e._ represents ontologies as terse directed acyclic graphs). A [tableaux decision procedure](http://www.cs.ox.ac.uk/ian.horrocks/Publications/download/2007/HoSa07a.pdf) is applied for **_SROIQ_** together with the set of [optimisation techniques](https://doi.org/10.1007/11814771_26), such as:

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

To tackle the OWL 2 computational complexity (double exponential in time for the worst case), FaCT++ presents [persistent and incremental reasoning](http://ceur-ws.org/Vol-1207/paper_7.pdf). In the persistent mode, FaCT++ saves the inferred information together with its internal state into a file, which can be reloaded later with much less computational effort than reasoning would require. In the incremental mode, FaCT++ determines which parts of the precomputed inferences may be affected by an incoming change and only recomputes a subset of the inferences.

The mentioned above allows to achieve a very good performance on such known ontologies as **FHKB**, **SNOMED CT**, and **Thesaurus**.

## Usage

FaCT++ supports [Java OWL-API](https://github.com/owlcs/owlapi), Lisp API and [DIG interface](http://dl.kr.org/dig/interface.html). It can also be used in [C](https://bitbucket.org/dtsarkov/factplusplus/src/master/FaCT++.C/test.c) and [C++](http://owl-cpp.sourceforge.net/tutorial_cpp.html) (see also [work of Levin and Cowell](https://doi.org/10.1186/s13326-015-0035-z)). Currently Java OWL-API is the recommended way of using FaCT++. There is also a number of ongoing efforts to provide [Python interface](https://sourceforge.net/p/owl-cpp/code/ci/master/tree/binding) and [Python RDFlib integration](https://bitbucket.org/wrobell/coras) (see also [this fork](https://bitbucket.org/wrobell/factplusplus/src/factpp/factpp)).

## Support

Please, use BitBucket issue tracker.
