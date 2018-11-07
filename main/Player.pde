//プレイヤーのステートマシンなどについて記述

//プレイヤーのアクションを管理するステートマシン
class PlayerStateMAchine extends StateChanger{
  MainStateMachine MainStateMachine;//メインステートマシン（親を参照するためのもの）

  int listIndex = 0;//どの子を選択しようとしているのかというindex

  String MyName;//自分の名前
  String nextPlayerName;//次の順番のプレイヤーの名前

  List<String> cardList = new ArrayList<String>();//所持しているカードのリスト

  //コンストラクタ メインステートマシンの実体と次のプレイヤーの名前
  public PlayerStateMAchine(MainStateMachine tmp,String tmp_MyName,String tmp_nextPlayerName){
    super();

    MainStateMachine = tmp;
    MyName = tmp_MyName;
    nextPlayerName = tmp_nextPlayerName;

    cardList.add("card1");
    cardList.add("card2");
    cardList.add("card3");
    cardList.add("card4");



    Add("dice1" ,new Dice(this));
    Add("choiceCard" ,new ChoiceCard(this));
    Add("tradeWithOther" ,new TradeWithOther(this));
    Add("useCard" ,new UseCard(this));
    Add("development" ,new Development(this));

  }

  //子の主導権を消し、自分に主導権が移る.子が呼ぶ
  public void ChildOFF(){
    Change("empty");
    childOn = false;
  }

  //次このステートマシンの親に当たるメインステートマシンの関数を呼んで、次のプレイヤーにステートを渡す
  public void ChangetoNextPlayer(){
    MainStateMachine.Change(nextPlayerName);
  }

  //カードリストを返す
  public List<String> GetCardList(){
    return cardList;
  }

  public void Update(int elapsedTime){

    //子に主導権が移ってるならここでの操作は行わんようにっていうやつ
    if(childOn == true){
      mCurrentState.Update(elapsedTime);//子の呼び出し
    }else{
      if(keyPushJudge.GetJudge("a") == true){
        ChangetoNextPlayer();
      }
      if(keyPushJudge.GetJudge("z") == true){
        listIndex++;
        if(childList.size() == listIndex)listIndex = 0;
      }
      if(keyPushJudge.GetJudge("ENTER") == true){
        //println(childList.get(listIndex));
        Change(childList.get(listIndex));
      }
    }
  };
  public void Render(){
    fill(50, 50, 50, 255);
    textSize(25);
    text(MyName +"  "+ childList.get(listIndex), 50, 50);
    mCurrentState.Render();//子の呼び出し
  };
  public void OnEnter(){};
  public void OnExit(){};
}


//サイコロを振る
class Dice extends PlayerActionBase{
  //PlayerStateMAchine playerStateMachine;//プレイヤーステートマシン（親の参照）

  //コンストラクタ
  public Dice(PlayerStateMAchine tmp_playerStateMachine){
    super(tmp_playerStateMachine);
  }

  public void Update(int elapsedTime){
    playerStateMachineChildOFF();//BACKSPACEで一つ戻る
  };
  public void Render(){
    fill(50, 50, 50, 255);
    text("Dice", 100, 100);
  };
  public void OnEnter(){};
  public void OnExit(){};
}

//カードを選択する
class ChoiceCard extends PlayerActionBase{
  //PlayerStateMAchine playerStateMachine;//プレイヤーステートマシン（親の参照）
  List<String> cardList;
  int cardIndex = 0;//カード選択のためのindex

  //コンストラクタ
  public ChoiceCard(PlayerStateMAchine tmp_playerStateMachine){
    super(tmp_playerStateMachine);
  }

  public void Update(int elapsedTime){
    playerStateMachineChildOFF();//BACKSPACEで一つ戻る
  };
  public void Render(){
    fill(50, 50, 50, 255);
    text("choiseCard", 100, 100);
    for(int i=0;i<cardList.size();i++){
       text(cardList.get(i), 200, 100 + 50*(i+1));
    }
  };
  public void OnEnter(){
    //プレイヤーの所持しているカードを取得
    cardList = playerStateMachine.GetCardList();
  };
  public void OnExit(){};
}

//他プレイヤーとの交易
class TradeWithOther extends PlayerActionBase{
  //PlayerStateMAchine playerStateMachine;//プレイヤーステートマシン（親の参照）

  //コンストラクタ
  public TradeWithOther(PlayerStateMAchine tmp_playerStateMachine){
    super(tmp_playerStateMachine);
  }

  public void Update(int elapsedTime){
    playerStateMachineChildOFF();//BACKSPACEで一つ戻る

  };
  public void Render(){
    fill(50, 50, 50, 255);
    text("TradeWithOther", 100, 100);

  };
  public void OnEnter(){};
  public void OnExit(){};
}

//カードの使用
class UseCard extends PlayerActionBase{
  //PlayerStateMAchine playerStateMachine;//プレイヤーステートマシン（親の参照）

  //コンストラクタ
  public UseCard(PlayerStateMAchine tmp_playerStateMachine){
    super(tmp_playerStateMachine);
  }

  public void Update(int elapsedTime){
    playerStateMachineChildOFF();//BACKSPACEで一つ戻る
  };
  public void Render(){
    fill(50, 50, 50, 255);
    text("UseCard", 100, 100);

  };
  public void OnEnter(){};
  public void OnExit(){};
}

//開発
class Development extends PlayerActionBase{
  //PlayerStateMAchine playerStateMachine;//プレイヤーステートマシン（親の参照）

  //コンストラクタ
  public Development(PlayerStateMAchine tmp_playerStateMachine){
    super(tmp_playerStateMachine);
  }

  public void Update(int elapsedTime){
    playerStateMachineChildOFF();//BACKSPACEで一つ戻る

  };
  public void Render(){
    fill(50, 50, 50, 255);
    text("Development", 100, 100);

  };
  public void OnEnter(){};
  public void OnExit(){};
}
