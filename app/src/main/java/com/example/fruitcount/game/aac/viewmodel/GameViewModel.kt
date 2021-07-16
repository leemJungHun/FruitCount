package com.example.fruitcount.game.aac.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fruitcount.game.data.Item

class GameViewModel:  ViewModel() {
    private val _gameItems = MutableLiveData<ArrayList<Item>>()
    val gameItems: LiveData<ArrayList<Item>> = _gameItems

    fun setItems(gameItems:ArrayList<Item>){
        _gameItems.value = gameItems
    }

}