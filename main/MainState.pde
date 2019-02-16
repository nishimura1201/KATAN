//メインのステートマシンやインタフェイスについて記述

//メインステートマシン
class MainStateMachine extends StateChanger{
    String orderPlayerName[] = new String[PLAYER_NUMBER];//プレイヤーのターン順序
    int whoseTurn = 0;//今誰のターンなのか管理する
    boolean debugFlag = false;//デバッグモードONのフラグ
    //コンストラクタ
    public MainStateMachine(){
      super();

      Add("player1",new PlayerStateMachine( "player1"));
      Add("player2",new PlayerStateMachine( "player2"));
      Add("player3",new PlayerStateMachine( "player3"));
      Add("debug",new Debug());
      //プレイヤーのターン順序
      orderPlayerName[0] = "player1";
      orderPlayerName[1] = "player2";
      orderPlayerName[2] = "player3";
      //最初はplayer1から
      Change("player1");
    }


    public String Update(int elapsedTime){
      //DebugModeのON,OFF
      if(keyPushJudge.GetJudge("d")){
        if(debugFlag){
          debugFlag = false;
          Change("player1");
        }
        else{
          debugFlag = true;
          Change("debug");
        }
      }

      //子ステート(PlayerStateとdebug)の実行
      String order = mCurrentState.Update(elapsedTime);
      switch(order){
        //次のプレイヤーにステートを移す
        case "ChangePlayer":
          if(whoseTurn+1 == orderPlayerName.length){
            whoseTurn = 0;
          }else{
            whoseTurn+=1;
          }
          Change(orderPlayerName[whoseTurn]);
          break;
      }

      return "null";
    }

    public void Render(){
        mCurrentState.Render();
        fill(50, 50, 50, 255);
        textSize(20);
        text("D         :debug mode", 10, FIELD_LENGTH_Y-100 + 0);
        text("A         :change player", 10, FIELD_LENGTH_Y-100 + 20);
        text("Z         :change motion", 10, FIELD_LENGTH_Y-100 + 40);
        text("ENTER     :choice motion", 10, FIELD_LENGTH_Y-100 + 60);
        text("BACK SPACE:back to player", 10, FIELD_LENGTH_Y-100 + 80);
    }
}

//複数の子を管理するステートマシンのベースとなるクラス
public class StateChanger implements IState{
  Map<String, IState> mStates = new HashMap<String, IState>();
  IState mCurrentState = new emptyState();
  List<String> childList = new ArrayList<String>();//子の名前のリスト
  boolean childOn = false;//子に主導権が移っているかどうか

  //コンストラクタ
  public StateChanger(){
    mStates.put("empty",new emptyState());//empty の Add
  }
  //子の主導権を消し、自分に主導権が移る.子が呼ぶ
  public void ChildOFF(){
    Change("empty");
    childOn = false;
  }

  public void Change(String stateName){
    mCurrentState.OnExit();
    mCurrentState = mStates.get(stateName);
    mCurrentState.OnEnter();

    childOn = true;
  }

  //子供のステートを追加する
  public void Add(String name, IState state){
    mStates.put(name,state);
    childList.add(name);//子リストに追加
  }
  //メッセージを受け取ってそれに応じた関数をキックする
  public void MessageOrder(String message){};
  public String Update(int elapsedTime){return "null";};
  public void Render(){};
  public void OnEnter(){};
  public void OnExit(){};
}

//複数の子を管理するステートマシンのベースとなるクラス
public class PlayerActionBase extends StateChanger{
  PlayerStateMachine PlayerStateMachine;//プレイヤーステートマシン（親の参照）
  //コンストラクタ
  public PlayerActionBase(PlayerStateMachine tmp){
    super();
    PlayerStateMachine = tmp;
  }

  public String PlayerStateMachineChildOFF(){
    if(keyPushJudge.GetJudge("BACKSPACE") == true){
      return "ChildOFF";
    }
    return "null";
  };
}

//ステートのベースとなるインタフェイス
public interface IState{
  public String Update(int elapsedTime);
  public void Render();
  public void OnEnter();
  public void OnExit();
  public void MessageOrder(String message);

}

//空のステート
class emptyState implements IState{
  public String Update(int elapsedTime){return "null";};
  public void Render(){};
  public void OnEnter(){};
  public void OnExit(){};
  public void MessageOrder(String message){};
}
