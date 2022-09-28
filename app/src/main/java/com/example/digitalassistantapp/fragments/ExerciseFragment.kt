package com.example.digitalassistantapp.fragments

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.digitalassistantapp.R
import kotlinx.android.synthetic.main.fragment_exercise.*

class ExerciseFragment : Fragment() {

    private lateinit var animatorSet: AnimatorSet
    private lateinit var startAnimator : ValueAnimator
    private lateinit var inhaleAnimator : ValueAnimator
    private lateinit var exhaleAnimator : ValueAnimator
    private lateinit var finishAnimator : ValueAnimator
    private var breathing: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewCircle.setOnClickListener{
            if (!breathing) {

            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_exercise, container, false)

    }

    /*override fun startAnimation() {
        // Breathing has started.
        breathing = true

        // Setup start and finish animation.
        startAnimator = ValueAnimator.ofFloat(1f, 0f)
        startAnimator.addUpdateListener {
            val value = it.animatedValue as Float
            viewCircle.scaleX = value
            viewCircle.scaleY = value
        }
        startAnimator.duration = 1000
        finishAnimator = ValueAnimator.ofFloat(0f, 1f)
        finishAnimator.addUpdateListener {
            val value = it.animatedValue as Float
            viewCircle.scaleX = value
            viewCircle.scaleY = value
        }
        finishAnimator.duration = 1000
        // Setup inhale, exhale bundle animation.
        inhaleAnimator = ValueAnimator.ofFloat(0f, 5f)
        inhaleAnimator.addUpdateListener {
            val value = it.animatedValue as Float
            viewCircle.scaleX = value
            viewCircle.scaleY = value
        }
        exhaleAnimator = ValueAnimator.ofFloat(5f, 0f)
        exhaleAnimator.addUpdateListener {
            val value = it.animatedValue as Float
            viewCircle.scaleX = value
            viewCircle.scaleY = value
        }
        animatorSet = AnimatorSet()
        animatorSet.play(inhaleAnimator).before(exhaleAnimator)
        animatorSet.duration = 5000

        // Start animation.
        startAnimator.start()

        // Start animator listener.
        startAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {}

            override fun onAnimationEnd(p0: Animator?) {
                //performNotification()
                // Start breathing cycle animation.
                if(breathing) {
                    animatorSet.start()
                }
            }

            override fun onAnimationCancel(p0: Animator?) {}

            override fun onAnimationStart(p0: Animator?) {}

        })

        // Breathing cycle animator listener.
        animatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}

            override fun onAnimationEnd(animation: Animator) {
                if(breathing) {
                    animatorSet.start()
                } else {
                    finishAnimator.start()
                }
            }

            override fun onAnimationCancel(animation: Animator) {}

            override fun onAnimationRepeat(animation: Animator) {}

        })

        inhaleAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}

            override fun onAnimationEnd(animation: Animator) {
                //performNotification()
            }

            override fun onAnimationCancel(animation: Animator) {}

            override fun onAnimationRepeat(animation: Animator) {}
        })

        exhaleAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}

            override fun onAnimationEnd(animation: Animator) {
                //performNotification()
            }

            override fun onAnimationCancel(animation: Animator) {}

            override fun onAnimationRepeat(animation: Animator) {}
        })
    }*/

    /*override fun stopAnimation() {
        // Stop breathing.
        breathing = false

        // Fonce animation end.
        startAnimator.removeAllListeners()
        startAnimator.end()
        startAnimator.cancel()
        finishAnimator.removeAllListeners()
        finishAnimator.end()
        finishAnimator.cancel()
        inhaleAnimator.removeAllListeners()
        inhaleAnimator.end()
        inhaleAnimator.cancel()
        exhaleAnimator.removeAllListeners()
        exhaleAnimator.end()
        exhaleAnimator.cancel()
        animatorSet.end()
        animatorSet.removeAllListeners()
        animatorSet.cancel()
    }*/
}