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

#ifndef TDLEXPRESSION_H
#define TDLEXPRESSION_H

#include <vector>
#include <string>

#include "eFaCTPlusPlus.h"
#include "fpp_assert.h"
#include "tNameSet.h"

// forward declaration for all expression classes: necessary for the visitor pattern
class TDLExpression;

class  TDLConceptExpression;
class   TDLConceptTop;
class   TDLConceptBottom;
class   TDLConceptName;
class   TDLConceptNot;
class   TDLConceptAnd;
class   TDLConceptOr;
class   TDLConceptOneOf;
class   TDLConceptObjectRoleExpression;
class    TDLConceptObjectSelf;
class    TDLConceptObjectValue;
class    TDLConceptObjectRCExpression;
class     TDLConceptObjectExists;
class     TDLConceptObjectForall;
class     TDLConceptObjectCardinalityExpression;
class      TDLConceptObjectMinCardinality;
class      TDLConceptObjectMaxCardinality;
class      TDLConceptObjectExactCardinality;
class   TDLConceptDataRoleExpression;
class    TDLConceptDataValue;
class    TDLConceptDataRVExpression;
class     TDLConceptDataExists;
class     TDLConceptDataForall;
class     TDLConceptDataCardinalityExpression;
class      TDLConceptDataMinCardinality;
class      TDLConceptDataMaxCardinality;
class      TDLConceptDataExactCardinality;

class  TDLIndividualExpression;
class   TDLIndividualName;

class  TDLRoleExpression;
class   TDLObjectRoleComplexExpression;
class    TDLObjectRoleExpression;
class     TDLObjectRoleTop;
class     TDLObjectRoleBottom;
class     TDLObjectRoleName;
class     TDLObjectRoleInverse;
class    TDLObjectRoleChain;
class    TDLObjectRoleProjectionFrom;
class    TDLObjectRoleProjectionInto;

class   TDLDataRoleExpression;
class    TDLDataRoleTop;
class    TDLDataRoleBottom;
class    TDLDataRoleName;

class  TDLDataExpression;
class   TDLDataTop;
class   TDLDataBottom;
class   TDLDataTypeExpression;
class    TDLDataTypeName;
class    TDLDataTypeRestriction;
class   TDLDataValue;
class   TDLDataNot;
class   TDLDataAnd;
class   TDLDataOr;
class   TDLDataOneOf;
class   TDLFacetExpression;
class    TDLFacetMinInclusive;
class    TDLFacetMinExclusive;
class    TDLFacetMaxInclusive;
class    TDLFacetMaxExclusive;
//class    TDLFacetLength;
//class    TDLFacetMinLength;
//class    TDLFacetMaxLength;
//class    TDLFacetPattern;
//class    TDLFacetLangRange;

/// general visitor for DL expressions
class DLExpressionVisitor
{
public:		// visitor interface
	// concept expressions
	virtual void visit ( const TDLConceptTop& expr ) = 0;
	virtual void visit ( const TDLConceptBottom& expr ) = 0;
	virtual void visit ( const TDLConceptName& expr ) = 0;
	virtual void visit ( const TDLConceptNot& expr ) = 0;
	virtual void visit ( const TDLConceptAnd& expr ) = 0;
	virtual void visit ( const TDLConceptOr& expr ) = 0;
	virtual void visit ( const TDLConceptOneOf& expr ) = 0;
	virtual void visit ( const TDLConceptObjectSelf& expr ) = 0;
	virtual void visit ( const TDLConceptObjectValue& expr ) = 0;
	virtual void visit ( const TDLConceptObjectExists& expr ) = 0;
	virtual void visit ( const TDLConceptObjectForall& expr ) = 0;
	virtual void visit ( const TDLConceptObjectMinCardinality& expr ) = 0;
	virtual void visit ( const TDLConceptObjectMaxCardinality& expr ) = 0;
	virtual void visit ( const TDLConceptObjectExactCardinality& expr ) = 0;
	virtual void visit ( const TDLConceptDataValue& expr ) = 0;
	virtual void visit ( const TDLConceptDataExists& expr ) = 0;
	virtual void visit ( const TDLConceptDataForall& expr ) = 0;
	virtual void visit ( const TDLConceptDataMinCardinality& expr ) = 0;
	virtual void visit ( const TDLConceptDataMaxCardinality& expr ) = 0;
	virtual void visit ( const TDLConceptDataExactCardinality& expr ) = 0;

	// individual expressions
	virtual void visit ( const TDLIndividualName& expr ) = 0;

	// object role expressions
	virtual void visit ( const TDLObjectRoleTop& expr ) = 0;
	virtual void visit ( const TDLObjectRoleBottom& expr ) = 0;
	virtual void visit ( const TDLObjectRoleName& expr ) = 0;
	virtual void visit ( const TDLObjectRoleInverse& expr ) = 0;
	virtual void visit ( const TDLObjectRoleChain& expr ) = 0;
	virtual void visit ( const TDLObjectRoleProjectionFrom& expr ) = 0;
	virtual void visit ( const TDLObjectRoleProjectionInto& expr ) = 0;

	// data role expressions
	virtual void visit ( const TDLDataRoleTop& expr ) = 0;
	virtual void visit ( const TDLDataRoleBottom& expr ) = 0;
	virtual void visit ( const TDLDataRoleName& expr ) = 0;

	// data expressions
	virtual void visit ( const TDLDataTop& expr ) = 0;
	virtual void visit ( const TDLDataBottom& expr ) = 0;
	virtual void visit ( const TDLDataTypeName& expr ) = 0;
	virtual void visit ( const TDLDataTypeRestriction& expr ) = 0;
	virtual void visit ( const TDLDataValue& expr ) = 0;
	virtual void visit ( const TDLDataNot& expr ) = 0;
	virtual void visit ( const TDLDataAnd& expr ) = 0;
	virtual void visit ( const TDLDataOr& expr ) = 0;
	virtual void visit ( const TDLDataOneOf& expr ) = 0;

	// facets
	virtual void visit ( const TDLFacetMinInclusive& expr ) = 0;
	virtual void visit ( const TDLFacetMinExclusive& expr ) = 0;
	virtual void visit ( const TDLFacetMaxInclusive& expr ) = 0;
	virtual void visit ( const TDLFacetMaxExclusive& expr ) = 0;

	// other methods
	virtual ~DLExpressionVisitor() = default;
}; // DLExpressionVisitor

/// empty visitor for DL expressions implementation
class DLExpressionVisitorEmpty: public DLExpressionVisitor
{
public:		// visitor interface
	// concept expressions
	void visit ( const TDLConceptTop& ) override {}
	void visit ( const TDLConceptBottom& ) override {}
	void visit ( const TDLConceptName& ) override {}
	void visit ( const TDLConceptNot& ) override {}
	void visit ( const TDLConceptAnd& ) override {}
	void visit ( const TDLConceptOr& ) override {}
	void visit ( const TDLConceptOneOf& ) override {}
	void visit ( const TDLConceptObjectSelf& ) override {}
	void visit ( const TDLConceptObjectValue& ) override {}
	void visit ( const TDLConceptObjectExists& ) override {}
	void visit ( const TDLConceptObjectForall& ) override {}
	void visit ( const TDLConceptObjectMinCardinality& ) override {}
	void visit ( const TDLConceptObjectMaxCardinality& ) override {}
	void visit ( const TDLConceptObjectExactCardinality& ) override {}
	void visit ( const TDLConceptDataValue& ) override {}
	void visit ( const TDLConceptDataExists& ) override {}
	void visit ( const TDLConceptDataForall& ) override {}
	void visit ( const TDLConceptDataMinCardinality& ) override {}
	void visit ( const TDLConceptDataMaxCardinality& ) override {}
	void visit ( const TDLConceptDataExactCardinality& ) override {}

	// individual expressions
	void visit ( const TDLIndividualName& ) override {}

	// object role expressions
	void visit ( const TDLObjectRoleTop& ) override {}
	void visit ( const TDLObjectRoleBottom& ) override {}
	void visit ( const TDLObjectRoleName& ) override {}
	void visit ( const TDLObjectRoleInverse& ) override {}
	void visit ( const TDLObjectRoleChain& ) override {}
	void visit ( const TDLObjectRoleProjectionFrom& ) override {}
	void visit ( const TDLObjectRoleProjectionInto& ) override {}

	// data role expressions
	void visit ( const TDLDataRoleTop& ) override {}
	void visit ( const TDLDataRoleBottom& ) override {}
	void visit ( const TDLDataRoleName& ) override {}

	// data expressions
	void visit ( const TDLDataTop& ) override {}
	void visit ( const TDLDataBottom& ) override {}
	void visit ( const TDLDataTypeName& ) override {}
	void visit ( const TDLDataTypeRestriction& ) override {}
	void visit ( const TDLDataValue& ) override {}
	void visit ( const TDLDataNot& ) override {}
	void visit ( const TDLDataAnd& ) override {}
	void visit ( const TDLDataOr& ) override {}
	void visit ( const TDLDataOneOf& ) override {}

	// facets
	void visit ( const TDLFacetMinInclusive& ) override {}
	void visit ( const TDLFacetMinExclusive& ) override {}
	void visit ( const TDLFacetMaxInclusive& ) override {}
	void visit ( const TDLFacetMaxExclusive& ) override {}
}; // DLExpressionVisitorEmpty


/// base class for the DL expression, which include concept-, (data)role-, individual-, and data ones
class TDLExpression
{
public:		// interface
		/// empty c'tor
	TDLExpression() = default;
		/// empty d'tor: note that no deep delete is necessary as all the elements are RO
	virtual ~TDLExpression() = default;

		/// accept method for the visitor pattern
	virtual void accept ( DLExpressionVisitor& visitor ) const = 0;
}; // TDLExpression


//------------------------------------------------------------------
//	helper classes
//------------------------------------------------------------------

class TNamedEntry;

//------------------------------------------------------------------
///	named entity
//------------------------------------------------------------------
class TNamedEntity
{
protected:	// members
		/// name of the entity
	std::string Name;
		/// translated version of it
	TNamedEntry* entry = nullptr;

public:		// interface
		/// c'tor: initialise name
	explicit TNamedEntity ( const std::string& name ) : Name(name) {}
		/// empty d'tor
	virtual ~TNamedEntity() = default;

		/// get access to the name
	const char* getName ( void ) const { return Name.c_str(); }
		/// get access to the element itself
	const TNamedEntity* getEntity ( void ) const { return this; }

		/// set entry
	void setEntry ( TNamedEntry* e ) { entry = e; }
		/// get entry
	TNamedEntry* getEntry ( void ) const { return entry; }
}; // TNamedEntity

//------------------------------------------------------------------
///	concept argument
//------------------------------------------------------------------
class TConceptArg
{
protected:	// members
		/// concept argument
	const TDLConceptExpression* C;

public:		// interface
		/// init c'tor
	explicit TConceptArg ( const TDLConceptExpression* c ) : C(c) {}
		/// empty d'tor
	virtual ~TConceptArg() = default;

		/// get access to the argument
	const TDLConceptExpression* getC ( void ) const { return C; }
}; // TConceptArg

//------------------------------------------------------------------
///	individual argument
//------------------------------------------------------------------
class TIndividualArg
{
protected:	// members
		/// individual argument
	const TDLIndividualExpression* I;

public:		// interface
		/// init c'tor
	explicit TIndividualArg ( const TDLIndividualExpression* i ) : I(i) {}
		/// empty d'tor
	virtual ~TIndividualArg() = default;

		/// get access to the argument
	const TDLIndividualExpression* getI ( void ) const { return I; }
}; // TIndividualArg

//------------------------------------------------------------------
///	numerical argument
//------------------------------------------------------------------
class TNumberArg
{
protected:	// members
		/// number argument
	unsigned int N;

public:		// interface
		/// init c'tor
	explicit TNumberArg ( unsigned int n ) : N(n) {}
		/// empty d'tor
	virtual ~TNumberArg() = default;

		/// get access to the argument
	unsigned int getNumber ( void ) const { return N; }
}; // TNumberArg

//------------------------------------------------------------------
///	object role argument
//------------------------------------------------------------------
class TObjectRoleArg
{
protected:	// members
		/// object role argument
	const TDLObjectRoleExpression* OR;

public:		// interface
		/// init c'tor
	explicit TObjectRoleArg ( const TDLObjectRoleExpression* oR ) : OR(oR) {}
		/// empty d'tor
	virtual ~TObjectRoleArg() = default;

		/// get access to the argument
	const TDLObjectRoleExpression* getOR ( void ) const { return OR; }
}; // TObjectRoleArg

//------------------------------------------------------------------
///	data role argument
//------------------------------------------------------------------
class TDataRoleArg
{
protected:	// members
		/// data role argument
	const TDLDataRoleExpression* DR;

public:		// interface
		/// init c'tor
	explicit TDataRoleArg ( const TDLDataRoleExpression* dR ) : DR(dR) {}
		/// empty d'tor
	virtual ~TDataRoleArg() = default;

		/// get access to the argument
	const TDLDataRoleExpression* getDR ( void ) const { return DR; }
}; // TDataRoleArg

//------------------------------------------------------------------
///	data expression argument (parameterised with the exact type)
//------------------------------------------------------------------
template <typename TExpression>
class TDataExpressionArg
{
protected:	// members
		/// data expression argument
	const TExpression* Expr;

public:		// interface
		/// init c'tor
	explicit TDataExpressionArg ( const TExpression* expr ) : Expr(expr) {}
		/// empty d'tor
	virtual ~TDataExpressionArg() = default;

		/// get access to the argument
	const TExpression* getExpr ( void ) const { return Expr; }
}; // TDataExpressionArg

//------------------------------------------------------------------
///	general n-argument expression
//------------------------------------------------------------------
template <typename Argument>
class TDLNAryExpression
{
public:		// types
		/// base type
	typedef std::vector<const Argument*> ArgumentArray;
		/// RW iterator over base type
	typedef typename ArgumentArray::const_iterator iterator;
		/// input array type
	typedef std::vector<const TDLExpression*> ExpressionArray;
		/// RW input iterator
	typedef ExpressionArray::const_iterator i_iterator;

protected:	// members
		/// set of equivalent concept descriptions
	ArgumentArray Base;
		/// name for exception depending on class name and direction
	std::string EString;

protected:	// methods
		/// transform general expression into the argument one
	const Argument* transform ( const TDLExpression* arg ) const
	{
		const Argument* p = dynamic_cast<const Argument*>(arg);
		if ( p == nullptr )
			throw EFaCTPlusPlus(EString.c_str());
		return p;
	}

public:		// interface
		/// c'tor: build an error string
	TDLNAryExpression ( const char* typeName, const char* className )
	{
		EString = "Expected ";
		EString += typeName;
		EString += " argument in the '";
		EString += className;
		EString += "' expression";
	}
		/// empty d'tor
	virtual ~TDLNAryExpression() = default;

		/// @return true iff the expression has no elements
	bool empty ( void ) const { return Base.empty(); }
		/// @return number of elements
	size_t size ( void ) const { return Base.size(); }

	// add elements to the array

		/// add a single element to the array
	void add ( const TDLExpression* p ) { Base.push_back(transform(p)); }
		/// add a range to the array
	void add ( i_iterator b, i_iterator e )
	{
		for ( ; b != e; ++b )
			add(*b);
	}
		/// add a vector
	void add ( const ExpressionArray& v ) { add ( v.begin(), v.end() ); }

	// access to members

		/// RW begin iterator for array
	iterator begin ( void ) const { return Base.begin(); }
		/// RW end iterator for array
	iterator end ( void ) const { return Base.end(); }
}; // TDLNAryExpression


//------------------------------------------------------------------
//	concept expressions
//------------------------------------------------------------------


//------------------------------------------------------------------
///	general concept expression
//------------------------------------------------------------------
class TDLConceptExpression: public TDLExpression
{
public:		// interface
		/// accept method for the visitor pattern
	void accept ( DLExpressionVisitor& visitor ) const override = 0;
}; // TDLConceptExpression

//------------------------------------------------------------------
///	concept TOP expression
//------------------------------------------------------------------
class TDLConceptTop: public TDLConceptExpression
{
public:		// interface
		/// accept method for the visitor pattern
	void accept ( DLExpressionVisitor& visitor ) const override { visitor.visit(*this); }
}; // TDLConceptTop

//------------------------------------------------------------------
///	concept BOTTOM expression
//------------------------------------------------------------------
class TDLConceptBottom: public TDLConceptExpression
{
public:		// interface
		/// accept method for the visitor pattern
	void accept ( DLExpressionVisitor& visitor ) const override { visitor.visit(*this); }
}; // TDLConceptBottom

//------------------------------------------------------------------
///	named concept expression
//------------------------------------------------------------------
class TDLConceptName: public TDLConceptExpression, public TNamedEntity
{
public:		// interface
		/// init c'tor
	explicit TDLConceptName ( const std::string& name ) : TDLConceptExpression(), TNamedEntity(name) {}

		/// accept method for the visitor pattern
	void accept ( DLExpressionVisitor& visitor ) const override { visitor.visit(*this); }
}; // TDLConceptName

//------------------------------------------------------------------
///	concept NOT expression
//------------------------------------------------------------------
class TDLConceptNot: public TDLConceptExpression, public TConceptArg
{
public:		// interface
		/// init c'tor
	explicit TDLConceptNot ( const TDLConceptExpression* C )
		: TDLConceptExpression()
		, TConceptArg(C)
		{}

		/// accept method for the visitor pattern
	void accept ( DLExpressionVisitor& visitor ) const override { visitor.visit(*this); }
}; // TDLConceptNot

//------------------------------------------------------------------
///	concept AND expression
//------------------------------------------------------------------
class TDLConceptAnd: public TDLConceptExpression, public TDLNAryExpression<TDLConceptExpression>
{
public:		// interface
		/// init c'tor: create AND of expressions from the given array
	explicit TDLConceptAnd ( const ExpressionArray& v )
		: TDLConceptExpression()
		, TDLNAryExpression<TDLConceptExpression>("concept expression","AND")
	{
		add(v);
	}

		/// comparison for AND
	bool operator == ( const TDLConceptAnd& expr ) const
	{
		if ( size() != expr.size() )
			return false;
		for ( iterator p = begin(), q = expr.begin(), p_end = end(); p != p_end; ++p, ++q )
			if ( *p != *q )
				return false;
		return true;
	}
		/// accept method for the visitor pattern
	void accept ( DLExpressionVisitor& visitor ) const override { visitor.visit(*this); }
}; // TDLConceptAnd

//------------------------------------------------------------------
///	concept OR expression
//------------------------------------------------------------------
class TDLConceptOr: public TDLConceptExpression, public TDLNAryExpression<TDLConceptExpression>
{
public:		// interface
		/// init c'tor: create OR of expressions from the given array
	explicit TDLConceptOr ( const ExpressionArray& v )
		: TDLConceptExpression()
		, TDLNAryExpression<TDLConceptExpression>("concept expression","OR")
	{
		add(v);
	}

		/// accept method for the visitor pattern
	void accept ( DLExpressionVisitor& visitor ) const override { visitor.visit(*this); }
}; // TDLConceptOr

//------------------------------------------------------------------
///	concept one-of expression
//------------------------------------------------------------------
class TDLConceptOneOf: public TDLConceptExpression, public TDLNAryExpression<TDLIndividualName>
{
public:		// interface
		/// init c'tor: create one-of from individuals in the given array
	explicit TDLConceptOneOf ( const ExpressionArray& v )
		: TDLConceptExpression()
		, TDLNAryExpression<TDLIndividualName>("individual name","OneOf")
	{
		add(v);
	}

		/// accept method for the visitor pattern
	void accept ( DLExpressionVisitor& visitor ) const override { visitor.visit(*this); }
}; // TDLConceptOneOf

//------------------------------------------------------------------
///	general concept expression that contains an object role
//------------------------------------------------------------------
class TDLConceptObjectRoleExpression: public TDLConceptExpression, public TObjectRoleArg
{
public:		// interface
		/// init c'tor
	explicit TDLConceptObjectRoleExpression ( const TDLObjectRoleExpression* R )
		: TDLConceptExpression()
		, TObjectRoleArg(R)
		{}

		/// accept method for the visitor pattern
	void accept ( DLExpressionVisitor& visitor ) const override = 0;
}; // TDLConceptObjectRoleExpression

//------------------------------------------------------------------
///	concept self-ref expression
//------------------------------------------------------------------
class TDLConceptObjectSelf: public TDLConceptObjectRoleExpression
{
public:		// interface
		/// init c'tor
	explicit TDLConceptObjectSelf ( const TDLObjectRoleExpression* R )
		: TDLConceptObjectRoleExpression(R)
		{}

		/// accept method for the visitor pattern
	void accept ( DLExpressionVisitor& visitor ) const override { visitor.visit(*this); }
}; // TDLConceptObjectSelf

//------------------------------------------------------------------
///	concept some value restriction expression
//------------------------------------------------------------------
class TDLConceptObjectValue: public TDLConceptObjectRoleExpression, public TIndividualArg
{
public:		// interface
		/// init c'tor
	TDLConceptObjectValue ( const TDLObjectRoleExpression* R, const TDLIndividualExpression* I )
		: TDLConceptObjectRoleExpression(R)
		, TIndividualArg(I)
		{}

		/// accept method for the visitor pattern
	void accept ( DLExpressionVisitor& visitor ) const override { visitor.visit(*this); }
}; // TDLConceptObjectValue

//------------------------------------------------------------------
///	general concept expression that contains an object role and a class expression
//------------------------------------------------------------------
class TDLConceptObjectRCExpression: public TDLConceptObjectRoleExpression, public TConceptArg
{
public:		// interface
		/// init c'tor
	TDLConceptObjectRCExpression ( const TDLObjectRoleExpression* R, const TDLConceptExpression* C )
		: TDLConceptObjectRoleExpression(R)
		, TConceptArg(C)
		{}

		/// accept method for the visitor pattern
	void accept ( DLExpressionVisitor& visitor ) const override = 0;
}; // TDLConceptObjectRCExpression

//------------------------------------------------------------------
///	concept object existential restriction expression
//------------------------------------------------------------------
class TDLConceptObjectExists: public TDLConceptObjectRCExpression
{
public:		// interface
		/// init c'tor
	TDLConceptObjectExists ( const TDLObjectRoleExpression* R, const TDLConceptExpression* C )
		: TDLConceptObjectRCExpression(R,C)
		{}

		/// accept method for the visitor pattern
	void accept ( DLExpressionVisitor& visitor ) const override { visitor.visit(*this); }
}; // TDLConceptObjectExists

//------------------------------------------------------------------
///	concept object universal restriction expression
//------------------------------------------------------------------
class TDLConceptObjectForall: public TDLConceptObjectRCExpression
{
public:		// interface
		/// init c'tor
	TDLConceptObjectForall ( const TDLObjectRoleExpression* R, const TDLConceptExpression* C )
		: TDLConceptObjectRCExpression(R,C)
		{}

		/// accept method for the visitor pattern
	void accept ( DLExpressionVisitor& visitor ) const override { visitor.visit(*this); }
}; // TDLConceptObjectForall

//------------------------------------------------------------------
///	general object role cardinality expression
//------------------------------------------------------------------
class TDLConceptObjectCardinalityExpression: public TDLConceptObjectRCExpression, public TNumberArg
{
public:		// interface
		/// init c'tor
	TDLConceptObjectCardinalityExpression ( unsigned int n, const TDLObjectRoleExpression* R, const TDLConceptExpression* C )
		: TDLConceptObjectRCExpression(R,C)
		, TNumberArg(n)
		{}

		/// accept method for the visitor pattern
	void accept ( DLExpressionVisitor& visitor ) const override = 0;
}; // TDLConceptObjectCardinalityExpression

//------------------------------------------------------------------
///	concept object min cardinality expression
//------------------------------------------------------------------
class TDLConceptObjectMinCardinality: public TDLConceptObjectCardinalityExpression
{
public:		// interface
		/// init c'tor
	TDLConceptObjectMinCardinality ( unsigned int n, const TDLObjectRoleExpression* R, const TDLConceptExpression* C )
		: TDLConceptObjectCardinalityExpression(n,R,C)
		{}

		/// accept method for the visitor pattern
	void accept ( DLExpressionVisitor& visitor ) const override { visitor.visit(*this); }
}; // TDLConceptObjectMinCardinality

//------------------------------------------------------------------
///	concept object max cardinality expression
//------------------------------------------------------------------
class TDLConceptObjectMaxCardinality: public TDLConceptObjectCardinalityExpression
{
public:		// interface
		/// init c'tor
	TDLConceptObjectMaxCardinality ( unsigned int n, const TDLObjectRoleExpression* R, const TDLConceptExpression* C )
		: TDLConceptObjectCardinalityExpression(n,R,C)
		{}

		/// accept method for the visitor pattern
	void accept ( DLExpressionVisitor& visitor ) const override { visitor.visit(*this); }
}; // TDLConceptObjectMaxCardinality

//------------------------------------------------------------------
///	concept object exact cardinality expression
//------------------------------------------------------------------
class TDLConceptObjectExactCardinality: public TDLConceptObjectCardinalityExpression
{
public:		// interface
		/// init c'tor
	TDLConceptObjectExactCardinality ( unsigned int n, const TDLObjectRoleExpression* R, const TDLConceptExpression* C )
		: TDLConceptObjectCardinalityExpression(n,R,C)
		{}

		/// accept method for the visitor pattern
	void accept ( DLExpressionVisitor& visitor ) const override { visitor.visit(*this); }
}; // TDLConceptObjectExactCardinality

//------------------------------------------------------------------
///	general concept expression that contains an data role
//------------------------------------------------------------------
class TDLConceptDataRoleExpression: public TDLConceptExpression, public TDataRoleArg
{
public:		// interface
		/// init c'tor
	explicit TDLConceptDataRoleExpression ( const TDLDataRoleExpression* R )
		: TDLConceptExpression()
		, TDataRoleArg(R)
		{}

		/// accept method for the visitor pattern
	void accept ( DLExpressionVisitor& visitor ) const override = 0;
}; // TDLConceptDataRoleExpression

//------------------------------------------------------------------
///	concept some value restriction expression
//------------------------------------------------------------------
class TDLConceptDataValue: public TDLConceptDataRoleExpression, public TDataExpressionArg<TDLDataValue>
{
public:		// interface
		/// init c'tor
	TDLConceptDataValue ( const TDLDataRoleExpression* R, const TDLDataValue* V )
		: TDLConceptDataRoleExpression(R)
		, TDataExpressionArg<TDLDataValue>(V)
		{}

		/// accept method for the visitor pattern
	void accept ( DLExpressionVisitor& visitor ) const override { visitor.visit(*this); }
}; // TDLConceptDataValue

//------------------------------------------------------------------
///	general concept expression that contains an data role and a data expression
//------------------------------------------------------------------
class TDLConceptDataRVExpression: public TDLConceptDataRoleExpression, public TDataExpressionArg<TDLDataExpression>
{
public:		// interface
		/// init c'tor
	TDLConceptDataRVExpression ( const TDLDataRoleExpression* R, const TDLDataExpression* E )
		: TDLConceptDataRoleExpression(R)
		, TDataExpressionArg<TDLDataExpression>(E)
		{}

		/// accept method for the visitor pattern
	void accept ( DLExpressionVisitor& visitor ) const override = 0;
}; // TDLConceptDataRVExpression

//------------------------------------------------------------------
///	concept data existential restriction expression
//------------------------------------------------------------------
class TDLConceptDataExists: public TDLConceptDataRVExpression
{
public:		// interface
		/// init c'tor
	TDLConceptDataExists ( const TDLDataRoleExpression* R, const TDLDataExpression* E )
		: TDLConceptDataRVExpression(R,E)
		{}

		/// accept method for the visitor pattern
	void accept ( DLExpressionVisitor& visitor ) const override { visitor.visit(*this); }
}; // TDLConceptDataExists

//------------------------------------------------------------------
///	concept data universal restriction expression
//------------------------------------------------------------------
class TDLConceptDataForall: public TDLConceptDataRVExpression
{
public:		// interface
		/// init c'tor
	TDLConceptDataForall ( const TDLDataRoleExpression* R, const TDLDataExpression* E )
		: TDLConceptDataRVExpression(R,E)
		{}

		/// accept method for the visitor pattern
	void accept ( DLExpressionVisitor& visitor ) const override { visitor.visit(*this); }
}; // TDLConceptDataForall

//------------------------------------------------------------------
///	general data role cardinality expression
//------------------------------------------------------------------
class TDLConceptDataCardinalityExpression: public TDLConceptDataRVExpression, public TNumberArg
{
public:		// interface
		/// init c'tor
	TDLConceptDataCardinalityExpression ( unsigned int n, const TDLDataRoleExpression* R, const TDLDataExpression* E )
		: TDLConceptDataRVExpression(R,E)
		, TNumberArg(n)
		{}

		/// accept method for the visitor pattern
	void accept ( DLExpressionVisitor& visitor ) const override = 0;
}; // TDLConceptDataCardinalityExpression

//------------------------------------------------------------------
///	concept data min cardinality expression
//------------------------------------------------------------------
class TDLConceptDataMinCardinality: public TDLConceptDataCardinalityExpression
{
public:		// interface
		/// init c'tor
	TDLConceptDataMinCardinality ( unsigned int n, const TDLDataRoleExpression* R, const TDLDataExpression* E )
		: TDLConceptDataCardinalityExpression(n,R,E)
		{}

		/// accept method for the visitor pattern
	void accept ( DLExpressionVisitor& visitor ) const override { visitor.visit(*this); }
}; // TDLConceptDataMinCardinality

//------------------------------------------------------------------
///	concept data max cardinality expression
//------------------------------------------------------------------
class TDLConceptDataMaxCardinality: public TDLConceptDataCardinalityExpression
{
public:		// interface
		/// init c'tor
	TDLConceptDataMaxCardinality ( unsigned int n, const TDLDataRoleExpression* R, const TDLDataExpression* E )
		: TDLConceptDataCardinalityExpression(n,R,E)
		{}

		/// accept method for the visitor pattern
	void accept ( DLExpressionVisitor& visitor ) const override { visitor.visit(*this); }
}; // TDLConceptDataMaxCardinality

//------------------------------------------------------------------
///	concept data exact cardinality expression
//------------------------------------------------------------------
class TDLConceptDataExactCardinality: public TDLConceptDataCardinalityExpression
{
public:		// interface
		/// init c'tor
	TDLConceptDataExactCardinality ( unsigned int n, const TDLDataRoleExpression* R, const TDLDataExpression* E )
		: TDLConceptDataCardinalityExpression(n,R,E)
		{}

		/// accept method for the visitor pattern
	void accept ( DLExpressionVisitor& visitor ) const override { visitor.visit(*this); }
}; // TDLConceptDataExactCardinality


//------------------------------------------------------------------
//	individual expressions
//------------------------------------------------------------------


//------------------------------------------------------------------
///	general individual expression
//------------------------------------------------------------------
class TDLIndividualExpression: public TDLExpression
{
public:		// interface
		/// accept method for the visitor pattern
	void accept ( DLExpressionVisitor& visitor ) const override = 0;
}; // TDLIndividualExpression

//------------------------------------------------------------------
///	named individual expression
//------------------------------------------------------------------
class TDLIndividualName: public TDLIndividualExpression, public TNamedEntity
{
public:		// interface
		/// init c'tor
	explicit TDLIndividualName ( const std::string& name ) : TDLIndividualExpression(), TNamedEntity(name) {}

		/// accept method for the visitor pattern
	void accept ( DLExpressionVisitor& visitor ) const override { visitor.visit(*this); }
}; // TDLIndividualName


//------------------------------------------------------------------
//	role expressions
//------------------------------------------------------------------


//------------------------------------------------------------------
///	general role expression
//------------------------------------------------------------------
class TDLRoleExpression: public TDLExpression
{
public:		// interface
		/// accept method for the visitor pattern
	void accept ( DLExpressionVisitor& visitor ) const override = 0;
}; // TDLRoleExpression


//------------------------------------------------------------------
//	object role expressions
//------------------------------------------------------------------


//------------------------------------------------------------------
///	complex object role expression (general expression, role chain or projection)
//------------------------------------------------------------------
class TDLObjectRoleComplexExpression: public TDLRoleExpression
{
public:		// interface
		/// accept method for the visitor pattern
	void accept ( DLExpressionVisitor& visitor ) const override = 0;
}; // TDLObjectRoleComplexExpression

//------------------------------------------------------------------
///	general object role expression
//------------------------------------------------------------------
class TDLObjectRoleExpression: public TDLObjectRoleComplexExpression
{
public:		// interface
		/// accept method for the visitor pattern
	void accept ( DLExpressionVisitor& visitor ) const override = 0;
}; // TDLObjectRoleExpression

//------------------------------------------------------------------
///	object role TOP expression
//------------------------------------------------------------------
class TDLObjectRoleTop: public TDLObjectRoleExpression
{
public:		// interface
		/// accept method for the visitor pattern
	void accept ( DLExpressionVisitor& visitor ) const override { visitor.visit(*this); }
}; // TDLObjectRoleTop

//------------------------------------------------------------------
///	object role BOTTOM expression
//------------------------------------------------------------------
class TDLObjectRoleBottom: public TDLObjectRoleExpression
{
public:		// interface
		/// accept method for the visitor pattern
	void accept ( DLExpressionVisitor& visitor ) const override { visitor.visit(*this); }
}; // TDLObjectRoleBottom

//------------------------------------------------------------------
///	named object role expression
//------------------------------------------------------------------
class TDLObjectRoleName: public TDLObjectRoleExpression, public TNamedEntity
{
public:		// interface
		/// init c'tor
	explicit TDLObjectRoleName ( const std::string& name ) : TDLObjectRoleExpression(), TNamedEntity(name) {}

		/// accept method for the visitor pattern
	void accept ( DLExpressionVisitor& visitor ) const override { visitor.visit(*this); }
}; // TDLObjectRoleName

//------------------------------------------------------------------
///	inverse object role expression
//------------------------------------------------------------------
class TDLObjectRoleInverse: public TDLObjectRoleExpression, public TObjectRoleArg
{
public:		// interface
		/// init c'tor
	explicit TDLObjectRoleInverse ( const TDLObjectRoleExpression* R )
		: TDLObjectRoleExpression()
		, TObjectRoleArg(R)
		{}

		/// accept method for the visitor pattern
	void accept ( DLExpressionVisitor& visitor ) const override { visitor.visit(*this); }
}; // TDLObjectRoleInverse

//------------------------------------------------------------------
/// object role chain expression
//------------------------------------------------------------------
class TDLObjectRoleChain: public TDLObjectRoleComplexExpression, public TDLNAryExpression<TDLObjectRoleExpression>
{
public:		// interface
		/// init c'tor: create role chain from given array
	explicit TDLObjectRoleChain ( const ExpressionArray& v )
		: TDLObjectRoleComplexExpression()
		, TDLNAryExpression<TDLObjectRoleExpression>("object role expression","role chain")
	{
		add(v);
	}

		/// accept method for the visitor pattern
	void accept ( DLExpressionVisitor& visitor ) const override { visitor.visit(*this); }
}; // TDLObjectRoleChain

//------------------------------------------------------------------
///	object role projection from expression
//------------------------------------------------------------------
class TDLObjectRoleProjectionFrom
	: public TDLObjectRoleComplexExpression
	, public TObjectRoleArg
	, public TConceptArg
{
public:		// interface
		/// init c'tor
	TDLObjectRoleProjectionFrom ( const TDLObjectRoleExpression* R, const TDLConceptExpression* C )
		: TDLObjectRoleComplexExpression()
		, TObjectRoleArg(R)
		, TConceptArg(C)
		{}

		/// accept method for the visitor pattern
	void accept ( DLExpressionVisitor& visitor ) const override { visitor.visit(*this); }
}; // TDLObjectRoleProjectionFrom

//------------------------------------------------------------------
///	object role projection from expression
//------------------------------------------------------------------
class TDLObjectRoleProjectionInto
	: public TDLObjectRoleComplexExpression
	, public TObjectRoleArg
	, public TConceptArg
{
public:		// interface
		/// init c'tor
	TDLObjectRoleProjectionInto ( const TDLObjectRoleExpression* R, const TDLConceptExpression* C )
		: TDLObjectRoleComplexExpression()
		, TObjectRoleArg(R)
		, TConceptArg(C)
		{}

		/// accept method for the visitor pattern
	void accept ( DLExpressionVisitor& visitor ) const override { visitor.visit(*this); }
}; // TDLObjectRoleProjectionInto


//------------------------------------------------------------------
//	data role expressions
//------------------------------------------------------------------


//------------------------------------------------------------------
///	general data role expression
//------------------------------------------------------------------
class TDLDataRoleExpression: public TDLRoleExpression
{
public:		// interface
		/// accept method for the visitor pattern
	void accept ( DLExpressionVisitor& visitor ) const override = 0;
}; // TDLDataRoleExpression

//------------------------------------------------------------------
///	data role TOP expression
//------------------------------------------------------------------
class TDLDataRoleTop: public TDLDataRoleExpression
{
public:		// interface
		/// accept method for the visitor pattern
	void accept ( DLExpressionVisitor& visitor ) const override { visitor.visit(*this); }
}; // TDLDataRoleTop

//------------------------------------------------------------------
///	data role BOTTOM expression
//------------------------------------------------------------------
class TDLDataRoleBottom: public TDLDataRoleExpression
{
public:		// interface
		/// accept method for the visitor pattern
	void accept ( DLExpressionVisitor& visitor ) const override { visitor.visit(*this); }
}; // TDLDataRoleBottom

//------------------------------------------------------------------
///	named data role expression
//------------------------------------------------------------------
class TDLDataRoleName: public TDLDataRoleExpression, public TNamedEntity
{
public:		// interface
		/// init c'tor
	explicit TDLDataRoleName ( const std::string& name ) : TDLDataRoleExpression(), TNamedEntity(name) {}

		/// accept method for the visitor pattern
	void accept ( DLExpressionVisitor& visitor ) const override { visitor.visit(*this); }
}; // TDLDataRoleName


//------------------------------------------------------------------
//	data expressions
//------------------------------------------------------------------


//------------------------------------------------------------------
///	general data expression
//------------------------------------------------------------------
class TDLDataExpression: public TDLExpression
{
public:		// interface
		/// accept method for the visitor pattern
	void accept ( DLExpressionVisitor& visitor ) const override = 0;
}; // TDLDataExpression

//------------------------------------------------------------------
///	data TOP expression
//------------------------------------------------------------------
class TDLDataTop: public TDLDataExpression
{
public:		// interface
		/// accept method for the visitor pattern
	void accept ( DLExpressionVisitor& visitor ) const override { visitor.visit(*this); }
}; // TDLDataTop

//------------------------------------------------------------------
///	data BOTTOM expression
//------------------------------------------------------------------
class TDLDataBottom: public TDLDataExpression
{
public:		// interface
		/// accept method for the visitor pattern
	void accept ( DLExpressionVisitor& visitor ) const override { visitor.visit(*this); }
}; // TDLDataBottom

//------------------------------------------------------------------
///	general data type expression
//------------------------------------------------------------------
class TDLDataTypeExpression: public TDLDataExpression
{
public:		// interface
		/// accept method for the visitor pattern
	void accept ( DLExpressionVisitor& visitor ) const override = 0;
}; // TDLDataTypeExpression

//------------------------------------------------------------------
///	restricted data type expression
//------------------------------------------------------------------
class TDLDataTypeRestriction: public TDLDataTypeExpression, public TDataExpressionArg<TDLDataTypeName>, public TDLNAryExpression<TDLFacetExpression>
{
public:		// interface
		/// init c'tor
	explicit TDLDataTypeRestriction ( const TDLDataTypeName* T )
		: TDLDataTypeExpression()
		, TDataExpressionArg<TDLDataTypeName>(T)
		, TDLNAryExpression<TDLFacetExpression>("facet expression","Datatype restriction")
		{}

		/// accept method for the visitor pattern
	void accept ( DLExpressionVisitor& visitor ) const override { visitor.visit(*this); }
}; // TDLDataTypeRestriction

//------------------------------------------------------------------
///	data value expression
//------------------------------------------------------------------
class TDLDataValue: public TDLDataExpression, public TNamedEntity, public TDataExpressionArg<TDLDataTypeExpression>
{
public:		// interface
		/// fake c'tor (to make TNameSet happy); shouldn't be called
	explicit TDLDataValue ( const std::string& value )
		: TDLDataExpression()
		, TNamedEntity(value)
		, TDataExpressionArg<TDLDataTypeExpression>(nullptr)
		{ fpp_unreachable(); }
		/// init c'tor
	TDLDataValue ( const std::string& value, const TDLDataTypeExpression* T )
		: TDLDataExpression()
		, TNamedEntity(value)
		, TDataExpressionArg<TDLDataTypeExpression>(T)
		{}

		/// accept method for the visitor pattern
	void accept ( DLExpressionVisitor& visitor ) const override { visitor.visit(*this); }
}; // TDLDataValue

//------------------------------------------------------------------
///	data NOT expression
//------------------------------------------------------------------
class TDLDataNot: public TDLDataExpression, public TDataExpressionArg<TDLDataExpression>
{
public:		// interface
		/// init c'tor
	explicit TDLDataNot ( const TDLDataExpression* E )
		: TDLDataExpression()
		, TDataExpressionArg<TDLDataExpression>(E)
		{}

		/// accept method for the visitor pattern
	void accept ( DLExpressionVisitor& visitor ) const override { visitor.visit(*this); }
}; // TDLDataNot

//------------------------------------------------------------------
///	data AND expression
//------------------------------------------------------------------
class TDLDataAnd: public TDLDataExpression, public TDLNAryExpression<TDLDataExpression>
{
public:		// interface
		/// init c'tor: create AND of expressions from the given array
	explicit TDLDataAnd ( const ExpressionArray& v )
		: TDLDataExpression()
		, TDLNAryExpression<TDLDataExpression>("data expression","data AND")
	{
		add(v);
	}

		/// accept method for the visitor pattern
	void accept ( DLExpressionVisitor& visitor ) const override { visitor.visit(*this); }
}; // TDLDataAnd

//------------------------------------------------------------------
///	data OR expression
//------------------------------------------------------------------
class TDLDataOr: public TDLDataExpression, public TDLNAryExpression<TDLDataExpression>
{
public:		// interface
		/// init c'tor: create OR of expressions from the given array
	explicit TDLDataOr ( const ExpressionArray& v )
		: TDLDataExpression()
		, TDLNAryExpression<TDLDataExpression>("data expression","data OR")
	{
		add(v);
	}

		/// accept method for the visitor pattern
	void accept ( DLExpressionVisitor& visitor ) const override { visitor.visit(*this); }
}; // TDLDataOr

//------------------------------------------------------------------
///	data one-of expression
//------------------------------------------------------------------
class TDLDataOneOf: public TDLDataExpression, public TDLNAryExpression<TDLDataValue>
{
public:		// interface
		/// init c'tor: create one-of from individuals in the given array
	explicit TDLDataOneOf ( const ExpressionArray& v )
		: TDLDataExpression()
		, TDLNAryExpression<TDLDataValue>("data value","data OneOf")
	{
		add(v);
	}

		/// accept method for the visitor pattern
	void accept ( DLExpressionVisitor& visitor ) const override { visitor.visit(*this); }
}; // TDLDataOneOf

//------------------------------------------------------------------
///	general data facet expression
//------------------------------------------------------------------
class TDLFacetExpression: public TDLDataExpression, public TDataExpressionArg<TDLDataValue>
{
public:		// interface
		/// init c'tor: create facet from a given value V
	explicit TDLFacetExpression ( const TDLDataValue* V )
		: TDLDataExpression()
		, TDataExpressionArg<TDLDataValue>(V)
		{}

		/// accept method for the visitor pattern
	void accept ( DLExpressionVisitor& visitor ) const override = 0;
}; // TDLFacetExpression

//------------------------------------------------------------------
///	min-inclusive facet expression
//------------------------------------------------------------------
class TDLFacetMinInclusive: public TDLFacetExpression
{
public:		// interface
		/// init c'tor
	explicit TDLFacetMinInclusive ( const TDLDataValue* V ) : TDLFacetExpression(V) {}

		/// accept method for the visitor pattern
	void accept ( DLExpressionVisitor& visitor ) const override { visitor.visit(*this); }
}; // TDLFacetMinInclusive

//------------------------------------------------------------------
///	min-exclusive facet expression
//------------------------------------------------------------------
class TDLFacetMinExclusive: public TDLFacetExpression
{
public:		// interface
		/// init c'tor
	explicit TDLFacetMinExclusive ( const TDLDataValue* V ) : TDLFacetExpression(V) {}

		/// accept method for the visitor pattern
	void accept ( DLExpressionVisitor& visitor ) const override { visitor.visit(*this); }
}; // TDLFacetMinExclusive

//------------------------------------------------------------------
///	max-inclusive facet expression
//------------------------------------------------------------------
class TDLFacetMaxInclusive: public TDLFacetExpression
{
public:		// interface
		/// init c'tor
	explicit TDLFacetMaxInclusive ( const TDLDataValue* V ) : TDLFacetExpression(V) {}

		/// accept method for the visitor pattern
	void accept ( DLExpressionVisitor& visitor ) const override { visitor.visit(*this); }
}; // TDLFacetMaxInclusive

//------------------------------------------------------------------
///	max-exclusive facet expression
//------------------------------------------------------------------
class TDLFacetMaxExclusive: public TDLFacetExpression
{
public:		// interface
		/// init c'tor
	explicit TDLFacetMaxExclusive ( const TDLDataValue* V ) : TDLFacetExpression(V) {}

		/// accept method for the visitor pattern
	void accept ( DLExpressionVisitor& visitor ) const override { visitor.visit(*this); }
}; // TDLFacetMaxExclusive


// data type is defined here as they are more complex than the rest

//------------------------------------------------------------------
/// data type implementation for the DL expressions
//------------------------------------------------------------------
class TDLDataType
{
protected:	// classes
		/// class to create a new entry with a given data type as a parameter
	class DVCreator: public TNameCreator<TDLDataValue>
	{
	protected:	// members
			/// type for all the values
		const TDLDataTypeExpression* type;
	public:		// interface
			/// init c'tor
		explicit DVCreator ( const TDLDataTypeExpression* t ) : type(t) {}
			/// create new value of a given type
		TDLDataValue* makeEntry ( const std::string& name ) const override { return new TDLDataValue(name,type); }
	}; // DVCreator

protected:	// members
		/// all the values of the datatype
	TNameSet<TDLDataValue> Values;

public:		// interface
		/// empty c'tor
	explicit TDLDataType ( const TDLDataTypeExpression* type ) : Values(new DVCreator(type)) {}
		/// empty d'tor
	virtual ~TDLDataType() = default;

		/// get new data value of the given type
	const TDLDataValue* getValue ( const std::string& name ) { return Values.insert(name); }
}; // TDLDataType


//------------------------------------------------------------------
///	named data type expression
//------------------------------------------------------------------
class TDLDataTypeName: public TDLDataTypeExpression, public TDLDataType, public TNamedEntity
{
public:		// interface
		/// init c'tor
	explicit TDLDataTypeName ( const std::string& name ) : TDLDataTypeExpression(), TDLDataType(this), TNamedEntity(name) {}

		/// accept method for the visitor pattern
	void accept ( DLExpressionVisitor& visitor ) const override { visitor.visit(*this); }
}; // TDLDataTypeName

/// @return named data type that is the most basic datatype underlying TYPE
inline TDLDataTypeName*
getBasicDataType ( TDLDataTypeExpression* type )
{
	TDLDataTypeName* ret = dynamic_cast<TDLDataTypeName*>(type);
	if ( ret == nullptr )
	{
		TDLDataTypeRestriction* hostType = dynamic_cast<TDLDataTypeRestriction*>(type);
		fpp_assert ( hostType != nullptr );
		ret = const_cast<TDLDataTypeName*>(hostType->getExpr());
	}
	return ret;
}

#endif
