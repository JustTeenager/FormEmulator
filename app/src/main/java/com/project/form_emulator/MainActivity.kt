package com.project.form_emulator

import android.app.ProgressDialog
import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Px
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.project.form_emulator.Room.DBHelper
import com.project.form_emulator.Room.PhotoModel
import jp.wasabeef.blurry.Blurry
import java.lang.Math.abs
import java.util.concurrent.*

class MainActivity : AppCompatActivity(),SnapOnScrollListener.OnSnapPositionChangeListener{


    private val NUMBER_OF_MODELS=10

    private lateinit var recyclerView:RecyclerView
    private lateinit var recyclerViewBackground:ImageView
    private lateinit var nameText: TextView
    private lateinit var snapHelper:SnapHelper
    private lateinit var listPhotosModel:MutableList<PhotoModel>

    private lateinit var callText:TextView
    private lateinit var heyText:TextView
    private lateinit var howText:TextView
    private lateinit var favouriteText:TextView
    private lateinit var groupsText:TextView

    private lateinit var progressDialog:ProgressDialog

    private lateinit var dbThread:DBHandlerThread


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        progressDialog= ProgressDialog(this)
        dbThread=DBHandlerThread("handlerThread",Executors.newFixedThreadPool(5))
        dbThread.start()
        val listName = this.assets.list("photoModelsImages")
        listPhotosModel = mutableListOf()
        listName?.forEach { buildModels(it) }

    }

    private fun setupRecView() {
        snapHelper = PagerSnapHelper()
        nameText = findViewById(R.id.text_name)
        recyclerView = findViewById(R.id.recycler_view)
        recyclerViewBackground = findViewById(R.id.image_background)
        recyclerView.addItemDecoration(LinearHorizontalSpacingDecoration(64))
        recyclerView.addItemDecoration(BoundsOffsetDecoration())
        recyclerView.adapter = CarouselAdapter(listPhotosModel, this)
        recyclerView.layoutManager = ProminentLayoutManager(this)
        snapHelper.attachToRecyclerView(recyclerView)
       // recyclerView.recycledViewPool.setMaxRecycledViews(0, 0);
       // recyclerView.setItemViewCacheSize(15);
        recyclerView.addOnScrollListener(SnapOnScrollListener(snapHelper, onSnapPositionChangeListener = this))
    }

    private fun buildModels(name: String?) {
        dbThread.checkModel(name)

    }

    private fun initButtons(){
        callText=findViewById(R.id.text_call)
        heyText=findViewById(R.id.text_hey)
        howText=findViewById(R.id.text_how)
        favouriteText=findViewById(R.id.text_favorite)
        groupsText=findViewById(R.id.text_group)
        val infoText=findViewById<TextView>(R.id.info_text)

        callText.setOnClickListener {
            Toast.makeText(this,getString(R.string.message_send_toast),Toast.LENGTH_SHORT).show()
            callText.visibility=View.INVISIBLE
        }
        heyText.setOnClickListener{
            Toast.makeText(this,getString(R.string.message_send_toast),Toast.LENGTH_SHORT).show()
            heyText.visibility=View.INVISIBLE
        }
        howText.setOnClickListener {
            Toast.makeText(this, getString(R.string.message_send_toast), Toast.LENGTH_SHORT).show()
            howText.visibility = View.INVISIBLE
        }

        infoText.setOnClickListener {
            val dialog=InformationDialog()
            dialog.isCancelable=false
            dialog.show(supportFragmentManager,null)
        }

        favouriteText.text=listPhotosModel[0].like.toString()
        groupsText.text=listPhotosModel[0].group.toString()
    }

    override fun onDestroy() {
        super.onDestroy()
        dbThread.quit()
    }

    inner class DBHandlerThread(name: String?, private val service:ExecutorService) : HandlerThread(name) {

        lateinit var handler:Handler
        private val CODE_TO_SHOW_DIALOG=-1
        private val CODE_TO_DISMISS_DIALOG=0
        private val CODE_TO_CHECK_MODEL=1
        private val CODE_TO_WRITE_MODEL=2
        private val CODE_TO_READ_MODEL=3


        //TODO Нужен ли диалог?
        override fun start() {
            super.start()
            showDialog()
        }

        override fun quit(): Boolean {
            service.shutdownNow()
            handler.looper.quit()
            return super.quit()
        }

        override fun onLooperPrepared() {
            super.onLooperPrepared()
            handler=Handler(looper){
                when (it.what){
                    CODE_TO_SHOW_DIALOG -> showDialog()
                    CODE_TO_DISMISS_DIALOG -> dismissDialog()
                    CODE_TO_CHECK_MODEL -> checkModel(it.obj as String?)
                    CODE_TO_WRITE_MODEL -> writeModel(it.obj as String?)
                    CODE_TO_READ_MODEL -> readModel(it.obj as String)
                }
                return@Handler true
            }
        }

        private fun writeModel(name: String?) {
            service.submit {
                val randomNum = { abs(ThreadLocalRandom.current().nextInt())%150+75 }
                val model = PhotoModel(
                    name!!.replace(".jpg", ""),
                    Drawable.createFromStream(assets.open("photoModelsImages/$name"), null),
                    randomNum(),
                    randomNum()
                )
                DBHelper.getHelper(this@MainActivity).insertModel(model)
                addToModelList(model)
            }
        }

        private fun readModel(name: String?){
            service.submit {
                val model =
                    DBHelper.getHelper(this@MainActivity).getModelByName(name?.replace(".jpg", ""))
                model.photoDrawable = Drawable.createFromStream(assets.open("photoModelsImages/$name"), null)
                addToModelList(model)
            }
        }

        @Synchronized
        private fun addToModelList(model: PhotoModel){
            listPhotosModel.add(model)
            if (listPhotosModel.size>=NUMBER_OF_MODELS && !service.isShutdown) {
                service.shutdown()
                endThreadLoading()
                handler.looper.quitSafely()
            }
        }

        private fun endThreadLoading() {
            setupRecView()
            initButtons()
            dismissDialog()
        }

        fun checkModel(name: String?) {
            val isExists:Future<Boolean> =service.submit(Callable {
                DBHelper.getHelper(this@MainActivity).isModelExists(name?.replace(".jpg", ""))
            })
            when(isExists.get()){
                true -> handler.sendMessage(Message.obtain(handler,CODE_TO_READ_MODEL,name))
                false ->handler.sendMessage(Message.obtain(handler,CODE_TO_WRITE_MODEL,name))
            }
        }

        private fun showDialog() {
            runOnUiThread{progressDialog.show() }
        }

        private fun dismissDialog() {
            runOnUiThread{progressDialog.dismiss() }
        }
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

    override fun onSnapPositionChange(position: Int) {
        Blurry.with(this).from(listPhotosModel[position].photoDrawable.toBitmap()).into(recyclerViewBackground)
        nameText.text = listPhotosModel[position].name
        callText.visibility=View.VISIBLE
        heyText.visibility=View.VISIBLE
        howText.visibility=View.VISIBLE
        favouriteText.text = listPhotosModel[position].like.toString()
        groupsText.text = listPhotosModel[position].group.toString()
    }

}

