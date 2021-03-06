package tw.tim.mvvm_greedy_snake.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Response
import tw.tim.mvvm_greedy_snake.model.data.SnakeScore
import tw.tim.mvvm_greedy_snake.model.DataModel
import tw.tim.mvvm_greedy_snake.model.data.Position
import tw.tim.mvvm_greedy_snake.model.enums.Direction
import tw.tim.mvvm_greedy_snake.model.enums.GameState
import tw.tim.mvvm_greedy_snake.rtmtutorial.AGApplication
import tw.tim.mvvm_greedy_snake.rtmtutorial.ChatManager
import kotlin.concurrent.fixedRateTimer
import kotlin.random.Random

class MainViewModel : ViewModel(), DataModel.OnDataReadyCallback{
    private val body = mutableListOf<Position>()
    private val size = 20
    var name = ""
    private var score = 0
    private var bonus : Position? = null
    private var direction = Direction.LEFT
    private var flag = true
    var total_score = 0

    // LiveData & MutableLiveData 區別
    // https://www.cnblogs.com/guanxinjing/p/11544273.html
    var snake = MutableLiveData<List<Position>>()
    var bonusPosition = MutableLiveData<Position>()
    var gameState = MutableLiveData<GameState>()
    var scoreData = MutableLiveData<Int>()

    val insertLiveData: MutableLiveData<Response<SnakeScore>> = MutableLiveData()
    val getRankLiveData: MutableLiveData<List<SnakeScore>> = MutableLiveData()
    private var mDataModel: DataModel = DataModel()

    val signUpLiveData: MutableLiveData<Response<SnakeScore>> = MutableLiveData()
    val logInLiveData: MutableLiveData<List<SnakeScore>> = MutableLiveData()


//    private var mChatManager: ChatManager = AGApplication.the().getChatManager()
//    private var mRtmClient: RtmClient? = mChatManager.getRtmClient()


    /**
     *  移動
     */
    fun move(dir: Direction){
        direction = dir
    }

    /**
     *  遊戲開始
     */
    fun start(){
        // 防止連點機制  Button連點造成多個的錯誤
        if(flag){

            initialization()

            // kotlin run, let, with, also, apply 用法
            // https://louis383.medium.com/%E7%B0%A1%E4%BB%8B-kotlin-run-let-with-also-%E5%92%8C-apply-f83860207a0c
            // 當第一次吃到紅點 bonusPosition的位置 = 之後隨機產生出來的紅點位置
            bonus = nextBonus().also{
                bonusPosition.value = it
            }

            // TODO 影片後面故意不給看 也可以用Timer().schedule(object : TimerTask() 這種方式做
            fixedRateTimer("timer",true,1,200){
                val pos = body.first().copy().apply {
                    // 按下上下左右鍵做動作
                    when (direction){
                        Direction.LEFT -> x--
                        Direction.RIGHT -> x++
                        Direction.UP -> y--
                        Direction.DOWN -> y++
                    }
//                    Log.e("x",x.toString() )
//                    Log.e("y",y.toString() )
                    // 左上座標為(0,0)
                    // 判斷吃到自己 & 超出範圍 遊戲結束
                    // x,y 大於等於20, x,y 小於0
                    if(body.contains(this) || x < 0 || x >= size || y < 0 || y >= size){
                        gameState.postValue(GameState.GAME_OVER)
                        flag = true
                        cancel()
                    }
                }
                // 每移動一次 就新增一次 然後判斷是否吃到紅點 吃到就+1 沒有就移除最後一個點
                body.add(0, pos)
                if(pos != bonus){
                    body.removeLast()
                }else{
                    // 吃到紅點 更新紅點位置
                    bonus = nextBonus().also {
                        bonusPosition.postValue(it)
                    }
                    score ++
//                    Log.e("score",score.toString())
                    scoreData.postValue(score)
                    total_score = score
                }
                snake.postValue(body)
            }
        }

    }

    /**
     *  初始化
     */
    private fun initialization(){
        flag = false
        // score 當重新開始時 , Score歸零且要通知他更新
        score = 0
        scoreData.postValue(score)

        gameState.postValue(GameState.ONGOING)

        // 當沒有初始化為向左的話 再你往右邊撞牆後 再按REPLAY 將會無限循環
        direction = Direction.LEFT

        body.clear()
        // 一開始給4個body 中心點 固定位置
        body.add(Position(10,10))
        body.add(Position(11,10))
        body.add(Position(12,10))
        body.add(Position(13,10))
    }

    /**
     *  保存分數
     */
     fun snakeScoreInsert(){
        mDataModel!!.snakeScoreInsert(name, total_score,this)
    }

    /**
     *  取得分數
     */
    fun getRankData(){
        mDataModel!!.getSnakeScore(this)
    }

    /**
     *  隨機給下個紅點
     */
    private fun nextBonus() : Position {
        return Position(Random.nextInt(size), Random.nextInt(size))
    }

    /**
     *  登入聊天室
     */
     fun loginChat(tname :String?) {

//        if (tname != null) {
//            name = tname
//        }
//        mChatManager = AGApplication.the().getChatManager()
//        mRtmClient = mChatManager.getRtmClient()

//        Log.e("mChatManager", mChatManager.toString())
//        Log.e("mRtmClient", mRtmClient.toString())
//
//        Log.e("loginChat","loginChat")
//        Log.e("loginChat_name",name)
//        mRtmClient?.login(null, name, object : ResultCallback<Void?> {
//            override fun onSuccess(p0: Void?) {
//                Log.e("Login Success: ", p0.toString())
//            }
//
//            override fun onFailure(p0: ErrorInfo?) {
//                Log.e("Login Failure: ", p0.toString())
//            }
//
//        })
    }

    fun signUpInsert(account: String, nickname: String ,email: String, password: String){
        mDataModel!!.signUpInsert(account, nickname, email, password, this)
    }

    fun logIn(account: String,  password: String){
        mDataModel!!.logIn(account, password, this)
    }

    override fun onListData(data: List<SnakeScore>?) {
//        getRankLiveData.value = data
        getRankLiveData.postValue(data)
//        Log.e("data",data.toString())
//        Log.e("getRankLiveData",getRankLiveData.toString())
    }

    override fun onData(data: Response<SnakeScore>?) {
        insertLiveData.postValue(data)
    }

    override fun onSignUpData(data: Response<SnakeScore>?) {
        signUpLiveData.postValue(data)
    }

    override fun onLogInListData(data: List<SnakeScore>?) {
        logInLiveData.postValue(data)
    }

}
