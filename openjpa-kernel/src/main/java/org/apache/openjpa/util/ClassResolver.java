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
package org.apache.openjpa.util;

import org.apache.openjpa.lib.util.MultiClassLoader;

/**
 * Interface to customize the loading of classes under OpenJPA.
 *
 * @since 0.3.0
 * @author Marc Prud'hommeaux
 * @author Abe White
 */
public interface ClassResolver {

    /**
     * Return a class loader that can be used to load classes and resources.
     * This can be a standard class loader, or a customized loader such
     * as a {@link MultiClassLoader}.
     *
     * @param contextClass the context class; may be null if no context class
     * @param envLoader the thread's context class loader when the
     * persistence environment (i.e. broker)
     * was obtained; may be null if operating outside
     * the context of persistence environment
     */
    public ClassLoader getClassLoader(Class contextClass,
        ClassLoader envLoader);
}
