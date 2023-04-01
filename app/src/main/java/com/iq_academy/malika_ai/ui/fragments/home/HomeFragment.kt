package com.iq_academy.malika_ai.ui.fragments.home

import android.app.ActivityManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.cloud.translate.Translate
import com.google.cloud.translate.TranslateOptions
import com.iq_academy.malika_ai.R
import com.iq_academy.malika_ai.adapter.ChatAdapter
import com.iq_academy.malika_ai.databinding.FragmentHomeBinding
import com.iq_academy.malika_ai.model.openia.ChatGPTRequest
import com.iq_academy.malika_ai.model.openia.Message
import com.iq_academy.malika_ai.model.room.Chat
import com.iq_academy.malika_ai.service.ListiningVoiceService
import com.iq_academy.malika_ai.ui.activity.MainActivity
import com.iq_academy.malika_ai.utils.Extensions.decodeString
import com.iq_academy.malika_ai.utils.Extensions.decodeString2
import com.iq_academy.malika_ai.utils.Extensions.getCurrentTime
import com.iq_academy.malika_ai.utils.Extensions.similarity
import com.iq_academy.malika_ai.utils.Extensions.testAnswer
import com.iq_academy.malika_ai.utils.Extensions.testAnswerEng
import com.iq_academy.malika_ai.utils.Extensions.testAnswerRu
import com.iq_academy.malika_ai.utils.Extensions.testQuestion
import com.iq_academy.malika_ai.utils.Extensions.testQuestionEng
import com.iq_academy.malika_ai.utils.Extensions.testQuestionRu
import com.iq_academy.malika_ai.utils.KeyValue.LANGUAGE
import com.iq_academy.malika_ai.utils.KeyValue.LOG_IN
import com.iq_academy.malika_ai.utils.SharedPref
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Rustamov Odilbek, Android developer
 * 28/03/2023  +998-91-775-17-79
 */

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var activity: MainActivity
    private val viewModel: HomeViewModel by viewModels<HomeViewModelImp>()
    private val googleTranslate = "AIzaSyANxBBWpXuCV1s7eLnB6Ox3MZXUTscNH8M"

    @Inject
    lateinit var sharedPref: SharedPref

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        initViews()

        return root
    }

    private fun initViews() {
        activity = requireActivity() as MainActivity
        getAllChats()
        setLanguagaeView()

        if (sharedPref.getLogIn(LOG_IN) && !isListeningVoiceServiceRunning(requireContext())) {
            activity.startService()
        }

        binding.ivMenu.setOnClickListener {
            activity.openDrawerLayout()
        }

        binding.ivSend.setOnClickListener {
            sendMessage()
        }

        binding.ivLanguage.setOnClickListener {
           popurMenue()
        }
    }

    //popur Menue
    fun popurMenue(){
        val popupMenu = PopupMenu(requireContext(), binding.ivLanguage)
        popupMenu.getMenuInflater().inflate(R.menu.my_menu, popupMenu.getMenu())

        popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
            override fun onMenuItemClick(item: MenuItem): Boolean {
                return when (item.getItemId()) {
                    R.id.action_uz ->
                        // Handle settings click
                        language("uz")
                    R.id.action_eng ->
                        // Handle about click
                        language("en")
                    R.id.action_ru ->
                        // Handle about click
                        language("ru")
                    else -> false
                }
            }
        })

        popupMenu.show()
    }

    //set language Image
    fun setLanguagaeView() {
        if (sharedPref.getLanguage(LANGUAGE) == "uz") {
            binding.ivLanguage.setImageResource(R.drawable.ic_uz_flag)
        } else if (sharedPref.getLanguage(LANGUAGE) == "en") {
            binding.ivLanguage.setImageResource(R.drawable.ic_eng_flag)
        } else if (sharedPref.getLanguage(LANGUAGE) == "ru") {
            binding.ivLanguage.setImageResource(R.drawable.ic_ru_flag)
        }
    }

    //change language
    private fun language(language: String): Boolean {
        sharedPref.saveLanguage(LANGUAGE, language)

        setLanguagaeView()
        return true
    }

    //Send message
    private fun sendMessage() {
        var sendMessage = binding.etMessage.text.toString()
        if (sendMessage.length > 0) {
            binding.rLayout.visibility = View.VISIBLE
            binding.messageTextUser.text = sendMessage
            binding.messageTimeUser.text = getCurrentTime()
        }
        CoroutineScope(Dispatchers.IO).launch {
            if (sendMessage.length > 0) {
                binding.etMessage.text.clear()
                val chat = Chat()
                chat.userQuestion = sendMessage
                chat.userTime = getCurrentTime()

                if (sharedPref.getLanguage(LANGUAGE) == "uz") {
                    testQuestion().forEach { question ->
                        if (similarity(question, sendMessage) > 0.5) {
                            chat.aiTime = getCurrentTime()
                            chat.aiAnswer = testAnswer()
                            chat.chatId = System.currentTimeMillis().toInt()
                            insertChat(chat)
                            getAllChats()
                            return@launch
                        }
                    }
                    sendMessage = translateText(sendMessage, "en")
                }

                if (sharedPref.getLanguage(LANGUAGE) == "ru") {
                    testQuestionRu().forEach { question ->
                        if (similarity(question, sendMessage) > 0.5) {
                            chat.aiTime = getCurrentTime()
                            chat.aiAnswer = testAnswerRu()
                            chat.chatId = System.currentTimeMillis().toInt()
                            insertChat(chat)
                            getAllChats()
                            return@launch
                        }
                    }
                }

                if (sharedPref.getLanguage(LANGUAGE) == "en") {
                    testQuestionEng().forEach { question ->
                        if (similarity(question, sendMessage) > 0.5) {
                            chat.aiTime = getCurrentTime()
                            chat.aiAnswer = testAnswerEng()
                            chat.chatId = System.currentTimeMillis().toInt()
                            insertChat(chat)
                            getAllChats()
                            return@launch
                        }
                    }
                }


                val messageList: ArrayList<Message> = arrayListOf()
                viewModel.getAllchats {
                    it.onSuccess { chatList ->
                        var size = chatList.size
                        for (i in 1..size) {
                            val messUser =
                                Message(role = "user", content = chatList[size - i].userQuestion)
                            val messAssistan =
                                Message(role = "assistant", content = chatList[size - i].aiAnswer)
                            messageList.add(messUser)
                            messageList.add(messAssistan)
                        }

                        sendMessageChat(messageList, sendMessage, chat)
                    }
                    it.onFailure {
                        print(it.message)
                    }
                }

            }
        }
    }

    //Send a question to ChatGPT and get an answer
    private fun sendMessageChat(messageList: ArrayList<Message>, sendMessage: String, chat: Chat) {
        val mess = Message(role = "user", content = sendMessage)
        messageList.add(mess)

        val chatGPTRequest = ChatGPTRequest(model = "gpt-3.5-turbo", messages = messageList)

        viewModel.getSendSMS(chatGPTRequest) {
            it.onSuccess { chatgptResponse ->
                CoroutineScope(Dispatchers.IO).launch {

                    var sendMessage = chatgptResponse.choices[0].message!!.content!!

                    if (sharedPref.getLanguage(LANGUAGE) == "uz") {
                        sendMessage = translateText(sendMessage, "uz")
                    }

                    sendMessage = decodeString(sendMessage)
                    sendMessage = decodeString2(sendMessage)

                    chat.aiAnswer = sendMessage
                    chat.aiTime = getCurrentTime()
                    chat.chatId = System.currentTimeMillis().toInt()
                    insertChat(chat)
                    getAllChats()
                    return@launch
                }
            }
            it.onFailure {
                print(it.message)
            }
        }
    }


    //Check service running
    fun isListeningVoiceServiceRunning(context: Context): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (ListiningVoiceService::class.java.name == service.service.className) {
                return true
            }
        }
        return false
    }

    //google translate
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

    //DB get all Chats
    private fun getAllChats() {
        viewModel.getAllchats {
            it.onSuccess { chatList ->
                binding.rLayout.visibility = View.GONE
                var adapter = ChatAdapter()
                val layoutManager = LinearLayoutManager(requireContext())
                layoutManager.stackFromEnd = true
                binding.recyclerView.setLayoutManager(layoutManager)
                binding.recyclerView.scrollToPosition(adapter.itemCount - 1)

                binding.recyclerView.adapter = adapter
                adapter.submitList(chatList)
            }
            it.onFailure {
                print(it.message)
            }
        }
    }

    //DB insert chat
    private fun insertChat(chat: Chat) {
        viewModel.insertChat(chat) {
            it.onSuccess {
                binding.etMessage.text.clear()
                getAllChats()
            }
            it.onFailure {
                print(it.message)
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}