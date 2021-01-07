package com.project.form_emulator

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.form_emulator.Room.PhotoModel
import de.hdodenhof.circleimageview.CircleImageView
import kotlin.math.roundToInt

class CarouselAdapter(private val photos:List<PhotoModel>, private val context: Context): RecyclerView.Adapter<CarouselAdapter.CarouselHolder>() {

    private var hasInitParentDimensions = false
    private var maxImageWidth: Int = 0
    private var maxImageHeight: Int = 0
    private var maxImageAspectRatio: Float = 1f

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarouselHolder {
        if (!hasInitParentDimensions) {
            maxImageWidth = (parent.width * 0.45f).roundToInt()
            maxImageHeight = parent.height
            maxImageAspectRatio = maxImageWidth.toFloat() / maxImageHeight.toFloat()
            hasInitParentDimensions = true
        }
        return CarouselHolder(
            LayoutInflater.from(context).inflate(R.layout.item_photo, parent, false)
        )
    }


    override fun onBindViewHolder(holder: CarouselHolder, position: Int) {
        holder.onBind(photos[position])
    }

    override fun getItemCount() = photos.size

    inner class CarouselHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val photoImage: CircleImageView = itemView.findViewById(R.id.circle_photo)

        fun onBind(photoModel: PhotoModel) {
            val imageAspectRatio = photoImage.width.toFloat() / photoImage.height.toFloat()
            val targetImageWidth: Int =
                if (imageAspectRatio < maxImageAspectRatio) {
                    // Tall image: height = max, width adjusts
                    (maxImageHeight * imageAspectRatio).roundToInt()
                } else {
                    // Wide image: width = max
                    maxImageWidth
                }

            photoImage.layoutParams = RecyclerView.LayoutParams(
                targetImageWidth,
                RecyclerView.LayoutParams.MATCH_PARENT
            )
            photoImage.setImageDrawable(photoModel.photoDrawable)
        }

    }

}