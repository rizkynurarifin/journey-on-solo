package com.rizky.journeyonsolo.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.rizky.journeyonsolo.R
import com.rizky.journeyonsolo.data.Result
import com.rizky.journeyonsolo.data.local.entity.FavoriteDestination
import com.rizky.journeyonsolo.databinding.FragmentDetailBinding
import com.rizky.journeyonsolo.ui.ViewModelFactory

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private val dataEntity = FavoriteDestination()

    private val viewModel by viewModels<DetailViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = DetailFragmentArgs.fromBundle(arguments as Bundle).id

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        viewModel.getData(id).observe(viewLifecycleOwner) {result->
            when(result) {
                is Result.Loading -> {
                    showLoading(true)
                }
                is Result.Success -> {
                    showLoading(false)
                    val newData = result.data

                    dataEntity.placeId = newData.placeId
                    dataEntity.name = newData.name
                    dataEntity.imageUrl = newData.imageUrl
                    dataEntity.address = newData.address
                    dataEntity.rating = newData.rating

                    binding.tvItemName.text = newData.name
                    Glide.with(requireContext())
                        .load(newData.imageUrl)
                        .into(binding.imgItemPhoto)
                    binding.tvItemLocation.text = newData.address
                    binding.tvItemRating.text = newData.rating
                    binding.tvItemDescription.text = newData.captionIdn

                    binding.btnBukaMaps.setOnClickListener {
                        val toDetailMapsFragment = DetailFragmentDirections.actionDetailFragmentToDetailMapsFragment()
                        toDetailMapsFragment.name = newData.name
                        toDetailMapsFragment.latitude = newData.lat.toFloat()
                        toDetailMapsFragment.longitude = newData.lon.toFloat()
                        view.findNavController().navigate(toDetailMapsFragment)
                    }
                }

                is Result.Error -> {
                    showLoading(false)
                    Toast.makeText(requireContext(), "Terjadi Kesalahan " + result.error, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        viewModel.getIsFavorite(id).observe(viewLifecycleOwner) { isFavorite ->
            if (isFavorite) {
                binding.btnFavorite.setImageResource(R.drawable.ic_favorite_full)
                binding.btnFavorite.setOnClickListener {
                    viewModel.deleteFavoriteDestination(dataEntity)
                }
            } else {
                binding.btnFavorite.setImageResource(R.drawable.ic_favorite_border)
                binding.btnFavorite.setOnClickListener {
                    viewModel.insertFavoriteDestination(dataEntity)
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}