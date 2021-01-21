/* This file is part of the FaCT++ DL reasoner
Copyright (C) 2009-2015 Dmitry Tsarkov and The University of Manchester
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

#ifndef FPP_ASSERT_H
#define FPP_ASSERT_H

#include "eFaCTPlusPlus.h"

/// assertion exception
class EFPPAssertion: public EFaCTPlusPlus
{
public:		// interface
		/// the default constructor
	explicit EFPPAssertion ( const char* reason ) : EFaCTPlusPlus(reason) {}
}; // EFPPAssertion

#undef fpp_assert
#undef fpp_assert_
#undef fpp_assert__
#undef fpp_unreachable

/// assert() helpers
#define fpp_assert__(e,file,line)	\
	throw EFPPAssertion(file ":" #line ": assertion '" e "' fails")
#define fpp_assert_(e,f,l) fpp_assert__(#e,f,l)

#ifdef NDEBUG
#	define fpp_assert(e) ((void)(false && (e)))
#else
#	define fpp_assert(e) ((void)((e) ? 0 : fpp_assert_ ( e, __FILE__, __LINE__ )))
#endif

#define fpp_unreachable() fpp_assert_ ( unreachable, __FILE__, __LINE__ )




#endif
