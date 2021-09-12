package com.killerinstinct.hobsapp.worker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.killerinstinct.hobsapp.R
import com.killerinstinct.hobsapp.Utils
import com.killerinstinct.hobsapp.adapters.WorkerPostAdapter
import com.killerinstinct.hobsapp.databinding.FragmentShowProfileBinding
import com.killerinstinct.hobsapp.model.Job
import com.killerinstinct.hobsapp.model.Post
import com.killerinstinct.hobsapp.model.Worker
import com.killerinstinct.hobsapp.user.fragments.UserShowProfileFragmentDirections
import com.killerinstinct.hobsapp.viewmodel.WorkerMainViewModel

class ShowProfileFragment : Fragment() {

    lateinit var binding: FragmentShowProfileBinding
    private val args: ShowProfileFragmentArgs by navArgs()
    private val userUid = FirebaseAuth.getInstance().currentUser?.uid
    private var worker: Worker? = null
    private val viewModel: WorkerMainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentShowProfileBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        binding.btnHire.setOnClickListener
        binding.nestedScrollViewWorkershowprofile.visibility = View.INVISIBLE
        binding.progbarLoad.visibility = View.VISIBLE

        Utils.getSpecificWorker(args.workerId){
            binding.apply {
                nestedScrollViewWorkershowprofile.visibility = View.VISIBLE
                progbarLoad.visibility = View.GONE
                worker = it
                tutorName.text = it.userName
                tutEmail.text = it.email
                tutPhonenum.text = it.phoneNumber
                tutMinwage.text = it.minWage
                tutExperience.text = it.experience
                tutCategory.text = it.category.toString().removePrefix("[").removeSuffix("]")
                wrkrReviewCount.text = it.ratersCount
                wrkrRating.text = it.rating
                if (it.profilePic != "") {
                    Log.d("Glidy", "onViewCreated: podA")
                    Glide.with(requireContext())
                        .load(it.profilePic)
                        .into(binding.tutorProfile)
                }else{
                    binding.tutorProfile.setImageDrawable(
                        AppCompatResources.getDrawable(
                            requireContext(),
                            R.drawable.ic_person
                        )
                    )
                }
            }
        }

        viewModel.getSpecificWorkerPosts(args.workerId){
            setUpRecyclerView(it)
        }

        binding.allReviewsTv.setOnClickListener {
            val action = ShowProfileFragmentDirections.actionShowProfileFragmentToReviewFragment2(
                args.workerId
            )
            findNavController().navigate(action)
        }



    }

    private fun setUpRecyclerView(list: List<Post>){
        binding.showprofileRv.adapter = WorkerPostAdapter(requireContext(),list)
        binding.showprofileRv.layoutManager = GridLayoutManager(requireContext(),3)
    }

}