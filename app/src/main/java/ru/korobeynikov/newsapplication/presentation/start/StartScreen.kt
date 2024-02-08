package ru.korobeynikov.newsapplication.presentation.start

import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.korobeynikov.newsapplication.databinding.ActivityStartScreenBinding
import ru.korobeynikov.newsapplication.presentation.base.BaseActivity

class StartScreen : AppCompatActivity(), BaseActivity<ActivityStartScreenBinding> {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = getBinding()
        setContentView(binding.root)
        binding.lottieAnimation.addAnimatorListener(object : Animator.AnimatorListener {

            override fun onAnimationStart(p0: Animator) {}

            override fun onAnimationEnd(p0: Animator) {
                startActivity(Intent(this@StartScreen, MainActivity::class.java))
                finish()
            }

            override fun onAnimationCancel(p0: Animator) {}

            override fun onAnimationRepeat(p0: Animator) {}
        })
    }

    override fun getBinding() = ActivityStartScreenBinding.inflate(layoutInflater)
}