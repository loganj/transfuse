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
package org.androidtransfuse.util;

import org.apache.commons.lang.StringUtils;

/**
 * Common utility methods for dealing with annotations
 *
 * @author John Ericksen
 */
public final class AnnotationUtil {

    private AnnotationUtil(){
        //noop utility class constructor
    }

    public static <T> T checkDefault(T input, T defaultValue) {
        if (input != null && input.equals(defaultValue)) {
            return null;
        }
        return input;
    }

    public static String checkBlank(String input) {
        if (StringUtils.isBlank(input)) {
            return null;
        }
        return input;
    }
}
