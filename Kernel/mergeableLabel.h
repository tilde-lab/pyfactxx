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

#ifndef MERGEABLELABEL_H
#define MERGEABLELABEL_H

/// implementation of labels that could be compared and merged to each other
class mergeableLabel
{
protected:	// members
		/// sample for all equivalent labels
	mergeableLabel* pSample;

public:		// interface
		/// empty c'tor
	mergeableLabel ( void ) : pSample(this) {}
		/// copy c'tor
	mergeableLabel ( mergeableLabel& p ) : pSample(p.resolve()) {}
		/// assignment
	mergeableLabel& operator = ( mergeableLabel& p ) { pSample = p.resolve(); return *this; }

	// general interface

		/// are 2 labels equal; works only for normalised labels
	bool operator == ( const mergeableLabel& p ) const { return (pSample == p.pSample); }
		/// are 2 labels different; works only for normalised labels
	bool operator != ( const mergeableLabel& p ) const { return (pSample != p.pSample); }
		/// make 2 labels equal
	void merge ( mergeableLabel& p )
	{
		mergeableLabel* sample = p.resolve();
		resolve();
		if ( pSample != sample )
			pSample->pSample = sample;
	}
		/// make label's depth <= 2; @return sample of the label
	mergeableLabel* resolve ( void )
	{
		// check if current node is itself sample
		if ( !isSample() )
			pSample = pSample->resolve();

		return pSample;
	}
		/// is given label a sample label
	bool isSample ( void ) const { return (pSample == this); }
}; // mergeableLabel

#endif
