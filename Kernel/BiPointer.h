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

#ifndef BIPOINTER_H
#define BIPOINTER_H

typedef int BipolarPointer;

inline BipolarPointer createBiPointer ( int index, bool pos ) { return (pos ? index : -index); }
inline BipolarPointer createBiPointer ( unsigned int index, bool pos ) { return createBiPointer ( (int)index, pos ); }

inline bool isCorrect ( BipolarPointer p ) { return (p!=0); }
inline bool isValid ( BipolarPointer p ) { return (p!=0); }
inline bool isPositive ( BipolarPointer p ) { return (p>0); }
inline bool isNegative ( BipolarPointer p ) { return (p<0); }

inline unsigned int getValue ( BipolarPointer p ) { return (p>0?(unsigned int)p:(unsigned int)-p); }

inline BipolarPointer inverse ( BipolarPointer p ) { return -p; }
inline BipolarPointer getPositive ( BipolarPointer p ) { return (p>0?p:-p); }
inline BipolarPointer getNegative ( BipolarPointer p ) { return (p<0?p:-p); }

const BipolarPointer bpINVALID = 0;
const BipolarPointer bpTOP = 1;
const BipolarPointer bpBOTTOM = -1;

#endif // BIPOINTER_H
