//プレイヤーのステートマシンなどについて記述

//プレイヤーのアクションを管理するステートマシン
class PlayerStateMachine extends StateChanger{
  int listIndex = 0;//どの子を選択しようとしているのかというindex
  String MyName;//自分の名前
  List<String> cardList = new ArrayList<String>();//所持しているカードのリスト
  HashMap<MaterialType, Integer> material = new HashMap<MaterialType, Integer>();//資材を管理するやつ
  int parameterRectSizeX = 300;//パラメータ表示の枠サイズX
  int parameterRectSizeY = 400;//パラメータ表示の枠サイズY
  //コンストラクタ メインステートマシンの実体と次のプレイヤーの名前
  public PlayerStateMachine(String tmp_MyName){

    MyName = tmp_MyName;

    CardAdd("card1");
    CardAdd("card2");
    CardAdd("card3");
    CardAdd("card4");

    //資材の初期化
    for (MaterialType m : MaterialType.values()) {
      material.put(m,0);
    }

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
  //資材を追加する(資材の種類, 追加する個数)
  public void AddMaterial(MaterialType m, int num){
    int tmp = material.get(m);
    material.put(m, tmp+num);
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
      if(keyPushJudge.GetJudge("a") == true){
        return "ChangePlayer";
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

    return "null";
  };
  public void Render(){
    //デバッグ用に今選択くしている行動を画面右下に表示
    fill(50, 50, 50, 255);
    textSize(20);
    text(MyName +"  "+ childList.get(listIndex), FIELD_LENGTH_X - 400, FIELD_LENGTH_Y - 200);

    ParameterRender();//パラメータの表示
    mCurrentState.Render();//子の呼び出し
  };

  //パラメータの表示
  public void ParameterRender(){
    fill(50, 50, 50, 255);

    //名前の表示
    textSize(30);
    text(MyName, 50, 30);

    //枠の表示
    stroke(100,50,50);
    noFill();
    strokeWeight(2);
    rect(50, 50, parameterRectSizeX, parameterRectSizeY);
    rect(50+4, 50+4, parameterRectSizeX-8, parameterRectSizeY-8);

    //パラメータの表示
    textSize(30);
    text("Brick     :"+material.get(MaterialType.Brick), 60, 85);
    text("Lumber :"+material.get(MaterialType.Lumber), 60, 115);
    text("Wool     :"+material.get(MaterialType.Wool), 60, 145);
    text("Grain    :"+material.get(MaterialType.Grain), 60, 175);
    text("Iron       :"+material.get(MaterialType.Iron), 60, 205);
  }
  public void MessageOrder(String message){
    switch(message){
      //デバッグ用のAddMaterialをキックする
      case "debug_AddMaterial":
        AddMaterial(debug_AddMaterial_m, debug_AddMaterial_num);
        break;
      case "ParameterRender":
        ParameterRender();
        break;
    }
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
    textSize(20);
    text("Dice", FIELD_LENGTH_X - 200, FIELD_LENGTH_Y - 180);
  };
  public void OnEnter(){};
  public void OnExit(){};
}

//カードを選択する
class ChoiceCard extends PlayerActionBase{
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
    textSize(20);
    text("choiseCard", FIELD_LENGTH_X - 200, FIELD_LENGTH_Y - 180);
    for(int i=0;i<cardList.size();i++){
       text(cardList.get(i), FIELD_LENGTH_X - 200, FIELD_LENGTH_Y - 180 + 20*(i+1));
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
    textSize(20);
    text("TradeWithOther", FIELD_LENGTH_X - 200, FIELD_LENGTH_Y - 180);

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
    textSize(20);
    text("UseCard", FIELD_LENGTH_X - 200, FIELD_LENGTH_Y - 180);

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
    textSize(20);
    text("Development", FIELD_LENGTH_X - 200, FIELD_LENGTH_Y - 180);

  };
  public void OnEnter(){};
  public void OnExit(){};
}
