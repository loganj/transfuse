/**
 * Copyright 2013 John Ericksen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.androidtransfuse.analysis;

import org.androidtransfuse.adapter.ASTType;

import java.util.List;

/**
 * @author John Ericksen
 */
public class Registration {
    private final String methodName;
    private final List<ASTType> parameters;

    public Registration(String methodName, List<ASTType> parameters) {
        this.parameters = parameters;
        this.methodName = methodName;
    }

    public String getMethodName() {
        return methodName;
    }

    public List<ASTType> getParameters() {
        return parameters;
    }
}
