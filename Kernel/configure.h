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

#ifndef CONFIGURE_H
#define CONFIGURE_H

const unsigned MaxConfLineLen = 1024;

#include <cstring>
#include <cstdlib>
#include <iosfwd>

#include <string>
#include <vector>

/// configure element in the form Name = Value
class ConfElem
{
public:		// members
		/// name of the field
	const std::string Name;
		/// value of the field
	std::string Value;

public:		// interface
		/// empty c'tor
	ConfElem() = default;
		/// init c'tor
	ConfElem ( const std::string& n, const std::string& v ) : Name (n), Value (v) {}

		/// save element to target stream
	void Save ( std::ostream& o ) const;

	// interface
	long GetLong ( void ) const { return std::stol(Value); }
	double GetDouble ( void ) const { return atof ( Value.c_str() ); }
	const char* GetString ( void ) const { return Value.c_str(); }
}; // ConfElem

/// class for storing configure section with all the keys
class ConfSection
{
protected:
	const std::string Name;

	typedef std::vector <ConfElem*> ConfBase;
	ConfBase Base;

public:
	explicit ConfSection ( const std::string& pc ) : Name ( pc ) {}
	~ConfSection();

	bool operator == ( const std::string& pc ) const { return ( Name == pc ); }
	bool operator != ( const std::string& pc ) const { return ( Name != pc ); }

	void addEntry ( const std::string& Name, const std::string& Value );

	// find element; return NULL if not found
	ConfElem* FindByName ( const std::string& name ) const;

	void Save ( std::ostream& o ) const;
}; // ConfSection

/// class for reading general configuration file
class Configuration
{
protected:	// parsing part
	std::string fileName;	// fileName
	char Line [MaxConfLineLen+1];	// \0
	bool isLoaded = false;	// flags
	bool isSaved = false;

	// parser methods
	void loadString ( std::istream& );
	bool isComment ( void ) const;
	bool isSection ( void ) const
		{ return ( Line [0] == '[' && Line [strlen(Line)-1] == ']' ); }
	void loadSection ( void );
	// splits line 'name=value' on 2 ASCIIZ parts
	int SplitLine ( char*& pName, char*& pValue );

protected:	// logic part
	typedef std::vector <ConfSection*> ConfSectBase;
	ConfSectBase Base;
	ConfSection* Section = nullptr;
	ConfElem* Element = nullptr;

	// navigation methods
	ConfSection* FindSection ( const std::string& pc ) const;

public:		// interface
		/// init c'tor
	Configuration() = default;
		/// no copy c'tor
	Configuration ( const Configuration& ) = delete;
		/// no assignment
	Configuration& operator = ( const Configuration& ) = delete;
		/// d'tor
	~Configuration();

	// load config from file; @return true if cannot
	bool Load ( const char* Filename );
	// save config to file; @return true if cannot
	bool Save ( const char* Filename );
	// save config to the loaded file; @return true if cannot
	bool Save ( void ) { return isLoaded ? Save(fileName.c_str()) : true; }

	// status methods
	bool Loaded ( void ) const { return isLoaded; }
	bool Saved  ( void ) const { return isSaved; }
		/// add section if necessary; set Current to new pointer
	void createSection ( const std::string& name );
		/// check if Sect.Field exists;
	bool checkValue ( const std::string& Section, const std::string& Element );
		/// check if Field exists if Current is set;
	bool checkValue ( const std::string& Element );
		/// add Field.value to current Section; sets Trying to new p.
	bool setValue ( const std::string& Element, const std::string& Value );

	// get checked value
	std::string getValue ( void ) const { return Element->Value; }
	long getLong ( void ) const { return Element->GetLong(); }
	double getDouble ( void ) const { return Element->GetDouble(); }
	const char* getString ( void ) const { return Element->GetString(); }
	// set Sect as a default
	bool useSection ( const std::string& name )
		{ Section = FindSection(name); return !Section; }
}; // Configuration

#endif
