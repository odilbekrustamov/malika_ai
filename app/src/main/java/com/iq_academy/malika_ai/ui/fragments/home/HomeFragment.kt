package com.iq_academy.malika_ai.ui.fragments.home

import android.app.ActivityManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.iq_academy.malika_ai.adapter.ChatAdapter
import com.iq_academy.malika_ai.databinding.FragmentHomeBinding
import com.iq_academy.malika_ai.model.openia.ChatGPTRequest
import com.iq_academy.malika_ai.model.openia.Message
import com.iq_academy.malika_ai.model.room.Chat
import com.iq_academy.malika_ai.service.ListiningVoiceService
import com.iq_academy.malika_ai.ui.activity.MainActivity
import com.iq_academy.malika_ai.utils.Extensions.getCurrentTime
import com.iq_academy.malika_ai.utils.KeyValue.LOG_IN
import com.iq_academy.malika_ai.utils.SharedPref
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var activity: MainActivity
    private val viewModel: HomeViewModel by viewModels<HomeViewModelImp>()
    private lateinit var adapter: ChatAdapter

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
        adapter = ChatAdapter()
        binding.recyclerView.adapter = adapter

        if (sharedPref.getLogIn(LOG_IN) && !isListeningVoiceServiceRunning(requireContext())){
            activity.startService()
        }

        binding.ivMenu.setOnClickListener {
            activity.openDrawerLayout()
        }

        getAllChats()



        binding.ivSend.setOnClickListener {
            val sendMessage = binding.etMessage.text.toString()
            if (sendMessage.length > 0) {
                val chat = Chat()
                val mess = Message(role = "user", content = sendMessage)
                var messageList: ArrayList<Message> = arrayListOf(mess)
                val chatGPTRequest = ChatGPTRequest(model = "gpt-3.5-turbo", messages = messageList)

                chat.userQuestion = sendMessage
                chat.userTime = getCurrentTime()

                viewModel.getSendSMS(chatGPTRequest) {
                    it.onSuccess { chatgptResponse ->
                        chat.aiTime = getCurrentTime()
                        chat.aiAnswer = chatgptResponse.choices[0].message!!.content!!
                        insertChat(chat)
                    }
                    it.onFailure {
                        print(it.message)
                    }
                }
            }
        }
    }

    private fun getAllChats() {
        viewModel.getAllchats {
            it.onSuccess { chatList ->
                adapter.submitList(chatList)
                binding.recyclerView.smoothScrollToPosition(chatList.size - 1 )

            }
            it.onFailure {
                print(it.message)
            }
        }
    }

    fun isListeningVoiceServiceRunning(context: Context): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (ListiningVoiceService::class.java.name == service.service.className) {
                return true
            }
        }
        return false
    }


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