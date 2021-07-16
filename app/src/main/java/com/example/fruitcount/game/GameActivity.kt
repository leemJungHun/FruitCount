package com.example.fruitcount.game

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.setPadding
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fruitcount.BaseActivity
import com.example.fruitcount.R
import com.example.fruitcount.customview.OutlineTextView
import com.example.fruitcount.databinding.ActivityGameBinding
import com.example.fruitcount.game.aac.viewmodel.GameViewModel
import com.example.fruitcount.game.adapter.FruitCountAdapter
import com.example.fruitcount.game.data.Item
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kr.rowan.superbrain_5gxr.game.dialog.DialogType
import kr.rowan.superbrain_5gxr.game.dialog.SharingDialog
import java.util.concurrent.TimeUnit
import kotlin.random.Random

@SuppressLint("ClickableViewAccessibility")
class GameActivity : BaseActivity() {
    private lateinit var binding: ActivityGameBinding
    var level = 8
    private var itemCount = 0
    var itemMax = 0
    private var gameNumList = ArrayList<Int>()
    private var gameItemList = ArrayList<Item>()
    private var gameImgList = ArrayList<Int>()
    private var resultList = ArrayList<Int>()
    private val answerImgViews = ArrayList<AppCompatTextView>()
    private var answerNumList = ArrayList<Int>()

    var answer = 0


    var scene: Int = 0
    var sound: Int = 0

    private var mCompositeDisposable: CompositeDisposable? = null
    private var mainTimerDisposable: Disposable? = null
    private var mainSeconds = 0
    private var timeLeft = 180000L

    private var fruitCountAdapter: FruitCountAdapter? = null
    private val viewModel: GameViewModel by viewModels()

    val operatorImgs = arrayListOf(
        R.drawable.operator_1,
        R.drawable.operator_2,
        R.drawable.operator_3,
        R.drawable.operator_4,
    )

    private val fruitImgs = arrayListOf(
        R.drawable.matching_icon_fr_01,
        R.drawable.matching_icon_fr_02,
        R.drawable.matching_icon_fr_03,
        R.drawable.matching_icon_fr_04,
        R.drawable.matching_icon_fr_05,
        R.drawable.matching_icon_fr_06,
        R.drawable.matching_icon_fr_07,
        R.drawable.matching_icon_fr_10,
        R.drawable.matching_icon_fr_11
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        answerImgViews.add(binding.answerSelectTxtView1)
        answerImgViews.add(binding.answerSelectTxtView2)
        answerImgViews.add(binding.answerSelectTxtView3)
        answerImgViews.add(binding.answerSelectTxtView4)
        setGameItemSize(
            binding.levelImgView,
            (standardSizeX!! / 100 * 12),
            (standardSizeY!! / 10),
            (standardSizeX!! / 100 * 2.8).toInt(),
            (standardSizeY!! / 100 * 4.33).toInt(),
            0,
            0
        )
        setGameItemSize(
            binding.showMenuImgView,
            (standardSizeX!! / 100 * 3.6).toInt(),
            (standardSizeX!! / 100 * 3.6).toInt(),
            0,
            (standardSizeY!! / 100 * 4.3).toInt(),
            (standardSizeX!! / 100 * 2.8).toInt(),
            0
        )

        for (i in 0 until answerImgViews.size) {
            setGameItemSize(
                answerImgViews[i],
                (standardSizeX!! / 5),
                (standardSizeX!! / 9),
                0,
                0,
                0,
                (standardSizeY!! / 100 * 2.67).toInt()
            )
            answerImgViews[i].textSize = (standardSizeX!! / 20).toFloat()
        }

        binding.timerTxtView.setPadding((standardSizeX!! / 100 * 0.8).toInt())
        binding.timerTxtView.textSize = (standardSizeX!! / 100 * 2.4 / density!!).toFloat()
        binding.levelImgView.textSize = (standardSizeX!! / 100 * 2.2 / density!!).toFloat()
        val timerTxtViewParam = binding.timerTxtView.layoutParams as ViewGroup.MarginLayoutParams
        timerTxtViewParam.setMargins(0, 0, (standardSizeX!! / 100 * 1.2).toInt(), 0)
        binding.timerTxtView.layoutParams = timerTxtViewParam

        fruitCountAdapter = FruitCountAdapter(
            (standardSizeY!! / 100 * 15),
            (standardSizeY!! / 100 * 10),
            this
        )

        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.gameListView.adapter = fruitCountAdapter
        binding.gameListView.layoutManager = linearLayoutManager
        viewModel.gameItems.observe(this, {
            fruitCountAdapter!!.submitList(it)
            CoroutineScope(mainDispatcher).launch {
                delay(100)
                val answerRan = Random.nextInt(4)
                answerNumList = ArrayList()
                for (i in 0 until answerImgViews.size) {
                    if (answerRan == i) {
                        answerNumList.add(answer)
                    } else {
                        answerNumList.add(answer + i + 1)
                    }
                }
                answerNumList.shuffle()
                for (i in 0 until answerNumList.size) {
                    answerImgViews[i].text = answerNumList[i].toString()
                    answerImgViews[i].tag = answerNumList[i].toString()
                    answerImgViews[i].setOnTouchListener(itemTouchListener)
                }
            }
        })

        mCompositeDisposable = CompositeDisposable()

        setTimerText(binding.timerTxtView, timeLeft)

        mainTimer(180)

        initGame(level)

    }

    private fun initGame(rstGameLevel: Int) {
        gameNumList = ArrayList()
        gameItemList = ArrayList()
        gameImgList = ArrayList()
        resultList = ArrayList()
        binding.levelImgView.text = ("${rstGameLevel}단계")
        when (rstGameLevel) {
            1, 2, 3, 4, 5 -> {
                itemCount = 2
                itemMax = if (rstGameLevel == 1 || rstGameLevel == 5) {
                    30
                } else {
                    40
                }
            }
            6, 7, 8 -> {
                itemCount = 3
                itemMax = 30
            }
        }

        for (i in 0 until itemCount) {
            var duplication = true
            while (duplication) {
                val randomValue = Random.nextInt(1, itemMax)
                if (!gameNumList.contains(randomValue)) {
                    gameNumList.add(randomValue)
                    duplication = false
                }
            }
        }

        for (i in 0 until itemCount) {
            var duplication = true
            while (duplication) {
                val randomImg = fruitImgs[Random.nextInt(1, fruitImgs.size)]
                if (!gameImgList.contains(randomImg)) {
                    gameImgList.add(randomImg)
                    duplication = false
                }
            }
        }


        CoroutineScope(mainDispatcher).launch {
            when (rstGameLevel) {
                1 -> {
                    itemSet(2, 1, 1, 1, false, rstGameLevel)
                    delay(100)
                    itemSet(2, 2, 1, 1, false, rstGameLevel)
                    delay(100)
                    itemSet(1, 1, 1, 1, true, rstGameLevel)
                }
                2 -> {
                    itemSet(2, 1, 1, 1, false, rstGameLevel)
                    delay(100)
                    itemSet(2, 2, 1, 2, false, rstGameLevel)
                    delay(100)
                    itemSet(2, 2, 1, 2, true, rstGameLevel)
                }
                3 -> {
                    itemSet(3, 1, 2, 1, false, rstGameLevel)
                    delay(100)
                    itemSet(3, 2, 2, 1, false, rstGameLevel)
                    delay(100)
                    itemSet(1, 1, 1, 2, true, rstGameLevel)
                }
                4 -> {
                    itemSet(3, 1, 2, 2, false, rstGameLevel)
                    delay(100)
                    itemSet(3, 2, 2, 2, false, rstGameLevel)
                    delay(100)
                    itemSet(2, 2, 1, 2, true, rstGameLevel)
                }
                5 -> {
                    itemSet(3, 1, 2, 2, false, rstGameLevel)
                    delay(100)
                    itemSet(3, 2, 2, 2, false, rstGameLevel)
                    delay(100)
                    itemSet(3, 2, 2, 3, true, rstGameLevel)
                }
                6 -> {
                    itemSet(3, 1, 2, 1, false, rstGameLevel)
                    delay(100)
                    itemSet(3, 2, 2, 1, false, rstGameLevel)
                    delay(100)
                    itemSet(3, 3, 2, 1, false, rstGameLevel)
                    delay(100)
                    itemSet(1, 1, 1, 1, true, rstGameLevel)
                }
                7 -> {
                    itemSet(3, 2, 2, 2, false, rstGameLevel)
                    delay(100)
                    itemSet(3, 3, 2, 2, false, rstGameLevel)
                    delay(100)
                    itemSet(3, 3, 2, 2, false, rstGameLevel)
                    delay(100)
                    itemSet(3, 3, 2, 4, true, rstGameLevel)
                }
                8 -> {
                    itemSet(3, 3, 2, 2, false, rstGameLevel)
                    delay(100)
                    itemSet(3, 3, 2, 2, false, rstGameLevel)
                    delay(100)
                    itemSet(3, 3, 2, 2, false, rstGameLevel)
                    delay(100)
                    itemSet(3, 3, 2, 4, true, rstGameLevel)
                }
            }
            delay(100)
            viewModel.setItems(gameItemList)
        }

    }

    /**
     * @numCount : 숫자 개수(ex: A+A+A 일 경우 3개)
     * @valuesCount : 값 종류 개수(ex: A+A+A 일 경우 1개)
     * @operatorCount : 연산자 개수(ex: A+A+A 일 경우 2개)
     * @operatorTypeCount : 연산자 종류 개수(ex: A+A+A 일 경우 1개, A+A-A일 경우 2개)
     * @isFinish : 마지막 아이템?
     */
    private fun itemSet(
        numCount: Int,
        numTypeCount: Int,
        operatorCount: Int,
        operatorTypeCount: Int,
        isFinish: Boolean,
        rstGameLevel: Int
    ) {
        var valueSetting = false
        var operatorSetting = false
        var resultValue = 0
        var ranValue = Random.nextInt(numTypeCount)
        var operatorList = ArrayList<Int>()
        var operatorImgList = ArrayList<Int>()
        val numList = ArrayList<Int>()
        val imgList = ArrayList<Int>()
        var duplicationCount = 0
        var itemAnswer = ""

        if (numCount == 1) {
            when (rstGameLevel) {
                1, 3 -> {
                    imgList.add(gameImgList[1])
                    answer = gameNumList[1]
                }
                6 -> {
                    imgList.add(gameImgList[2])
                    answer = gameNumList[2]
                }
            }
            itemAnswer = "?"
            val item = Item(imgList, operatorImgList, itemAnswer, isFinish)
            gameItemList.add(item)
        } else if (rstGameLevel == 2 && isFinish) {
            if (gameItemList[1].operatorImgList[0] == R.drawable.operator_1) {
                answer = if (numList.add(gameNumList[0]) < numList.add(gameNumList[1])) {
                    imgList.add(gameImgList[1])
                    imgList.add(gameImgList[0])
                    gameNumList[1] - gameNumList[0]
                } else {
                    imgList.add(gameImgList[0])
                    imgList.add(gameImgList[1])
                    gameNumList[0] - gameNumList[1]
                }
                operatorImgList.add(R.drawable.operator_2)
            } else {
                imgList.add(gameImgList[0])
                imgList.add(gameImgList[1])
                gameNumList[0] + gameNumList[1]
                operatorImgList.add(R.drawable.operator_1)
            }
            itemAnswer = "?"
            val item = Item(imgList, operatorImgList, itemAnswer, isFinish)
            gameItemList.add(item)
        } else {
            while (!valueSetting) {
                when (numTypeCount) {
                    1 -> {
                        imgList.add(gameImgList[ranValue])
                        numList.add(gameNumList[ranValue])
                    }
                    2 -> {
                        if (numCount == 2) {
                            ranValue = Random.nextInt(numTypeCount)
                            if (!numList.contains(gameNumList[ranValue])) {
                                imgList.add(gameImgList[ranValue])
                                numList.add(gameNumList[ranValue])
                            }
                        } else if (numCount == 3) {
                            ranValue = Random.nextInt(numTypeCount)
                            if (numList.contains(gameNumList[ranValue])) {
                                if (duplicationCount != 1) {
                                    imgList.add(gameImgList[ranValue])
                                    numList.add(gameNumList[ranValue])
                                    duplicationCount++
                                }
                            } else {
                                imgList.add(gameImgList[ranValue])
                                numList.add(gameNumList[ranValue])
                            }
                        }
                    }
                    3 -> {
                        ranValue = Random.nextInt(numTypeCount)
                        if (!numList.contains(gameNumList[ranValue])) {
                            imgList.add(gameImgList[ranValue])
                            numList.add(gameNumList[ranValue])
                        }
                    }
                }
                if (numCount == numList.size) {
                    valueSetting = true
                }
            }


            while (!operatorSetting) {
                var reSet = false
                //val ranOperator = Random.nextInt(operator)
                if (operatorTypeCount >= 3) {
                    operatorList.add(Random.nextInt(operatorTypeCount))
                    operatorList.add(Random.nextInt(operatorTypeCount))
                } else {
                    operatorList.add(Random.nextInt(operatorTypeCount))
                    operatorList.add(Random.nextInt(operatorTypeCount))
                }
                operatorList.shuffle()
                operatorImgList.add(operatorImgs[operatorList[0]])
                operatorImgList.add(operatorImgs[operatorList[1]])
                when (operatorCount) {
                    1 -> {
                        when (operatorList[0]) {
                            0 -> {
                                resultValue = numList[0] + numList[1]
                            }
                            1 -> {
                                resultValue = numList[0] - numList[1]
                            }
                        }
                    }
                    2 -> {
                        when (operatorList[0]) {
                            0 -> {
                                when (operatorList[1]) {
                                    0 -> {
                                        resultValue = numList[0] + numList[1] + numList[2]
                                    }
                                    1 -> {
                                        resultValue = numList[0] + numList[1] - numList[2]
                                        if (numList[1] - numList[2] == 0 || numList.contains(
                                                resultValue
                                            )
                                        ) {
                                            reSet = true
                                        }
                                    }
                                    2 -> {
                                        resultValue = numList[0] + numList[1] * numList[2]
                                    }
                                    3 -> {
                                        resultValue = numList[0] + numList[1] / numList[2]
                                        if(numList[1] / numList[2] <= 0 || (numList[1].toFloat() / numList[2]).toString().split(".")[1] != "0"){
                                            reSet = true
                                        }
                                    }
                                }
                            }
                            1 -> {
                                when (operatorList[1]) {
                                    0 -> {
                                        resultValue = numList[0] - numList[1] + numList[2]
                                        if (numList[0] - numList[1] <= 0 || numList.contains(
                                                resultValue
                                            )
                                        ) {
                                            reSet = true
                                        }
                                    }
                                    1 -> {
                                        resultValue = numList[0] - numList[1] - numList[2]
                                    }
                                    2 -> {
                                        resultValue = numList[0] - numList[1] * numList[2]
                                    }
                                    3 -> {
                                        resultValue = numList[0] - numList[1] / numList[2]
                                        if(numList[1] / numList[2] <= 0 || (numList[1].toFloat() / numList[2]).toString().split(".")[1] != "0"){
                                            reSet = true
                                        }
                                    }
                                }
                            }
                            2 -> {
                                when (operatorList[1]) {
                                    0 -> {
                                        resultValue = numList[0] * numList[1] + numList[2]
                                    }
                                    1 -> {
                                        resultValue = numList[0] * numList[1] - numList[2]
                                    }
                                }
                            }
                            3 -> {
                                when (operatorList[1]) {
                                    0 -> {
                                        resultValue = numList[0] / numList[1] + numList[2]
                                    }
                                    1 -> {
                                        resultValue = numList[0] / numList[1] - numList[2]
                                    }
                                }
                                if(numList[0] / numList[1] <= 0 || (numList[0].toFloat() / numList[1]).toString().split(".")[1] != "0"){
                                    reSet = true
                                }
                            }
                        }
                    }
                }

                Log.e("resultList","$resultList")
                Log.e("resultValue","$resultValue")
                if (resultValue > 0 && !reSet && !resultList.contains(resultValue)) {
                    operatorSetting = true
                    if (operatorCount == 1) {
                        operatorImgList.removeAt(1)
                    }
                    if (isFinish) {
                        answer = resultValue
                        itemAnswer = "?"
                    } else {
                        Log.e("numList[0]", "${numList[0]}")
                        Log.e("numList[1]", "${numList[1]}")
                        Log.e("numList[2]", "${numList[2]}")
                        itemAnswer = resultValue.toString()
                        resultList.add(resultValue)
                    }
                    val item = Item(imgList, operatorImgList, itemAnswer, isFinish)
                    gameItemList.add(item)
                }else if(resultList.contains(resultValue)){
                    val num = numList[0]
                    val image = imgList[0]
                    numList[0] = numList[1]
                    numList[1] = numList[2]
                    numList[2] = num
                    imgList[0] = imgList[1]
                    imgList[1] = imgList[2]
                    imgList[2] = image
                    operatorList = ArrayList()
                    operatorImgList = ArrayList()
                } else {
                    operatorList = ArrayList()
                    operatorImgList = ArrayList()
                }

            }
        }
    }

    private val itemTouchListener = View.OnTouchListener { view: View?, evt: MotionEvent ->
        Log.e("action", "${evt.action}")
        when (evt.action) {
            MotionEvent.ACTION_DOWN -> {
                view!!.setBackgroundResource(R.drawable.img_calculation_box_s_2)
            }
            MotionEvent.ACTION_UP -> {
                view!!.setBackgroundResource(R.drawable.img_calculation_box_s)
                if (view.tag.toString().toInt() == answer) {
                    rightAnswer()
                } else {
                    wrongAnswer()
                }

                CoroutineScope(mainDispatcher).launch {
                    showToast(scene)
                    playSoundPoolEffect(sound, 0)
                }
            }
        }
        true
    }

    fun rightAnswer() {
        scene = R.drawable.img_game_o
        sound = R.raw.powerup_success
    }

    fun wrongAnswer() {
        scene = R.drawable.img_game_x
        sound = R.raw.spring_bounce
    }

    fun showToast(scene: Int) {
        for (i in 0 until answerImgViews.size) {
            answerImgViews[i].setOnTouchListener(null)
        }
        val dialog = showOxDialog(scene)

        CoroutineScope(mainDispatcher).launch {
            delay(800L)
            dialog.dismiss()
            initGame(level)
        }
    }

    private fun showOxDialog(img: Int): SharingDialog {
        val oxDialog = SharingDialog(this)
        oxDialog
            .setType(DialogType.GAME_OX)
            .setOxIcon(img)
            .setSize((standardSizeX!! / 100 * 20), (standardSizeX!! / 100 * 20))
            .window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        oxDialog
            .window!!.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        /*.window!!.attributes.windowAnimations = R.anim.alpha_500*/
        oxDialog.setOwnerActivity(this)
        oxDialog.show()
        return oxDialog
    }

    private fun setTimerText(textView: OutlineTextView, remainedTime: Long) {
        val time = remainedTime / 1000
        val minute = time / 60
        val second = if (time % 60 >= 10) time % 60 else "0${(time % 60)}"

        textView.text = (" $minute : $second ")
    }


    private fun mainTimer(limit: Int) {

        val duration = Observable.interval(1, TimeUnit.SECONDS)
        mainTimerDisposable = duration.observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                mainSeconds++
                timeLeft -= 1000

                setTimerText(binding.timerTxtView, timeLeft)
                if (timeLeft <= 10000) binding.timerTxtView.startAnimation()
                Log.e("mainTimer", "mainSeconds = $mainSeconds")
                if (mainSeconds == limit) {
                    mainSeconds--
                    setTimerText(binding.timerTxtView, 0)
                }
            }
        mCompositeDisposable!!.add(mainTimerDisposable!!)
    }
}