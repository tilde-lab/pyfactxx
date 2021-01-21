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

#ifndef TSTTREE_H
#define TSTTREE_H

template < class T >
class TsTTree
{
private:	// members
		/// element in the tree node
	T elem;
		/// pointer to left subtree
	TsTTree *left;
		/// pointer to right subtree
	TsTTree *right;

public:		// interface
		/// default c'tor
	explicit TsTTree ( const T& Init, TsTTree *l = nullptr, TsTTree *r = nullptr )
		: elem(Init)
		, left(l)
		, right(r)
		{}
		/// no copy c'tor
	TsTTree ( const TsTTree& ) = delete;
		/// no assignment
	TsTTree& operator = ( const TsTTree& ) = delete;
		/// d'tor
	~TsTTree() = default;

	// access to members

	T& Element ( void )	{ return elem; }
	const T& Element ( void ) const	{ return elem; }

	TsTTree* Left ( void ) const { return left; }
	TsTTree* Right ( void ) const { return right; }

	void SetLeft ( TsTTree *l ) { left = l; }
	void SetRight ( TsTTree *r ) { right = r; }

	TsTTree* clone ( void ) const
	{
		TsTTree* p = new TsTTree(Element());
		if ( left )
			p->SetLeft(left->clone());
		if ( right )
			p->SetRight(right->clone());
		return p;
	}
}; // TsTTree

/// delete the whole tree
template <typename T>
void deleteTree ( TsTTree<T>* t )
{
	if ( t )
	{
		deleteTree(t->Left());
		deleteTree(t->Right());
		delete t;
	}
}

#endif
