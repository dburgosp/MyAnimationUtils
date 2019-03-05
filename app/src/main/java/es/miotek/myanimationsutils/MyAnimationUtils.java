package es.miotek.myanimationsutils;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.ArrayList;

public class MyAnimationUtils {

    private static final boolean BOUNCE_YES = true;
    private static final boolean BOUNCE_NO = false;

    /* ************************ */
    /* Private member variables */
    /* ************************ */

    private Context mContext;
    private ArrayList<MyAnimationItem> mAnimatedViews;

    /* *********** */
    /* Constructor */
    /* *********** */

    public MyAnimationUtils(Context context) {
        mContext = context;
    }

    /* ************** */
    /* Public methods */
    /* ************** */

    /**
     * Performs a "fade out" animation on a view.
     *
     * @param view is the view to be animated.
     */
    public void fadeOut(final View view) {
        if (view != null) {
            Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.fade_out);
            view.startAnimation(anim);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    // Show view when animation starts.
                    view.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        }
    }

    /**
     * Performs a "fade in" animation on a view.
     *
     * @param view is the view to be animated.
     */
    public void fadeIn(final View view) {
        if (view != null) {
            Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.fade_in);
            view.startAnimation(anim);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    // Hide view when animation has ended.
                    view.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
    }

    /**
     * Adds a view to the current animations queue, mAnimatedViews.
     *
     * @param view      is the View to be animated and added to the current animations queue.
     * @param animResId is the resource id of the animation to be performed on the view.
     */
    public void add(View view, int animResId) {
        addToQueue(view, animResId, BOUNCE_NO);
    }

    /**
     * Adds a view to the current animations queue, mAnimatedViews. The view will be animated later
     * with a bounce effect.
     *
     * @param view      is the View to be animated and added to the current animations queue.
     * @param animResId is the resource id of the animation to be performed on the view.
     */
    public void addWithBounce(View view, int animResId) {
        addToQueue(view, animResId, BOUNCE_YES);
    }

    /**
     * Display animations for every view included in the global mAnimatedViews array.
     *
     * @param delayMillis is the delay in milliseconds to wait between two consecutive animations.
     */
    public void animate(final int delayMillis) {
        if (mAnimatedViews != null) {
            // Initially make invisible all elements in the animation queue.
            for (MyAnimationItem animatedView : mAnimatedViews) {
                animatedView.getView().setVisibility(View.INVISIBLE);
            }

            // Animate queue of initially invisible Views from the first index (0) of the list.
            animateQueue(new Handler(), delayMillis, 0);
        }
    }

    /* *************** */
    /* Private methods */
    /* *************** */

    /**
     * Adds a view to the current animations queue, mAnimatedViews.
     *
     * @param view      is the View to be animated and added to the current animations queue.
     * @param animResId is the resource id of the animation to be performed on the view.
     * @param isBounce  is true {@link MyAnimationUtils#BOUNCE_YES} if the animation must end with a
     *                  bounce effect, false {@link MyAnimationUtils#BOUNCE_NO} otherwise.
     */
    private void addToQueue(View view, int animResId, boolean isBounce) {
        if (mAnimatedViews == null) {
            // Create the animations queue.
            mAnimatedViews = new ArrayList<>();
        }
        mAnimatedViews.add(new MyAnimationItem(view, animResId, isBounce));
    }

    /**
     * Display animations for every view included in the global mAnimatedViews array.
     *
     * @param handler      is the Handler used to send and process messages and Runnable objects
     *                     associated with a thread's message queue.
     * @param delayMillis  is the delay in milliseconds to wait between two consecutive animations.
     * @param currentIndex is the index of the current animated View in the list.
     */
    private void animateQueue(final Handler handler, final int delayMillis, final int currentIndex) {
        // Create a new runnable to be added to the message queue.
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                // Set animation.
                int animResId = mAnimatedViews.get(currentIndex).getAnimResId();
                final Animation animation =
                        AnimationUtils.loadAnimation(mContext, animResId);
                if (mAnimatedViews.get(currentIndex).isBounce()) {
                    double amplitude = 0.2;
                    double frequency = 20.0;
                    animation.setInterpolator(new MyBounceInterpolator(amplitude, frequency));

                } else {
                    animation.setInterpolator(new LinearOutSlowInInterpolator());
                }

                // Animate current view.
                View view = mAnimatedViews.get(currentIndex).getView();
                view.setVisibility(View.VISIBLE);
                view.startAnimation(animation);

                // Order animation for next view, if there's still another one in the queue.
                if (currentIndex < (mAnimatedViews.size() - 1))
                    animateQueue(handler, delayMillis, currentIndex + 1);
            }
        };

        // Place the runnable in the message queue to be executed after delayMillis.
        if (!handler.postDelayed(runnable, delayMillis)) {
            Log.e("animateQueue", "Error placing the runnable in the message queue");
        }
    }

    /* ******************************************************* */
    /* Inner class for animation items in the animations queue */
    /* ******************************************************* */

    private class MyAnimationItem {
        private View view;
        private int animResId;
        private boolean isBounce;

        MyAnimationItem(View view, int animResId, boolean isBounce) {
            this.view = view;
            this.animResId = animResId;
            this.isBounce = isBounce;
        }

        View getView() {
            return view;
        }

        void setView(View view) {
            this.view = view;
        }

        int getAnimResId() {
            return animResId;
        }

        void setAnimResId(int animResId) {
            this.animResId = animResId;
        }

        boolean isBounce() {
            return isBounce;
        }

        public void setBounce(boolean bounce) {
            isBounce = bounce;
        }
    }

    /* ****************************************** */
    /* Inner class for bounce interpolator effect */
    /* ****************************************** */

    private class MyBounceInterpolator implements android.view.animation.Interpolator {
        private double mAmplitude = 1;
        private double mFrequency = 10;

        MyBounceInterpolator(double amplitude, double frequency) {
            mAmplitude = amplitude;
            mFrequency = frequency;
        }

        public float getInterpolation(float time) {
            return (float) (-1 * Math.pow(Math.E, -time / mAmplitude) *
                    Math.cos(mFrequency * time) + 1);
        }
    }
}