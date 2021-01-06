package com.project.form_emulator

import android.content.Context
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.annotation.Px
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper

class MainActivity : AppCompatActivity(), CarouselAdapter.Callback{
    private lateinit var recyclerView:RecyclerView
    private lateinit var nameText: TextView
    private lateinit var snapHelper:SnapHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        snapHelper = PagerSnapHelper()
        val listName = this.assets.list("images")
        val listPhotosModel:MutableList<PhotoModel>  = mutableListOf()
        listName?.forEach {
            val model = PhotoModel(it, this.assets)
            listPhotosModel.add(model)
        }
        nameText  = findViewById(R.id.text_name)
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.addItemDecoration(LinearHorizontalSpacingDecoration(64))
        recyclerView.addItemDecoration(BoundsOffsetDecoration())
        recyclerView.adapter = CarouselAdapter(listPhotosModel,this)
        recyclerView.layoutManager = ProminentLayoutManager(this)
        snapHelper.attachToRecyclerView(recyclerView)
    }

    override fun change(photoModel: PhotoModel) {
        recyclerView.background = photoModel.photoDrawable
        nameText.text = photoModel.getName()
    }

    class LinearHorizontalSpacingDecoration(@Px private val innerSpacing: Int) :
        RecyclerView.ItemDecoration() {

        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)

            val itemPosition = parent.getChildAdapterPosition(view)

            outRect.left = if (itemPosition == 0) 0 else innerSpacing / 2
            outRect.right = if (itemPosition == state.itemCount - 1) 0 else innerSpacing / 2
        }
    }

    class BoundsOffsetDecoration : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)

            val itemPosition = parent.getChildAdapterPosition(view)

            // It is crucial to refer to layoutParams.width (view.width is 0 at this time)!
            val itemWidth = view.layoutParams.width
            val offset = (parent.width - itemWidth) / 2

            if (itemPosition == 0) {
                outRect.left = offset
            } else if (itemPosition == state.itemCount - 1) {
                outRect.right = offset
            }
        }
    }

    internal class ProminentLayoutManager(
        context: Context,
        private val minScaleDistanceFactor: Float = 1.5f,
        private val scaleDownBy: Float = 0.5f
    ) : LinearLayoutManager(context, HORIZONTAL, false) {

        override fun onLayoutCompleted(state: RecyclerView.State?) =
            super.onLayoutCompleted(state).also { scaleChildren() }

        override fun scrollHorizontallyBy(
            dx: Int,
            recycler: RecyclerView.Recycler,
            state: RecyclerView.State
        ) = super.scrollHorizontallyBy(dx, recycler, state).also {
            if (orientation == HORIZONTAL) scaleChildren()
        }

        private fun scaleChildren() {
            val containerCenter = width / 2f

            // Any view further than this threshold will be fully scaled down
            val scaleDistanceThreshold = minScaleDistanceFactor * containerCenter

            for (i in 0 until childCount) {
                val child = getChildAt(i)!!

                val childCenter = (child.left + child.right) / 2f
                val distanceToCenter = kotlin.math.abs(childCenter - containerCenter)

                val scaleDownAmount = (distanceToCenter / scaleDistanceThreshold).coerceAtMost(1f)
                val scale = 1f - scaleDownBy * scaleDownAmount

                child.scaleX = scale
                child.scaleY = scale
            }
        }
    }

}

