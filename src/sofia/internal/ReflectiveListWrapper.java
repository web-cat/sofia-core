/*
 * Copyright (C) 2011 Virginia Tech Department of Computer Science
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

package sofia.internal;

import java.util.AbstractList;
import java.util.List;
import java.util.Map;

// -------------------------------------------------------------------------
/**
 * An implementation of {@link List} that maps a list of Java objects into a
 * list of {@link ReflectiveMap}s so that their properties can be accessed
 * using key-value notation. Useful when adapting a list of plain objects into
 * a {@code SimpleAdapter} for a {@code ListView} or similar UI component.
 *
 * @author Tony Allevato
 */
public class ReflectiveListWrapper extends AbstractList<Map<String, Object>>
{
    // ----------------------------------------------------------
    private List<?> list;


    // ----------------------------------------------------------
    public ReflectiveListWrapper(List<?> list)
    {
        this.list = list;
    }


    // ----------------------------------------------------------
    @Override
    public Map<String, Object> get(int index)
    {
        return new ReflectiveMap(list.get(index));
    }


    // ----------------------------------------------------------
    @Override
    public int size()
    {
        return list.size();
    }
}
