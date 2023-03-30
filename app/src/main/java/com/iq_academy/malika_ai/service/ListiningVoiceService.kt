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
import com.iq_academy.malika_ai.utils.Extensions.similarity
import com.iq_academy.malika_ai.utils.Extensions.testAnswer
import com.iq_academy.malika_ai.utils.Extensions.testQuestion
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


@AndroidEntryPoint
class ListiningVoiceService : Service() {
    private val CHANNEL_ID = "ListiningVoiceService"
    private val NOTIFICATION_ID = 123

    var isMAzure = false
    private val googleTranslate = "AIzaSyANxBBWpXuCV1s7eLnB6Ox3MZXUTscNH8M"

    // Replace below with your own subscription key
    private val azureSubscriptionKey = "0d81028223814e53ba6ecfea0a8ad008"

    // Replace below with your own service region (e.g., "westus").
    private val azureServiceRegion = "eastus"
    var soundListener: SoundListener? = null

    private lateinit var chat: Chat

    @Inject
    lateinit var mainRepository: MainRepository

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

                voiceAnalysis()
            }
        }
        soundListener!!.startDetecting()
    }

    private fun voiceAnalysis() {

        CoroutineScope(Dispatchers.IO).launch {

            var result = startMicrosoftAzure()
            Log.d("DscsDDdfssf", "initViews:RDEYGTHREYHR ${result}")

            chat.userQuestion = result
            chat.userTime = getCurrentTime()

            testQuestion().forEach { question ->
                if (similarity(question, result) > 0.4) {
                    textToSpeech(testAnswer())
                    isMAzure = false
                    return@launch
                }
            }

            getApps().forEach { appinfo ->

                if (checkApp(result.uppercase(), appinfo.appname!!.uppercase())) {
                    Log.d("DscsDDdfssf", "initViews:RDEYGTHREYHR ${appinfo.appname}")
                    Log.d("DscsDDdfssf", "initViews:RDEYGTHREYHR ${appinfo.pname}")

                    val intent = packageManager.getLaunchIntentForPackage(appinfo.pname!!)
                    intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                    isMAzure = false
                    return@launch
                }
            }

            if (result.length < 15 || !checkApp(result.uppercase(), "Malika".uppercase())) {
                Log.d("DscsDDdfssf", "initViews:RDEYGTHdxscscREYHR ${result}")
                isMAzure = false
                return@launch
            } else {
                result = result.substring(7, result.length)
            }

            result =
                decodeString(result) + " Bu savolga eng ko'pi bilan 3 ta gap bilan javob qaytar"

            val translatedTextUzToEng = translateText(result!!, "en")
            // Update UI with translated text
            Log.d("DscsDDdfssf", "initViews: ${translatedTextUzToEng}")

            var responseGatCHPT = chatGPTApi(translatedTextUzToEng)

            var translatedTextEngToUz = translateText(responseGatCHPT, "uz")

            translatedTextEngToUz = decodeString2(translatedTextEngToUz)
            translatedTextEngToUz = decodeString(translatedTextEngToUz)

            Log.d("DscsDDdfssf", "initViews: translatedTextEngToUz ${translatedTextEngToUz}")

            chat.aiAnswer = translatedTextEngToUz
            chat.aiTime = getCurrentTime()
            chat.chatId = System.currentTimeMillis().toInt()

            mainRepository.insertChatToDB(chat)
            chat = Chat()

            textToSpeech(translatedTextEngToUz)
        }
    }

    private fun startMicrosoftAzure(): String {
        val config: SpeechConfig = SpeechConfig.fromSubscription(
            azureSubscriptionKey,
            azureServiceRegion
        )

        config.setSpeechRecognitionLanguage("uz-UZ")
        config.setProfanity(ProfanityOption.Raw)

        val reco = SpeechRecognizer(config)
        val task: Future<SpeechRecognitionResult> = reco.recognizeOnceAsync()

        var result: SpeechRecognitionResult? = null
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
                val mess = Message(role = "user", content = translatedTextUzToEng)
                var messageList: ArrayList<Message> = arrayListOf(mess)

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
        )

        speechConfig.setProperty(PropertyId.SpeechServiceConnection_SynthLanguage, "uz-UZ")

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