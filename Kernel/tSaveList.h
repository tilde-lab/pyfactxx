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

#ifndef TSAVELIST_H
#define TSAVELIST_H

/**
 *	template list for Saving/Restoring internal state (saving class is template parameter).
 *  template class should define empty/copy c'tors, member function level()
 */
template <typename T>
class TSaveList
{
protected:	// internal types
		/// type of the list element
	struct List : public T
	{
	public:		// members
			/// pointer to next element
		List* next;

	public:		// interface
			/// empty c'tor
		explicit List ( List* n = nullptr ) : next(n) {}
			/// create object from given one and from given next
		List ( const T& obj, List* n ) : T(obj), next(n) {}
			/// no copy c'tor
		List ( const List& ) = delete;
			/// no assignment
		List& operator = ( const List& ) = delete;
			/// d'tor: do nothing
		~List() = default;

			/// clone given sub-list
		List* clone ( void ) { return new List ( *this, next ? next->clone() : nullptr ); }
			/// clear sub-list
		void clear ( void )
		{
			if ( next )
			{
				next->clear();
				delete next;
			}
		}
	}; // List

protected:	// members
		/// pointer to head of list
	List* head = nullptr;

public:		// interface
		/// empty c'tor
	TSaveList() = default;
		/// copy c'tor
	TSaveList ( const TSaveList& copy ) : head ( copy.head ? copy.head->clone() : nullptr ) {}
		/// d'tor -- clear stack
	~TSaveList() { clear(); }

	// stack operations

		/// check that stack is empty
	bool empty ( void ) const { return (head == nullptr); }
		/// put empty element to stack; @return pointer to it
	T* push ( void ) { head = new List ( head ); return head; }
		/// put given element to stack; @return pointer to it
	T* push ( const T& el ) { head = new List ( el, head ); return head; }
		/// get top element from stack
	T* pop ( void )
	{
		T* ret = head;
		if ( !empty() )
			head = head->next;
		return ret;
	}
		/// get element from stack with given level
	T* pop ( unsigned int level )
	{
		List* p = head;
		while ( p && p->level() > level )
		{
			head = p->next;
			delete p;
			p = head;
		}
		// here p==head and either both == NULL or points to proper element
		if ( p )
			head = head->next;
		return p;
	}

	// extra operations

		/// clear the stack
	void clear ( void )
	{
		if ( !empty() )
		{
			head->clear();
			delete head;
			head = nullptr;
		}
	}
}; // TSaveList

#endif
