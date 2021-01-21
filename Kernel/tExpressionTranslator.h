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

#ifndef TEXPRESSIONTRANSLATOR_H
#define TEXPRESSIONTRANSLATOR_H

#include "tDLExpression.h"
#include "tDataTypeManager.h"
#include "dlTBox.h"

class TExpressionTranslator: public DLExpressionVisitor
{
protected:	// members
		/// tree corresponding to a processing expression
	DLTree* tree = nullptr;
		/// TBox to get access to the named entities
	TBox& KB;
		/// signature of non-trivial entities; used in semantic locality checkers only
	const TSignature* sig = nullptr;

#define THROW_UNSUPPORTED(name) \
	throw EFaCTPlusPlus("Unsupported expression '" name "' in transformation")

protected:	// methods
		/// create DLTree of given TAG and named ENTRY; set the entry's ENTITY if necessary
	TNamedEntry* matchEntry ( TNamedEntry* entry, const TNamedEntity* entity )
	{
		entry->setEntity(entity);
		const_cast<TNamedEntity*>(entity)->setEntry(entry);
		return entry;
	}
		/// @return true iff ENTRY is not in signature
	bool nc ( const TNamedEntity* entity ) const { return unlikely(sig != nullptr) && !sig->contains(entity); }
		/// get role expression by given ENTITY from given RM
	TNamedEntry* getRoleEntry ( RoleMaster& RM, const TNamedEntity* entity )
	{
		// return appropriate constant if the role is not in signature
		if ( nc(entity) )
			return sig->topRLocal() ? RM.getTopRole() : RM.getBotRole();
		// get (and set up if necessary) entry by given entity
		auto role = entity->getEntry();
		if ( role == nullptr )
			role = matchEntry ( RM.ensureRoleName(entity->getName()), entity );
		return role;
	}

public:		// interface
		/// empty c'tor
	explicit TExpressionTranslator ( TBox& kb ) : KB(kb) {}
		/// empty d'tor
	~TExpressionTranslator() override { deleteTree(tree); }

		/// get (single) access to the tree
	operator DLTree* ( void ) { DLTree* ret = tree; tree = nullptr; return ret; }
		/// set internal signature to a given signature S
	void setSignature ( const TSignature* s ) { sig = s; }

public:		// visitor interface
	// concept expressions
	void visit ( const TDLConceptTop& ) override { tree = createTop(); }
	void visit ( const TDLConceptBottom& ) override { tree = createBottom(); }
	void visit ( const TDLConceptName& expr ) override
	{
		if ( nc(&expr) )
			tree = sig->topCLocal() ? createTop() : createBottom();
		else
		{
			TNamedEntry* entry = expr.getEntry();
			if ( entry == nullptr )
				entry = matchEntry ( KB.getConcept(expr.getName()), &expr );
			tree = createEntry(CNAME,entry);
		}
	}
	void visit ( const TDLConceptNot& expr ) override { expr.getC()->accept(*this); tree = createSNFNot(*this); }
	void visit ( const TDLConceptAnd& expr ) override
	{
		DLTree* acc = createTop();

		for ( const auto* arg : expr )
		{
			arg->accept(*this);
			acc = createSNFAnd ( acc, *this );
		}

		tree = acc;
	}
	void visit ( const TDLConceptOr& expr ) override
	{
		DLTree* acc = createBottom();

		for ( const auto* arg : expr )
		{
			arg->accept(*this);
			acc = createSNFOr ( acc, *this );
		}

		tree = acc;
	}
	void visit ( const TDLConceptOneOf& expr ) override
	{
		DLTree* acc = createBottom();

		for ( const auto* arg : expr )
		{
			arg->accept(*this);
			acc = createSNFOr ( acc, *this );
		}

		tree = acc;
	}
	void visit ( const TDLConceptObjectSelf& expr ) override
	{
		expr.getOR()->accept(*this);
		tree = createSNFSelf(*this);
	}
	void visit ( const TDLConceptObjectValue& expr ) override
	{
		expr.getOR()->accept(*this);
		DLTree* R = *this;
		expr.getI()->accept(*this);
		tree = createSNFExists ( R, *this );
	}
	void visit ( const TDLConceptObjectExists& expr ) override
	{
		expr.getOR()->accept(*this);
		DLTree* R = *this;
		expr.getC()->accept(*this);
		tree = createSNFExists ( R, *this );
	}
	void visit ( const TDLConceptObjectForall& expr ) override
	{
		expr.getOR()->accept(*this);
		DLTree* R = *this;
		expr.getC()->accept(*this);
		tree = createSNFForall ( R, *this );
	}
	void visit ( const TDLConceptObjectMinCardinality& expr ) override
	{
		expr.getOR()->accept(*this);
		DLTree* R = *this;
		expr.getC()->accept(*this);
		tree = createSNFGE ( expr.getNumber(), R, *this );
	}
	void visit ( const TDLConceptObjectMaxCardinality& expr ) override
	{
		expr.getOR()->accept(*this);
		DLTree* R = *this;
		expr.getC()->accept(*this);
		tree = createSNFLE ( expr.getNumber(), R, *this );
	}
	void visit ( const TDLConceptObjectExactCardinality& expr ) override
	{
		expr.getOR()->accept(*this);
		DLTree* R = *this;
		expr.getC()->accept(*this);
		DLTree* C = *this;
		DLTree* LE = createSNFLE ( expr.getNumber(), clone(R), clone(C) );
		DLTree* GE = createSNFGE ( expr.getNumber(), R, C );
		tree = createSNFAnd ( GE, LE );
	}
	void visit ( const TDLConceptDataValue& expr ) override
	{
		expr.getDR()->accept(*this);
		DLTree* R = *this;
		expr.getExpr()->accept(*this);
		tree = createSNFExists ( R, *this );
	}
	void visit ( const TDLConceptDataExists& expr ) override
	{
		expr.getDR()->accept(*this);
		DLTree* R = *this;
		expr.getExpr()->accept(*this);
		tree = createSNFExists ( R, *this );
	}
	void visit ( const TDLConceptDataForall& expr ) override
	{
		expr.getDR()->accept(*this);
		DLTree* R = *this;
		expr.getExpr()->accept(*this);
		tree = createSNFForall ( R, *this );
	}
	void visit ( const TDLConceptDataMinCardinality& expr ) override
	{
		expr.getDR()->accept(*this);
		DLTree* R = *this;
		expr.getExpr()->accept(*this);
		tree = createSNFGE ( expr.getNumber(), R, *this );
	}
	void visit ( const TDLConceptDataMaxCardinality& expr ) override
	{
		expr.getDR()->accept(*this);
		DLTree* R = *this;
		expr.getExpr()->accept(*this);
		tree = createSNFLE ( expr.getNumber(), R, *this );
	}
	void visit ( const TDLConceptDataExactCardinality& expr ) override
	{
		expr.getDR()->accept(*this);
		DLTree* R = *this;
		expr.getExpr()->accept(*this);
		DLTree* C = *this;
		DLTree* LE = createSNFLE ( expr.getNumber(), clone(R), clone(C) );
		DLTree* GE = createSNFGE ( expr.getNumber(), R, C );
		tree = createSNFAnd ( GE, LE );
	}

	// individual expressions
	void visit ( const TDLIndividualName& expr ) override
	{
		TNamedEntry* entry = expr.getEntry();
		if ( entry == nullptr )
			entry = matchEntry ( KB.getIndividual(expr.getName()), &expr );
		tree = createEntry(INAME,entry);
	}

	// object role expressions
	void visit ( const TDLObjectRoleTop& ) override { THROW_UNSUPPORTED("top object role"); }
	void visit ( const TDLObjectRoleBottom& ) override { THROW_UNSUPPORTED("bottom object role"); }
	void visit ( const TDLObjectRoleName& expr ) override
	{
		auto role = getRoleEntry ( KB.getORM(), &expr );
		tree = createEntry(RNAME,role);
	}
	void visit ( const TDLObjectRoleInverse& expr ) override { expr.getOR()->accept(*this); tree = createInverse(*this); }
	void visit ( const TDLObjectRoleChain& expr ) override
	{
		TDLObjectRoleChain::iterator p = expr.begin(), p_end = expr.end();
		if ( p == p_end )
			THROW_UNSUPPORTED("empty role chain");

		(*p)->accept(*this);
		DLTree* acc = *this;

		while ( ++p != p_end )
		{
			(*p)->accept(*this);
			acc = new DLTree ( TLexeme(RCOMPOSITION), acc, *this );
		}

		tree = acc;
	}
	void visit ( const TDLObjectRoleProjectionFrom& expr ) override
	{
		expr.getOR()->accept(*this);
		DLTree* R = *this;
		expr.getC()->accept(*this);
		DLTree* C = *this;
		tree = new DLTree ( TLexeme(PROJFROM), R, C );
	}
	void visit ( const TDLObjectRoleProjectionInto& expr ) override
	{
		expr.getOR()->accept(*this);
		DLTree* R = *this;
		expr.getC()->accept(*this);
		DLTree* C = *this;
		tree = new DLTree ( TLexeme(PROJINTO), R, C );
	}

	// data role expressions
	void visit ( const TDLDataRoleTop& ) override { THROW_UNSUPPORTED("top data role");  }
	void visit ( const TDLDataRoleBottom& ) override { THROW_UNSUPPORTED("bottom data role"); }
	void visit ( const TDLDataRoleName& expr ) override
	{
		auto role = getRoleEntry ( KB.getDRM(), &expr );
		tree = createEntry(DNAME,role);
	}

	// data expressions
	void visit ( const TDLDataTop& ) override { tree = createTop(); }
	void visit ( const TDLDataBottom& ) override { tree = createBottom(); }
	void visit ( const TDLDataTypeName& expr ) override
	{
		DataTypeCenter& DTC = KB.getDataTypeCenter();
		if ( isStrDataType(&expr) )
			tree = DTC.getStringType();
		else if ( isIntDataType(&expr) )
			tree = DTC.getNumberType();
		else if ( isRealDataType(&expr) )
			tree = DTC.getRealType();
		else if ( isBoolDataType(&expr) )
			tree = DTC.getBoolType();	// get-by-name("bool")??
		else if ( isTimeDataType(&expr) )
			tree = DTC.getTimeType();
		else
			THROW_UNSUPPORTED("data type name");
	}
	void visit ( const TDLDataTypeRestriction& expr ) override
	{
		DLTree* acc = createTop();

		for ( const auto* arg : expr )
		{
			arg->accept(*this);
			acc = createSNFAnd ( acc, *this );
		}

		tree = acc;
	}
	void visit ( const TDLDataValue& expr ) override
	{
		expr.getExpr()->accept(*this);	// process type
		DLTree* type = *this;
		tree = KB.getDataTypeCenter().getDataValue(expr.getName(),type);
		deleteTree(type);
	}
	void visit ( const TDLDataNot& expr ) override { expr.getExpr()->accept(*this); tree = createSNFNot(*this); }
	void visit ( const TDLDataAnd& expr ) override
	{
		DLTree* acc = createTop();

		for ( const auto* arg : expr )
		{
			arg->accept(*this);
			acc = createSNFAnd ( acc, *this );
		}

		tree = acc;
	}
	void visit ( const TDLDataOr& expr ) override
	{
		DLTree* acc = createBottom();

		for ( const auto* arg : expr )
		{
			arg->accept(*this);
			acc = createSNFOr ( acc, *this );
		}

		tree = acc;
	}
	void visit ( const TDLDataOneOf& expr ) override
	{
		DLTree* acc = createBottom();

		for ( const auto* arg : expr )
		{
			arg->accept(*this);
			acc = createSNFOr ( acc, *this );
		}

		tree = acc;
	}

	// facets
	void visit ( const TDLFacetMinInclusive& expr ) override
	{
		expr.getExpr()->accept(*this);
		tree = KB.getDataTypeCenter().getIntervalFacetExpr ( tree, /*min=*/true, /*excl=*/false );
	}
	void visit ( const TDLFacetMinExclusive& expr ) override
	{
		expr.getExpr()->accept(*this);
		tree = KB.getDataTypeCenter().getIntervalFacetExpr ( tree, /*min=*/true, /*excl=*/true );
	}
	void visit ( const TDLFacetMaxInclusive& expr ) override
	{
		expr.getExpr()->accept(*this);
		tree = KB.getDataTypeCenter().getIntervalFacetExpr ( tree, /*min=*/false, /*excl=*/false );
	}
	void visit ( const TDLFacetMaxExclusive& expr ) override
	{
		expr.getExpr()->accept(*this);
		tree = KB.getDataTypeCenter().getIntervalFacetExpr ( tree, /*min=*/false, /*excl=*/true );
	}

#undef THROW_UNSUPPORTED
}; // TExpressionTranslator

#endif
