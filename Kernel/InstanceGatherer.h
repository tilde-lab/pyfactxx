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

#ifndef INSTANCEGATHERER_H
#define INSTANCEGATHERER_H

#include <vector>
#include <string>
#include "taxVertex.h"
#include "RDF_URIs.h"
#include "tIndividual.h"

class InstanceGatherer {
    std::set<std::vector<std::string>>* triples;
    std::string typeName;

public:

    InstanceGatherer(std::set<std::vector<std::string>>* triples, const std::string& typeName) : triples(triples), typeName(typeName)
    {}

    void clear()
    {}

    void addInstance(const ClassifiableEntry* individual)
    {
        const TIndividual* named = dynamic_cast<const TIndividual*>(individual);

        if (named != nullptr && named->isSingleton())
        {
            std::vector<std::string> triple;

            triple.push_back(named->getName());
            triple.push_back(RDF_TYPE);
            triple.push_back(typeName);

            triples->insert(triple);
        }
    }

    /// taxonomy walking method.
    /// @return true if node was processed
    /// @return false if node can not be processed in current settings
    bool apply(const TaxonomyVertex& vertex)
    {
        addInstance(vertex.getPrimer());

        for (const auto& synonym : vertex.synonyms())
            addInstance(synonym);

        return true;
    }
}; // InstanceGatherer

#endif
