package com.example.stopwatch

import android.app.Dialog
import android.os.Bundle
import android.os.SystemClock
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Chronometer
import android.widget.NumberPicker
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.example.stopwatch.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    companion object {
        private const val MAX_TIME = 5 // Maximum minutes allowed in time picker
    }

    private var isRunning = false
    private var minute: String = "0"
    private var countdownTimeInMillis: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(binding.root)

        val lapList = ArrayList<String>()
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, lapList)
        binding.list.adapter = arrayAdapter

        // Adding a lap time to the list when the lap button is pressed
        binding.lapButton.setOnClickListener {
            if (isRunning) {
                lapList.add(binding.timer.text.toString())
                arrayAdapter.notifyDataSetChanged()
            }
        }

        // Show time picker dialog when the clock is clicked
        binding.clock.setOnClickListener {
            showTimePickerDialog()
        }

        // Start or stop the timer based on current state
        binding.runButton.setOnClickListener {
            if (!isRunning) {
                startTimer()
            } else {
                stopTimer()
            }
        }
    }

    private fun startTimer() {
        if (minute != "0") {
            countdownTimeInMillis = minute.toLong() * 60 * 1000L
            binding.timer.base = SystemClock.elapsedRealtime() + countdownTimeInMillis

            binding.timer.onChronometerTickListener = Chronometer.OnChronometerTickListener {
                val elapsedTime = SystemClock.elapsedRealtime() - binding.timer.base
                val remainingTime = countdownTimeInMillis + elapsedTime

                // Calculate minutes and seconds remaining
                val seconds = (remainingTime / 1000) % 60
                val minutes = (remainingTime / (1000 * 60)) % 60

                // Update format of the timer display
                binding.timer.text = String.format("%02d:%02d", minutes, seconds)

                if (remainingTime <= 0) {
                    stopTimer() // Stop the timer when countdown reaches zero
                }
            }

            binding.timer.start()
            isRunning = true
            binding.runButton.text = "STOP"
        }
    }

    private fun stopTimer() {
        binding.timer.stop()
        isRunning = false
        binding.runButton.text = "START"
    }

    private fun showTimePickerDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog)

        val numberPicker = dialog.findViewById<NumberPicker>(R.id.numberPicker).apply {
            minValue = 0
            maxValue = MAX_TIME
        }

        dialog.findViewById<Button>(R.id.setTime).setOnClickListener {
            minute = numberPicker.value.toString()
            binding.timerDisplay.text = "$minute:00"
            dialog.dismiss()
        }
        dialog.show()
    }
}
