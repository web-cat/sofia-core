package sofia.app.internal;

import java.util.HashMap;

import sofia.internal.events.EventDispatcher;
import sofia.internal.events.OptionalEventDispatcher;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsSpinner;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;

//-------------------------------------------------------------------------
/**
 * TODO document
 *
 * @author  Tony Allevato
 * @version 2012.11.02
 */
public class EventBinder
{
    //~ Fields ................................................................

    private static final String ANDROID_NS =
            "http://schemas.android.com/apk/res/android";
    private static HashMap<
        Class<? extends View>, Binder<? extends View>> binders;

    private Object receiver;


    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Initializes a new {@code EventBinder} that dispatches events to the
     * specified receiver.
     *
     * @param receiver the object that will receive the event notifications
     */
    public EventBinder(Object receiver)
    {
        this.receiver = receiver;
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * Binds any Sofia event handlers that apply to the specified view.
     *
     * @param view the view to bind events to
     * @param attrs the attribute set that was used during inflation (may be
     *     null if this is called outside of the inflater)
     */
    @SuppressWarnings("unchecked")
    public void bindEvents(View view, AttributeSet attrs)
    {
        Class<?> viewClass = view.getClass();
        Binder<? extends View> binder = null;

        while (binder == null && !viewClass.equals(Object.class))
        {
            binder = binders.get(viewClass);
            viewClass = viewClass.getSuperclass();
        }

        if (binder != null)
        {
            ((Binder<View>) binder).bind(receiver, view, attrs);
        }
    }


    // ----------------------------------------------------------
    /**
     * Gets the string name of the specified resource ID.
     *
     * @param context the context that contains the resources
     * @param id the ID to look up
     * @return the string name of the ID, or null if it was not found
     */
    private static String getIdName(Context context, int id)
    {
        if (id != View.NO_ID)
        {
            return context.getResources().getResourceEntryName(id);
        }
        else
        {
            return null;
        }
    }


    //~ Nested classes and interfaces .........................................

    // ----------------------------------------------------------
    /**
     * An interface that represents how an event should be bound to a widget of
     * a particular type.
     *
     * Binders are added to the binder map such that only the binder for the
     * most specific subclass of a widget that has a binder will be called. If
     * the binder wants superclass bindings to be added as well, it must
     * explicitly chain a call to that binder.
     *
     * @param <ViewType> the actual view type
     */
    private static interface Binder<ViewType>
    {
        // ----------------------------------------------------------
        /**
         * Binds a listener for the event to the specified receiver.
         *
         * @param receiver the object that will receive the event notification
         * @param view the view sending the event
         * @param attrs the attributes set in the layout XML (if any)
         */
        public void bind(Object receiver, ViewType view, AttributeSet attrs);
    }


    // ----------------------------------------------------------
    /**
     * Implements the following binding rule: if a view is clickable, and it
     * does not already have a method bound to the onClick attribute, then
     * connect it to the context method "${id}Clicked", where "${id}" is the
     * string name of the view's identifier.
     */
    private static Binder<View> ViewBinder = new Binder<View>() {
        @Override
        public void bind(final Object receiver, View view, AttributeSet attrs)
        {
            if (!AdapterView.class.isAssignableFrom(view.getClass())
                    && view.isClickable()
                    && (attrs == null ||
                        attrs.getAttributeValue(ANDROID_NS, "onClick") == null))
            {
                final String id = getIdName(view.getContext(), view.getId());

                if (id != null)
                {
                    final OptionalEventDispatcher event =
                            new OptionalEventDispatcher(id + "Clicked", 0);

                    try
                    {
                        view.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v)
                            {
                                event.dispatch(receiver, v);
                            }
                        });
                    }
                    catch (Exception e)
                    {
                        // Do nothing. This try/catch was placed here because
                        // it was discovered that setOnClickListener on an
                        // AdapterView throws an exception (Barbara Liskov
                        // would be furious) with a message directing you to
                        // use setOnItemClickListener instead. For
                        // future-proofing, we want to catch any such possible
                        // exceptions and log them so that we can place
                        // appropriate checks at the front of this method.

                        Log.d("EventBinder", e.getMessage());
                    }
                }
            }
        }
    };


    // ----------------------------------------------------------
    /**
     * Implements the following binding rule: for each AbsListView,
     * attach an OnItemClickListener to it that calls the context method
     * "${id}ItemClicked", where "${id}" is the string name of the view's
     * identifier.
     */
    private static Binder<AbsListView> AbsListViewBinder =
            new Binder<AbsListView>() {
        @Override
        public void bind(
                final Object receiver, AbsListView view, AttributeSet attrs)
        {
            // Fall back to "listView" as a default handler prefix if the
            // list doesn't have its own ID -- this supports the
            // automatically created ListView on a ListScreen.

            String resourceId = getIdName(view.getContext(), view.getId());
            final String id = (resourceId != null)
                    ? resourceId : "listView";

            final OptionalEventDispatcher event =
                    new OptionalEventDispatcher(id + "ItemClicked", 1);

            view.setOnItemClickListener(
                    new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent,
                        View view, int position, long id)
                {
                    Object item = parent.getAdapter().getItem(position);
                    event.dispatch(receiver, item, position);
                }
            });
        }
    };


    // ----------------------------------------------------------
    /**
     * Implements the following binding rule: for each AbsSpinner,
     * attach an OnItemSelectedListener to it that calls the context method
     * "${id}ItemSelected", where "${id}" is the string name of the view's
     * identifier.
     */
    private static Binder<AbsSpinner> AbsSpinnerBinder =
            new Binder<AbsSpinner>() {
        @Override
        public void bind(
                final Object receiver, AbsSpinner view, AttributeSet attrs)
        {
            String id = getIdName(view.getContext(), view.getId());

            if (id != null)
            {
                final OptionalEventDispatcher itemEvent =
                        new OptionalEventDispatcher(id + "ItemSelected", 1);
                final EventDispatcher nothingEvent =
                        new EventDispatcher(id + "NothingSelected");

                view.setOnItemSelectedListener(
                        new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent,
                            View view, int position, long id)
                    {
                        Object item = parent.getAdapter().getItem(position);
                        itemEvent.dispatch(receiver, item, position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent)
                    {
                        nothingEvent.dispatch(receiver);
                    }
                });
            }
        }
    };


    // ----------------------------------------------------------
    /**
     * Implements the following binding rule: for each EditText
     * view, attach an OnEditorActionListener to it that calls the context
     * method "${id}EditingDone", where "${id}" is the string name of the
     * view's identifier.
     */
    private static Binder<EditText> EditTextBinder = new Binder<EditText>() {
        @Override
        public void bind(
                final Object receiver, EditText view, AttributeSet attrs)
        {
            EditText editText = (EditText) view;
            editText.setOnEditorActionListener(
                    new EditorActionListener(receiver));
        }
    };


    // ----------------------------------------------------------
    /**
     * Implements the following binding rule: for each SeekBar,
     * attach an OnSeekBarChangedListener to it that calls the context method
     * "${id}Changed", where "${id}" is the string name of the view's
     * identifier.
     */
    private static Binder<SeekBar> SeekBarBinder = new Binder<SeekBar>() {
        @Override
        public void bind(
                final Object receiver, SeekBar view, AttributeSet attrs)
        {
            String id = getIdName(view.getContext(), view.getId());

            if (id != null)
            {
                final OptionalEventDispatcher changedEvent =
                        new OptionalEventDispatcher(
                                id + "ProgressChanged", 0);
                final OptionalEventDispatcher startedEvent =
                        new OptionalEventDispatcher(
                                id + "TrackingStarted", 0);
                final OptionalEventDispatcher stoppedEvent =
                        new OptionalEventDispatcher(
                                id + "TrackingStopped", 0);

                view.setOnSeekBarChangeListener(
                        new SeekBar.OnSeekBarChangeListener() {
                            public void onProgressChanged(SeekBar seekBar,
                                    int progress, boolean fromUser)
                            {
                                changedEvent.dispatch(receiver, seekBar,
                                        progress, fromUser);
                            }

                            public void onStartTrackingTouch(SeekBar seekBar)
                            {
                                startedEvent.dispatch(receiver, seekBar,
                                        seekBar.getProgress());
                            }

                            public void onStopTrackingTouch(SeekBar seekBar)
                            {
                                stoppedEvent.dispatch(receiver, seekBar,
                                        seekBar.getProgress());
                            }
                });
            }
        }
    };


    // ----------------------------------------------------------
    /**
     * Implements the following binding rule: for each SeekBar,
     * attach an OnSeekBarChangedListener to it that calls the context method
     * "${id}Changed", where "${id}" is the string name of the view's
     * identifier.
     */
    private static Binder<RatingBar> RatingBarBinder =
            new Binder<RatingBar>() {
        @Override
        public void bind(
                final Object receiver, RatingBar view, AttributeSet attrs)
        {
            String id = getIdName(view.getContext(), view.getId());

            if (id != null)
            {
                final OptionalEventDispatcher changedEvent =
                        new OptionalEventDispatcher(
                                id + "RatingChanged", 0);

                view.setOnRatingBarChangeListener(
                        new RatingBar.OnRatingBarChangeListener() {
                            public void onRatingChanged(RatingBar ratingBar,
                                    float rating, boolean fromUser)
                            {
                                changedEvent.dispatch(receiver, ratingBar,
                                        rating, fromUser);
                            }
                });
            }
        }
    };


    // ----------------------------------------------------------
    private static class EditorActionListener
        implements TextView.OnEditorActionListener
    {
        private Object receiver;


        // ----------------------------------------------------------
        public EditorActionListener(Object receiver)
        {
            this.receiver = receiver;
        }


        // ----------------------------------------------------------
        @Override
        public boolean onEditorAction(TextView v, int actionId,
                KeyEvent event)
        {
            // TODO We need to test this across multiple devices with both soft
            // and hard keyboards.

            if (actionMatchesImeOptions(v, actionId)
                    || (event != null
                        && event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
            {
                dispatchEvent(v);

                if (actionId == EditorInfo.IME_ACTION_DONE)
                {
                    InputMethodManager imm = (InputMethodManager)
                            v.getContext().getSystemService(
                                    Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }

                return true;
            }
            else
            {
                return false;
            }
        }


        // ----------------------------------------------------------
        /**
         * By default, if the user does not explicitly set any IME options on
         * a text view, it sends either the Next or Done event depending on
         * its position in the GUI layout. We want to capture both of those in
         * that case. Otherwise, if the user has explicitly set a specific
         * action through {@link TextView#setImeOptions(int)}, then we only
         * want to capture that specific one to dispatch an event.
         *
         * @param v the text view
         * @param actionId the IME action
         *
         * @return true if the IME action matches the options of the text view
         */
        private boolean actionMatchesImeOptions(TextView v, int actionId)
        {
            int options = v.getImeOptions();

            if (options == 0)
            {
                return actionId == EditorInfo.IME_ACTION_NEXT
                        || actionId == EditorInfo.IME_ACTION_DONE;
            }
            else
            {
                return actionId == (options & EditorInfo.IME_MASK_ACTION);
            }
        }


        // ----------------------------------------------------------
        /**
         * Dispatch the "EditingDone" event to the text view's context.
         *
         * @param v the text view
         */
        private void dispatchEvent(TextView v)
        {
            final String id = getIdName(v.getContext(), v.getId());

            if (id != null)
            {
                final OptionalEventDispatcher event =
                        new OptionalEventDispatcher(id + "EditingDone");
                event.dispatch(receiver, v);
            }
        }
    };


    // ----------------------------------------------------------
    /**
     * Add the binders to the static map.
     */
    static
    {
        binders = new HashMap<Class<? extends View>, Binder<? extends View>>();

        binders.put(View.class, ViewBinder);
        binders.put(EditText.class, EditTextBinder);
        binders.put(AbsListView.class, AbsListViewBinder);
        binders.put(AbsSpinner.class, AbsSpinnerBinder);
        binders.put(SeekBar.class, SeekBarBinder);
        binders.put(RatingBar.class, RatingBarBinder);
    }
}
