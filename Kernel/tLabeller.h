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

#ifndef TLABELLER_H
#define TLABELLER_H

#include "fpp_assert.h"
#include "tCounter.h"

/** define class that implements support for labelling entries with
 *  cheap 'unselect' operation. An external entity is 'marked' iff
 *  it's value equal to the internal counter.
 */
class TLabeller
{
private:	// internal type definition
		/// type of a counter
	typedef TCounter<unsigned int> LabelCounter;

public:		// type interface
		/// define integral type of a label
	typedef LabelCounter::IntType LabelType;

protected:	// members
		/// counter
	LabelCounter counter{1};

public:		// interface

	// operations with Labeller

		/// create a new label value
	void newLabel ( void )
	{
		counter.inc();
		fpp_assert ( counter.val() != 0 );
	}

	// operations with Labels

		/// set given label's value to the counter's one
	void set ( LabelType& lab ) const { lab = counter.val(); }
		/// clear given label's value (independent of a labeller)
	static void clear ( LabelType& lab ) { lab = 0; }
		/// check if given label is labelled
	bool isLabelled ( const LabelType& lab ) const { return (lab == counter.val()); }
}; // TLabeller

#endif
