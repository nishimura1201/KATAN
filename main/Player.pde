//プレイヤーのステートマシンなどについて記述

//プレイヤーのアクションを管理するステートマシン
class PlayerStateMachine extends StateChanger{
  int listIndex = 0;//どの子を選択しようとしているのかというindex
  String MyName;//自分の名前
  List<String> cardList = new ArrayList<String>();//所持しているカードのリスト

  //コンストラクタ メインステートマシンの実体と次のプレイヤーの名前
  public PlayerStateMachine(String tmp_MyName){

    MyName = tmp_MyName;

    CardAdd("card1");
    CardAdd("card2");
    CardAdd("card3");
    CardAdd("card4");


    Add(PlayerSelectable.dice.getString()           ,new Dice(this));
    Add(PlayerSelectable.choiceCard.getString()     ,new ChoiceCard(this));
    Add(PlayerSelectable.tradeWithOther.getString() ,new TradeWithOther(this));
    Add(PlayerSelectable.useCard.getString()        ,new UseCard(this));
    Add(PlayerSelectable.development.getString()    ,new Development(this));

  }

  //子の主導権を消し、自分に主導権が戻る.子が呼ぶ
  public void ChildOFF(){
    Change("empty");
    childOn = false;
  }

  //所持カードを追加
  public void CardAdd(String cardName){
    cardList.add(cardName);
  }
  //カードリストを返す
  public List<String> GetCardList(){
    return cardList;
  }


  public String Update(int elapsedTime){

    //子に主導権が移ってるならここでの操作は行わんようにっていうやつ
    if(childOn == true){
      String order = mCurrentState.Update(elapsedTime);//子の呼び出し
      switch(order){
        case "ChildOFF":
          ChildOFF();
          break;
      }
    }else{
      //次のプレイヤーに所有権を移す
      if(keyPushJudge.GetJudge("a")){
        return "ChangePlayer";
      }
      if(keyPushJudge.GetJudge("z")){
        listIndex++;
        if(childList.size() == listIndex)listIndex = 0;
      }
      if(keyPushJudge.GetJudge("ENTER")){
        //println(childList.get(listIndex));
        Change(childList.get(listIndex));
      }
    }

    return "null";
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
  //コンストラクタ
  public Dice(PlayerStateMachine tmp){
    //PlayerActionBaseのコンストラクタを起動
    super(tmp);
  }

  public String Update(int elapsedTime){
    return PlayerStateMachineChildOFF();//BACKSPACEで一つ戻る
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
  //PlayerStateMachine PlayerStateMachine;//プレイヤーステートマシン（親の参照）
  List<String> cardList;
  int cardIndex = 0;//カード選択のためのindex

  //コンストラクタ
  public ChoiceCard(PlayerStateMachine tmp){
    //PlayerActionBaseのコンストラクタを起動
    super(tmp);
  }

  //cardListを設定
  public void SetcardList(List<String> tmp){
    cardList = tmp;
  };

  public String Update(int elapsedTime){
    return PlayerStateMachineChildOFF();//BACKSPACEで一つ戻る
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
    cardList = PlayerStateMachine.GetCardList();
  };
  public void OnExit(){};
}

//他プレイヤーとの交易
class TradeWithOther extends PlayerActionBase{
  //PlayerStateMachine PlayerStateMachine;//プレイヤーステートマシン（親の参照）

  //コンストラクタ
  public TradeWithOther(PlayerStateMachine tmp){
    //PlayerActionBaseのコンストラクタを起動
    super(tmp);
  }

  public String Update(int elapsedTime){
    return PlayerStateMachineChildOFF();//BACKSPACEで一つ戻る

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
  //PlayerStateMachine PlayerStateMachine;//プレイヤーステートマシン（親の参照）

  //コンストラクタ
  public UseCard(PlayerStateMachine tmp){
    //PlayerActionBaseのコンストラクタを起動
    super(tmp);
  }

  public String Update(int elapsedTime){
    return PlayerStateMachineChildOFF();//BACKSPACEで一つ戻る
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
  //PlayerStateMachine PlayerStateMachine;//プレイヤーステートマシン（親の参照）

  //コンストラクタ
  public Development(PlayerStateMachine tmp){
    //PlayerActionBaseのコンストラクタを起動
    super(tmp);
  }

  public String Update(int elapsedTime){
    return PlayerStateMachineChildOFF();//BACKSPACEで一つ戻る

  };
  public void Render(){
    fill(50, 50, 50, 255);
    text("Development", 100, 100);

  };
  public void OnEnter(){};
  public void OnExit(){};
}
