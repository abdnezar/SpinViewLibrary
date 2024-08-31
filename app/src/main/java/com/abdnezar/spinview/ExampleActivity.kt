package com.abdnezar.spinview

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.abdnezar.spinview.example.databinding.ActivityExampleBinding
import com.abdnezar.spinview.model.SpinItem
import java.util.ArrayList
import com.abdnezar.spinview.example.R

class ExampleActivity : AppCompatActivity() {
    private val TAG = this.javaClass.simpleName
    private lateinit var binding: ActivityExampleBinding
    private var _binding: ActivityExampleBinding? = null
    private var data: MutableList<SpinItem> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityExampleBinding.inflate(layoutInflater)
        binding = _binding!!
        setContentView(binding.root)

        getSpinItems()

        binding.spinWheel.setData(data)
        binding.spinWheel.setRound(5)
        binding.spinWheel.isTouchEnabled = true
    }

    override fun onResume() {
        super.onResume()

        binding.spinWheel.setSpinWheelRoundItemSelectedListener { index: Int ->
            Toast.makeText(this, "$index", Toast.LENGTH_SHORT).show()
        }

        binding.spinButton.setOnClickListener {
            binding.spinWheel.startSpinWheelWithTargetIndex(0)
        }

    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    private fun getSpinItems(): MutableList<SpinItem> {
        val item1 = SpinItem()
        item1.topText = "0"
        item1.icon = R.drawable.ic_gold
        item1.color = -0xc20
        data.add(item1)

        val item2 = SpinItem()
        item2.topText = "1"
        item2.icon = R.drawable.ic_gold
        item2.color = -0x1f4e
        data.add(item2)

        val item3 = SpinItem()
        item3.topText = "2"
        item3.icon = R.drawable.ic_gold
        item3.color = -0x3380
        data.add(item3)

        val item4 = SpinItem()
        item4.topText = "3"
        item4.icon = R.drawable.ic_gold
        item4.color = -0xc20
        data.add(item4)

        val item5 = SpinItem()
        item5.topText = "4"
        item5.icon = R.drawable.ic_gold
        item5.color = -0x1f4e
        data.add(item5)

        val item6 = SpinItem()
        item6.topText = "5"
        item6.icon = R.drawable.ic_gold
        item6.color = -0x3380
        data.add(item6)

        return data
    }
}