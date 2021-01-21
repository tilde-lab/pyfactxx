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

#ifndef TODOLIST_H
#define TODOLIST_H

#include <vector>

#include "globaldef.h"
#include "fpp_assert.h"
#include "PriorityMatrix.h"
#include "tRareSaveStack.h"

/// the entry of TODO table
struct ToDoEntry
{
		/// node to include concept
	DlCompletionTree* Node = nullptr;
		/// offset of included concept in Node's label
		// (it's not possible to use pointers because
		// std::vector invalidates them)
	int offset = 0;

		/// empty c'tor
	ToDoEntry() = default;
		/// init c'tor
	ToDoEntry ( DlCompletionTree* n, int off ) : Node(n), offset(off) {}
}; // ToDoEntry

/// All-in-one version of arrayToDoTable
class ToDoList
{
protected:	// classes
		/// class for saving/restoring ToDoQueue
	class QueueSaveState
	{
	public:		// members
			/// save start point of queue of entries
		size_t sp = 0;
			/// save end point of queue of entries
		size_t ep = 0;
	}; // QueueSaveState
	//--------------------------------------------------------------------------

		/// class to represent single queue
	class arrayQueue
	{
	protected:	// members
			/// waiting ops queue
		std::vector<ToDoEntry> Wait;
			/// start pointer; points to the 1st element in the queue
		size_t sPointer = 0;

	protected:	// types
			/// type for restore the whole queue
		class QueueRestorer: public TRestorer
		{
		protected:	// members
				/// copy of a queue
			std::vector<ToDoEntry> Wait;
				/// pointer to a queue to restore
			arrayQueue* queue;
				/// start pointer
			size_t sp;

		public:		// interface
				/// init c'tor
			explicit QueueRestorer ( arrayQueue* q ) : Wait(q->Wait), queue(q), sp(q->sPointer) {}
				/// restore: copy the queue back, adjust pointers
			void restore () override { std::swap(queue->Wait, Wait); queue->sPointer = sp; }
		};

	public:		// interface
			/// c'tor: init queue with proper size and reset it
		arrayQueue()
		{
			Wait.reserve(50);	// initial size
			Wait.clear();
		}

			/// add entry to a queue
		virtual void add ( DlCompletionTree* node, int offset ) { Wait.emplace_back(node,offset); }
			/// clear queue
		void clear ( void ) { sPointer = 0; Wait.clear(); }
			/// check if queue empty
		bool empty ( void ) const { return sPointer == Wait.size(); }
			/// get next entry from the queue; works for non-empty queues
		const ToDoEntry* get ( void ) { return &(Wait[sPointer++]); }

			/// save queue content to the given entry
		void save ( QueueSaveState& tss ) const
		{
			tss.sp = sPointer;
			tss.ep = Wait.size();
		}
			/// restore queue content from the given entry
		void restore ( const QueueSaveState& tss )
		{
			sPointer = tss.sp;
			Wait.resize(tss.ep);
		}
	}; // arrayQueue
	//--------------------------------------------------------------------------

		/// class to represent single priority queue
	class queueQueue: public arrayQueue
	{
	protected:	// members
			/// stack to save states for the overwritten queue
		TRareSaveStack* stack;

	public:		// interface
			/// c'tor: make an empty queue
		explicit queueQueue ( TRareSaveStack* s ) : arrayQueue(), stack(s) {}

			/// add entry to a queue
		void add ( DlCompletionTree* Node, int offset ) override
		{
			auto nominalLevel = Node->getNominalLevel();
			auto greaterNominalLevel = [=] (const ToDoEntry& entry) { return entry.Node->getNominalLevel() > nominalLevel; };
			if ( empty() ||	// no problems with empty queue and if no priority clashes
				 !greaterNominalLevel(Wait.back()) )
			{
				Wait.emplace_back(Node,offset);
				return;
			}

			// here we need to put new entry into a proper place
			// this will invalidate the array so save it
			stack->push(new QueueRestorer(this));
			// find a place for the new entry
			auto i = std::find_if ( Wait.begin()+sPointer, Wait.end(), greaterNominalLevel );
			Wait.emplace(i,Node,offset);
		}
	}; // queueQueue
	//--------------------------------------------------------------------------

protected:	// internal typedefs
		/// typedef for NN-queue (which should support complete S/R)
	typedef queueQueue NNQueue;

protected:	// classes
		/// class for saving/restoring array TODO table
	class SaveState
	{
	public:		// members
			/// save state for queueID
		QueueSaveState backupID;
			/// save state for queueNN
		QueueSaveState backupNN;
			/// save state of all regular queues
		QueueSaveState backup[nRegularOps];
			/// save number-of-entries to do
		unsigned int noe = 0;
	}; // SaveState
	//--------------------------------------------------------------------------

protected:	// members
		/// waiting ops queue for IDs
	arrayQueue queueID;
		/// waiting ops queue for <= ops in nominal nodes
	NNQueue queueNN;
		/// waiting ops queues
	arrayQueue Wait[nRegularOps];
		/// stack of saved states
	TSaveStack<SaveState> SaveStack;
		/// priority matrix
	const ToDoPriorMatrix& Matrix;
		/// number of un-processed entries
	unsigned int noe = 0;

protected:	// methods
		/// save current TODO table content to given saveState entry
	void saveState ( SaveState* tss )
	{
		queueID.save(tss->backupID);
		queueNN.save(tss->backupNN);
		for ( auto i = 0; i < nRegularOps; ++i )
			Wait[i].save(tss->backup[i]);

		tss->noe = noe;
	}
		/// restore TODO table content from given saveState entry
	void restoreState ( const SaveState* tss )
	{
		queueID.restore(tss->backupID);
		queueNN.restore(tss->backupNN);
		for ( auto i = 0; i < nRegularOps; ++i )
			Wait[i].restore(tss->backup[i]);

		noe = tss->noe;
	}

public:
		/// init c'tor
	ToDoList( const ToDoPriorMatrix& matrix, TRareSaveStack* stack ) : queueNN(stack), Matrix(matrix) {}
		/// no copy c'tor
	ToDoList( const ToDoList& ) = delete;
		/// no assignment
	ToDoList& operator = ( const ToDoList& ) = delete;
		/// d'tor: delete all entries
	~ToDoList() { clear(); }

	// global methods

		/// clear TODO table
	void clear ( void )
	{
		queueID.clear();
		queueNN.clear();
		for ( auto& queue: Wait )
			queue.clear();

		SaveStack.clear();
		noe = 0;
	}
		/// check if TODO table is empty
	bool empty ( void ) const { return !noe; }

	// work with entries

		/// add entry with given NODE and CONCEPT with given OFFSET to the TODO table
	void addEntry ( DlCompletionTree* node, DagTag type, BipolarPointer bp, int offset )
	{
		auto index = Matrix.getIndex ( type, isPositive(bp), node->isNominalNode() );
		switch ( index )
		{
		case nRegularOps:	// unused entry
			return;
		case iId:			// ID
			queueID.add(node,offset); break;
		case iNN:			// NN
			queueNN.add(node,offset); break;
		default:			// regular queue
			Wait[index].add(node,offset); break;
		}
		++noe;
	}
		/// add entry with given NODE and CONCEPT of a TYPE to the ToDo table
	void addEntry ( DlCompletionTree* node, DagTag type, BipolarPointer bp )
		{ addEntry ( node, type, bp, node->label().getLast(type) ); }
		/// get the next TODO entry. @return NULL if the table is empty
	const ToDoEntry* getNextEntry ( void );

	// save/restore methods

		/// save current state using internal stack
	void save ( void ) { saveState(SaveStack.push()); }
		/// restore state using internal stack
	void restore ( void ) { fpp_assert ( !SaveStack.empty() ); restoreState(SaveStack.pop()); }
		/// restore state to the given level using internal stack
	void restore ( unsigned int level ) { restoreState(SaveStack.pop(level)); }
}; // ToDoList

inline const ToDoEntry* ToDoList :: getNextEntry ( void )
{
#ifdef ENABLE_CHECKING
	fpp_assert ( !empty () );	// safety check
#endif

	// decrease amount of elements-to-process
	--noe;

	// check ID queue
	if ( !queueID.empty() )
		return queueID.get();

	// check NN queue
	if ( !queueNN.empty() )
		return queueNN.get();

	// check regular queues
	for ( auto& queue: Wait )
		if ( !queue.empty() )
			return queue.get();

	// that's impossible, but still...
	return nullptr;
}

#endif
