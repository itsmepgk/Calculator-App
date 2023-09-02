package com.example.calculator

import com.example.calculator.databinding.ActivityMainBinding
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var canAddAnOperation: Boolean = false
    private var canAddADecimal: Boolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    fun numberAction(view: View) {
        if (view is Button) {
            if (view.text == ".") {
                if (canAddADecimal) {
                    binding.workingsTV.append(view.text)
                    canAddAnOperation = false
                }

                canAddADecimal = false
            } else
                binding.workingsTV.append(view.text)

            canAddAnOperation = true
        }
    }

    fun operationAction(view: View) {
        if (view is Button && canAddAnOperation) {
            binding.workingsTV.append(view.text)
            canAddAnOperation = false
            canAddADecimal = true
        }
    }

    fun allClearAction(view: View) {
        binding.workingsTV.text = ""
        binding.resultsTV.text = "0" // Set a default value
        canAddAnOperation = false
        canAddADecimal = true
    }


    fun backSpaceAction(view: View) {
        val length = binding.workingsTV.length()
        if (length > 0)
            binding.workingsTV.text = binding.workingsTV.text.subSequence(0, length - 1)
    }

    fun equalsAction(view: View) {
        binding.resultsTV.text = calculateResults()
    }

    private fun calculateResults(): String {
        val digitsOperators = digitsOperators()
        if (digitsOperators.isEmpty()) return ""

        val multiplyOrDivide =
            multiplyOrDivideCalculate(digitsOperators) // for precedence of division and multiplication
        if (multiplyOrDivide.isEmpty()) return ""

        val result = addOrSubtractCalculate(multiplyOrDivide)
        return result.toString()
    }

    private fun addOrSubtractCalculate(passedList: MutableList<Any>): Float {
        var result = passedList[0] as Float

        for (i in passedList.indices) {
            if (passedList[i] is Char && i != passedList.lastIndex) {
                val operator = passedList[i]
                val nextDigit = passedList[i + 1] as Float
                if (operator == '+')
                    result += nextDigit
                if (operator == '-')
                    result -= nextDigit
            }
        }
        return result
    }

    private fun multiplyOrDivideCalculate(passedList: MutableList<Any>): MutableList<Any> {
        var list = passedList
        while (list.contains('x') || list.contains('/')) {
            list = calcTimesDiv(list)
        }
        return list
    }

    private fun calcTimesDiv(passedList: MutableList<Any>): MutableList<Any> {
        val newList = mutableListOf<Any>()
        var restartIndex = passedList.size

        for (i in passedList.indices) {
            if (passedList[i] is Char && i != passedList.lastIndex && i < restartIndex) {
                val operator = passedList[i]
                val prevDigit = passedList[i - 1] as Float
                val nextDigit = passedList[i + 1] as Float
                when (operator) {
                    'x' -> {
                        newList.add(prevDigit * nextDigit)
                        restartIndex = i + 1
                    }

                    '/' -> {
                        newList.add(prevDigit / nextDigit)
                        restartIndex = i + 1
                    }

                    else -> {
                        newList.add(prevDigit)
                        newList.add(operator)
                    }
                }
            }

            if (i > restartIndex)
                newList.add(passedList[i])
        }

        return newList
    }

    private fun digitsOperators(): MutableList<Any> {
        val list = mutableListOf<Any>()
        var currentDigit = ""

        for (character in binding.workingsTV.text) {
            if (character.isDigit() || character == '.') {
                currentDigit += character
            } else {
                if (currentDigit.isNotEmpty()) {
                    list.add(currentDigit.toFloat())
                    currentDigit = ""
                }
                list.add(character)
            }
        }

        if (currentDigit.isNotEmpty()) {
            list.add(currentDigit.toFloat())
        }

        return list
    }


}