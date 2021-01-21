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

#ifndef PROGRESSINDICATORINTERFACE_H
#define PROGRESSINDICATORINTERFACE_H

/// interface of the progress indicator
class ProgressIndicatorInterface
{
protected:	// members
		/// limit of the progress: indicate [0..uLimit]
	unsigned long uLimit = 0;
		/// current value of an indicator
	unsigned long uCurrent = 0;

protected:	// methods
		/// initial exposure method: can be overridden in derived classes
	virtual void initExposure ( void ) {}
		/// indicate current value somehow
	virtual void expose ( void ) = 0;
		/// check whether the limit is reached
	bool checkMax ( void )
	{
		if ( uCurrent > uLimit )
		{
			uCurrent = uLimit;
			return true;
		}
		else
			return false;
	}

public:		// interface
		/// empty c'tor
	ProgressIndicatorInterface() = default;
		/// init c'tor
	explicit ProgressIndicatorInterface ( unsigned long limit ) { setLimit (limit); }
		/// empty d'tor
	virtual ~ProgressIndicatorInterface() = default;

		/// set indicator to a given VALUE
	void setIndicator ( unsigned long value )
	{
		if ( uCurrent != value )
		{
			uCurrent = value;
			checkMax ();
			expose ();
		}
	}
		/// increment current value of an indicator to DELTA steps
	void incIndicator ( unsigned long delta = 1 ) { setIndicator(uCurrent+delta); }
		/// set indicator to 0
	void reset ( void ) { setIndicator (0); }
		/// set the limit of an indicator to a given VALUE
	void setLimit ( unsigned long limit ) { uLimit = limit; reset(); initExposure(); }
}; // ProgressIndicatorInterface

#endif
