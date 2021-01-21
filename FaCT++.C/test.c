/* This file is part of the FaCT++ DL reasoner
Copyright (C) 2011-2015 Dmitry Tsarkov and The University of Manchester
Copyright (C) 2015-2017 Dmitry Tsarkov

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
*/

#include <stdio.h>
#include "fact.h"

void print2Darray ( const char*** names )
{
	printf("[\n");
	int n=0;
	const char** syns = names[0];
	while ( syns != NULL )
	{
		printf("[");
		int m = 0;
		const char* name;
		while ( (name = syns[m++]) != NULL )
			printf("%s ", name);
		printf("]\n");
		syns=names[++n];
	}
	printf("]\n");
}

int main ( void )
{
	puts("Starting FaCT++ C interface tests");
	// create kernel
	fact_reasoning_kernel* k = fact_reasoning_kernel_new();

	// create classes C,D, property R
	puts("Creating entities");
	fact_concept_expression* c = fact_concept(k,"C");
	fact_concept_expression* d = fact_concept(k,"D");
	fact_individual_expression* i = fact_individual(k,"I");
	fact_o_role_expression* r = fact_object_role(k,"R");

	// create C [= ER.T, ER.T [= D
	puts("Creating axioms");
	fact_concept_expression* some = fact_o_exists ( k, r, fact_top(k));
	fact_implies_concepts ( k, c, some );
	fact_implies_concepts ( k, some, d );
	fact_instance_of ( k, i, c );

	// classify KB is not necessary: it's done automatically depending on a query
	puts("Classifying ontology");
	fact_classify_kb(k);

	// check whether C [= D
	puts("Is C subsumed by D?");
	if ( fact_is_subsumed_by(k,c,d) )
		puts("Yes!\n");
	else
		puts("No...\n");

	// create a concept actor and use it to get all superclasses of C
	puts("All superclasses of C:");
	fact_actor* actor = fact_concept_actor_new();
	fact_get_sup_concepts(k,c,0,&actor);
	print2Darray(fact_get_elements_2d(actor));
	fact_actor_free(actor);

	// create an individual actor and use it to get all instances of C
	puts("All instances of C:");
	fact_actor* i_actor = fact_individual_actor_new();
	fact_get_instances(k, c, &i_actor);
	print2Darray(fact_get_elements_2d(i_actor));
	fact_actor_free(i_actor);

	// get all the properties
	puts("All object properties:");
	fact_o_role_expression* o_top = fact_object_role_top(k);
	actor = fact_o_role_actor_new();
	fact_get_sub_roles(k,(fact_role_expression*)o_top,0,&actor);
	print2Darray(fact_get_elements_2d(actor));
	fact_actor_free(actor);

	// we done so let's free memory
	puts("Destroying reasoning kernel");
	fact_reasoning_kernel_free(k);
	puts("All done");
	return 0;
}
