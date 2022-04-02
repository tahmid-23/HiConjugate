package com.github.tahmid_23.hiconjugate.shaker

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View

const val REPEAT_LENGTH = 250L
const val REPEAT_COUNT = 10

fun shake(view: View, factor: Float = 1.0F, repeatCount: Int = REPEAT_COUNT): AnimatorSet {
    val first = ObjectAnimator
        .ofFloat(view, "translationX",
            0.0F,
            -10.0F * factor,
            -20.0F * factor,
            65.0F * factor,
            -73.0F * factor,
            -100.0F * factor,
            -90.0F * factor,
            90.0F * factor,
            -110.0F * factor,
            -85.0F * factor,
            -42.0F * factor,
            -50.0F * factor,
            20.0F * factor,
            0.0F)
    first.repeatMode = ObjectAnimator.REVERSE
    first.repeatCount = repeatCount
    first.duration = REPEAT_LENGTH
    val second = ObjectAnimator
        .ofFloat(view, "translationY",
            0.0F,
            -90.0F * factor,
            50.0F * factor,
            110.0F * factor,
            75.0F * factor,
            -10.0F * factor,
            -75.0F * factor,
            -60.0F * factor,
            90.0F * factor,
            80.0F * factor,
            85.0F * factor,
            37.0F * factor,
            -19.0F * factor,
            0.0F)
    second.repeatMode = ObjectAnimator.RESTART
    second.repeatCount = repeatCount
    second.duration = REPEAT_LENGTH

    val animatorSet = AnimatorSet()
    animatorSet.playTogether(first, second)
    animatorSet.start()

    return animatorSet
}