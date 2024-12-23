package com.example.photogallery

import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.photogallery.API.GalleryItem
import com.example.photogallery.API.Uri
import com.example.photogallery.databinding.ListItemGalleryBinding

class PhotoViewHolder(private  val binding: ListItemGalleryBinding) :RecyclerView.ViewHolder(binding.root) {
    fun bind(item: GalleryItem, onItemClick:(Uri)->Unit) {
        binding.itemImageView.load(item.url.small){
            placeholder(R.drawable.bill_up_close)
            binding.root.setOnClickListener{
                onItemClick(item.photoPageUri)
            }
        }

    }

}
