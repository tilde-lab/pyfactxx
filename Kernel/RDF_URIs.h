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
#define OWL_SAME_AS "http://www.w3.org/2002/07/owl#sameAs"
#define OWL_EQUIVALENT_CLASS "http://www.w3.org/2002/07/owl#equivalentClass"
#define OWL_THING "http://www.w3.org/2002/07/owl#Thing"
#define OWL_NOTHING "http://www.w3.org/2002/07/owl#Nothing"

const std::string TOP_CONCEPT = "TOP";
const std::string BOTTOM_CONCEPT = "BOTTOM";

bool isSpecialRole(const std::string& role_name)
{
    return (role_name == RDF_TYPE || role_name == OWL_SAME_AS || role_name == OWL_EQUIVALENT_CLASS || role_name == RDFS_SUBCLASS_OF);        
}

const char* toRdf(const char* name)
{
    if (strcmp(name, "TOP") == 0)
        return OWL_THING;
    else if (strcmp(name, "BOTTOM") == 0)
        return OWL_NOTHING;
    else
        return name;
}

const std::string& toFactPP(const std::string& name)
{
    if (name == OWL_THING)
        return TOP_CONCEPT;
    else if (name == OWL_NOTHING)
        return BOTTOM_CONCEPT;
    else
        return name;
}

#endif