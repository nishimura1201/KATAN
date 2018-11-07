//メインのステートマシンやインタフェイスについて記述

//メインステートマシン
class MainStateMachine extends StateChanger{

    //コンストラクタ
    public MainStateMachine(){
      super();

      Add("player1",new PlayerStateMAchine(this, "player1","player2"));
      Add("player2",new PlayerStateMAchine(this, "player2","player3"));
      Add("player3",new PlayerStateMAchine(this, "player3","player1"));

      Change("player1");
    }


    public void Update(int elapsedTime){
        mCurrentState.Update(elapsedTime);
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
  public void Update(int elapsedTime){};
  public void Render(){};
  public void OnEnter(){};
  public void OnExit(){};
}

//複数の子を管理するステートマシンのベースとなるクラス
public class PlayerActionBase extends StateChanger{
  PlayerStateMAchine playerStateMachine;//プレイヤーステートマシン（親の参照）


  //コンストラクタ
  public PlayerActionBase(PlayerStateMAchine tmp_playerStateMachine){
    super();
    playerStateMachine = tmp_playerStateMachine;
  }

  public void playerStateMachineChildOFF(){
    if(keyPushJudge.GetJudge("BACKSPACE") == true){
      playerStateMachine.ChildOFF();
    }
  };
}

//ステートのベースとなるインタフェイス
public interface IState{
  public void Update(int elapsedTime);
  public void Render();
  public void OnEnter();
  public void OnExit();
}

//空のステート
class emptyState implements IState{
  public void Update(int elapsedTime){};
  public void Render(){};
  public void OnEnter(){};
  public void OnExit(){};
}
