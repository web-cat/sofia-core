/*
 * Copyright (C) 2010 The Android Open Source Project
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

package sofia.view;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

/**
 * Detects transformation gestures involving more than one pointer ("multitouch")
 * using the supplied {@link MotionEvent}s. The {@link OnRotateGestureListener}
 * callback will notify users when a particular gesture event has occurred.
 * This class should only be used with {@link MotionEvent}s reported via touch.
 *
 * To use this class:
 * <ul>
 *  <li>Create an instance of the {@code RotateGestureDetector} for your
 *      {@link View}
 *  <li>In the {@link View#onTouchEvent(MotionEvent)} method ensure you call
 *          {@link #onTouchEvent(MotionEvent)}. The methods defined in your
 *          callback will be executed when the events occur.
 * </ul>
 */
public class RotateGestureDetector
{
    /**
     * The listener for receiving notifications when gestures occur.
     * If you want to listen for all the different gestures then implement
     * this interface. If you only want to listen for a subset it might
     * be easier to extend {@link SimpleOnRotateGestureListener}.
     *
     * An application will receive events in the following order:
     * <ul>
     *  <li>One {@link OnRotateGestureListener#onRotateBegin(RotateGestureDetector)}
     *  <li>Zero or more {@link OnRotateGestureListener#onRotate(RotateGestureDetector)}
     *  <li>One {@link OnRotateGestureListener#onRotateEnd(RotateGestureDetector)}
     * </ul>
     */
    public interface OnRotateGestureListener {
        /**
         * Responds to rotation events for a gesture in progress.
         * Reported by pointer motion.
         *
         * @param detector The detector reporting the event - use this to
         *          retrieve extended info about event state.
         * @return Whether or not the detector should consider this event
         *          as handled. If an event was not handled, the detector
         *          will continue to accumulate movement until an event is
         *          handled. This can be useful if an application, for example,
         *          only wants to update rotation factors if the change is
         *          greater than 0.01.
         */
        public boolean onRotate(RotateGestureDetector detector);

        /**
         * Responds to the beginning of a rotation gesture. Reported by
         * new pointers going down.
         *
         * @param detector The detector reporting the event - use this to
         *          retrieve extended info about event state.
         * @return Whether or not the detector should continue recognizing
         *          this gesture. For example, if a gesture is beginning
         *          with a focal point outside of a region where it makes
         *          sense, onRotateBegin() may return false to ignore the
         *          rest of the gesture.
         */
        public boolean onRotateBegin(RotateGestureDetector detector);

        /**
         * Responds to the end of a rotation gesture. Reported by existing
         * pointers going up.
         *
         * Once a rotation has ended, {@link RotateGestureDetector#getFocusX()}
         * and {@link RotateGestureDetector#getFocusY()} will return the location
         * of the pointer remaining on the screen.
         *
         * @param detector The detector reporting the event - use this to
         *          retrieve extended info about event state.
         */
        public void onRotateEnd(RotateGestureDetector detector);
    }

    /**
     * A convenience class to extend when you only want to listen for a subset
     * of rotation-related events. This implements all methods in
     * {@link OnRotateGestureListener} but does nothing.
     * {@link OnRotateGestureListener#onRotate(RotateGestureDetector)} returns
     * {@code false} so that a subclass can retrieve the accumulated rotation
     * factor in an overridden onRotateEnd.
     * {@link OnRotateGestureListener#onRotateBegin(RotateGestureDetector)} returns
     * {@code true}.
     */
    public static class SimpleOnRotateGestureListener implements OnRotateGestureListener {

        public boolean onRotate(RotateGestureDetector detector) {
            return false;
        }

        public boolean onRotateBegin(RotateGestureDetector detector) {
            return true;
        }

        public void onRotateEnd(RotateGestureDetector detector) {
            // Intentionally empty
        }
    }

    /**
     * This value is the threshold ratio between our previous combined pressure
     * and the current combined pressure. We will only fire an onRotate event if
     * the computed ratio between the current and previous event pressures is
     * greater than this value. When pressure decreases rapidly between events
     * the position values can often be imprecise, as it usually indicates
     * that the user is in the process of lifting a pointer off of the device.
     * Its value was tuned experimentally.
     */
    private static final float PRESSURE_THRESHOLD = 0.67f;

    private final Context mContext;
    private final OnRotateGestureListener mListener;
    private boolean mGestureInProgress;

    private MotionEvent mPrevEvent;
    private MotionEvent mCurrEvent;

    private float mFocusX;
    private float mFocusY;
    private float mPrevFingerDiffX;
    private float mPrevFingerDiffY;
    private float mCurrFingerDiffX;
    private float mCurrFingerDiffY;
    private float mCurrSlope;
    private float mPrevSlope;
    private float mRotation;
    private float mCurrPressure;
    private float mPrevPressure;
    private long mTimeDelta;

    private final float mEdgeSlop;
    private float mRightSlopEdge;
    private float mBottomSlopEdge;
    private boolean mSloppyGesture;

    public RotateGestureDetector(Context context, OnRotateGestureListener listener) {
        ViewConfiguration config = ViewConfiguration.get(context);
        mContext = context;
        mListener = listener;
        mEdgeSlop = config.getScaledEdgeSlop();
    }

    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        boolean handled = true;

        if (!mGestureInProgress) {
            switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_POINTER_DOWN: {
                // We have a new multi-finger gesture

                // as orientation can change, query the metrics in touch down
                DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
                mRightSlopEdge = metrics.widthPixels - mEdgeSlop;
                mBottomSlopEdge = metrics.heightPixels - mEdgeSlop;

                // Be paranoid in case we missed an event
                reset();

                mPrevEvent = MotionEvent.obtain(event);
                mTimeDelta = 0;

                setContext(event);

                // Check if we have a sloppy gesture. If so, delay
                // the beginning of the gesture until we're sure that's
                // what the user wanted. Sloppy gestures can happen if the
                // edge of the user's hand is touching the screen, for example.
                final float edgeSlop = mEdgeSlop;
                final float rightSlop = mRightSlopEdge;
                final float bottomSlop = mBottomSlopEdge;
                final float x0 = event.getRawX();
                final float y0 = event.getRawY();
                final float x1 = getRawX(event, 1);
                final float y1 = getRawY(event, 1);

                boolean p0sloppy = x0 < edgeSlop || y0 < edgeSlop
                        || x0 > rightSlop || y0 > bottomSlop;
                boolean p1sloppy = x1 < edgeSlop || y1 < edgeSlop
                        || x1 > rightSlop || y1 > bottomSlop;

                if (p0sloppy && p1sloppy) {
                    mFocusX = -1;
                    mFocusY = -1;
                    mSloppyGesture = true;
                } else if (p0sloppy) {
                    mFocusX = event.getX(1);
                    mFocusY = event.getY(1);
                    mSloppyGesture = true;
                } else if (p1sloppy) {
                    mFocusX = event.getX(0);
                    mFocusY = event.getY(0);
                    mSloppyGesture = true;
                } else {
                    mGestureInProgress = mListener.onRotateBegin(this);
                }
            }
            break;

            case MotionEvent.ACTION_MOVE:
                if (mSloppyGesture) {
                    // Initiate sloppy gestures if we've moved outside of the slop area.
                    final float edgeSlop = mEdgeSlop;
                    final float rightSlop = mRightSlopEdge;
                    final float bottomSlop = mBottomSlopEdge;
                    final float x0 = event.getRawX();
                    final float y0 = event.getRawY();
                    final float x1 = getRawX(event, 1);
                    final float y1 = getRawY(event, 1);

                    boolean p0sloppy = x0 < edgeSlop || y0 < edgeSlop
                    || x0 > rightSlop || y0 > bottomSlop;
                    boolean p1sloppy = x1 < edgeSlop || y1 < edgeSlop
                    || x1 > rightSlop || y1 > bottomSlop;

                    if(p0sloppy && p1sloppy) {
                        mFocusX = -1;
                        mFocusY = -1;
                    } else if (p0sloppy) {
                        mFocusX = event.getX(1);
                        mFocusY = event.getY(1);
                    } else if (p1sloppy) {
                        mFocusX = event.getX(0);
                        mFocusY = event.getY(0);
                    } else {
                        mSloppyGesture = false;
                        mGestureInProgress = mListener.onRotateBegin(this);
                    }
                }
                break;

            case MotionEvent.ACTION_POINTER_UP:
                if (mSloppyGesture) {
                    // Set focus point to the remaining finger
                    int id = (((action & MotionEvent.ACTION_POINTER_INDEX_MASK)
                            >> MotionEvent.ACTION_POINTER_INDEX_SHIFT) == 0) ? 1 : 0;
                    mFocusX = event.getX(id);
                    mFocusY = event.getY(id);
                }
                break;
            }
        } else {
            // Transform gesture in progress - attempt to handle it
            switch (action & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_POINTER_UP:
                    // Gesture ended
                    setContext(event);

                    // Set focus point to the remaining finger
                    int id = (((action & MotionEvent.ACTION_POINTER_INDEX_MASK)
                            >> MotionEvent.ACTION_POINTER_INDEX_SHIFT) == 0) ? 1 : 0;
                    mFocusX = event.getX(id);
                    mFocusY = event.getY(id);

                    if (!mSloppyGesture) {
                        mListener.onRotateEnd(this);
                    }

                    reset();
                    break;

                case MotionEvent.ACTION_CANCEL:
                    if (!mSloppyGesture) {
                        mListener.onRotateEnd(this);
                    }

                    reset();
                    break;

                case MotionEvent.ACTION_MOVE:
                    setContext(event);

                    // Only accept the event if our relative pressure is within
                    // a certain limit - this can help filter shaky data as a
                    // finger is lifted.
                    if (mCurrPressure / mPrevPressure > PRESSURE_THRESHOLD) {
                        final boolean updatePrevious = mListener.onRotate(this);

                        if (updatePrevious) {
                            mPrevEvent.recycle();
                            mPrevEvent = MotionEvent.obtain(event);
                        }
                    }
                    break;
            }
        }
        return handled;
    }

    /**
     * MotionEvent has no getRawX(int) method; simulate it pending future API approval.
     */
    private static float getRawX(MotionEvent event, int pointerIndex) {
        float offset = event.getRawX() - event.getX();
        return event.getX(pointerIndex) + offset;
    }

    /**
     * MotionEvent has no getRawY(int) method; simulate it pending future API approval.
     */
    private static float getRawY(MotionEvent event, int pointerIndex) {
        float offset = event.getRawY() - event.getY();
        return event.getY(pointerIndex) + offset;
    }

    private void setContext(MotionEvent curr) {
        if (mCurrEvent != null) {
            mCurrEvent.recycle();
        }
        mCurrEvent = MotionEvent.obtain(curr);

        mCurrSlope = -1;
        mPrevSlope = -1;
        mRotation = -1;

        final MotionEvent prev = mPrevEvent;
        final float px0 = prev.getX(0);
        final float py0 = prev.getY(0);
        final float px1 = prev.getX(1);
        final float py1 = prev.getY(1);
        final float cx0 = curr.getX(0);
        final float cy0 = curr.getY(0);
        final float cx1 = curr.getX(1);
        final float cy1 = curr.getY(1);

        final float pvx = px1 - px0;
        final float pvy = py1 - py0;
        final float cvx = cx1 - cx0;
        final float cvy = cy1 - cy0;
        mPrevFingerDiffX = pvx;
        mPrevFingerDiffY = pvy;
        mCurrFingerDiffX = cvx;
        mCurrFingerDiffY = cvy;

        mFocusX = cx0 + cvx * 0.5f;
        mFocusY = cy0 + cvy * 0.5f;

        mTimeDelta = curr.getEventTime() - prev.getEventTime();
        mCurrPressure = curr.getPressure(0) + curr.getPressure(1);
        mPrevPressure = prev.getPressure(0) + prev.getPressure(1);
    }

    private void reset() {
        if (mPrevEvent != null) {
            mPrevEvent.recycle();
            mPrevEvent = null;
        }
        if (mCurrEvent != null) {
            mCurrEvent.recycle();
            mCurrEvent = null;
        }
        mSloppyGesture = false;
        mGestureInProgress = false;
    }

    /**
     * Returns {@code true} if a two-finger rotate gesture is in progress.
     * @return {@code true} if a rotate gesture is in progress, {@code false} otherwise.
     */
    public boolean isInProgress() {
        return mGestureInProgress;
    }

    /**
     * Get the X coordinate of the current gesture's focal point.
     * If a gesture is in progress, the focal point is directly between
     * the two pointers forming the gesture.
     * If a gesture is ending, the focal point is the location of the
     * remaining pointer on the screen.
     * If {@link #isInProgress()} would return false, the result of this
     * function is undefined.
     *
     * @return X coordinate of the focal point in pixels.
     */
    public float getFocusX() {
        return mFocusX;
    }

    /**
     * Get the Y coordinate of the current gesture's focal point.
     * If a gesture is in progress, the focal point is directly between
     * the two pointers forming the gesture.
     * If a gesture is ending, the focal point is the location of the
     * remaining pointer on the screen.
     * If {@link #isInProgress()} would return false, the result of this
     * function is undefined.
     *
     * @return Y coordinate of the focal point in pixels.
     */
    public float getFocusY() {
        return mFocusY;
    }

    /**
     * Return the current distance between the two pointers forming the
     * gesture in progress.
     *
     * @return Distance between pointers in pixels.
     */
    public float getCurrentSlope() {
        if (mCurrSlope == -1) {
            mCurrSlope = mCurrFingerDiffY / mCurrFingerDiffX;
        }
        return mCurrSlope;
    }

    /**
     * Return the previous distance between the two pointers forming the
     * gesture in progress.
     *
     * @return Previous distance between pointers in pixels.
     */
    public float getPreviousSlope() {
        if (mPrevSlope == -1) {
            mPrevSlope = mPrevFingerDiffY / mPrevFingerDiffX;
        }
        return mPrevSlope;
    }

    /**
     * Return the rotation factor from the previous rotation event to the current
     * event. This value is defined as
     * ({@link #getCurrentAngle()} / {@link #getPreviousAngle()}).
     *
     * @return The current rotation factor.
     */
    public float getRotation() {
        if (mRotation == -1) {
            float prevAngle =
                (float) Math.atan2(mPrevFingerDiffY, mPrevFingerDiffX);
            float currAngle =
                (float) Math.atan2(mCurrFingerDiffY, mCurrFingerDiffX);

            mRotation = (float) ((currAngle - prevAngle) * 180 / Math.PI);
        }

        return mRotation;
    }

    /**
     * Return the time difference in milliseconds between the previous
     * accepted rotation event and the current rotation event.
     *
     * @return Time difference since the last rotation event in milliseconds.
     */
    public long getTimeDelta() {
        return mTimeDelta;
    }

    /**
     * Return the event time of the current event being processed.
     *
     * @return Current event time in milliseconds.
     */
    public long getEventTime() {
        return mCurrEvent.getEventTime();
    }
}
