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

#include <ostream>

#include "ifOptions.h"
#include "configure.h"

bool ifOption :: setAValue ( const std::string& s )
{
	if ( type == iotBool )	// check possible boolean values
	{
		if ( s == "0" || s == "off" || s == "no" || s == "false" )
			bValue = false;
		else if ( s == "1" || s == "on" || s == "yes" || s == "true" )
			bValue = true;
		else
			return true;
	}
	else if ( type == iotInt )	// check possible integer values
	{
		if ( !isdigit (s[0]) )
			return true;
		else
			iValue = stoi(s);
	}
	else	// text values
		tValue = s;

	return false;
}

/// output in the form of config file
void ifOption :: printConfString ( std::ostream& o ) const
{
	// name and type
	o << "\n;---\n;--- Option '" << optionName << "': ";
	if ( type == iotBool )
		o << "boolean";
	else if ( type == iotInt )
		o << "integer";
	else if ( type == iotText )
		o << "text";
	else	// sanity check
		fpp_unreachable();

	// description, default, name
	o << " ---\n;---\n;* " << optionDescription << "\n;* Default value: '"
	  << defaultValue << "'\n\n; " << optionName << " = ";

	// value
	if ( type == iotBool )
		o << getBool ();
	else if ( type == iotInt )
		o << getInt ();
	else if ( type == iotText )
		o << getText();
	else	// sanity check
		fpp_unreachable();

	o << "\n";
}

bool ifOptionSet :: initByConfigure ( Configuration& Config, const std::string& Section )
{
	// try to load given config section
	if ( Config.useSection ( Section ) )
		return true;

	// for all registered options
	for ( const auto& p: Base )
		if ( !Config.checkValue(p.first) )			// if option located in config file
			if ( p.second->setAValue(Config.getValue()))	// ... set up its value
				return true;	// can't set value

	// all done without errors
	return false;
}

/// output in the form of config file
void ifOptionSet :: printConfig ( std::ostream& o ) const
{
	// print LeveLogger info
	o << "[LeveLogger]\n\n;--- Logging file name\n file = reasoning.log\n"
		 ";--- Logging level (the less level you give, the less information will be logged)\n allowedLevel = 0\n\n";

	// print header
	o << "\n[Tuning]\n";

	for ( const auto& opt: Base )
		opt.second->printConfString(o);
	o << std::endl;
}
