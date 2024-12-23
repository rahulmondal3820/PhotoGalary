package com.example.photogallery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.photogallery.API.GalleryItem
import com.example.photogallery.API.Uri
import com.example.photogallery.databinding.ListItemGalleryBinding

class PhotoRecyclerAdapter(
    private val galleryItems:List<GalleryItem> ,
        private val onItemClick:(Uri)->Unit
): RecyclerView.Adapter<PhotoViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemGalleryBinding.inflate(inflater,parent,false)
        return PhotoViewHolder(binding)

    }

    override fun getItemCount(): Int {
return galleryItems.size
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
               val item = galleryItems[position]
        holder.bind(item, onItemClick)
    }
}