package com.example.fruitcount.game.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fruitcount.BaseActivity
import com.example.fruitcount.R
import com.example.fruitcount.databinding.ItemFruitcountBinding
import com.example.fruitcount.game.data.Item
import kotlin.random.Random

class FruitCountAdapter(val imgSize: Int, val textSize: Int, val activity: BaseActivity) :
    ListAdapter<Item, FruitCountAdapter.FruitCountViewHolder>(
        MyDiffCallback
    ) {

    var ranOperator = Random.nextInt(2)
    var imgViewList = ArrayList<AppCompatImageView>()
    var contentImgViews = ArrayList<AppCompatImageView>()
    var operatorImgViews = ArrayList<AppCompatImageView>()




    inner class FruitCountViewHolder(val binding: ItemFruitcountBinding) :
        RecyclerView.ViewHolder(binding.root) {
        // 그냥 View하고 데이터 연결하는 거 생각하면 됩니다
        fun bind(item: Item) {
            binding.item = item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FruitCountViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: ItemFruitcountBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.item_fruitcount, parent, false)
        return FruitCountViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FruitCountViewHolder, position: Int) {
        holder.bind(getItem(position))
        val binding = holder.binding

        ranOperator = Random.nextInt(2)


        contentImgViews = ArrayList()
        operatorImgViews = ArrayList()
        contentImgViews.add(binding.contentImgView1)
        contentImgViews.add(binding.contentImgView2)
        contentImgViews.add(binding.contentImgView3)
        operatorImgViews.add(binding.operatorImgView1)
        operatorImgViews.add(binding.operatorImgView2)
        operatorImgViews.add(binding.operatorImgView3)

        for (i in 0 until contentImgViews.size) {
            contentImgViews[i].layoutParams.width = imgSize
            contentImgViews[i].layoutParams.height = imgSize
            operatorImgViews[i].layoutParams.width = imgSize
            operatorImgViews[i].layoutParams.height = imgSize
            contentImgViews[i].visibility = View.GONE
            if (i != operatorImgViews.size - 1) {
                operatorImgViews[i].visibility = View.GONE
            }
        }

        Log.e("position:$position","img${getItem(position).imageList}")
        Log.e("position:$position","img${getItem(position).operatorImgList}")

        for(i in 0 until getItem(position).imageList.size){
            setImage(contentImgViews[i], getItem(position).imageList[i])
        }

        for(i in 0 until getItem(position).operatorImgList.size){
            setImage(operatorImgViews[i], getItem(position).operatorImgList[i])
        }

        binding.answerTxtView.textSize = textSize.toFloat()

        binding.answerTxtView.text = getItem(position).answer


    }

    private fun setImage(view: AppCompatImageView, image: Int) {
        view.setImageResource(image)
        view.visibility = View.VISIBLE
    }
}