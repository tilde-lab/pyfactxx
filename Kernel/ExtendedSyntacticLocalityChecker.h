/* This file is part of the FaCT++ DL reasoner
Copyright (C) 2013-2015 Dmitry Tsarkov and The University of Manchester
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

#ifndef EXTENDEDSYNTACTICLOCALITYCHECKER_H
#define EXTENDEDSYNTACTICLOCALITYCHECKER_H

// uncomment the following line to debug the locality checker
//#define FPP_DEBUG_EXTENDED_LOCALITY

// debug also the general locality checker
#ifdef FPP_DEBUG_EXTENDED_LOCALITY
#	define FPP_DEBUG_LOCALITY
#endif

#include "GeneralSyntacticLocalityChecker.h"

#ifdef FPP_DEBUG_EXTENDED_LOCALITY
#	include "tExpressionPrinterLISP.h"
#endif

// forward declarations
class UpperBoundDirectEvaluator;
class LowerBoundDirectEvaluator;
class UpperBoundComplementEvaluator;
class LowerBoundComplementEvaluator;

class CardinalityEvaluatorBase: protected SigAccessor, public DLExpressionVisitorEmpty
{
protected:	// members
#ifdef FPP_DEBUG_EXTENDED_LOCALITY
	TLISPExpressionPrinter lp;
	const char* name;
#endif

	UpperBoundDirectEvaluator* UBD = nullptr;
	LowerBoundDirectEvaluator* LBD = nullptr;
	UpperBoundComplementEvaluator* UBC = nullptr;
	LowerBoundComplementEvaluator* LBC = nullptr;

		/// keep the value here
	int value = 0;

protected:	// methods to
		/// main method to use
	int getValue ( const TDLExpression& expr )
	{
		expr.accept(*this);
		return value;
	}

	void dumpValue ( const TDLExpression& expr ATTR_UNUSED )
	{
#	ifdef FPP_DEBUG_EXTENDED_LOCALITY
		std::cout << name << ": value for";
		expr.accept(lp);
		std::cout << " is " << value << std::endl;
#	endif
	}

		/// implementation of evaluation
	int getUpperBoundDirect ( const TDLExpression& expr );
		/// implementation of evaluation
	int getUpperBoundComplement ( const TDLExpression& expr );
		/// implementation of evaluation
	int getLowerBoundDirect ( const TDLExpression& expr );
		/// implementation of evaluation
	int getLowerBoundComplement ( const TDLExpression& expr );

protected:	// visitor helpers
		/// define a special value for concepts that are not in C[C}^{<= n}
	int noUpperValue ( void ) const { return -1; }
		/// define a special value for concepts that are in C[C]^{<= n} for all n
	int anyUpperValue ( void ) const { return 0; }
		/// return all or none values depending on the condition
	int getAllNoneUpper ( bool condition ) const { return condition ? anyUpperValue() : noUpperValue(); }
		/// define a special value for concepts that are not in C[C]^{>= n}
	int noLowerValue ( void ) const { return 0; }
		/// define a special value for concepts that are in C[C]^{>= n} for all n
	int anyLowerValue ( void ) const { return -1; }
		/// return 1 or none values depending on the condition
	int getOneNoneLower ( bool condition ) const { return condition ? 1 : noLowerValue(); }

		/// @return true if given upper VALUE is less than M
	bool isUpperLT ( int value, unsigned int m ) const
	{
		if ( value == noUpperValue() )
			return false;
		return value == anyUpperValue() || (unsigned int) value < m;
	}
		/// @return true if given upper VALUE is less than or equal to M
	bool isUpperLE ( int value, unsigned int m ) const { return isUpperLT ( value, m+1 ); }
		/// @return true if given lower VALUE is greater than or equal to M
	bool isLowerGE ( int value, unsigned int m ) const
	{
		if ( value == noLowerValue() )
			return false;
		return value == anyLowerValue() || (unsigned int) value >= m;
	}
		/// @return true if given upper VALUE is greater than M
	bool isLowerGT ( int value, unsigned int m ) const { return isLowerGE ( value, m+1 ); }

	bool isBotEquivalent ( const TDLExpression* expr ) { return isUpperLE ( getUpperBoundDirect(*expr), 0 ); }
	bool isTopEquivalent ( const TDLExpression* expr ) { return isUpperLE ( getUpperBoundComplement(*expr), 0 ); }

	// helpers for and/or

		/// return minimal of the two Upper Bounds
	int minUpperValue ( int uv1, int uv2 )
	{
		// noUpperValue is a maximal element
		if ( uv1 == noUpperValue() )
			return uv2;
		if ( uv2 == noUpperValue() )
			return uv1;
		// now return the smallest value, with anyUpperValue being
		// the smallest and deal with automatically
		return std::min(uv1,uv2);
	}
/*		/// return maximal of the two Lower Bounds
	int maxLowerValue ( int lv1, int lv2 )
	{
		// noLowerValue is a maximal element
		if ( lv1 == noLowerValue() || lv2 == noLowerValue() )
			return noLowerValue();
		// we are looking for the maximal value here; ANY need to be special-cased
		for ( typename TDLNAryExpression<C>::iterator p = expr.begin(), p_end = expr.end(); p != p_end; ++p )
		{
			int n = getLowerBoundDirect(*p);
			if ( n == anyLowerValue() )
				return anyLowerValue();
			max = std::max ( max, n );
		}
		return max;

	}
*/
	// tunable for every case

		/// helper for entities
	virtual int getEntityValue ( const TNamedEntity* entity ) = 0;
		/// helper for All
	virtual int getForallValue ( const TDLRoleExpression* R, const TDLExpression* C ) = 0;
		/// helper for things like >= m R.C
	virtual int getMinValue ( unsigned int m, const TDLRoleExpression* R, const TDLExpression* C ) = 0;
		/// helper for things like <= m R.C
	virtual int getMaxValue ( unsigned int m, const TDLRoleExpression* R, const TDLExpression* C ) = 0;
		/// helper for things like = m R.C
	virtual int getExactValue ( unsigned int m, const TDLRoleExpression* R, const TDLExpression* C ) = 0;

public:		// interface
		/// init c'tor
	CardinalityEvaluatorBase ( const TSignature* s, const char* n ATTR_UNUSED )
		: SigAccessor(s)
#	ifdef FPP_DEBUG_EXTENDED_LOCALITY
		, lp(std::cout)
		, name(n)
#	endif
		{}

		/// set all other evaluators
	void setEvaluators ( UpperBoundDirectEvaluator* pUD, LowerBoundDirectEvaluator* pLD, UpperBoundComplementEvaluator* pUC, LowerBoundComplementEvaluator* pLC )
	{
		UBD = pUD;
		LBD = pLD;
		UBC = pUC;
		LBC = pLC;
		fpp_assert ( (void*)UBD == this || (void*)LBD == this || (void*)UBC == this || (void*)LBC == this );
	}

		/// implementation of evaluation
	int getUpperBoundDirect ( const TDLExpression* expr ) { return getUpperBoundDirect(*expr); }
		/// implementation of evaluation
	int getUpperBoundComplement ( const TDLExpression* expr ) { return getUpperBoundComplement(*expr); }
		/// implementation of evaluation
	int getLowerBoundDirect ( const TDLExpression* expr ) { return getLowerBoundDirect(*expr); }
		/// implementation of evaluation
	int getLowerBoundComplement ( const TDLExpression* expr ) { return getLowerBoundComplement(*expr); }

public:		// visitor implementation: common cases
	// concept expressions
	void visit ( const TDLConceptName& expr ) override
		{ value = getEntityValue(expr.getEntity()); }
	void visit ( const TDLConceptObjectExists& expr ) override
		{ value = getMinValue ( 1, expr.getOR(), expr.getC() ); dumpValue(expr); }
	void visit ( const TDLConceptObjectForall& expr ) override
		{ value = getForallValue ( expr.getOR(), expr.getC() ); }
	void visit ( const TDLConceptObjectMinCardinality& expr ) override
		{ value = getMinValue ( expr.getNumber(), expr.getOR(), expr.getC() ); }
	void visit ( const TDLConceptObjectMaxCardinality& expr ) override
		{ value = getMaxValue ( expr.getNumber(), expr.getOR(), expr.getC() ); }
	void visit ( const TDLConceptObjectExactCardinality& expr ) override
		{ value = getExactValue ( expr.getNumber(), expr.getOR(), expr.getC() ); }
	void visit ( const TDLConceptDataExists& expr ) override
		{ value = getMinValue ( 1, expr.getDR(), expr.getExpr() ); }
	void visit ( const TDLConceptDataForall& expr ) override
		{ value = getForallValue ( expr.getDR(), expr.getExpr() ); }
	void visit ( const TDLConceptDataMinCardinality& expr ) override
		{ value = getMinValue ( expr.getNumber(), expr.getDR(), expr.getExpr() ); }
	void visit ( const TDLConceptDataMaxCardinality& expr ) override
		{ value = getMaxValue ( expr.getNumber(), expr.getDR(), expr.getExpr() ); }
	void visit ( const TDLConceptDataExactCardinality& expr ) override
		{ value = getExactValue ( expr.getNumber(), expr.getDR(), expr.getExpr() ); }

	// object role expressions
	void visit ( const TDLObjectRoleName& expr ) override
		{ value = getEntityValue(expr.getEntity()); }
		// FaCT++ extension: equivalent to R(x,y) and C(x), so copy behaviour from ER.X
	void visit ( const TDLObjectRoleProjectionFrom& expr ) override
		{ value = getMinValue ( 1, expr.getOR(), expr.getC() ); }
		// FaCT++ extension: equivalent to R(x,y) and C(y), so copy behaviour from ER.X
	void visit ( const TDLObjectRoleProjectionInto& expr ) override
		{ value = getMinValue ( 1, expr.getOR(), expr.getC() ); }

	// data role expressions
	void visit ( const TDLDataRoleName& expr ) override
		{ value = getEntityValue(expr.getEntity()); }
}; // CardinalityEvaluatorBase

/// determine how many instances can an expression have;
/// all methods return minimal n such that expr\in C^{<= n}, n >= 0
class UpperBoundDirectEvaluator: public CardinalityEvaluatorBase
{
protected:	// methods
		/// helper for entities TODO: checks only C top-locality, not R
	int getEntityValue ( const TNamedEntity* entity ) override
		{ return getAllNoneUpper ( botCLocal() && nc(entity) ); }
		/// helper for All
	int getForallValue ( const TDLRoleExpression* R, const TDLExpression* C ) override
		{ return getAllNoneUpper ( isTopEquivalent(R) && isLowerGE ( getLowerBoundComplement(C), 1 ) ); }
		/// helper for things like >= m R.C
	int getMinValue ( unsigned int m, const TDLRoleExpression* R, const TDLExpression* C ) override
	{
		// m > 0 and...
		if ( m <= 0 )
			return noUpperValue();
		// R = \bot or...
		if ( isBotEquivalent(R) )
			return anyUpperValue();
		// C \in C^{<= m-1}
		return getAllNoneUpper ( isUpperLT ( getUpperBoundDirect(C), m ) );
	}
		/// helper for things like <= m R.C
	int getMaxValue ( unsigned int m, const TDLRoleExpression* R, const TDLExpression* C ) override
	{
		// R = \top and...
		if ( !isTopEquivalent(R) )
			return noUpperValue();
		// C\in C^{>= m+1}
		return getAllNoneUpper ( isLowerGT ( getLowerBoundDirect(C), m ) );
	}
		/// helper for = m R.C
	int getExactValue ( unsigned int m, const TDLRoleExpression* R, const TDLExpression* C ) override
	{
		// conjunction of Min and Max values
		return minUpperValue ( getMinValue ( m, R, C ), getMaxValue ( m, R, C ) );
	}

		/// helper for And
	template <typename C>
	int getAndValue ( const TDLNAryExpression<C>& expr )
	{
		// noUpperValue is a maximal element
		int min = noUpperValue();
		// we are looking for the minimal value here, use an appropriate helper
		for ( typename TDLNAryExpression<C>::iterator p = expr.begin(), p_end = expr.end(); p != p_end; ++p )
			min = minUpperValue ( min, getUpperBoundDirect(*p) );
		return min;
	}
		/// helper for Or
	template <typename C>
	int getOrValue ( const TDLNAryExpression<C>& expr )
	{
		int sum = 0;
		for ( typename TDLNAryExpression<C>::iterator p = expr.begin(), p_end = expr.end(); p != p_end; ++p )
		{
			int n = getUpperBoundDirect(*p);
			if ( n == noUpperValue() )
				return noUpperValue();
			sum += n;
		}
		return sum;
	}

public:		// interface
		/// init c'tor
	explicit UpperBoundDirectEvaluator ( const TSignature* s ) : CardinalityEvaluatorBase ( s, "UpperBoundDirect" ) {}

public:		// visitor implementation
	// make all other visit() methods from the base implementation visible
	using CardinalityEvaluatorBase::visit;
	// concept expressions
	void visit ( const TDLConceptTop& ) override { value = noUpperValue(); }
	void visit ( const TDLConceptBottom& ) override { value = anyUpperValue(); }
	void visit ( const TDLConceptNot& expr ) override { value = getUpperBoundComplement(expr.getC()); }
	void visit ( const TDLConceptAnd& expr ) override { value = getAndValue(expr); dumpValue(expr); }
	void visit ( const TDLConceptOr& expr ) override { value = getOrValue(expr); }
	void visit ( const TDLConceptOneOf& expr ) override { value = int(expr.size()); }
	void visit ( const TDLConceptObjectSelf& expr ) override { value = getAllNoneUpper(isBotEquivalent(expr.getOR())); }
	void visit ( const TDLConceptObjectValue& expr ) override { value = getAllNoneUpper(isBotEquivalent(expr.getOR())); }
	void visit ( const TDLConceptDataValue& expr ) override { value = getAllNoneUpper(isBotEquivalent(expr.getDR())); }

	// object role expressions
	// TODO!! properly process roles that are isTop/isBot
	void visit ( const TDLObjectRoleTop& ) override { value = noUpperValue(); }
	void visit ( const TDLObjectRoleBottom& ) override { value = anyUpperValue(); }
	void visit ( const TDLObjectRoleInverse& expr ) override { value = getUpperBoundDirect(expr.getOR()); }
	void visit ( const TDLObjectRoleChain& expr ) override
	{
		for ( const auto* arg : expr )
			if ( isBotEquivalent(arg) )
			{
				value = anyUpperValue();
				return;
			}
		value = noUpperValue();
	}

	// data role expressions
	void visit ( const TDLDataRoleTop& ) override { value = noUpperValue(); }
	void visit ( const TDLDataRoleBottom& ) override { value = anyUpperValue(); }

	// data expressions
	void visit ( const TDLDataTop& ) override { value = noUpperValue(); }
	void visit ( const TDLDataBottom& ) override { value = anyUpperValue(); }
	// FIXME!! not ready
	//void visit ( const TDLDataTypeName& ) override { isBotEq = false; }
	// FIXME!! not ready
	//void visit ( const TDLDataTypeRestriction& ) override { isBotEq = false; }
	void visit ( const TDLDataValue& ) override { value = 1; }
	void visit ( const TDLDataNot& expr ) override { value = getUpperBoundComplement(expr.getExpr()); }
	void visit ( const TDLDataAnd& expr ) override { value = getAndValue(expr); }
	void visit ( const TDLDataOr& expr ) override { value = getOrValue(expr); }
	void visit ( const TDLDataOneOf& expr ) override { value = (int)expr.size(); }
}; // UpperBoundDirectEvaluator

/// determine how many instances can a complement of expression have;
/// all methods return minimal n such that expr\in CC^{<= n}, n >= 0
class UpperBoundComplementEvaluator: public CardinalityEvaluatorBase
{
protected:	// methods
		/// helper for entities TODO: checks only C top-locality, not R
	int getEntityValue ( const TNamedEntity* entity ) override
		{ return getAllNoneUpper ( topCLocal() && nc(entity) ); }
		/// helper for All
	int getForallValue ( const TDLRoleExpression* R, const TDLExpression* C ) override
		{ return getAllNoneUpper ( isBotEquivalent(R) || isUpperLE ( getUpperBoundComplement(C), 0 ) ); }
		/// helper for things like >= m R.C
	int getMinValue ( unsigned int m, const TDLRoleExpression* R, const TDLExpression* C ) override
	{
		// m == 0 or...
		if ( m == 0 )
			return anyUpperValue();
		// R = \top and...
		if ( !isTopEquivalent(R) )
			return noUpperValue();
		// C \in C^{>= m}
		return getAllNoneUpper ( isLowerGE ( getLowerBoundDirect(C), m ) );
	}
		/// helper for things like <= m R.C
	int getMaxValue ( unsigned int m, const TDLRoleExpression* R, const TDLExpression* C ) override
	{
		// R = \bot or...
		if ( isBotEquivalent(R)  )
			return anyUpperValue();
		// C\in C^{<= m}
		return getAllNoneUpper ( isUpperLE ( getUpperBoundDirect(C), m ) );
	}
		/// helper for things like = m R.C
	int getExactValue ( unsigned int m, const TDLRoleExpression* R, const TDLExpression* C ) override
	{
		// here the minimal value between Mix and Max is an answer. The -1 case will be dealt with automagically
		return std::min ( getMinValue ( m, R, C ), getMaxValue ( m, R, C ) );
	}

		/// helper for And
	template <typename C>
	int getAndValue ( const TDLNAryExpression<C>& expr )
	{
		int sum = 0;
		for ( typename TDLNAryExpression<C>::iterator p = expr.begin(), p_end = expr.end(); p != p_end; ++p )
		{
			int n = getUpperBoundComplement(*p);
			if ( n == noUpperValue() )
				return noUpperValue();
			sum += n;
		}
		return sum;
	}
		/// helper for Or
	template <typename C>
	int getOrValue ( const TDLNAryExpression<C>& expr )
	{
		// noUpperValue is a maximal element
		int min = noUpperValue();
		// we are looking for the minimal value here, use an appropriate helper
		for ( typename TDLNAryExpression<C>::iterator p = expr.begin(), p_end = expr.end(); p != p_end; ++p )
			min = minUpperValue ( min, getUpperBoundDirect(*p) );
		return min;
	}

public:		// interface
		/// init c'tor
	explicit UpperBoundComplementEvaluator ( const TSignature* s ) : CardinalityEvaluatorBase ( s, "UpperBoundCompliment" ) {}

public:		// visitor interface
	// make all other visit() methods from the base implementation visible
	using CardinalityEvaluatorBase::visit;
	// concept expressions
	void visit ( const TDLConceptTop& ) override { value = anyUpperValue(); }
	void visit ( const TDLConceptBottom& ) override { value = noUpperValue(); }
	void visit ( const TDLConceptNot& expr ) override { value = getUpperBoundDirect(expr.getC()); }
	void visit ( const TDLConceptAnd& expr ) override { value = getAndValue(expr); dumpValue(expr); }
	void visit ( const TDLConceptOr& expr ) override { value = getOrValue(expr); }
	void visit ( const TDLConceptOneOf& ) override { value = noUpperValue(); }
	void visit ( const TDLConceptObjectSelf& expr ) override { value = getAllNoneUpper(isTopEquivalent(expr.getOR())); }
	void visit ( const TDLConceptObjectValue& expr ) override { value = getAllNoneUpper(isTopEquivalent(expr.getOR())); }
	void visit ( const TDLConceptDataValue& expr ) override { value = getAllNoneUpper(isTopEquivalent(expr.getDR())); }

	// object role expressions
	void visit ( const TDLObjectRoleTop& ) override { value = anyUpperValue(); }
	void visit ( const TDLObjectRoleBottom& ) override { value = noUpperValue(); }
	void visit ( const TDLObjectRoleInverse& expr ) override { value = getUpperBoundComplement(expr.getOR()); }
	void visit ( const TDLObjectRoleChain& expr ) override
	{
		for ( const auto* arg : expr )
			if ( !isTopEquivalent(arg) )
			{
				value = noUpperValue();
				return;
			}
		value = anyUpperValue();
	}

	// data role expressions
	void visit ( const TDLDataRoleTop& ) override { value = anyUpperValue(); }
	void visit ( const TDLDataRoleBottom& ) override { value = noUpperValue(); }

	// data expressions
	void visit ( const TDLDataTop& ) override { value = anyUpperValue(); }
	void visit ( const TDLDataBottom& ) override { value = noUpperValue(); }
	// negated datatype is a union of all other DTs that are infinite
	void visit ( const TDLDataTypeName& ) override { value = noUpperValue(); }
	// negated restriction include negated DT
	void visit ( const TDLDataTypeRestriction& ) override { value = noUpperValue(); }
	void visit ( const TDLDataValue& ) override { value = noUpperValue(); }
	void visit ( const TDLDataNot& expr ) override { value = getUpperBoundDirect(expr.getExpr()); }
	void visit ( const TDLDataAnd& expr ) override { value = getAndValue(expr); }
	void visit ( const TDLDataOr& expr ) override { value = getOrValue(expr); }
	void visit ( const TDLDataOneOf& ) override { value = noUpperValue(); }
}; // UpperBoundComplementEvaluator

/// determine how many instances can an expression have;
/// all methods return maximal n such that expr\in C^{>= n}, n >= 1
class LowerBoundDirectEvaluator: public CardinalityEvaluatorBase
{
protected:	// methods
		/// helper for entities TODO: checks only C top-locality, not R
	int getEntityValue ( const TNamedEntity* entity ) override
		{ return getOneNoneLower ( topCLocal() && nc(entity) ); }
		/// helper for All
	int getForallValue ( const TDLRoleExpression* R, const TDLExpression* C ) override
		{ return getOneNoneLower ( isBotEquivalent(R) || isUpperLE ( getUpperBoundComplement(C), 0 ) ); }
		/// helper for things like >= m R.C
	int getMinValue ( unsigned int m, const TDLRoleExpression* R, const TDLExpression* C ) override
	{
		// m == 0 or...
		if ( m == 0 )
			return anyLowerValue();
		// R = \top and...
		if ( !isTopEquivalent(R) )
			return noLowerValue();
		// C \in C^{>= m}
		return isLowerGE ( getLowerBoundDirect(C), m ) ? (int)m : noLowerValue();
	}
		/// helper for things like <= m R.C
	int getMaxValue ( unsigned int m, const TDLRoleExpression* R, const TDLExpression* C ) override
	{
		// R = \bot or...
		if ( isBotEquivalent(R) )
			return 1;
		// C\in C^{<= m}
		return getOneNoneLower ( isUpperLE ( getUpperBoundDirect(C), m ) );
	}
		/// helper for things like = m R.C
	int getExactValue ( unsigned int m, const TDLRoleExpression* R, const TDLExpression* C ) override
	{
		int min = getMinValue ( m, R, C ), max = getMaxValue ( m, R, C );
		// we need to take the lowest value
		if ( min == noLowerValue() || max == noLowerValue() )
			return noLowerValue();
		if ( min == anyLowerValue() )
			return max;
		if ( max == anyLowerValue() )
			return min;
		return std::min ( min, max );
	}

		/// helper for And
		// FIXME!! not done yet
	template <typename C>
	int getAndValue ( const TDLNAryExpression<C>& expr )
	{
		// return m - sumK, where
		bool foundC = false;	// true if found a conjunct that is in C^{>=}
		int foundM = 0;
		int mMax = 0, kMax = 0;	// the m- and k- values for the C_j with max m+k
		int sumK = 0;			// sum of all known k
		// 1st pass: check for none-case, deals with deterministic cases
		for ( typename TDLNAryExpression<C>::iterator p = expr.begin(), p_end = expr.end(); p != p_end; ++p )
		{
			int m = getLowerBoundDirect(*p);		// C_j \in C^{>= m}
			int k = getUpperBoundComplement(*p);	// C_j \in CC^{<= k}
			// note bound flip for K
			// case 0: if both aren't known then we don't know
			if ( m == noLowerValue() && k == noUpperValue() )
				return noLowerValue();
			// if only k exists then add it to k
			if ( m == noLowerValue() )
			{
//				if ( k == getAllValue() )	// we don't have any bound then
//					return getNoneValue();
				sumK += k;
				continue;
			}
			// if only m exists then set it to m
			if ( k == noUpperValue() )
			{
				if ( foundC )	// should not have 2 elements in C
					return noLowerValue();
				foundC = true;
				foundM = m;
				continue;
			}
			// here both k and m are values
			sumK += k;	// count k for the
			if ( k+m > kMax + mMax )
			{
				kMax = k;
				mMax = m;
			}
		}
		// here we know the best candidate for M, and only need to set it up
		if ( foundC )	// found during the deterministic case
		{
			foundM -= sumK;
			return foundM > 0 ? foundM : noLowerValue();
		}
		else	// no deterministic option; choose the best one
		{
			sumK -= kMax;
			mMax -= sumK;
			return mMax > 0 ? mMax : noLowerValue();
		}
	}
		/// helper for Or
	template <typename C>
	int getOrValue ( const TDLNAryExpression<C>& expr )
	{
		int max = noLowerValue();
		// we are looking for the maximal value here; ANY need to be special-cased
		for ( typename TDLNAryExpression<C>::iterator p = expr.begin(), p_end = expr.end(); p != p_end; ++p )
		{
			int n = getLowerBoundDirect(*p);
			if ( n == anyLowerValue() )
				return anyLowerValue();
			max = std::max ( max, n );
		}
		return max;
	}

public:		// interface
		/// init c'tor
	explicit LowerBoundDirectEvaluator ( const TSignature* s ) : CardinalityEvaluatorBase ( s, "LowerBoundDirect" ) {}

public:		// visitor interface
	// make all other visit() methods from the base implementation visible
	using CardinalityEvaluatorBase::visit;
	// concept expressions
	void visit ( const TDLConceptTop& ) override { value = 1; }
	void visit ( const TDLConceptBottom& ) override { value = noLowerValue(); }
	void visit ( const TDLConceptNot& expr ) override { value = getLowerBoundComplement(expr.getC()); }
	void visit ( const TDLConceptAnd& expr ) override { value = getAndValue(expr); dumpValue(expr); }
	void visit ( const TDLConceptOr& expr ) override { value = getOrValue(expr); }
	void visit ( const TDLConceptOneOf& expr ) override { value = getOneNoneLower(expr.size() > 0); }
	void visit ( const TDLConceptObjectSelf& expr ) override { value = getOneNoneLower(isTopEquivalent(expr.getOR())); }
	// FIXME!! differ from the paper
	void visit ( const TDLConceptObjectValue& expr ) override { value = getOneNoneLower(isTopEquivalent(expr.getOR())); }
	void visit ( const TDLConceptDataValue& expr ) override { value = getOneNoneLower(isTopEquivalent(expr.getDR())); }

	// object role expressions
	void visit ( const TDLObjectRoleTop& ) override { value = anyLowerValue(); }
	void visit ( const TDLObjectRoleBottom& ) override { value = noLowerValue(); }
	void visit ( const TDLObjectRoleInverse& expr ) override { value = getLowerBoundDirect(expr.getOR()); }
	void visit ( const TDLObjectRoleChain& expr ) override
	{
		for ( const auto* arg : expr )
			if ( !isTopEquivalent(arg) )
			{
				value = noLowerValue();
				return;
			}
		value = anyLowerValue();
	}

	// data role expressions
	void visit ( const TDLDataRoleTop& ) override { value = anyLowerValue(); }
	void visit ( const TDLDataRoleBottom& ) override { value = noLowerValue(); }

	// data expressions
	void visit ( const TDLDataTop& ) override { value = anyLowerValue(); }
	void visit ( const TDLDataBottom& ) override { value = noLowerValue(); }
	// negated datatype is a union of all other DTs that are infinite
	void visit ( const TDLDataTypeName& ) override { value = noLowerValue(); }
	// negated restriction include negated DT
	void visit ( const TDLDataTypeRestriction& ) override { value = noLowerValue(); }
	void visit ( const TDLDataValue& ) override { value = noLowerValue(); }
	void visit ( const TDLDataNot& expr ) override { value = getLowerBoundComplement(expr.getExpr()); }
	void visit ( const TDLDataAnd& expr ) override { value = getAndValue(expr); }
	void visit ( const TDLDataOr& expr ) override { value = getOrValue(expr); }
	void visit ( const TDLDataOneOf& expr ) override { value = getOneNoneLower(expr.size() > 0); }
}; // LowerBoundDirectEvaluator

/// determine how many instances can an expression have;
/// all methods return maximal n such that expr\in CC^{>= n}, n >= 1
class LowerBoundComplementEvaluator: public CardinalityEvaluatorBase
{
protected:	// methods
		/// helper for entities TODO: checks only C top-locality, not R
	int getEntityValue ( const TNamedEntity* entity ) override
		{ return getOneNoneLower ( botCLocal() && nc(entity) ); }
		/// helper for All
	int getForallValue ( const TDLRoleExpression* R, const TDLExpression* C ) override
		{ return getOneNoneLower ( isTopEquivalent(R) && isLowerGE ( getLowerBoundComplement(C), 1 ) ); }
		/// helper for things like >= m R.C
	int getMinValue ( unsigned int m, const TDLRoleExpression* R, const TDLExpression* C ) override
	{
		// m > 0 and...
		if ( m <= 0 )
			return noLowerValue();
		// R = \bot or...
		if ( isBotEquivalent(R) )
			return 1;
		// C \in C^{<= m-1}
		return getOneNoneLower ( isUpperLT ( getUpperBoundDirect(C), m ) );
	}
		/// helper for things like <= m R.C
	int getMaxValue ( unsigned int m, const TDLRoleExpression* R, const TDLExpression* C ) override
	{
		// R = \top and...
		if ( !isTopEquivalent(R) )
			return noLowerValue();
		// C\in C^{>= m+1}
		if ( isLowerGT ( getLowerBoundDirect(C), m ) )
			return (int)m+1;
		else
			return noLowerValue();
	}
		/// helper for things like = m R.C
	int getExactValue ( unsigned int m, const TDLRoleExpression* R, const TDLExpression* C ) override
	{
		// here the maximal value between Mix and Max is an answer. The -1 case will be dealt with automagically
		// because both min and max are between 0 and m+1
		return std::max ( getMinValue ( m, R, C ), getMaxValue ( m, R, C ) );
	}

		/// helper for And
	template <typename C>
	int getAndValue ( const TDLNAryExpression<C>& expr )
	{
		int max = noLowerValue();
		// we are looking for the maximal value here; ANY need to be special-cased
		for ( typename TDLNAryExpression<C>::iterator p = expr.begin(), p_end = expr.end(); p != p_end; ++p )
		{
			int n = getLowerBoundComplement(*p);
			if ( n == anyLowerValue() )
				return anyLowerValue();
			max = std::max ( max, n );
		}
		return max;
	}
		/// helper for Or
	template <typename C>
	int getOrValue ( const TDLNAryExpression<C>& expr )
	{
		// return m - sumK, where
		bool foundC = false;	// true if found a conjunct that is in C^{>=}
		int foundM = 0;
		int mMax = 0, kMax = 0;	// the m- and k- values for the C_j with max m+k
		int sumK = 0;			// sum of all known k
		// 1st pass: check for none-case, deals with deterministic cases
		for ( typename TDLNAryExpression<C>::iterator p = expr.begin(), p_end = expr.end(); p != p_end; ++p )
		{
			int m = getLowerBoundComplement(*p);	// C_j \in CC^{>= m}
			int k = getUpperBoundDirect(*p);		// C_j \in C^{<= k}
			// note bound flip for K
			// case 0: if both aren't known then we don't know
			if ( m == noLowerValue() && k == noUpperValue() )
				return noLowerValue();
			// if only k exists then add it to k
			if ( m == noLowerValue() )
			{
//				if ( k == getAllValue() )	// we don't have any bound then
//					return getNoneValue();
				sumK += k;
				continue;
			}
			// if only m exists then set it to m
			if ( k == noUpperValue() )
			{
				if ( foundC )	// should not have 2 elements in C
					return noLowerValue();
				foundC = true;
				foundM = m;
				continue;
			}
			// here both k and m are values
			sumK += k;	// count k for the
			if ( k+m > kMax + mMax )
			{
				kMax = k;
				mMax = m;
			}
		}
		// here we know the best candidate for M, and only need to set it up
		if ( foundC )	// found during the deterministic case
		{
			foundM -= sumK;
			return foundM > 0 ? foundM : noLowerValue();
		}
		else	// no deterministic option; choose the best one
		{
			sumK -= kMax;
			mMax -= sumK;
			return mMax > 0 ? mMax : noLowerValue();
		}
	}

public:		// interface
		/// init c'tor
	explicit LowerBoundComplementEvaluator ( const TSignature* s ) : CardinalityEvaluatorBase ( s, "LowerBoundComplement" ) {}

public:		// visitor implementation
	// make all other visit() methods from the base implementation visible
	using CardinalityEvaluatorBase::visit;
	// concept expressions
	void visit ( const TDLConceptTop& ) override { value = noLowerValue(); }
	void visit ( const TDLConceptBottom& ) override { value = 1; }
	void visit ( const TDLConceptNot& expr ) override { value = getLowerBoundDirect(expr.getC()); }
	void visit ( const TDLConceptAnd& expr ) override { value = getAndValue(expr); dumpValue(expr); }
	void visit ( const TDLConceptOr& expr ) override { value = getOrValue(expr); }
	void visit ( const TDLConceptOneOf& ) override { value = noLowerValue(); }
	void visit ( const TDLConceptObjectSelf& expr ) override { value = getOneNoneLower(isBotEquivalent(expr.getOR())); }
	void visit ( const TDLConceptObjectValue& expr ) override { value = getOneNoneLower(isBotEquivalent(expr.getOR())); }
	void visit ( const TDLConceptDataValue& expr ) override { value = getOneNoneLower(isBotEquivalent(expr.getDR())); }

	// object role expressions
	void visit ( const TDLObjectRoleTop& ) override { value = noLowerValue(); }
	void visit ( const TDLObjectRoleBottom& ) override { value = anyLowerValue(); }
	void visit ( const TDLObjectRoleInverse& expr ) override { value = getLowerBoundComplement(expr.getOR()); }
	void visit ( const TDLObjectRoleChain& expr ) override
	{
		for ( const auto* arg : expr )
			if ( isBotEquivalent(arg) )
			{
				value = anyLowerValue();
				return;
			}
		value = noLowerValue();
	}

	// data role expressions
	void visit ( const TDLDataRoleTop& ) override { value = noLowerValue(); }
	void visit ( const TDLDataRoleBottom& ) override { value = anyLowerValue(); }

	// data expressions
	void visit ( const TDLDataTop& ) override { value = noLowerValue(); }
	void visit ( const TDLDataBottom& ) override { value = anyLowerValue(); }
	// FIXME!! not ready
	//void visit ( const TDLDataTypeName& ) override { isBotEq = false; }
	// FIXME!! not ready
	//void visit ( const TDLDataTypeRestriction& ) override { isBotEq = false; }
	void visit ( const TDLDataValue& ) override { value = 1; }
	void visit ( const TDLDataNot& expr ) override { value = getLowerBoundDirect(expr.getExpr()); }
	void visit ( const TDLDataAnd& expr ) override { value = getAndValue(expr); }
	void visit ( const TDLDataOr& expr ) override { value = getOrValue(expr); }
	void visit ( const TDLDataOneOf& ) override { value = noLowerValue(); }
}; // LowerBoundComplementEvaluator

/// implementation of evaluation
inline int
CardinalityEvaluatorBase :: getUpperBoundDirect ( const TDLExpression& expr ) { return UBD->getValue(expr); }
/// implementation of evaluation
inline int
CardinalityEvaluatorBase :: getUpperBoundComplement ( const TDLExpression& expr ) { return UBC->getValue(expr); }
/// implementation of evaluation
inline int
CardinalityEvaluatorBase :: getLowerBoundDirect ( const TDLExpression& expr ) { return LBD->getValue(expr); }
/// implementation of evaluation
inline int
CardinalityEvaluatorBase :: getLowerBoundComplement ( const TDLExpression& expr ) { return LBC->getValue(expr); }

/// syntactic locality checker for DL axioms
class ExtendedSyntacticLocalityChecker: public GeneralSyntacticLocalityChecker
{
protected:	// members
	UpperBoundDirectEvaluator UBD;
	LowerBoundDirectEvaluator LBD;
	UpperBoundComplementEvaluator UBC;
	LowerBoundComplementEvaluator LBC;
#ifdef FPP_DEBUG_EXTENDED_LOCALITY
	TLISPExpressionPrinter lp;
#endif

protected:	// methods
		/// @return true iff EXPR is top equivalent
	bool isTopEquivalent ( const TDLExpression* expr ) override {
#	ifdef FPP_DEBUG_EXTENDED_LOCALITY
		std::cout << "Checking top locality of";
		expr->accept(lp);
		std::cout << "\n";
#	endif
		bool ret = UBC.getUpperBoundComplement(expr) == 0;
#	ifdef FPP_DEBUG_EXTENDED_LOCALITY
		std::cout << "top loc: " << ret << "\n";
#	endif
		return ret;
	}
		/// @return true iff EXPR is bottom equivalent
	bool isBotEquivalent ( const TDLExpression* expr ) override {
#	ifdef FPP_DEBUG_EXTENDED_LOCALITY
		std::cout << "Checking bot locality of";
		expr->accept(lp);
		std::cout << "\n";
#	endif
		bool ret = UBD.getUpperBoundDirect(expr) == 0;
#	ifdef FPP_DEBUG_EXTENDED_LOCALITY
		std::cout << "bot loc: " << ret << "\n";
#	endif
		return ret;
	}

public:		// interface
		/// init c'tor
	explicit ExtendedSyntacticLocalityChecker ( const TSignature* s )
		: GeneralSyntacticLocalityChecker(s)
		, UBD(s)
		, LBD(s)
		, UBC(s)
		, LBC(s)
#	ifdef FPP_DEBUG_EXTENDED_LOCALITY
		, lp(std::cout)
#	endif
	{
		UBD.setEvaluators ( &UBD, &LBD, &UBC, &LBC );
		LBD.setEvaluators ( &UBD, &LBD, &UBC, &LBC );
		UBC.setEvaluators ( &UBD, &LBD, &UBC, &LBC );
		LBC.setEvaluators ( &UBD, &LBD, &UBC, &LBC );
	}
}; // ExtendedSyntacticLocalityChecker

#endif
