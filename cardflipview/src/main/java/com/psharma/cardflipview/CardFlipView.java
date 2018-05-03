package com.psharma.cardflipview;

/*
 * Copyright (c) psharma, 2018.
 * All rights reserved.
 */

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class CardFlipView extends FrameLayout {

    public static final int CARD_FLIP_DURATION = 400;
    private int animCardFlipHorizontalOut = R.animator.card_animation_horizontal_flip_out;
    private int animCardFlipHorizontalIn = R.animator.card_animation_horizontal_flip_in;
    private int animCardFlipVerticalOut = R.animator.card_animation_vertical_flip_out;
    private int animCardFlipVerticalIn = R.animator.card_animation_vertical_flip_in;

    public enum CardFlipState {
        CARD_FRONT_SIDE, CARD_BACK_SIDE
    }

    public static class CardFlipType {
        public static int HORIZONTAL = 0;
        public static int VERTICAL = 1;
    }

    private AnimatorSet mSetRightOut;
    private AnimatorSet mSetLeftIn;
    private AnimatorSet mSetTopOut;
    private AnimatorSet mSetBottomIn;
    private boolean mCardBackVisible = false;
    private View mCardFrontLayout;
    private View mCardBackLayout;
    private int cardFlipType = CardFlipType.VERTICAL;

    private boolean cardFlipOnTouch;
    private int cardFlipDuration;
    private boolean cardFlipEnabled;

    private Context context;
    private float x1;
    private float y1;

    private CardFlipState mCardFlipState = CardFlipState.CARD_FRONT_SIDE;

    private OnCardFlipAnimationListener onCardFlipAnimationListener = null;

    public CardFlipView(Context context) {
        super(context);
        this.context = context;
        init(context, null);
    }

    public CardFlipView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        // Setting Default Values
        cardFlipOnTouch = true;
        cardFlipDuration = CARD_FLIP_DURATION;
        cardFlipEnabled = true;
        cardFlipType = CardFlipType.VERTICAL;

        // Check for the attributes
        if (attrs != null) {
            // Attribute initialization
            final TypedArray attrArray = context.obtainStyledAttributes(attrs, R.styleable.card_flip_view, 0, 0);
            try {
                cardFlipOnTouch = attrArray.getBoolean(R.styleable.card_flip_view_cardFlipOnTouch, true);
                cardFlipDuration = attrArray.getInt(R.styleable.card_flip_view_cardFlipDuration, CARD_FLIP_DURATION);
                cardFlipEnabled = attrArray.getBoolean(R.styleable.card_flip_view_cardFlipEnabled, true);
                cardFlipType = attrArray.getInt(R.styleable.card_flip_view_cardFlipType, CardFlipType.HORIZONTAL);
            } finally {
                attrArray.recycle();
            }
        }

        loadAnimations();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        if (getChildCount() > 2) {
            throw new IllegalStateException("CardFlipView can host only two direct children!");
        }

        findViews();
        changeCameraDistance();
    }

    @Override
    public void addView(View v, int pos, ViewGroup.LayoutParams params) {
        if (getChildCount() == 2) {
            throw new IllegalStateException("CardFlipView can host only two direct children!");
        }

        super.addView(v, pos, params);

        findViews();
        changeCameraDistance();
    }

    @Override
    public void removeView(View v) {
        super.removeView(v);

        findViews();
    }

    @Override
    public void removeAllViewsInLayout() {
        super.removeAllViewsInLayout();

        // Reset the state
        mCardFlipState = CardFlipState.CARD_FRONT_SIDE;

        findViews();
    }

    private void findViews() {
        // Invalidation since we use this also on removeView
        mCardBackLayout = null;
        mCardFrontLayout = null;

        int childs = getChildCount();
        if (childs < 1) {
            return;
        }

        if (childs < 2) {
            // Only invalidate flip state if we have a single child
            mCardFlipState = CardFlipState.CARD_FRONT_SIDE;

            mCardFrontLayout = getChildAt(0);
        } else if (childs == 2) {
            mCardFrontLayout = getChildAt(1);
            mCardBackLayout = getChildAt(0);
        }

        if (!isCardFlipOnTouch()) {
            mCardFrontLayout.setVisibility(VISIBLE);

            if (mCardBackLayout != null) {
                mCardBackLayout.setVisibility(GONE);
            }
        }
    }

    private void loadAnimations() {
        if (cardFlipType == CardFlipType.HORIZONTAL) {
            mSetRightOut =
                    (AnimatorSet) AnimatorInflater.loadAnimator(this.context, animCardFlipHorizontalOut);
            mSetLeftIn =
                    (AnimatorSet) AnimatorInflater.loadAnimator(this.context, animCardFlipHorizontalIn);
            if (mSetRightOut == null || mSetLeftIn == null) {
                throw new RuntimeException(
                        "No Animations Found! Please set Flip in and Flip out animation Ids.");
            }

            mSetRightOut.removeAllListeners();
            mSetRightOut.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {

                    if (mCardFlipState == CardFlipState.CARD_FRONT_SIDE) {
                        mCardBackLayout.setVisibility(GONE);
                        mCardFrontLayout.setVisibility(VISIBLE);

                        if (onCardFlipAnimationListener != null)
                            onCardFlipAnimationListener.onCardFlipCompleted(CardFlipView.this, CardFlipState.CARD_FRONT_SIDE);
                    } else {
                        mCardBackLayout.setVisibility(VISIBLE);
                        mCardFrontLayout.setVisibility(GONE);

                        if (onCardFlipAnimationListener != null)
                            onCardFlipAnimationListener.onCardFlipCompleted(CardFlipView.this, CardFlipState.CARD_BACK_SIDE);
                    }
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            setCardFlipDuration(cardFlipDuration);
        } else {
            mSetTopOut = (AnimatorSet) AnimatorInflater.loadAnimator(this.context, animCardFlipVerticalOut);
            mSetBottomIn =
                    (AnimatorSet) AnimatorInflater.loadAnimator(this.context, animCardFlipVerticalIn);

            if (mSetTopOut == null || mSetBottomIn == null) {
                throw new RuntimeException(
                        "No Animations Found! Please set Flip in and Flip out animation Ids.");
            }

            mSetTopOut.removeAllListeners();
            mSetTopOut.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {

                    if (mCardFlipState == CardFlipState.CARD_FRONT_SIDE) {
                        mCardBackLayout.setVisibility(GONE);
                        mCardFrontLayout.setVisibility(VISIBLE);

                        if (onCardFlipAnimationListener != null)
                            onCardFlipAnimationListener.onCardFlipCompleted(CardFlipView.this, CardFlipState.CARD_FRONT_SIDE);
                    } else {
                        mCardBackLayout.setVisibility(VISIBLE);
                        mCardFrontLayout.setVisibility(GONE);

                        if (onCardFlipAnimationListener != null)
                            onCardFlipAnimationListener.onCardFlipCompleted(CardFlipView.this, CardFlipState.CARD_BACK_SIDE);
                    }
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            setCardFlipDuration(cardFlipDuration);
        }
    }

    private void changeCameraDistance() {
        int distance = 8000;
        float scale = getResources().getDisplayMetrics().density * distance;

        if (mCardFrontLayout != null) {
            mCardFrontLayout.setCameraDistance(scale);
        }
        if (mCardBackLayout != null) {
            mCardBackLayout.setCameraDistance(scale);
        }
    }

    /**
     * Play the animation of flipping of card from one side to another
     */
    public void flipCard() {
        if (!cardFlipEnabled || getChildCount() < 2) return;

        if (cardFlipType == CardFlipType.HORIZONTAL) {
            if (mSetRightOut.isRunning() || mSetLeftIn.isRunning()) return;

            mCardBackLayout.setVisibility(VISIBLE);
            mCardFrontLayout.setVisibility(VISIBLE);

            if (mCardFlipState == CardFlipState.CARD_FRONT_SIDE) {
                // From front to back
                mSetRightOut.setTarget(mCardFrontLayout);
                mSetLeftIn.setTarget(mCardBackLayout);
                mSetRightOut.start();
                mSetLeftIn.start();
                mCardBackVisible = true;
                mCardFlipState = CardFlipState.CARD_BACK_SIDE;
            } else {
                // from back to front
                mSetRightOut.setTarget(mCardBackLayout);
                mSetLeftIn.setTarget(mCardFrontLayout);
                mSetRightOut.start();
                mSetLeftIn.start();
                mCardBackVisible = false;
                mCardFlipState = CardFlipState.CARD_FRONT_SIDE;
            }
        } else {
            if (mSetTopOut.isRunning() || mSetBottomIn.isRunning()) return;

            mCardBackLayout.setVisibility(VISIBLE);
            mCardFrontLayout.setVisibility(VISIBLE);

            if (mCardFlipState == CardFlipState.CARD_FRONT_SIDE) {
                // From front to back
                mSetTopOut.setTarget(mCardFrontLayout);
                mSetBottomIn.setTarget(mCardBackLayout);
                mSetTopOut.start();
                mSetBottomIn.start();
                mCardBackVisible = true;
                mCardFlipState = CardFlipState.CARD_BACK_SIDE;
            } else {
                // from back to front
                mSetTopOut.setTarget(mCardBackLayout);
                mSetBottomIn.setTarget(mCardFrontLayout);
                mSetTopOut.start();
                mSetBottomIn.start();
                mCardBackVisible = false;
                mCardFlipState = CardFlipState.CARD_FRONT_SIDE;
            }
        }
    }

    /**
     * Play the animation of flipping of card from one side to another with or without animation.
     *
     * @param withAnimation true means flip view with animation otherwise without animation.
     */
    public void flipCard(boolean withAnimation) {
        if (getChildCount() < 2) return;

        if (cardFlipType == CardFlipType.HORIZONTAL) {
            if (!withAnimation) {
                mSetLeftIn.setDuration(0);
                mSetRightOut.setDuration(0);
                boolean oldFlipEnabled = cardFlipEnabled;
                cardFlipEnabled = true;

                flipCard();

                mSetLeftIn.setDuration(cardFlipDuration);
                mSetRightOut.setDuration(cardFlipDuration);
                cardFlipEnabled = oldFlipEnabled;
            } else {
                flipCard();
            }
        } else {
            if (!withAnimation) {
                mSetBottomIn.setDuration(0);
                mSetTopOut.setDuration(0);
                boolean oldFlipEnabled = cardFlipEnabled;
                cardFlipEnabled = true;

                flipCard();

                mSetBottomIn.setDuration(cardFlipDuration);
                mSetTopOut.setDuration(cardFlipDuration);
                cardFlipEnabled = oldFlipEnabled;
            } else {
                flipCard();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (isEnabled() && cardFlipOnTouch) {
            this.getParent().requestDisallowInterceptTouchEvent(true);
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x1 = event.getX();
                    y1 = event.getY();
                    return true;
                case MotionEvent.ACTION_UP:
                    float x2 = event.getX();
                    float y2 = event.getY();
                    float dx = x2 - x1;
                    float dy = y2 - y1;
                    float MAX_CLICK_DISTANCE = 0.5f;
                    if ((dx >= 0 && dx < MAX_CLICK_DISTANCE) && (dy >= 0 && dy < MAX_CLICK_DISTANCE)) {
                        flipCard();
                    }
                    return true;
            }
        } else {
            return super.onTouchEvent(event);
        }
        return super.onTouchEvent(event);
    }

    /**
     * Whether view is set to card flip on touch or not.
     *
     * @return true or false
     */
    public boolean isCardFlipOnTouch() {
        return cardFlipOnTouch;
    }

    /**
     * Set whether card should be flipped on touch or not!
     *
     * @param cardFlipOnTouch value (true or false)
     */
    public void setCardFlipOnTouch(boolean cardFlipOnTouch) {
        this.cardFlipOnTouch = cardFlipOnTouch;
    }

    /**
     * Returns duration of card flip in milliseconds!
     *
     * @return duration in milliseconds
     */
    public int getCardFlipDuration() {
        return cardFlipDuration;
    }

    /**
     * Sets the card flip duration (in milliseconds)
     *
     * @param cardFlipDuration duration in milliseconds
     */
    public void setCardFlipDuration(int cardFlipDuration) {
        this.cardFlipDuration = cardFlipDuration;
        if (cardFlipType == CardFlipType.HORIZONTAL) {
            //mSetRightOut.setDuration(cardFlipDuration);
            mSetRightOut.getChildAnimations().get(0).setDuration(cardFlipDuration);
            mSetRightOut.getChildAnimations().get(1).setStartDelay(cardFlipDuration / 2);

            //mSetLeftIn.setDuration(cardFlipDuration);
            mSetLeftIn.getChildAnimations().get(1).setDuration(cardFlipDuration);
            mSetLeftIn.getChildAnimations().get(2).setStartDelay(cardFlipDuration / 2);
        } else {
            mSetTopOut.getChildAnimations().get(0).setDuration(cardFlipDuration);
            mSetTopOut.getChildAnimations().get(1).setStartDelay(cardFlipDuration / 2);

            mSetBottomIn.getChildAnimations().get(1).setDuration(cardFlipDuration);
            mSetBottomIn.getChildAnimations().get(2).setStartDelay(cardFlipDuration / 2);
        }
    }

    /**
     * Returns whether card flip is enabled or not!
     *
     * @return true or false
     */
    public boolean isCardFlipEnabled() {
        return cardFlipEnabled;
    }

    /**
     * Enable / Disable card flip.
     *
     * @param cardFlipEnabled true or false
     */
    public void setCardFlipEnabled(boolean cardFlipEnabled) {
        this.cardFlipEnabled = cardFlipEnabled;
    }

    /**
     * Returns which card flip state is currently on.
     *
     * @return current state of card flip view
     */
    public CardFlipState getCurrentFlipState() {
        return mCardFlipState;
    }

    /**
     * Returns true if the front side of card flip view is visible.
     *
     * @return true if the front side of card flip view is visible.
     */
    public boolean isFrontSide() {
        return (mCardFlipState == CardFlipState.CARD_FRONT_SIDE);
    }

    /**
     * Returns true if the back side of card flip view is visible.
     *
     * @return true if the back side of card flip view is visible.
     */
    public boolean isBackSide() {
        return (mCardFlipState == CardFlipState.CARD_BACK_SIDE);
    }

    /**
     * Returns the current OnCardFlipAnimationListener. Null if no listener is set.
     *
     * @return Returns the current OnCardFlipAnimationListener. Null if no listener is set.
     */
    public OnCardFlipAnimationListener getOnCardFlipAnimationListener() {
        return onCardFlipAnimationListener;
    }

    /**
     * Sets the OnCardFlipAnimationListener for the view
     *
     * @param onCardFlipAnimationListener
     */
    public void setOnCardFlipAnimationListener(OnCardFlipAnimationListener onCardFlipAnimationListener) {
        this.onCardFlipAnimationListener = onCardFlipAnimationListener;
    }

    /**
     * Returns true if the Flip Type of animation is CardFlipType.HORIZONTAL?
     */
    public boolean isHorizontalType() {
        return cardFlipType == CardFlipType.HORIZONTAL;
    }

    /**
     * Returns true if the Flip Type of animation is CardFlipType.VERTICAL?
     */
    public boolean isVerticalType() {
        return cardFlipType == CardFlipType.VERTICAL;
    }

    /**
     * Sets the Flip Type of animation to CardFlipType.HORIZONTAL
     */
    public void setToHorizontalType() {
        cardFlipType = CardFlipType.HORIZONTAL;
    }

    /**
     * Sets the Flip Type of animation to CardFlipType.VERTICAL
     */
    public void setToVerticalType() {
        cardFlipType = CardFlipType.VERTICAL;
    }

    /**
     * The Card Flip Animation Listener for animations and flipping complete listeners
     */
    public interface OnCardFlipAnimationListener {
        /**
         * Called when Card flip animation is completed.
         *
         * @param cardFlipView   The current CardFlipView instance
         * @param newCurrentSide After animation, the new side of the view. Either can be
         *                       CardFlipState.CARD_FRONT_SIDE or CardFlipState.CARD_BACK_SIDE
         */
        void onCardFlipCompleted(CardFlipView cardFlipView, CardFlipState newCurrentSide);
    }
}
