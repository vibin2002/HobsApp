package com.killerinstinct.hobsapp.user.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.killerinstinct.hobsapp.adapters.SearchAdapter
import com.killerinstinct.hobsapp.databinding.FragmentUserSearchBinding
import com.killerinstinct.hobsapp.model.Worker
import com.killerinstinct.hobsapp.viewmodel.UserMainViewModel

class UserSearchFragment : Fragment() {

    lateinit var binding: FragmentUserSearchBinding
    private val viewModel: UserMainViewModel by activityViewModels()
    private val workerList = mutableListOf<Worker>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val searchAdapter = SearchAdapter(requireContext(),workerList){
            val action = UserSearchFragmentDirections.actionUserNavigationSearchToUserShowProfileFragment(it)
            findNavController().navigate(action)
        }

        binding.searchRv.apply {
            adapter = searchAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        viewModel.allWorkers.observe(viewLifecycleOwner){
            workerList.clear()
            workerList.addAll(it)
            searchAdapter.notifyDataSetChanged()
        }

        binding.wrkrsearchview.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchAdapter.filter.filter(newText)
                return true
            }

        })

    }

}