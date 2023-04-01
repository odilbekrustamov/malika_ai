package com.iq_academy.malika_ai.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.cloud.translate.Translate
import com.google.cloud.translate.TranslateOptions
import com.iq_academy.malika_ai.R
import com.iq_academy.malika_ai.helper.SoundListener
import com.iq_academy.malika_ai.model.AppInfo
import com.iq_academy.malika_ai.model.openia.ChatGPTRequest
import com.iq_academy.malika_ai.model.openia.Message
import com.iq_academy.malika_ai.model.room.Chat
import com.iq_academy.malika_ai.repository.MainRepository
import com.iq_academy.malika_ai.utils.Extensions.checkApp
import com.iq_academy.malika_ai.utils.Extensions.decodeString
import com.iq_academy.malika_ai.utils.Extensions.decodeString2
import com.iq_academy.malika_ai.utils.Extensions.getCurrentTime
import com.iq_academy.malika_ai.utils.Extensions.getSentanse
import com.iq_academy.malika_ai.utils.Extensions.similarity
import com.iq_academy.malika_ai.utils.Extensions.testAnswer
import com.iq_academy.malika_ai.utils.Extensions.testAnswerEng
import com.iq_academy.malika_ai.utils.Extensions.testAnswerRu
import com.iq_academy.malika_ai.utils.Extensions.testQuestion
import com.iq_academy.malika_ai.utils.Extensions.testQuestionEng
import com.iq_academy.malika_ai.utils.Extensions.testQuestionRu
import com.iq_academy.malika_ai.utils.KeyValue.LANGUAGE
import com.iq_academy.malika_ai.utils.SharedPref
import com.microsoft.cognitiveservices.speech.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.annotations.Nullable
import java.util.concurrent.ExecutionException
import java.util.concurrent.Future
import javax.inject.Inject

/**
 * Rustamov Odilbek, Android developer
 * 28/03/2023  +998-91-775-17-79
 */

@AndroidEntryPoint
class ListiningVoiceService : Service() {
    private val CHANNEL_ID = "ListiningVoiceService"
    private val NOTIFICATION_ID = 123
    private var language = "uz"
    var isMAzure = false
    private val googleTranslate = "AIzaSyANxBBWpXuCV1s7eLnB6Ox3MZXUTscNH8M"

    // Replace below with your own subscription key
    private val azureSubscriptionKey = "23902f50272446568b8acefd04a76353"

    // Replace below with your own service region (e.g., "westus").
    private val azureServiceRegion = "eastus"
    var soundListener: SoundListener? = null
    private lateinit var chat: Chat

    @Inject
    lateinit var mainRepository: MainRepository

    @Inject
    lateinit var sharedPref: SharedPref

    override fun onCreate() {
        super.onCreate()
        chat = Chat()

        if (soundListener == null) {
            recorderVoise()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Create and show a notification for the foreground service
        val notification: Notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)

        return START_REDELIVER_INTENT
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    @Nullable
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotification(): Notification {
        // Create a notification channel for the foreground service
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "My Foreground Service",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(channel)
        }

        // Create and return the notification for the foreground service
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Malika AI")
            .setContentText("Biz sizni savollarizga javob beramiz")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        return builder.build()
    }

    fun recorderVoise() {
        soundListener = SoundListener { isSoundHeard ->
            if (isInternet() && !isMAzure) {
                isMAzure = true

                language = sharedPref.getLanguage(LANGUAGE)
                voiceAnalysis()
            }
        }
        soundListener!!.startDetecting()
    }

    private fun voiceAnalysis() {

        CoroutineScope(Dispatchers.IO).launch {

            var result = startMicrosoftAzure()

            chat.userQuestion = result
            chat.userTime = getCurrentTime()

            result = decodeString(result)
            result = decodeString2(result)

            if (result.length < 15 ||
                !checkApp(result.uppercase(), "Malika".uppercase()) && !checkApp(
                    result.uppercase(),
                    "Malaka".uppercase()
                ) &&
                !checkApp(result.uppercase(), "Малика".uppercase()) && !checkApp(
                    result.uppercase(),
                    "Малики".uppercase()
                ) &&
                !checkApp(
                    result.uppercase(),
                    "Malikia".uppercase()
                ) && !checkApp(result.uppercase(), "Маленькие".uppercase())
            ) {
                isMAzure = false
                return@launch
            } else {
                result = result.substring(7, result.length)
            }

            if (language == "uz") {
                testQuestion().forEach { question ->
                    if (similarity(question, result) > 0.4) {
                        textToSpeech(testAnswer())
                        isMAzure = false
                        return@launch
                    }
                }

                getApps().forEach { appinfo ->
                    if (checkApp(result.uppercase(), appinfo.appname!!.uppercase())) {

                        val intent =
                            applicationContext.packageManager.getLaunchIntentForPackage(appinfo.pname!!)
                        intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)

                        isMAzure = false
                        return@launch
                    }
                }

                result = translateText(result, "en")
            }

            if (language == "ru") {
                testQuestionRu().forEach { question ->
                    if (similarity(question, result) > 0.4) {
                        textToSpeech(testAnswerRu())
                        isMAzure = false
                        return@launch
                    }
                }
            }

            if (language == "en") {
                testQuestionEng().forEach { question ->
                    if (similarity(question, result) > 0.4) {
                        textToSpeech(testAnswerEng())
                        isMAzure = false
                        return@launch
                    }
                }
            }


            var responseGatCHPT = chatGPTApi(result)

            if (language == "uz") {
                responseGatCHPT = translateText(responseGatCHPT, language)
            }

            responseGatCHPT = decodeString2(responseGatCHPT)
            responseGatCHPT = decodeString(responseGatCHPT)


            chat.aiAnswer = responseGatCHPT
            chat.aiTime = getCurrentTime()
            chat.chatId = System.currentTimeMillis().toInt()


            textToSpeech(getSentanse(responseGatCHPT))

            mainRepository.insertChatToDB(chat)
            chat = Chat()
        }
    }

    private fun startMicrosoftAzure(): String {
        val config: SpeechConfig = SpeechConfig.fromSubscription(
            azureSubscriptionKey,
            azureServiceRegion
        )

        if (language == "uz") {
            config.setSpeechRecognitionLanguage("uz-UZ")
        } else if (language == "en") {
            config.setSpeechRecognitionLanguage("en-US")
        } else if (language == "ru") {
            config.setSpeechRecognitionLanguage("ru-RU")
        }

        config.setProfanity(ProfanityOption.Raw)

        val reco = SpeechRecognizer(config)
        val task: Future<SpeechRecognitionResult> = reco.recognizeOnceAsync()

        val result: SpeechRecognitionResult?
        result = try {
            task.get()
        } catch (e: ExecutionException) {
            throw RuntimeException(e)
        } catch (e: InterruptedException) {
            throw RuntimeException(e)
        }

        if (result!!.getReason() === ResultReason.RecognizedSpeech) {
            return result!!.text
        }
        return ""
    }

    suspend fun translateText(textToTranslate: String, language: String): String {
        return withContext(Dispatchers.IO) {
            try {
                val translateOptions = TranslateOptions.newBuilder()
                    .setApiKey(googleTranslate)
                    .build()
                val translate = translateOptions.service
                val translation = translate.translate(
                    textToTranslate,
                    Translate.TranslateOption.targetLanguage(language)
                )
                translation.translatedText
            } catch (e: Exception) {
                e.printStackTrace()
                ""
            }
        }
    }

    suspend fun chatGPTApi(translatedTextUzToEng: String): String {
        return withContext(Dispatchers.IO) {
            try {
                val messageList: ArrayList<Message> = arrayListOf()

                val chatList = mainRepository.getAllchats()

                val size = chatList.size
                for (i in 1..size) {
                    val messUser = Message(role = "user", content = chatList[size - i].userQuestion)
                    val messAssistan =
                        Message(role = "assistant", content = chatList[size - i].aiAnswer)
                    messageList.add(messUser)
                    messageList.add(messAssistan)
                }
                val mess = Message(role = "user", content = translatedTextUzToEng)
                messageList.add(mess)

                messageList.add(mess)

                val chatGPTRequest = ChatGPTRequest(model = "gpt-3.5-turbo", messages = messageList)

                val res = mainRepository.getQuestion(chatGPTRequest)
                Log.d("DscsDDdfssf", "onStartCommand: ${res}")

                res.choices[0].message!!.content!!
            } catch (e: Exception) {
                e.printStackTrace()
                ""
            }
        }
    }

    fun textToSpeech(translatedTextEngToUz: String) {
        // Initialize speech synthesizer and its dependencies
        val speechConfig = SpeechConfig.fromSubscription(
            azureSubscriptionKey,
            azureServiceRegion
        )!!

        if (language == "uz") {
            speechConfig.setProperty(PropertyId.SpeechServiceConnection_SynthLanguage, "uz-UZ")
        } else if (language == "en") {
            speechConfig.setProperty(PropertyId.SpeechServiceConnection_SynthLanguage, "en-US")
        } else if (language == "ru") {
            speechConfig.setProperty(PropertyId.SpeechServiceConnection_SynthLanguage, "ru-RU")
        }

        assert(speechConfig != null)

        val synthesizer = SpeechSynthesizer(speechConfig)
        assert(synthesizer != null)

        val result = synthesizer.SpeakText(translatedTextEngToUz)!!


        if (result.reason == ResultReason.SynthesizingAudioCompleted) {

        } else if (result.reason == ResultReason.Canceled) {

            SpeechSynthesisCancellationDetails.fromResult(result).toString()

        }
        result.close()

        isMAzure = false
    }

    //check internet connect
    fun isInternet(): Boolean {
        val cm =
            applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                ?: return false // assume network is down if ConnectivityManager is null
        val wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        if (wifiNetwork != null && wifiNetwork.isConnected) {
            return true // WiFi is connected
        }
        val mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
        return if (mobileNetwork != null && mobileNetwork.isConnected) {
            true // Mobile internet is connected
        } else false
        // Neither WiFi nor mobile internet is connected
    }

    fun getApps(): ArrayList<AppInfo> {
        val apps = packageManager.getInstalledPackages(0)

        val res = ArrayList<AppInfo>()
        for (i in apps.indices) {
            val p = apps[i]
            val newInfo = AppInfo()
            newInfo.appname = p.applicationInfo.loadLabel(packageManager).toString()
            newInfo.pname = p.packageName
            res.add(newInfo)
        }

        return res;
    }
}