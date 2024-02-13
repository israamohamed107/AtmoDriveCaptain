package com.israa.atmodrivecaptain.auth.presentation.fragments

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.israa.atmodrivecaptain.R
import com.israa.atmodrivecaptain.auth.data.model.SendCodeResponse
import com.israa.atmodrivecaptain.auth.domain.model.CaptainDetails
import com.israa.atmodrivecaptain.auth.presentation.viewmodels.LoginViewModel
import com.israa.atmodrivecaptain.databinding.FragmentLoginBinding
import com.israa.atmodrivecaptain.home.presentation.HomeActivity
import com.israa.atmodrivecaptain.utils.Progressbar
import com.israa.atmodrivecaptain.utils.UiState
import com.israa.atmodrivecaptain.utils.showToast
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val loginViewModel:LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        _binding = FragmentLoginBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onClick()
        enableSendCode()
        enableContinueButton()
        observeOnSendCode()
    }

    private fun onClick() {

        binding.apply {

            txtSendCode.setOnClickListener{
                val mobile = if(editTextPhone.text.isNotEmpty()) "0${editTextPhone.text}" else ""

                loginViewModel.sendCode(mobile)
            }
            txtResend.setOnClickListener{
                val mobile = if(editTextPhone.text.isNotEmpty()) "0${editTextPhone.text}" else ""

                loginViewModel.sendCode(mobile)
            }
            btnContinue.setOnClickListener {
                val mobile = if(editTextPhone.text.isNotEmpty()) "0${editTextPhone.text}" else ""

                loginViewModel.checkCode(mobile,editTextOtp.text.toString(),"device_token:${mobile}")
                observeOnCheckCode()
            }
            editTextPhone.setOnLongClickListener {

                editCopiedNumber()
                return@setOnLongClickListener true
            }
            editTextPhone.addTextChangedListener(object :TextWatcher{
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    editCopiedNumber()
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    enableSendCode()
                }

            })

            editTextOtp.addTextChangedListener(object :TextWatcher{
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {

                    enableContinueButton()
                }

            })
        }
    }

    private fun editCopiedNumber() {
        val clipboard =
            (requireActivity().getSystemService(Context.CLIPBOARD_SERVICE)) as? ClipboardManager
        var pasteText = ""
        clipboard?.let {
            if (it.hasPrimaryClip()) {
                val item = it.primaryClip!!.getItemAt(0)
                pasteText = item.text.toString()
            }
            if (pasteText.isNotEmpty()) {
                pasteText = pasteText.replace("+20", "")
                    .replace(" ", "")
                if(pasteText[0] == '0'){
                    pasteText = pasteText.replaceFirst("0","")
                }
                if (pasteText.length != 10) {
                    pasteText = ""
                }
                val clip = ClipData.newPlainText("simple text", pasteText)
                it.setPrimaryClip(clip)
            }
        }
    }


    private fun enableSendCode() {
        binding.apply {
            txtSendCode.isEnabled = editTextPhone.text.toString().length == 10
        }
    }

    private fun enableContinueButton() {
        binding.apply {
            btnContinue.isEnabled = editTextOtp.text.toString().length == 4 && editTextPhone.text.toString().length == 10
        }
    }

    private fun startCounter() {

        Handler(Looper.myLooper()!!).postDelayed({
            binding.apply {
                txtResend.isEnabled = false
                txtCounter.visibility = View.VISIBLE
                txtResend.setTextColor(resources.getColor(R.color.hint_color))
                object : CountDownTimer(120000, 100) {
                    override fun onTick(millisUntilFinished: Long) {
                        val minutes = "${millisUntilFinished / 60000}"
                        var seconds = "${millisUntilFinished % 60000 / 1000}"
                        if (seconds.toInt() < 10)
                            seconds = "0${seconds}"
                        txtCounter.text = getString(R.string.counter, minutes, seconds)
                    }

                    override fun onFinish() {
                        txtResend.isEnabled = true
                        txtResend.setTextColor(resources.getColor(R.color.main_color))
                        txtCounter.visibility = View.GONE
                    }

                }.start()
            }
        },500)

    }

    private fun observeOnSendCode() {
            viewLifecycleOwner.lifecycleScope.launchWhenStarted{
                loginViewModel.sendCodeData.collect{
                    when (it) {
                        is UiState.Success -> {
                            //navigate(mobile!!)
                            val data = it.data as SendCodeResponse
                            Toast.makeText(requireContext(), data.message, Toast.LENGTH_SHORT).show()
                            Progressbar.dismiss()
                            startCounter()
                        }

                        is UiState.Failure -> {
                            Progressbar.dismiss()
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        }

                        is UiState.Loading ->
                            Progressbar.show(requireActivity())

                    }
                }
            }
    }

    private fun observeOnCheckCode() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted{
            loginViewModel.checkCodeData.collect{
                when (it) {
                    is UiState.Success -> {
                        val data = it.data as CaptainDetails

                        //navigate(mobile!!)
                        loginViewModel.navigate(data.registerStep)
                        navigate("0${binding.editTextPhone.text}")
                        binding.apply {
                            editTextPhone.text = null
                            editTextOtp.text = null
                        }
                        Progressbar.dismiss()
                    }

                    is UiState.Failure -> {
                        Progressbar.dismiss()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    is UiState.Loading ->
                        Progressbar.show(requireActivity())

                }
            }
        }
    }


    private fun navigate(mobile: String) {
        lifecycleScope.launchWhenStarted {
            loginViewModel.loginEvent.collect{ event->
                when(event){
                    LoginViewModel.LoginEvents.NavigateToBankAccountInfo ->{
                        findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToBankAccountFragment())
                    }
                    LoginViewModel.LoginEvents.NavigateToHome -> {
                        startActivity(Intent(requireActivity(), HomeActivity::class.java))
                        requireActivity().finish()
                    }
                    LoginViewModel.LoginEvents.NavigateToPersonalInfo ->
                        findNavController().
                        navigate(LoginFragmentDirections.actionLoginFragmentToPersonalInfoFragment(mobile))

                    LoginViewModel.LoginEvents.NavigateToVehicleInfo ->{
                        findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToVehicleInformationFragment())
                    }

                    LoginViewModel.LoginEvents.NotActive -> showToast("Wait until your account is activated")
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}