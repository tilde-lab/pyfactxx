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

#ifndef CPPI_H
#define CPPI_H

/*
 * Console percent progress indicator
 */

#include <iostream>
#include <iomanip>

#include "ProgressIndicatorInterface.h"

class CPPI: public ProgressIndicatorInterface
{
protected:	// members
		/// previous and current numbers shown
	unsigned int oldPercent = 0;
	unsigned int curPercent = 0;

protected:	// methods
		/// initial exposition
	void initExposure ( void ) override { std::cerr << "   0%"; }
		/// expose current value
	void expose ( void ) override
	{
		curPercent = (unsigned int)(((float)uCurrent/uLimit)*100);

		// don't do anything if figure doesn't change
		if ( curPercent != oldPercent )
		{
			std::cerr << "\b\b\b\b\b" << std::setw(4) << curPercent << '%';
			oldPercent = curPercent;
		}
	}

public:		// interface
		/// empty c'tor
	CPPI ( void )
		: ProgressIndicatorInterface()
		{}
		/// init c'tor
	explicit CPPI ( unsigned long limit )
		: ProgressIndicatorInterface(limit)
		{}
}; // CPPI

#endif
