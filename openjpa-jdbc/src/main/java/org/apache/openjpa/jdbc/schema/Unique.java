/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package org.apache.openjpa.jdbc.schema;

/**
 * Represents a unique constraint. It can also represent a partial constraint.
 *
 * @author Abe White
 * @author Pinaki Poddar
 */
public class Unique
    extends LocalConstraint {
	
	public Unique() {
		super();
	}
    
    /**
     * Construct with given name.
     * 
     * @param name the name of the constraint, if any
     * @param table the table of the constraint
     */
    public Unique(String name, Table table) {
        super(name, table);
    }

    public boolean isLogical() {
        return false;
    }
    
    /**
     * Adds the given column. 
     * The added column is set to non-nullable because a unique constraint
     * on the database requires that its constituent columns are NOT NULL. 
     * @see Column#setNotNull(boolean)
     */
    public void addColumn(Column col) {
    	super.addColumn(col);
    	col.setNotNull(true);
    }
    
    /**
     * Set the name of the constraint. This method cannot be called if the
     * constraint already belongs to a table. 
     */
    public void setName(String name) {
        super.setName(name);
    }
    
    /**
     * Return true if the structure of this primary key matches that of
     * the given one (same table, same columns).
     */
    public boolean equalsUnique(Unique unq) {
        return equalsLocalConstraint(unq);
    }
}
