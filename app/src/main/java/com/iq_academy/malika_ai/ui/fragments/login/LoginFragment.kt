package com.iq_academy.malika_ai.ui.fragments.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.iq_academy.malika_ai.R
import com.iq_academy.malika_ai.databinding.FragmentLoginBinding
import com.iq_academy.malika_ai.model.verify.SendSMSRequest
import com.iq_academy.malika_ai.model.verify.VerifySMSRequest
import com.iq_academy.malika_ai.utils.Extensions.snackBar
import com.iq_academy.malika_ai.utils.KeyValue.LOG_IN
import com.iq_academy.malika_ai.utils.SharedPref
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Rustamov Odilbek, Android developer
 * 28/03/2023  +998-91-775-17-79
 */

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private var isSendSMS = false
    private val viewModel: LoginViewModel by viewModels<LoginViewModelImp>()

    @Inject
    lateinit var sharedPref: SharedPref

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val root: View = binding.root

        initViews()

        return root
    }

    private fun initViews() {
        binding.btnLogin.setOnClickListener {

            findNavController().popBackStack()
            findNavController().navigate(R.id.homeFragment)
            sharedPref.saveLogIn(LOG_IN, true)

            if (!isSendSMS) {

                var etNumber = binding.etPhoneNumber.text.toString().trim()
                if (etNumber.startsWith("+998") && etNumber.length == 13) {
                    var sendSMSRequest = SendSMSRequest(etNumber)
                    viewModel.sendSMS(sendSMSRequest) {
                        it.onSuccess {
                            isSendSMS = true
                            binding.etPhoneNumber.isEnabled = false
                            binding.etSMS.visibility = View.VISIBLE
                            binding.btnLogin.text = "KIRISH"

                        }
                        it.onFailure {
                            snackBar("Qayatadan urunib ko'rish.")
                            print(it.message)
                        }
                    }
                } else {
                    snackBar("Iltimos telefon nomerni to'g'ri kiriting!!  +998917751779 ko'rinishida.")
                }
            } else {
                val etNumber = binding.etPhoneNumber.text.toString().trim()
                val etCode = binding.etSMS.text.toString().trim()

                if (etNumber.startsWith("+998") && etNumber.length == 13 && etCode.length == 6) {
                    var verifySMSRequest = VerifySMSRequest(etCode, etNumber)
                    viewModel.verifySMS(verifySMSRequest) {
                        it.onSuccess {

                            if(it.token.isNotEmpty()){
//                                findNavController().popBackStack()
//                                findNavController().navigate(R.id.homeFragment)
//                                sharedPref.saveLogIn(LOG_IN, true)
                            }else{
                                snackBar("Iltimos SMS parolni to'g'ri kiriting!!  771779 ko'rinishida.")
                            }

                        }
                        it.onFailure {
                            print(it.message)
                        }
                    }
                } else {
                    snackBar("Iltimos SMS parolni to'g'ri kiriting!!  1779 ko'rinishida.")
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}