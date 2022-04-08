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

#ifndef TRIPLEGATHERER_H
#define TRIPLEGATHERER_H

#include <vector>
#include <string>
#include <functional>
#include "taxVertex.h"
#include "RDF_URIs.h"
#include "tIndividual.h"

void pushTriple(std::set<std::vector<std::string>>& triples, const char* subj, const char* role, const char* obj)
{
    std::vector<std::string> triple;

    triple.push_back(toRdf(subj));
    triple.push_back(toRdf(role));
    triple.push_back(toRdf(obj));

    triples.insert(triple);
}

class TripleGatherer {
    std::set<std::vector<std::string>>* triples;
    bool isDirect;
    const char* roleName;
    const char* nodeName;
    std::function<bool(const ClassifiableEntry*)> filter;

public:

    TripleGatherer(std::set<std::vector<std::string>>* triples, bool isDirect, const char* roleName, const char* nodeName) : triples(triples), isDirect(isDirect), roleName(roleName), nodeName(nodeName)
    {
        filter = [](const ClassifiableEntry*)
        {
            return true;
        };
    }

    TripleGatherer(std::set<std::vector<std::string>>* triples, bool isDirect, const char* roleName, const char* nodeName, std::function<bool(const ClassifiableEntry*)> filter) : triples(triples), isDirect(isDirect), roleName(roleName), nodeName(nodeName), filter(filter)
    {}

    void clear()
    {}

    void addTriple(const ClassifiableEntry* entry)
    {
        if (filter(entry))
        {
            const char* entityName = toRdf(entry->getName());

            std::vector<std::string> triple;

            if (isDirect)
                pushTriple(*triples, nodeName, roleName, entityName);
            else
                pushTriple(*triples, entityName, roleName, nodeName);
        }
    }

    /// taxonomy walking method.
    /// @return true if node was processed
    /// @return false if node can not be processed in current settings
    bool apply(const TaxonomyVertex& vertex)
    {
        addTriple(vertex.getPrimer());

        for (const auto& synonym : vertex.synonyms())
            addTriple(synonym);

        return true;
    }
}; // TripleGatherer

#endif
