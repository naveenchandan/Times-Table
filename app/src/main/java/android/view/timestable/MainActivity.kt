package android.view.timestable

import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), TimesTableView.TimesValueCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        view_times_table.setTimesValueCallback(this)

        seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (!isProgrammaticSeek) {
                    Log.d("MainActivity", "onProgressChanged: ")
                    text_times.text = "${progress / 10.0f}"
                    view_times_table.times = progress / 10.0f
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })

        text_animate.setOnClickListener {
            if (view_times_table.isAnimationRunning()) {
                view_times_table.cancelAnimation()
            } else {
                view_times_table.animateTimesValue()
            }
        }

    }

    var isProgrammaticSeek = false

    override fun onTimesValueUpdated(times: Float) {
        text_times.text = "${Math.round(times * 10.0f) / 10.0f}"
        seekbar.progress = times.toInt() * 10
    }

    override fun onTimesValueAnimationStarted() {
        Log.d("MainActivity", "onTimesValueAnimationStarted: ")
        isProgrammaticSeek = true
        text_animate.text = "Cancel"
    }

    override fun onTimesValueAnimationEnded() {
        Log.d("MainActivity", "onTimesValueAnimationEnded: ")
        isProgrammaticSeek = false
        text_animate.text = "Animate"
    }
}