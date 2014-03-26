/*
 * Copyright (c) Siemens AG, 2013
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package restapidoc

import java.text.SimpleDateFormat

/**
 * @author Stephan Linkel
 * @author Derk Muenchhausen
 * @since 0.1
 */

class DateUtils {
    static def DATE_FORMATTERS = [:]

    static Date parseDate(String formatted, format = null) {
        if (format)
            return getDateFormatter(format).parse(formatted)
        else
            return ISO8601DateParser.parse(formatted)
    }

    static SimpleDateFormat getDateFormatter(f) {
        if (!DATE_FORMATTERS[f]) {
            DATE_FORMATTERS[f] = new SimpleDateFormat(f)
        }
        return DATE_FORMATTERS[f]
    }

}
