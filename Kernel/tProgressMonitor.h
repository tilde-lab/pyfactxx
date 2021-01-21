/* This file is part of the FaCT++ DL reasoner
Copyright (C) 2006-2015 Dmitry Tsarkov and The University of Manchester
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

#ifndef TPROGRESSMONITOR_H
#define TPROGRESSMONITOR_H

/// progress monitor and canceller for the classification
class TProgressMonitor
{
public:
		/// empty c'tor
	TProgressMonitor() = default;
		/// empty d'tor
	virtual ~TProgressMonitor() = default;

	// interface

		/// informs about beginning of classification with number of concepts to be classified
	virtual void setClassificationStarted ( unsigned int ) {}
		/// informs about beginning of classification of a given CONCEPT
	virtual void nextClass ( void ) {}
		/// informs that the reasoning is done
	virtual void setFinished ( void ) {}
		// @return true iff reasoner have to be stopped
	virtual bool isCancelled ( void ) { return false; }
}; // TProgressMonitor

#endif
