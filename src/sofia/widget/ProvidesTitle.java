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

package sofia.widget;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//-------------------------------------------------------------------------
/**
 * <p>
 * Indicates which method should be called on an object to provide its title
 * in a {@link ListView}, {@link Spinner}, or some other widget that uses
 * <em>decoration</em> to render arbitrary objects.
 * </p><p>
 * If a widget tries to render an object that does not have any methods marked
 * with this annotation, then it will call the {@link Object#toString()}
 * method to get its title.
 * </p>
 * 
 * @author Tony Allevato
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ProvidesTitle
{
}
