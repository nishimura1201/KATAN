//メインのステートマシンやインタフェイスについて記述

//メインステートマシン
class MainStateMachine extends StateChanger{
    String orderPlayer[] = new String[3];//プレイヤーのターン順序
    int whoseTurn = 0;//今誰のターンなのか管理する
    //コンストラクタ
    public MainStateMachine(){
      super();

      Add("player1",new PlayerStateMAchine( "player1"));
      Add("player2",new PlayerStateMAchine( "player2"));
      Add("player3",new PlayerStateMAchine( "player3"));
      //プレイヤーのターン順序
      orderPlayer[0] = "player1";
      orderPlayer[1] = "player2";
      orderPlayer[2] = "player3";
      //最初はplayer1から
      Change("player1");
    }


    public String Update(int elapsedTime){
        String order = mCurrentState.Update(elapsedTime);

        switch(order){
          //次のプレイヤーにステートを移す
          case "ChangePlayer":
            if(whoseTurn+1 == orderPlayer.length){
              whoseTurn = 0;
            }else{
              whoseTurn+=1;
            }
            Change(orderPlayer[whoseTurn]);
            break;
        }

        return "null";
    }

    public void Render(){
        mCurrentState.Render();
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

  public void Add(String name, IState state){
    mStates.put(name,state);
    childList.add(name);//子リストに追加
  }
  public String Update(int elapsedTime){return "null";};
  public void Render(){};
  public void OnEnter(){};
  public void OnExit(){};
}

//複数の子を管理するステートマシンのベースとなるクラス
public class PlayerActionBase extends StateChanger{
  PlayerStateMAchine playerStateMachine;//プレイヤーステートマシン（親の参照）
  //コンストラクタ
  public PlayerActionBase(PlayerStateMAchine tmp){
    super();
    playerStateMachine = tmp;
  }

  public String playerStateMachineChildOFF(){
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
}

//空のステート
class emptyState implements IState{
  public String Update(int elapsedTime){return "null";};
  public void Render(){};
  public void OnEnter(){};
  public void OnExit(){};
}
