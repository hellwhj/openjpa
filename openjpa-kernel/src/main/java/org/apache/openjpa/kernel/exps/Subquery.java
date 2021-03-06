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
package org.apache.openjpa.kernel.exps;

/**
 * Query value representing a subquery.
 *
 * @author Abe White
 * @since 0.3.2
 */
public interface Subquery
    extends Value {

    /**
     * Return the candidate alias for this subquery.
     */
    public String getCandidateAlias();

    /**
     * Set the parsed subquery.
     */
    public void setQueryExpressions(QueryExpressions query);
}
