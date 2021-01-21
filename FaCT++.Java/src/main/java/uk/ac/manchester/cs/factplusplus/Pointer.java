package uk.ac.manchester.cs.factplusplus;

/*
 * Copyright (C) 2009-2010, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
/**
 * @author Matthew Horridge, The University Of Manchester, Medical Informatics
 *         Group, 10-Jul-2006
 */
public class Pointer {

    private long node = 0;

    /**
     * @return node pointer
     */
    public long getNode() {
        return node;
    }

    @Override
    public int hashCode() {
        return (int) node;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Pointer)) {
            return false;
        }
        return ((Pointer) obj).node == node;
    }

    @Override
    public final String toString() {
        return this.getClass().getSimpleName() + "[" + node + "]";
    }
}
