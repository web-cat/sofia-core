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

/**
 * <p>
 * Extensions to built-in Android widgets and new widgets based on those.
 * </p><p>
 * Some of the classes in this package mirror those in the
 * {@link android.widget} class (for example, {@link sofia.widget.ListView}
 * extends {@link android.widget.ListView}). If your activity extends
 * {@link sofia.app.Screen} and your XML layout files refer to those widgets
 * using the simple class name, like the following example:
 * </p>
 * <pre>
 * &lt;ListView android:id="myList" ... /&gt;</pre>
 * <p>
 * then the Sofia versions will be automatically used instead of the
 * traditional Android versions.
 * </p>
 * 
 * @since API level 1
 */
package sofia.widget;
