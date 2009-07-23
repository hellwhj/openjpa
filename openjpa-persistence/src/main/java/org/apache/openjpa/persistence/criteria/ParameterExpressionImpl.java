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
package org.apache.openjpa.persistence.criteria;

import java.util.Collection;

import javax.persistence.criteria.ParameterExpression;

import org.apache.openjpa.kernel.exps.ExpressionFactory;
import org.apache.openjpa.kernel.exps.Value;
import org.apache.openjpa.meta.ClassMetaData;
import org.apache.openjpa.persistence.ParameterImpl;
import org.apache.openjpa.persistence.QueryParameter;
import org.apache.openjpa.persistence.meta.MetamodelImpl;
import org.apache.openjpa.util.InternalException;

/**
 * Parameter of a criteria query.
 * <br>
 * A parameter in CriteriaQuery is always a named parameter but
 * can be constructed without a name.
 * Positional parameters are not allowed in CriteraQuery.
 * The unnamed Parameter will be assigned a name automatically
 * when they are registered to a CriteriaQuery.
 * <br> 
 * This expression uses a delegate Parameter implementation because
 * multiple inheritance is not allowed in Java language. 
 * 
 * @author Pinaki Poddar
 * @author Fay wang
 * 
 * @param <T> the type of value held by this parameter.
 */
public class ParameterExpressionImpl<T> extends ExpressionImpl<T> 
    implements ParameterExpression<T>, QueryParameter<T> {
    
    private String _autoName = null;
	private final ParameterImpl<T> _delegate;
	
	/**
	 * Construct a Parameter of given expected value type and name.
	 * 
	 * @param cls expected value type
	 * @param name name of the parameter which can be null.
	 */
    public ParameterExpressionImpl(Class<T> cls, String name) {
        super(cls);
        _delegate = new ParameterImpl<T>(name, cls);
    }

    /**
     * Gets the name of this parameter.
     * The name can either be assigned by the user or automatically
     * generated by the implementation.
     * 
     * @see #assignAutoName(String)
     */
    public final String getName() {
        return _autoName != null ? _autoName : _delegate.getName();
    }
    
    /**
     * Assigns an automatically generated name to this parameter.
     * Package protected for internal use only.
     *  
     * @exception if this parameter has been given a non-null
     * name at construction.
     */
    final void assignAutoName(String auto) {
        if (_delegate.getName() != null)
            throw new InternalException("Can not assign name [" + auto + "] to " + this);
        _autoName = auto;
    }
	
    /**
     * Raises an internal exception because parameters of CriteriaQuery
     * are not positional. 
     */
    public final Integer getPosition() {
        throw new InternalException(this + " must not be asked for its position");
    }
    
    public final void clearBinding() {
        _delegate.clearBinding();
    }
    
    public final QueryParameter<T> bindValue(Object v) {
        return _delegate.bindValue(v);
    }

    public Class<?> getExpectedValueType() {
        return _delegate.getExpectedValueType();
    }

    public final T getValue() {
        return (T)_delegate.getValue();
    }

    public final Object getValue(boolean mustBeBound) {
        return _delegate.getValue(mustBeBound);
    }

    public final boolean isBound() {
        return _delegate.isBound();
    }

    public final boolean isNamed() {
        return true;
    }

    public final boolean isPositional() {
        return false;
    }

    public final boolean isValueAssignable(Object v) {
        return _delegate.isValueAssignable(v);
    }
    
    public String toString() {
        StringBuilder buf = new StringBuilder("ParameterExpression");
        buf.append("<" + (getExpectedValueType() == null ? "?" : getExpectedValueType().getName()) + ">");
        buf.append("("+getName()+":"); 
        buf.append((isBound() ? getValue() : "UNBOUND") + ")");

        return buf.toString();
    }
    
    @Override
    public Value toValue(ExpressionFactory factory, MetamodelImpl model, CriteriaQueryImpl<?> q) {
        q.registerParameter(this);
        
        ClassMetaData meta = null;
        Class<?> clzz = getJavaType();
        Object paramKey = getName();
        if (paramKey == null)
            throw new InternalException(this + " should have been assigned a name");
        boolean isCollectionValued  = Collection.class.isAssignableFrom(clzz);
        org.apache.openjpa.kernel.exps.Parameter param = isCollectionValued 
            ? factory.newCollectionValuedParameter(paramKey, clzz) 
            : factory.newParameter(paramKey, clzz);
        param.setMetaData(meta);
        
        return param;
    }   
}
