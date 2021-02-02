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

#ifndef IFOPTIONS_H
#define IFOPTIONS_H

/********************************************************\
|* Interface for the options management for the FaCT++  *|
\********************************************************/

#include <string>
#include <map>
#include <iosfwd>

#include "fpp_assert.h"

class Configuration;

/// class for working with general options with boolean, integer or text values
class ifOption
{
public:		// type interface
		/// type of an option
	enum ioType { iotBool, iotInt, iotText };

protected:	// members
		/// option name
	std::string optionName;
		/// informal description
	std::string optionDescription;
		/// default value (name of type)
	std::string defaultValue;
		/// textual value [relevant iff (type == iotText)]
	std::string tValue;
		/// type of value: bool, int or text
	ioType type;
		/// integer value [relevant iff (type == iotInt)]
	int iValue = 0;
		/// boolean value [relevant iff (type == iotBool)]
	bool bValue = 0;

public:		// interface
		/// c'tor (init all values including proper ?Value)
	ifOption ( const std::string& name, const std::string& desc, ioType t, const std::string& defVal );
		/// no empty c'tor
	ifOption ( void ) = delete;
		/// no copy c'tor
	ifOption ( const ifOption& ) = delete;
		/// no assignment
	ifOption& operator = ( const ifOption& ) = delete;

	// write methods

		/// set boolean value; @return false in case of error
	bool setValue ( bool b ) { bValue = b; return (type != iotBool); }
		/// set integer value; @return false in case of error
	bool setValue ( int i ) { iValue = i; return (type != iotInt); }
		/// set string value; @return false in case of error
	bool setValue ( const std::string& t ) { tValue = t; return (type != iotText); }
		/// set textually given value of current type; @return false in case of error
	bool setAValue ( const std::string& s );

	// access methods
		/// get value of a Boolean option
	bool getBool ( void ) const { fpp_assert ( type == iotBool ); return bValue; }
		/// get value of an integer option
	int getInt ( void ) const { fpp_assert ( type == iotInt ); return iValue; }
		/// get value of a string option
	const std::string& getText ( void ) const { fpp_assert ( type == iotText ); return tValue; }

		/// output in the form of config file
	void printConfString ( std::ostream& o ) const;
}; // ifOption

// implementation of class ifOption

inline ifOption :: ifOption ( const std::string& name, const std::string& desc, ioType t, const std::string& defVal )
	: optionName(name)
	, optionDescription(desc)
	, defaultValue(defVal)
	, type(t)
{
	setAValue (defVal);
}

/// set of options with access by name
class ifOptionSet
{
protected:	// internal type definitions
		/// base internal type
	typedef std::map<std::string,ifOption*> OptionSet;

protected:	// members
		/// set of all available (given) options
	OptionSet Base;

protected:	// methods
		/// get access option structure by name; @return NULL if no such option was registered
	const ifOption* locateOption ( const std::string& name ) const
	{
		auto p = Base.find(name);
		return p == Base.end() ? nullptr : p->second;
	}

public:		// interface
		/// empty c'tor
	ifOptionSet() = default;
		/// d'tor (delete all registered options)
	~ifOptionSet()
	{
		for ( auto& p: Base )
			delete p.second;
	}

		/// register an option with given name, description, type and default. @return true iff such option exists
	bool RegisterOption (
		const std::string& name,
		const std::string& desc,
		ifOption::ioType t,
		const std::string& defVal )
	{
		if ( locateOption (name) != nullptr )
			return true;
		Base[name] = new ifOption ( name, desc, t, defVal );
		return false;
	}
		/// init all registered option using given section of given configuration
	bool initByConfigure ( Configuration& conf, const std::string& Section );

	bool setOption( const std::string& optionName, const std::string& optionValue)
	{
		auto p = Base.find(optionName);
		if ( p == Base.end() )
			return true;
		if ( p->second->setAValue(optionValue))	// ... set up its value
				return true;	// can't set value
		return false;
	}

	// read access

		/// get Boolean value of given option
	bool getBool ( const std::string& optionName ) const
	{
		const ifOption* p = locateOption ( optionName );
		fpp_assert ( p != nullptr );
		return p->getBool ();
	}
		/// get integral value of given option
	int getInt ( const std::string& optionName ) const
	{
		const ifOption* p = locateOption ( optionName );
		fpp_assert ( p != nullptr );
		return p->getInt ();
	}
		/// get string value of given option
	const std::string& getText ( const std::string& optionName ) const
	{
		const ifOption* p = locateOption ( optionName );
		fpp_assert ( p != nullptr );
		return p->getText ();
	}

		/// output option set in the form of config file
	void printConfig ( std::ostream& o ) const;
}; // ifOptionSet

#endif
