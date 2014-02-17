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

import android.view.View;
import android.view.ViewGroup;

public abstract class ViewVisitor
{
    public void accept(View view)
    {
        boolean processChildren = visit(view);

        if (processChildren && view instanceof ViewGroup)
        {
            ViewGroup group = (ViewGroup) view;

            for (int i = 0; i < group.getChildCount(); i++)
            {
                View child = group.getChildAt(i);
                accept(child);
            }
        }
    }


    protected abstract boolean visit(View view);
}
