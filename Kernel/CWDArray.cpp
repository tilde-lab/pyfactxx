/* This file is part of the FaCT++ DL reasoner
Copyright (C) 2003-2015 Dmitry Tsarkov and The University of Manchester
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

#include "CWDArray.h"
#include "tRestorer.h"

/// restore dep-set of the duplicated label element of the merged node
class UnMerge: public TRestorer
{
protected:
	CWDArray& label;
	int offset;
	DepSet dep;
public:
	UnMerge ( CWDArray& lab, CWDArray::iterator p )
		: label(lab)
		, offset(int(p-lab.begin()))
		, dep(p->getDep())
	{}
	void restore ( void ) override
	{
		CWDArray::iterator p = label.Base.begin() + offset;
		*p = ConceptWDep(p->bp(),dep);
	}
}; // UnMerge

TRestorer*
CWDArray :: updateDepSet ( BipolarPointer bp, const DepSet& dep )
{
	if ( dep.empty() )
		return nullptr;

	for ( iterator i = Base.begin(), i_end = Base.end(); i < i_end; ++i )
		if ( i->bp() == bp )
		{
			TRestorer* ret = new UnMerge ( *this, i );
//			DepSet odep(i->getDep());
			i->addDep(dep);
/*			if ( odep == i->getDep() )
			{
				delete ret;
				ret = nullptr;
			}*/
			return ret;
		}
	return nullptr;
}

/// restore label to given LEVEL using given SS
void
CWDArray :: restore ( const SaveState& ss, unsigned int level ATTR_UNUSED )
{
	if ( RKG_USE_DYNAMIC_BACKJUMPING )
	{
		for ( size_t j = ss.ep; j < Base.size(); ++j )
			if ( Base[j].getDep().contains(level) )
			{
				// replace concept that depend on a given BC with TOP
				Base[j] = ConceptWDep(1,DepSet());
			}
	}
	else
		Base.resize(ss.ep);
}

/// print label part between given iterators
void
CWDArray :: print ( std::ostream& o ) const
{
	o << " [";
	const_iterator p = begin(), p_end = end();

	if ( p != p_end )
	{
		o << *p;

		while ( ++p < p_end )
			o << ", " << *p;
	}
	o << "]";
}
