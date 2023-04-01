package com.iq_academy.malika_ai.utils

import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

/**
 * Rustamov Odilbek, Android developer
 * 28/03/2023  +998-91-775-17-79
 */
object Extensions {
    fun showDialog(context: Context?) {
        val dialog = AlertDialog.Builder(context)
        dialog.setTitle("Warning!")
        dialog.setMessage("You are using USB to debugging!\nPlease unplug your phone from the computer!!!")
        dialog.setCancelable(false)
        dialog.setPositiveButton(
            "Ok"
        ) { _, _ -> throw Exception() }
        dialog.show()
    }

    fun isEmulator(): Boolean {
        return (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")
                || "google_sdk" == Build.PRODUCT)
    }

    fun Fragment.snackBar(msg: String) {
        Snackbar.make(this.requireView(), msg, Snackbar.LENGTH_SHORT).show()
    }

    //time convert string
    fun getCurrentTime(): String {
        val dateFormat = SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault())
        val currentDate = Date()
        return dateFormat.format(currentDate)
    }

    fun decodeString(input: String): String {
        // Replace "dhdahb" with non-letter characters
        val result = StringBuilder()
        var i = 0
        while (i < input.length) {
            if (input.substring(i).startsWith("&#39;")) {
                result.append("'")
                i += 5
            } else {
                result.append(input[i])
                i++
            }
        }

        return result.toString()
    }

    fun decodeString2(input: String): String {
        // Replace "dhdahb" with non-letter characters
        val result = StringBuilder()
        var i = 0
        while (i < input.length) {
            if (input.substring(i).startsWith("&quot;")) {
                result.append(" ")
                i += 6
            } else {
                result.append(input[i])
                i++
            }
        }

        return result.toString()
    }

    fun checkApp(input: String, appName: String): Boolean {

        var i = 0
        while (i < input.length) {
            if (input.substring(i).startsWith(appName)) {
                return true
            } else {
                i++
            }
        }

        return false
    }

    fun testQuestion(): ArrayList<String> {
        val questions = ArrayList<String>()
        questions.add("Sen kimsan?");
        questions.add("Kimsan sen?");
        questions.add("Sen nimasan?");
        questions.add("Sen o'zi nimasan?");
        questions.add("Sen o'zi kimsan?");
        questions.add("Sen nimadan tashkil topgansan?");
        questions.add("Nimadan tashkil topgansan?");
        questions.add("Kim tomonidan yaratilgansan?");
        questions.add("Qachon tashkil topgansan?");
        questions.add("Seni kim yaratgan?");
        questions.add("Kim yaratgan?");
        questions.add("Kim seni yaratgan?");
        questions.add("Yaratgan seni?");
        questions.add("Yaratuvching kim?");
        questions.add("Kim yaratuvching?");
        questions.add("Ixtiroching kim?");
        questions.add("Seni kim dasturlagan?");
        questions.add("Dasturlashni kim senga o'rgatgan?");
        questions.add("O'qishni senga kim o'rgatgan?");
        questions.add("Yozishni senga kim o'rgatgan?");
        questions.add("Buncha narsani senga kim o'rgatgan?");

        return questions
    }

    fun testQuestionRu(): ArrayList<String> {
        val questions = ArrayList<String>()
        questions.add("Кто ты?");
        questions.add("Ты кто?");
        questions.add("Ты что?");
        questions.add("Ты кто?");
        questions.add("из чего ты сделан??");
        questions.add("из чего ты сделан??");
        questions.add("кем вы были созданы?");
        questions.add("из чего ты сделан??");
        questions.add("когда вы образовались??");
        questions.add("кто сделал тебя?");
        questions.add("кто сделал?");
        questions.add("сделал тебя?");
        questions.add("кто твой создатель?");
        questions.add("кто твой изобретатель?");
        questions.add("кто изобрел тебя?");
        questions.add("кто тебя запрограммировал?");
        questions.add("Кто научил тебя программировать?");
        questions.add("Кто научил тебя программировать?");
        questions.add("кто научил тебя читать?");
        questions.add("кто научил тебя читать?");
        return questions
    }

    fun testQuestionEng(): ArrayList<String> {
        val questions = ArrayList<String>()
        questions.add("Who are you?");
        questions.add("What are you?");
        questions.add("Who are you?");
        questions.add("What are you made of?");
        questions.add("What are you made of?");
        questions.add("Who created you?");
        questions.add("When were you founded?");
        questions.add("Who made you?");
        questions.add("Who created?");
        questions.add("Who created you?");
        questions.add("Who created you?");
        questions.add("Who is your creator?");
        questions.add("Who is your creator?");
        questions.add("Who is your invention?");
        questions.add("Who programmed you?");
        questions.add("Who taught you programming?");
        questions.add("Who taught you to read?");
        questions.add("Who taught you to write?");
        questions.add("Who taught you all these things?");

        return questions
    }

    fun testAnswer(): String {
        return "Men IQ academy tomonidan yaratilgan Malika ismli sun'iy intellektman. Vazifam sizga yordamchi bo'lish, qulaylik yaratish va og'iringizni yengil qilishdan iborat.\n" +
                "Menga o'z savollaringizni berishingiz mumkin. Turli fan va sohalar doirasida o'z bilimlarimdan foydalangan holda javob beraman."
    }

    fun testAnswerRu(): String {
        return "Я искусственный интеллект по имени Малика созданный \"IQ Academy\".  Моя работа состоит в том, чтобы помочь вам, сделать вас удобными и облегчить ваше бремя.\n" +
                " Вы можете задать мне свои вопросы.  Я отвечу, используя свои знания в различных дисциплинах и областях."
    }

    fun testAnswerEng(): String {
        return "I am an artificial intelligence named \"Malika\" created by \"IQ Academy\". My function is to help you, create comfort and lighten your burden.\n" +
                "You can ask me your questions. I will answer using my knowledge in various fields."
    }


    fun similarity(s1: String, s2: String): Double {
        var longer = s1
        var shorter = s2
        if (s1.length < s2.length) { // longer should always have greater length
            longer = s2
            shorter = s1
        }
        val longerLength = longer.length
        return if (longerLength == 0) {
            1.0
        } else (longerLength - editDistance(longer, shorter)) / longerLength.toDouble()
    }

    fun editDistance(s1: String, s2: String): Int {
        var s1 = s1
        var s2 = s2
        s1 = s1.lowercase(Locale.getDefault())
        s2 = s2.lowercase(Locale.getDefault())
        val costs = IntArray(s2.length + 1)
        for (i in 0..s1.length) {
            var lastValue = i
            for (j in 0..s2.length) {
                if (i == 0) costs[j] = j else {
                    if (j > 0) {
                        var newValue = costs[j - 1]
                        if (s1[i - 1] != s2[j - 1]) newValue = Math.min(
                            Math.min(newValue, lastValue),
                            costs[j]
                        ) + 1
                        costs[j - 1] = lastValue
                        lastValue = newValue
                    }
                }
            }
            if (i > 0) costs[s2.length] = lastValue
        }
        return costs[s2.length]
    }

    fun getSentanse(input: String): String {
        val sentences = input.split(". ")

        val firstFourSentences = sentences.take(4)

        val output = firstFourSentences.joinToString(". ")
        Log.d("DscsDDdfssf", "initViews: output ${output}")
        return output
    }
}