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

#ifndef PROCTIMER_H
#define PROCTIMER_H

#include <ctime>

/**
  * Class TsProcTimer definition & implementation
  *
  * Useful for calculating processor time intervals up to 10^-3 sec
  *
  */
class TsProcTimer
{
private:	// members
		/// save the starting time of the timer
	clock_t startTime = 0;
		/// calculated time between Start() and Stop() calls
	float resultTime = 0.0f;
		/// flag to show timer is started
	bool Started = false;

private:	// methods
		/// get time interval between startTime and current time
	float calcDelta ( void ) const;

public:		// interface
		/// reset timer
	void Reset ( void );

		/// record current time
	void Start ( void );
		/// save time interval from starting point to current moment
	void Stop ( void );

		/// get time interval
	operator float ( void ) const;
}; // TsProcTimer

/**
  *   TsTimer implementation
  */

inline void TsProcTimer :: Reset ( void )
{
	Started = false;
	resultTime = 0;
}

inline float TsProcTimer :: calcDelta ( void ) const
{
	clock_t finishTime = clock();

	// calculate difference between current time and start time
	float realProcTime = float(finishTime>=startTime ?
		finishTime-startTime :
		((clock_t)-1)-startTime+finishTime ) / CLOCKS_PER_SEC;

	// correct times less than a millisecond
	if ( realProcTime < 1e-3 )
		realProcTime = 0;

	return realProcTime;
}

inline TsProcTimer :: operator float ( void ) const
{
	const unsigned int norm = 100;	// normalisation constant
	float realProcTime;

	if ( Started )
		realProcTime = calcDelta();
	else
		realProcTime = resultTime;

	// normalise value up to CONST
	return ((unsigned long)(realProcTime*norm))/(float)norm;
}

inline void TsProcTimer :: Start ( void )
{
	if ( !Started )
	{
		startTime = clock();
		Started = true;
	}
}

inline void TsProcTimer :: Stop ( void )
{
	if ( Started )
	{
		Started = false;
		resultTime += calcDelta ();
	}
}

#endif
