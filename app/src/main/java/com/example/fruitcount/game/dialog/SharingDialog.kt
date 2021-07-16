package kr.rowan.superbrain_5gxr.game.dialog

/**
 * @since 2021.02.08.Tue
 * @author Thomas Park in ROWAN INC.
 */

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import com.bumptech.glide.Glide
import com.example.fruitcount.databinding.DialogGameoxBinding
import kr.rowan.superbrain_5gxr.game.listener.OnSingleClickListener


private val TAG = SharingDialog::class.java.simpleName

class SharingDialog constructor(
    context: Context
) : BaseDialog(context) {


    private lateinit var gameOXDialog: DialogGameoxBinding

    private var mSoundPool: SoundPool? = null
    private var mStreamId = 0

    var medal = ""
    private var width = 0
    private var height = 0
    private var oxIcon = 0
    private var level = 0
    private var tintColor: Int = 0
    private var fireCracker = false
    private var negativeBtnTxt: String? = null
    private var onPositiveClickListener: OnSingleClickListener? = null
    private var onNegativeClickListener: OnSingleClickListener? = null
    private lateinit var score: String
    private lateinit var correct: String
    private lateinit var total: String
    private lateinit var responseTimeAvg: String
    private lateinit var title: String
    private lateinit var content: String
    private lateinit var content2: String
    private lateinit var type: DialogType
    private lateinit var positiveBtnTxt: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        setCancelable(false)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        when (type) {
            DialogType.GAME_OX -> {
                gameOXDialog = DialogGameoxBinding.inflate(layoutInflater)
                setContentView(gameOXDialog.root)
                gameOXDialog.apply {
                    imgView.layoutParams.width = width
                    imgView.layoutParams.height = height
                    Glide.with(context).load(oxIcon).into(imgView)
                }
            }
        }
    }

    var repeatCount = 0
    var mediaPlayer: MediaPlayer? = null

    fun setFireCracker(fireCracker: Boolean): SharingDialog {
        this.fireCracker = fireCracker
        return this
    }

    fun setTitle(title: String): SharingDialog {
        this.title = title
        return this
    }

    fun setContent(content: String): SharingDialog {
        this.content = content
        return this
    }

    fun setContent2(content: String): SharingDialog {
        this.content2 = content
        return this
    }

    fun setOxIcon(icon: Int): SharingDialog {
        this.oxIcon = icon
        return this
    }

    fun setLevel(level: Int): SharingDialog {
        this.level = level
        return this
    }

    fun setCorrectTxt(correctTxt: String): SharingDialog {
        this.correct = correctTxt
        return this
    }

    fun setTotalTxt(totalTxt: String): SharingDialog {
        this.total = totalTxt
        return this
    }

    fun setScore(score: String): SharingDialog {
        this.score = score
        return this
    }

    fun setResponseTimeAvg(responseTimeAvg: String): SharingDialog {
        this.responseTimeAvg = responseTimeAvg
        return this
    }

    fun setPositiveBtnTxt(txt: String): SharingDialog {
        this.positiveBtnTxt = txt
        return this
    }

    fun setNegativeBtnTxt(txt: String): SharingDialog {
        this.negativeBtnTxt = txt
        return this
    }

    fun setOnPositiveButtonClickListener(onPositiveClickListener: OnSingleClickListener): SharingDialog {
        this.onPositiveClickListener = onPositiveClickListener
        return this
    }

    fun setOnNegativeButtonClickListener(onNegativeClickListener: OnSingleClickListener): SharingDialog {
        this.onNegativeClickListener = onNegativeClickListener
        return this
    }

    fun setType(type: DialogType): SharingDialog {
        this.type = type
        return this
    }


    fun setTintColor(tintColor: Int): SharingDialog {
        this.tintColor = tintColor
        return this
    }

    fun setSize(width: Int, height: Int): SharingDialog {
        this.width = width
        this.height = height
        return this
    }


    override fun dismiss() {
        if (mediaPlayer != null) {

            mediaPlayer!!.apply {
                stop()
                release()
            }
            mediaPlayer = null
        }

        super.dismiss()
    }

    override fun show() {
        super.show()
    }

    interface CallBack {
        fun onClick(medal: String)
    }

}
