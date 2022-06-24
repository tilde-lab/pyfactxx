/* This file is part of the FaCT++ DL reasoner
Copyright (C) 2015-2015 Dmitry Tsarkov and The University of Manchester
Copyright (C) 2015-2017 Dmitry Tsarkov

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
*/

#ifndef RDF_URIS_H
#define RDF_URIS_H

#include <string>

#define RDF_TYPE "http://www.w3.org/1999/02/22-rdf-syntax-ns#type"

#define RDFS_SUBCLASS_OF "http://www.w3.org/2000/01/rdf-schema#subClassOf"
#define RDFS_SUBPROPERTY_OF "http://www.w3.org/2000/01/rdf-schema#subPropertyOf"
#define RDFS_DOMAIN "http://www.w3.org/2000/01/rdf-schema#domain"
#define RDFS_RANGE "http://www.w3.org/2000/01/rdf-schema#range"

#define OWL_CLASS "http://www.w3.org/2002/07/owl#Class"
#define OWL_SAME_AS "http://www.w3.org/2002/07/owl#sameAs"
#define OWL_EQUIVALENT_CLASS "http://www.w3.org/2002/07/owl#equivalentClass"
#define OWL_DISJOINT_WITH "http://www.w3.org/2002/07/owl#disjointWith"
#define OWL_THING "http://www.w3.org/2002/07/owl#Thing"
#define OWL_NOTHING "http://www.w3.org/2002/07/owl#Nothing"

#define OWL_OBJECT_PROPERTY "http://www.w3.org/2002/07/owl#ObjectProperty"
#define OWL_DATATYPE_PROPERTY "http://www.w3.org/2002/07/owl#DatatypeProperty"
#define OWL_TOP_OBJECT_PROPERTY "http://www.w3.org/2002/07/owl#topObjectProperty"
#define OWL_BOTTOM_OBJECT_PROPERTY "http://www.w3.org/2002/07/owl#bottomObjectProperty"
#define OWL_TOP_DATA_PROPERTY "http://www.w3.org/2002/07/owl#topDataProperty"
#define OWL_BOTTOM_DATA_PROPERTY "http://www.w3.org/2002/07/owl#bottomDataProperty"
#define OWL_INVERSE_OF "http://www.w3.org/2002/07/owl#inverseOf"
#define OWL_EQUIVALENT_PROPERTY "http://www.w3.org/2002/07/owl#equivalentProperty"
#define OWL_FUNCTIONAL_PROPERTY "http://www.w3.org/2002/07/owl#FunctionalProperty"
#define OWL_INVERSE_FUNCTIONAL_PROPERTY "http://www.w3.org/2002/07/owl#InverseFunctionalProperty"
#define OWL_SYMMETRIC_PROPERTY "http://www.w3.org/2002/07/owl#SymmetricProperty"
#define OWL_ASYMMETRIC_PROPERTY "http://www.w3.org/2002/07/owl#AsymmetricProperty"
#define OWL_TRANSITIVE_PROPERTY "http://www.w3.org/2002/07/owl#TransitiveProperty"
#define OWL_REFLEXIVE_PROPERTY "http://www.w3.org/2002/07/owl#ReflexiveProperty"
#define OWL_IRREFLEXIVE_PROPERTY "http://www.w3.org/2002/07/owl#IrreflexiveProperty"

bool isSpecialRole(const std::string& role_name)
{
    return (role_name == RDF_TYPE || role_name == OWL_SAME_AS || role_name == OWL_EQUIVALENT_CLASS || role_name == RDFS_SUBCLASS_OF || role_name == RDFS_SUBPROPERTY_OF || role_name == OWL_DISJOINT_WITH || role_name == RDFS_DOMAIN || role_name == RDFS_RANGE || role_name == OWL_EQUIVALENT_PROPERTY || role_name == OWL_INVERSE_OF);
}

const char* toRdf(const char* name)
{
    if (strcmp(name, "TOP") == 0)
        return OWL_THING;
    else if (strcmp(name, "BOTTOM") == 0)
        return OWL_NOTHING;
    else if (strcmp(name, "*UROLE*") == 0)
        return OWL_TOP_OBJECT_PROPERTY;
    else if (strcmp(name, "*EROLE*") == 0)
        return OWL_BOTTOM_OBJECT_PROPERTY;
    else if (strcmp(name, "*UDROLE*") == 0)
        return OWL_TOP_DATA_PROPERTY;
    else if (strcmp(name, "*EDROLE*") == 0)
        return OWL_BOTTOM_DATA_PROPERTY;
    else
        return name;
}

#endif