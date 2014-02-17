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

package sofia.app;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//-------------------------------------------------------------------------
/**
 * <p>
 * Use this annotation on a subclass of {@link Screen} to specify the layout
 * resource that should be inflated when that screen is displayed.
 * </p><p>
 * This annotation supports three usage forms:
 * </p>
 * <dl>
 * <dt>{@code @ScreenLayout("foo")}</dt>
 * <dd>This will search for a layout defined in the project's
 * {@code res/layout/foo.xml} file. This is the preferred form, since it
 * performs a more advanced search than the numeric "id" form below.</dd>
 * <dt>{@code @ScreenLayout(id = R.layout.foo)}</dt>
 * <dd>This will find the same layout as above, but using the auto-generated
 * numeric ID of the layout resource instead.</dd>
 * <dt>{@code @ScreenLayout}</dt>
 * <dd>When specified without parameters, a lookup based on the name of the
 * class is performed. For example, if the annotation is placed on a class
 * named {@code MyScreen}, then this will search for a layout in
 * {@code res/layout/myscreen.xml}, followed by
 * {@code res/layout/my_screen.xml}.</dd>
 * </dl>
 * <p>
 * If you leave the {@code @ScreenLayout} annotation off a {@code Screen}
 * subclass entirely, the behavior is identical to the third case above.
 * </p>
 *
 * @author Tony Allevato
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface ScreenLayout
{
    //~ Values ................................................................

    // ----------------------------------------------------------
    /**
     * The name of a layout resource, without the {@code "res/layout"}
     * prefix or {@code .xml} extension. For example, {@code "foo"} would
     * refer to {@code "res/layout/foo.xml"}.
     */
    public String value() default "";


    // ----------------------------------------------------------
    /**
     * The ID of a layout resource; for example, {@code R.layout.foo}.
     */
    public int id() default 0;


    // ----------------------------------------------------------
    /**
     * If true, the layout will be wrapped in a
     * {@link android.widget.ScrollView} so that users can scroll it vertically
     * if the layout does not fit on the screen.
     */
    public boolean scroll() default false;
}
