/* This file is part of the FaCT++ DL reasoner
Copyright (C) 2010-2015 Dmitry Tsarkov and The University of Manchester
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

#ifndef TDATATYPEMANAGER_H
#define TDATATYPEMANAGER_H

#include "tDLExpression.h"
#include "tNameSet.h"

/// keeps all the TDLDataTypeName datatypes together with the names
class TDataTypeManager: public TNameSet<TDLDataTypeName>
{
public:		// interface
	// names to build/compare datatypes

		/// get name of the default string datatype
	static const char* getStrTypeName ( void ) { return "http://www.w3.org/2001/XMLSchema#string"; }
		/// get name of the default integer datatype
	static const char* getIntTypeName ( void ) { return "http://www.w3.org/2001/XMLSchema#integer"; }
		/// get name of the default floating point datatype
	static const char* getRealTypeName ( void ) { return "http://www.w3.org/2001/XMLSchema#float"; }
		/// get name of the default boolean datatype
	static const char* getBoolTypeName ( void ) { return "http://www.w3.org/2001/XMLSchema#boolean"; }
		/// get name of the default date-time datatype
	static const char* getTimeTypeName ( void ) { return "http://www.w3.org/2001/XMLSchema#dateTimeAsLong"; }
}; // TDataTypeManager

// helpers for the users

	/// @return true iff TYPE is a default string data type
inline bool isStrDataType ( const TDLDataTypeName* type ) { return std::string(type->getName()) == TDataTypeManager::getStrTypeName(); }
	/// @return true iff TYPE is a default integer data type
inline bool isIntDataType ( const TDLDataTypeName* type ) { return std::string(type->getName()) == TDataTypeManager::getIntTypeName(); }
	/// @return true iff TYPE is a default floating point data type
inline bool isRealDataType ( const TDLDataTypeName* type ) { return std::string(type->getName()) == TDataTypeManager::getRealTypeName(); }
	/// @return true iff TYPE is a default boolean data type
inline bool isBoolDataType ( const TDLDataTypeName* type ) { return std::string(type->getName()) == TDataTypeManager::getBoolTypeName(); }
	/// @return true iff TYPE is a default date-time data type
inline bool isTimeDataType ( const TDLDataTypeName* type ) { return std::string(type->getName()) == TDataTypeManager::getTimeTypeName(); }

#endif
