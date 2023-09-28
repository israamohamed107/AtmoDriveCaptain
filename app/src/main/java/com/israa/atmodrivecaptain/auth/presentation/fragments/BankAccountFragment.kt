package com.israa.atmodrivecaptain.auth.presentation.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.israa.atmodrivecaptain.auth.presentation.viewmodels.RegisterBankAccountViewModel
import com.israa.atmodrivecaptain.databinding.FragmentBankAccountBinding
import com.israa.atmodrivecaptain.home.HomeActivity
import com.israa.atmodrivecaptain.utils.Progressbar
import com.israa.atmodrivecaptain.utils.UiState
import com.israa.atmodrivecaptain.utils.exhaustive
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BankAccountFragment : Fragment() {

    private var _binding: FragmentBankAccountBinding? = null
    private val binding get() = _binding!!

    private val registerBankAccountViewModel:RegisterBankAccountViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentBankAccountBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        onClick()
        onObserve()
    }

    private fun onObserve() {
        registerBankAccountViewModel.registerAccount.observe(viewLifecycleOwner){ result->
            when(result){
                is UiState.Success ->{
                    Progressbar.dismiss()
                    goToHome()
                }

                is UiState.Failure ->{
                    Progressbar.dismiss()
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                }
                is UiState.Loading ->Progressbar.show(requireActivity())

                else ->{
                }
            }.exhaustive
        }
    }

    private fun onClick(){
        binding.apply {
            btnSubmit.setOnClickListener{
                registerBankAccountViewModel.registerBankAccount(
                    editTextBankName.text.toString(),
                    editTextAccountPersonalName.text.toString(),
                    editTextAccountNumber.text.toString(),
                    editTextIbanNumber.text.toString()
                )
            }

            btnSkip.setOnClickListener {
                goToHome()
            }
        }
    }

    private fun goToHome() {
        startActivity(Intent(requireActivity(), HomeActivity::class.java))
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}