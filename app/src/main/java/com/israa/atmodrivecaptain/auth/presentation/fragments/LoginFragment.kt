package com.israa.atmodrivecaptain.auth.presentation.fragments

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.israa.atmodrivecaptain.R
import com.israa.atmodrivecaptain.auth.AuthActivity
import com.israa.atmodrivecaptain.auth.data.model.CheckCodeResponse
import com.israa.atmodrivecaptain.auth.data.model.SendCodeResponse
import com.israa.atmodrivecaptain.auth.domain.model.CheckCode
import com.israa.atmodrivecaptain.auth.presentation.viewmodels.LoginViewModel
import com.israa.atmodrivecaptain.databinding.FragmentLoginBinding
import com.israa.atmodrivecaptain.home.HomeActivity
import com.israa.atmodrivecaptain.utils.Progressbar
import com.israa.atmodrivecaptain.utils.UiState
import com.israa.atmodrivecaptain.utils.exhaustive
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlin.math.log


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
        observeOnSendCode()
    }

    private fun onClick() {

        binding.apply {

            txtSendCode.setOnClickListener{
                loginViewModel.sendCode("0${editTextPhone.text}")
            }
            txtResend.setOnClickListener{
                loginViewModel.sendCode("0${editTextPhone.text}")
            }
            btnContinue.setOnClickListener {
                val mobile = "0${editTextPhone.text}"
                loginViewModel.checkCode(mobile,editTextOtp.text.toString(),"device_token:${mobile}")
                observeOnCheckCode()
            }


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
                        val data = it.data as CheckCode

                        //navigate(mobile!!)
                        loginViewModel.navigate(data.register_step) //"
                        navigate("0${binding.editTextPhone.text}")
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


    private fun navigate(mobile: String ) {
        lifecycleScope.launchWhenStarted {
            loginViewModel.loginEvent.collect{ event->
                when(event){
                    LoginViewModel.LoginEvents.NavigateToBankAccountInfo ->{
                        findNavController().navigate(R.id.bankAccountFragment)
                    }
                    LoginViewModel.LoginEvents.NavigateToHome -> {
                        startActivity(Intent(requireActivity(), HomeActivity::class.java))
                        requireActivity().finish()
                    }
                    LoginViewModel.LoginEvents.NavigateToPersonalInfo ->
                        findNavController().
                        navigate(LoginFragmentDirections.actionLoginFragmentToPersonalInfoFragment(mobile))

                    LoginViewModel.LoginEvents.NavigateToVehicleInfo ->{
                        findNavController().navigate(R.id.vehicleInformationFragment)
                    }
                }.exhaustive
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}