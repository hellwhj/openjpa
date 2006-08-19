/*
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.openjpa.jdbc.kernel.exps;

import java.util.Map;

import org.apache.openjpa.jdbc.kernel.JDBCFetchConfiguration;
import org.apache.openjpa.jdbc.kernel.JDBCStore;
import org.apache.openjpa.jdbc.meta.ClassMapping;
import org.apache.openjpa.jdbc.meta.FieldMapping;
import org.apache.openjpa.jdbc.schema.Column;
import org.apache.openjpa.jdbc.schema.Table;
import org.apache.openjpa.jdbc.sql.DBDictionary;
import org.apache.openjpa.jdbc.sql.Joins;
import org.apache.openjpa.jdbc.sql.SQLBuffer;
import org.apache.openjpa.jdbc.sql.Select;
import org.apache.openjpa.kernel.exps.ExpressionVisitor;
import serp.util.Numbers;

/**
 * Test if one string starts with another.
 *
 * @author Abe White
 */
class StartsWithExpression
    implements Exp {

    private final Val _val1;
    private final Val _val2;
    private Joins _joins = null;
    private String _pre = null;
    private String _post = null;

    /**
     * Constructor. Supply values.
     */
    public StartsWithExpression(Val val1, Val val2) {
        _val1 = val1;
        _val2 = val2;
    }

    public void initialize(Select sel, JDBCStore store,
        Object[] params, Map contains) {
        _val1.initialize(sel, store, false);
        _val2.initialize(sel, store, false);
        _joins = sel.and(_val1.getJoins(), _val2.getJoins());

        DBDictionary dict = store.getDBDictionary();
        String func = dict.stringLengthFunction;
        if (func != null) {
            int idx = func.indexOf("{0}");
            _pre = func.substring(0, idx);
            _post = func.substring(idx + 3);
        }
    }

    public void appendTo(SQLBuffer buf, Select sel, JDBCStore store,
        Object[] params, JDBCFetchConfiguration fetch) {
        _val1.calculateValue(sel, store, params, _val2, fetch);
        _val2.calculateValue(sel, store, params, _val1, fetch);

        if (_val1 instanceof Const && ((Const) _val1).getValue() == null)
            buf.append("1 <> 1");
        else if (_val2 instanceof Const) {
            Object o = ((Const) _val2).getValue();
            if (o == null)
                buf.append("1 <> 1");
            else {
                Column col = null;
                if (_val1 instanceof PCPath) {
                    Column[] cols = ((PCPath) _val1).getColumns();
                    if (cols.length == 1)
                        col = cols[0];
                }

                _val1.appendTo(buf, 0, sel, store, params, fetch);
                buf.append(" LIKE ");
                buf.appendValue(o.toString() + "%", col);
            }
        } else {
            // if we can't use LIKE, we have to take the substring of the
            // first value and compare it to the second
            DBDictionary dict = store.getDBDictionary();
            dict.assertSupport(_pre != null, "StringLengthFunction");
            dict.substring(buf,
                new FilterValueImpl(_val1, sel, store, params, fetch),
                new ZeroFilterValue(sel),
                new StringLengthFilterValue(sel, store, params, fetch));
            buf.append(" = ");
            _val2.appendTo(buf, 0, sel, store, params, fetch);
        }

        sel.append(buf, _joins);
        _val1.clearParameters();
        _val2.clearParameters();
    }

    public void selectColumns(Select sel, JDBCStore store,
        Object[] params, boolean pks, JDBCFetchConfiguration fetch) {
        _val1.selectColumns(sel, store, params, true, fetch);
        _val2.selectColumns(sel, store, params, true, fetch);
    }

    public Joins getJoins() {
        return _joins;
    }

    public void acceptVisit(ExpressionVisitor visitor) {
        visitor.enter(this);
        _val1.acceptVisit(visitor);
        _val2.acceptVisit(visitor);
        visitor.exit(this);
    }

    /**
     * Evaluates to 0.
     */
    private class ZeroFilterValue
        implements FilterValue {

        private final Select _sel;

        public ZeroFilterValue(Select sel) {
            _sel = sel;
        }

        public Class getType() {
            return int.class;
        }

        public int length() {
            return 1;
        }

        public void appendTo(SQLBuffer buf) {
            appendTo(buf, 0);
        }

        public void appendTo(SQLBuffer buf, int index) {
            buf.appendValue(0);
        }

        public String getColumnAlias(Column col) {
            return _sel.getColumnAlias(col, _joins);
        }

        public String getColumnAlias(String col, Table table) {
            return _sel.getColumnAlias(col, table, _joins);
        }

        public Object toDataStoreValue(Object val) {
            return val;
        }

        public boolean isConstant() {
            return true;
        }

        public Object getValue() {
            return Numbers.valueOf(0);
        }

        public Object getSQLValue() {
            return Numbers.valueOf(0);
        }

        public boolean isPath() {
            return false;
        }

        public ClassMapping getClassMapping() {
            return null;
        }

        public FieldMapping getFieldMapping() {
            return null;
        }
    }

    /**
     * Evaluates to the length of a given value.
     */
    private class StringLengthFilterValue
        implements FilterValue {

        private final Select _sel;
        private final JDBCStore _store;
        private final Object[] _params;
        private final JDBCFetchConfiguration _fetch;

        public StringLengthFilterValue(Select sel, JDBCStore store,
            Object[] params, JDBCFetchConfiguration fetch) {
            _sel = sel;
            _store = store;
            _params = params;
            _fetch = fetch;
        }

        public Class getType() {
            return int.class;
        }

        public int length() {
            return 1;
        }

        public void appendTo(SQLBuffer buf) {
            appendTo(buf, 0);
        }

        public void appendTo(SQLBuffer buf, int index) {
            buf.append(_pre);
            _val2.appendTo(buf, index, _sel, _store, _params, _fetch);
            buf.append(_post);
        }

        public String getColumnAlias(Column col) {
            return _sel.getColumnAlias(col, _val2.getJoins());
        }

        public String getColumnAlias(String col, Table table) {
            return _sel.getColumnAlias(col, table, _val2.getJoins());
        }

        public Object toDataStoreValue(Object val) {
            return _val2.toDataStoreValue(val, _store);
        }

        public boolean isConstant() {
            return false;
        }

        public Object getValue() {
            return null;
        }

        public Object getSQLValue() {
            return null;
        }

        public boolean isPath() {
            return false;
        }

        public ClassMapping getClassMapping() {
            return null;
        }

        public FieldMapping getFieldMapping() {
            return null;
        }
    }
}
