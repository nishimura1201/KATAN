//プレイヤーのステートマシンなどについて記述

//プレイヤーのアクションを管理するステートマシン
class PlayerStateMachine extends StateChanger{
  int listIndex = 1;//どの子を選択しようとしているのかというindex,0はDiceやから1からスタート
  String myName;//自分の名前
  int myNumber;//自分の番号、プレイヤー番号,1以上の数値
  List<CardList> myCards = new ArrayList<CardList>();//所持しているカードのリスト
  HashMap<MaterialType, Integer> material = new HashMap<MaterialType, Integer>();//資材を管理するやつ
  int parameterRectSizeX = 200;//パラメータ表示の枠サイズX
  int parameterRectSizeY = 180;//パラメータ表示の枠サイズY
  int actionChoosesRectSizeX = 180;//行動選択表示の枠サイズX
  int actionChoosesRectSizeY = 180;//行動選択表示の枠サイズY

  //コンストラクタ メインステートマシンの実体と次のプレイヤーの名前
  public PlayerStateMachine(String tmp_myName,int tmp_myNumber){

    myName   = tmp_myName;
    myNumber = tmp_myNumber;

    CardAdd(CardList.BuildLoad);
//    CardAdd("card2");
//    CardAdd("card3");
//    CardAdd("card4");

    //資材の初期化
    for (MaterialType m : MaterialType.values()) {
      material.put(m,10);
    }

    //順番大事
    Add(PlayerSelectable.dice.getString()           ,new Dice(this));
    Add(PlayerSelectable.development.getString()    ,new Development(this, myNumber));
    Add(PlayerSelectable.buyCard.getString()        ,new BuyCard(this));
    Add(PlayerSelectable.useCard.getString()     ,new UseCard(this));
    Add(PlayerSelectable.tradeWithOther.getString() ,new TradeWithOther(this));
    Add(PlayerSelectable.endTurn.getString()        ,new EndTurn(this));
    //Add(PlayerSelectable.useCard.getString()        ,new UseCard(this));
  }

  //子の主導権を消し、自分に主導権が戻る.子が呼ぶ
  public void ChildOFF(){
    Change("empty");
    childOn = false;
  }

  //所持カードを追加
  public void CardAdd(CardList card){
    myCards.add(card);
  }

  //カードリストを返す
  public List<CardList> GetCardList(){
    return myCards;
  }

  //資材を追加する(資材の種類, 追加する個数)
  public void AddMaterial(MaterialType m, int num){
    int tmp = material.get(m);
    material.put(m, tmp+num);
  }

  //資材を消費する(資材の種類, 消費する個数)
  public void UseMaterial(MaterialType m, int num){
    int tmp = material.get(m);
    //マイナスになったら例外通知
    if( (tmp-num) < 0)
      println("PlayerStateMachine -> UseMaterial");

    material.put(m, tmp-num);
  }

  //開発等に必要な資材を持っているか確認する. target:町or街道orETC
  public boolean CheckMaterialsforDevelopment(String target){
    //true:大丈夫  false:だめ
    switch( target ){
      case "Load":
        if( material.get(MaterialType.Brick)     < 1
            || material.get(MaterialType.Lumber) < 1)return false;
            break;
      case "Settlement":
      if( material.get(MaterialType.Brick)     < 1
          || material.get(MaterialType.Lumber) < 1
          || material.get(MaterialType.Wool)   < 1
          || material.get(MaterialType.Grain)  < 1)return false;
          break;
      case "City":
            if( material.get(MaterialType.Grain)   < 2
                || material.get(MaterialType.Iron) < 3)return false;
                break;
      case "Card":
        if( material.get(MaterialType.Wool)     < 1
            || material.get(MaterialType.Grain) < 1
            || material.get(MaterialType.Iron)  < 1)return false;
        break;
    }

    return true;
  }

  //ダイスの目から、自分の所有する開拓地に合わせた資材をゲット
  //結果はStringにまとめて戻すように
  public void GetMaterial_FromDice(){
    //fieldInfomationにアクセスして、自分の開拓地の周辺にある資材を返してもらう
    //引数をエリアの種類とプレイヤーの番号に、返り値を取得した個数にする
    int result;//戻り値の箱
    String str = "";
    for(MaterialType m :MaterialType.values()){
      result = fieldInfomation.DiceReturnMaterial(Parameter_Player.diceNumber,
                                                  Parameter_Player.playerNumber+1,//Nodeに登録されているプレイヤー番号に合わせる
                                                  m);
      AddMaterial(m,result);
      if(result > 0){
        str += m.getName();
        str += ":";
        str += result;
        str += ", ";
      }
    }
    Parameter_Player.resultSTR = str;
    //タイプと個数をもとにAddMaterialで保持数の更新
    //Parameter_Player.resultSTR = Integer.toString(Parameter_Player.diceNumber);
    //取得した資材の結果をSringにまとめる
  }

  public String Update(int elapsedTime){
    //子に主導権が移ってるならここでの操作は行わんようにっていうやつ
    if(childOn == true){
      String order = mCurrentState.Update(elapsedTime);//子の更新処理実行
      switch(order){
        case "ChildOFF":
          ChildOFF();
          break;
        case "ChangePlayer":
          ChildOFF();
          return "ChangePlayer";
      }
    }else{
      //次のプレイヤーに所有権を移す
      if(keyPushJudge.GetJudge("RIGHT") == true){
        return "ChangePlayer";
      }
      if(keyPushJudge.GetJudge("DOWN") == true){
        listIndex++;
        if(childList.size() == listIndex)listIndex = 1;//0はダイスなので除いている
      }
      if(keyPushJudge.GetJudge("ENTER") == true){
        Change(childList.get(listIndex));
      }
    }

    return "null";
  };
  public void Render(){
    //デバッグ用に今選択くしている行動を画面右下に表示
    fill(50, 50, 50, 255);
    textSize(20);
    text(myName +"  "+ childList.get(listIndex), FIELD_LENGTH_X - 400, FIELD_LENGTH_Y - 200);

    ActionChoosesRender();//行動選択の表示
    ParameterRender();//パラメータの表示
    mCurrentState.Render();//子の呼び出し
  };

  //選択肢の表示
  public void ActionChoosesRender(){
    //選択肢の表示
    pushMatrix();
    translate(30,80);
    {
      //枠の表示
      DrawRect(0, 0, actionChoosesRectSizeX, actionChoosesRectSizeY);
      DrawRect(4, 4, actionChoosesRectSizeX-8, actionChoosesRectSizeY-8);

      //選択肢の表示
      textSize(30);
      fill(50, 50, 50, 255);
      text("->", 10, 10 + 30*listIndex);
      //text("Dice", 50, 40);
      text("Develop", 50, 40);
      text("BuyCard", 50, 70);
      text("UseCard", 50, 100);
      text("Trade", 50, 130);
      text("*END*", 50, 160);
    }
    popMatrix();
  }

  //パラメータの表示
  public void ParameterRender(){
    fill(50, 50, 50, 255);

    //名前の表示
    textSize(30);
    text(myName, 50, 30);

    //資材の表示
    pushMatrix();
    translate(300,80);
    {
      //枠の表示
      DrawRect(0, 0, parameterRectSizeX, parameterRectSizeY);
      DrawRect(4, 4, parameterRectSizeX-8, parameterRectSizeY-8);

      //パラメータの表示
      textSize(30);
      fill(50, 50, 50, 255);
      text("Brick     :" +material.get(MaterialType.Brick), 10, 40);
      text("Lumber :"    +material.get(MaterialType.Lumber), 10, 70);
      text("Wool     :"  +material.get(MaterialType.Wool), 10, 100);
      text("Grain    :"  +material.get(MaterialType.Grain), 10, 130);
      text("Iron       :"+material.get(MaterialType.Iron), 10, 160);
    }
    popMatrix();
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
      case "GetMaterial_FromDice":
        GetMaterial_FromDice();
        break;
    }
  };
  public void OnEnter(){
    //最初はダイスからなので0
    listIndex = 1;
    Change(childList.get(0));
  };
  public void OnExit(){
    messageBox.MessageOFF();
  };
}


//サイコロを振る
class Dice extends PlayerActionBase{
  String m_state = "YESorNO";//簡易に状態を管理する
  int diceNumber = 0;//振って出たダイスの合計値
  int targetPlayer = -1;//メッセージウィンドウの進捗を管理,最初に++したいから-1から始める

  //コンストラクタ
  public Dice(PlayerStateMachine tmp){
    //PlayerActionBaseのコンストラクタを起動
    super(tmp);
  }

  public String Update(int elapsedTime){
    switch(m_state){
      //本当にダイスwp振るかどうかの確認
      case "YESorNO":
        //ENTERで決定
        if(keyPushJudge.GetJudge("ENTER")){
          diceNumber = int(random(1,7)) + int(random(1,7));
          Parameter_Player.diceNumber = diceNumber;
          messageBox.MessageON("dice sum-->" + Integer.toString(diceNumber), "");
          m_state = "Distribution";
        }
        break;

      //ダイスの目に応じて資材の分配
      case "Distribution":
        //ENTER押すごとに,プレイヤーに資材の分配と成果の表示を行う
        String tmpStr = "";
        if(keyPushJudge.GetJudge("ENTER")){
          targetPlayer++;
          if(targetPlayer == PLAYER_NUMBER)return "ChildOFF";

          Parameter_Player.playerNumber = targetPlayer;//プレイヤー番号の設定

          //プレイヤーごとに資材の分配の計算と更新
          String playerName = mainStateMachine.orderPlayerName[targetPlayer];//プレイヤーの名前
          IState tmpPSM = mainStateMachine.mStates.get(playerName);//対象プレイヤーのステートマシン
          tmpPSM.MessageOrder("GetMaterial_FromDice");//ダイスの合計値から資源の取得

          messageBox.MessageON("dice sum-->" + Integer.toString(diceNumber), mainStateMachine.orderPlayerName[targetPlayer] +":"+ Parameter_Player.resultSTR);

        }

        return "null";

      default:
        break;
    }


    return "null";
  };
  public void Render(){
    //DIceと表示する
    fill(50, 50, 50, 255);
    textSize(20);
    text("Dice", FIELD_LENGTH_X - 200, FIELD_LENGTH_Y - 180);
    textSize(30);
    text("Dice",200, 30);

    //ステートに応じた表示
    switch(m_state){
      //本当にダイスwp振るかどうかの確認
      case "YESorNO":
        break;

      //ダイスの目に応じて資材の分配
      case "Distribution":
        break;

      default:
        break;
    }
  };
  public void OnEnter(){
    m_state = "YESorNO";
    diceNumber = 0;
    targetPlayer = -1;//最初に++したいから-1から始める
    messageBox.MessageON(" roll dice?(ENTER or BACKSPACE)","");
  };
  public void OnExit(){
    messageBox.MessageOFF();
  };
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
  public void OnExit(){
    messageBox.MessageOFF();
  };
}

//カードを購入する
class BuyCard extends PlayerActionBase{
  PlayerStateMachine p;         //プレイヤーステートマシン（親の参照）
  boolean childOFF_flag = false;//親に権限を返すフラグ
  //コンストラクタ
  public BuyCard(PlayerStateMachine tmp){
    //PlayerActionBaseのコンストラクタを起動
    super(tmp);
    p = tmp;//親の参照

  }

  public String Update(int elapsedTime){
    //資材が足りないならすぐに権限を親に返す
    if(childOFF_flag)return "ChildOFF";


    //カードの購入
    if(keyPushJudge.GetJudge("ENTER")){
      //資材の消費
      p.UseMaterial(MaterialType.Wool,   1);
      p.UseMaterial(MaterialType.Grain,  1);
      p.UseMaterial(MaterialType.Iron,   1);
      //カードの追加
      p.CardAdd(cards.get(0));
      //山札を1枚減らす
      cards.remove(0);

      //親に権限を返す
      messageBox.MessageOFF();
      return "ChildOFF";
    }
    return PlayerStateMachineChildOFF();//BACKSPACEで一つ戻る
  };
  public void Render(){
  };
  public void OnEnter(){
    //購入できる環境が整っているかの判断
    if( p.CheckMaterialsforDevelopment("Card") == false ){
      //資材もってるか確認
      messageBox.MessageON("materials for buying a card are not enough","");
      childOFF_flag = true;
    }else if( cards.size() == 0 ){
      //残りのカードが0ならすぐに権限を親に返す
      messageBox.MessageON("There are no remaining cards","");
      childOFF_flag = true;
    }else{
      messageBox.MessageON("Buy a card?. ","YES:ENTER,  NO:BACKSPACE");
    }

  };
  public void OnExit(){
  };
}

//カードの使用
class UseCard extends PlayerActionBase{
  PlayerStateMachine playerStateMachine;//プレイヤーステートマシン（親の参照）

  //コンストラクタ
  public UseCard(PlayerStateMachine tmp){
    //PlayerActionBaseのコンストラクタを起動
    super(tmp);
    playerStateMachine = tmp;
  }

  public String Update(int elapsedTime){
    return PlayerStateMachineChildOFF();//BACKSPACEで一つ戻る
  };
  public void Render(){
    fill(50, 50, 50, 255);
    textSize(20);
    text("UseCard", FIELD_LENGTH_X - 200, FIELD_LENGTH_Y - 180);

    int i=0;
    for(CardList tmp : playerStateMachine.myCards){
      i++;
      text(tmp.returnName(), FIELD_LENGTH_X - 150, FIELD_LENGTH_Y - 180 + i*20);
    }


  };
  public void OnEnter(){};
  public void OnExit(){
    messageBox.MessageOFF();
  };
}

//開発
class Development extends PlayerActionBase{
  PlayerStateMachine playerStateMachine;//プレイヤーステートマシン（親の参照）
  int targetEdge   = 0;//所有者を変更しようとするエッジの番号
  int targetNode   = 0;//所有者を変更しようとするエッジの番号
  int whichSetting = 0;//設定しようとしているのはどの要素か(0..エッジ,1..ノード)
  int myNumber     = 0;//プレイヤーの固有番号
  final static int KIND_OF_SETTING = 2;//設定できる要素の数

  //コンストラクタ
  public Development(PlayerStateMachine tmp, int tmp_myNumber){
    //PlayerActionBaseのコンストラクタを起動
    super(tmp);
    playerStateMachine = tmp;

    myNumber = tmp_myNumber;
  }

  public String Update(int elapsedTime){

    //設定する要素の選択
    if(keyPushJudge.GetJudge("s")){
      if(whichSetting == KIND_OF_SETTING-1)whichSetting = 0;
      else whichSetting++;

      //設定対象が切り替わるタイミングで、設定できる場所を探しておく
      switch(whichSetting){
        //エッジの所有者の設定
        case 0:
          targetEdge = 0;
          //最初に開発モードに入るときに開発できるエッジを探す
          while( fieldInfomation.Edge_JudgeDevelopable(myNumber, targetEdge) == false ){
            targetEdge++;
            //★★エッジすら開発できない事はないだろうっていう設計
          }
        break;

        //ノードの所有者の設定
        case 1:
          targetNode = 0;
          //最初に開発モードに入るときに開発できるノードを探す
          while( fieldInfomation.Node_JudgeDevelopable(myNumber, targetNode) == false ){
            targetNode++;
            if(targetNode+1 == FieldInfomation.NodeNum){
              targetNode   = 0;
              whichSetting = 0;
              messageBox.MessageON("place you can develop doesn't exist. ","");
              break;
            }
          }
        break;

        default:break;
      }
    }


    //開発の実行
    switch(whichSetting){
      //エッジの所有者の設定
      case 0:developEdge();break;
      //ノードの所有者の設定
      case 1:developNode();break;
      default:break;
    }

    return PlayerStateMachineChildOFF();//BACKSPACEで一つ戻る
  };

  public void Render(){
    //今設定しようとしている要素名を表示
    fill(50, 50, 50, 255);
    textSize(20);
    text("Development", FIELD_LENGTH_X - 400, FIELD_LENGTH_Y - 180);
    switch(whichSetting){
      case 0:text("Edge", FIELD_LENGTH_X - 250, FIELD_LENGTH_Y - 180);break;
      case 1:text("Node", FIELD_LENGTH_X - 250, FIELD_LENGTH_Y - 180);break;
      default:break;
    }

    //編集対象となるノード・エッジの表示
    switch(whichSetting){
      //エッジの所有者の設定
      case 0:
        //変数の表示
        text("targetEdge:"+targetEdge, FIELD_LENGTH_X - 350, FIELD_LENGTH_Y - 150);
        //選択しているエッジの強調描画
        stroke( 200, 200, 200 );
        strokeWeight( 10 );
        pushMatrix();
        translate(FIELD_POSITION_X, FIELD_POSITION_Y);
        fieldInfomation.drawEdge(targetEdge);
        popMatrix();
        break;

      //ノードの所有者の設定
      case 1:
        //変数の表示
        text("targetNode:"+targetNode, FIELD_LENGTH_X - 350, FIELD_LENGTH_Y - 150);
        stroke( 200, 200, 200 );
        strokeWeight( 10 );
        pushMatrix();
        translate(FIELD_POSITION_X, FIELD_POSITION_Y);
        fieldInfomation.drawNode(targetNode);
        popMatrix();
        break;
    }
  };
  public void OnEnter(){
    targetEdge   = 0;
    targetNode   = 0;
    whichSetting = 0;

    //最初に開発モードに入るときに開発できるエッジを探す
    while( fieldInfomation.Edge_JudgeDevelopable(myNumber, targetEdge) == false ){
      targetEdge++;
      //★★エッジすら開発できない事はないだろうっていう設計
    }

  };
  public void OnExit(){
    messageBox.MessageOFF();
  };


  //エッジの所有者の設定
  public void developEdge(){
    //右を押したらtargetEdgeを進めて、左を押したらtargetEdgeを減らす
    //上を押したら所有者の切り替え
    //下を押したら+10
    //ENTERで所有者の決定
    if(keyPushJudge.GetJudge("RIGHT")){
      do{
        if(targetEdge+1 == FieldInfomation.EdgeNum)targetEdge=0;
        else targetEdge++;
      }while( fieldInfomation.Edge_JudgeDevelopable(myNumber, targetEdge) == false );
    }else if(keyPushJudge.GetJudge("LEFT")){
      do{
        if(targetEdge == 0)targetEdge=FieldInfomation.EdgeNum-1;
        else targetEdge--;
      }while( fieldInfomation.Edge_JudgeDevelopable(myNumber, targetEdge) == false );
    }else if(keyPushJudge.GetJudge("DOWN")){
      if(targetEdge+10 > FieldInfomation.EdgeNum)targetEdge = 0;
      else targetEdge+=10;
      while( fieldInfomation.Edge_JudgeDevelopable(myNumber, targetEdge) == false ){
        if(targetEdge+1 == FieldInfomation.EdgeNum)targetEdge=0;
        else targetEdge++;
      }
    }else if(keyPushJudge.GetJudge("ENTER")){
      //資材が十分か確認
      if( playerStateMachine.CheckMaterialsforDevelopment("Load") )
        DoDevelopment_Edge();
      else
        messageBox.MessageON("materials for development are not enough", "");

    }
  }

  //街道の開発の実行
  public void DoDevelopment_Edge(){
    fieldInfomation.BuildEdge(targetEdge, myNumber);
    //資材の消費
    playerStateMachine.UseMaterial(MaterialType.Brick,  1);
    playerStateMachine.UseMaterial(MaterialType.Lumber, 1);

    //カーソルの移動
    do{
      if(targetEdge+1 == FieldInfomation.EdgeNum)targetEdge=0;
      else targetEdge++;
    }while( fieldInfomation.Edge_JudgeDevelopable(myNumber, targetEdge) == false );
  }

  //ノードの所有者の設定
  public void developNode(){
    //右を押したらtargetNodeを進めて、左を押したらtargetNodeを減らす
    //上を押したら所有者の切り替え
    //下を押したら+10
    //ENTERで開発

    if(keyPushJudge.GetJudge("RIGHT")){
      do{
        if(targetNode+1 == FieldInfomation.NodeNum)targetNode=0;
        else targetNode++;
      }while( fieldInfomation.Node_JudgeDevelopable(myNumber, targetNode) == false );
    }else if(keyPushJudge.GetJudge("LEFT")){
      do{
        if(targetNode == 0)targetNode=FieldInfomation.NodeNum-1;
        else targetNode--;
      }while( fieldInfomation.Node_JudgeDevelopable(myNumber, targetNode) == false );
    }else if(keyPushJudge.GetJudge("DOWN")){
      if(targetNode+10 > FieldInfomation.NodeNum)targetNode = 0;
      else targetNode+=10;
      while( fieldInfomation.Node_JudgeDevelopable(myNumber, targetNode) == false ){
        if(targetNode+1 == FieldInfomation.NodeNum)targetNode=0;
        else targetNode++;
      }
    }else if(keyPushJudge.GetJudge("ENTER")){
      boolean check = true;
      //資材の確認
      switch( fieldInfomation.whichDevelopment(targetNode) ){
        case "Settlement":
          if( playerStateMachine.CheckMaterialsforDevelopment("Settlement") == false ){
            messageBox.MessageON("materials for development are not enough", "");
            check = false;
          }break;
        case "City":
          if( playerStateMachine.CheckMaterialsforDevelopment("City") == false  ){
            messageBox.MessageON("materials for development are not enough", "");
            check = false;
          }break;
      }

      //開発の実行
      if( check )
        DoDevelopment_Node();
    }
  }

  //町・都市の開発の実行
  public void DoDevelopment_Node(){
    //都市の開発、もしくは町の開発
    String res = fieldInfomation.BuildNode(targetNode, myNumber);
    //資材の消費
    //町を作ったのか、都市に発展させたのかで分岐
    if(res == "Settlement"){
      //町の開発

      //資材の消費
      playerStateMachine.UseMaterial(MaterialType.Brick,  1);
      playerStateMachine.UseMaterial(MaterialType.Lumber, 1);
      playerStateMachine.UseMaterial(MaterialType.Wool,   1);
      playerStateMachine.UseMaterial(MaterialType.Grain,  1);
    }else if(res == "City"){
      //都市に発展

      //資材の消費
      playerStateMachine.UseMaterial(MaterialType.Wool,  2);
      playerStateMachine.UseMaterial(MaterialType.Iron,  3);

      //カーソルの移動
      //建設できんなら街道建設に移行
      int tmp = 0;
      while( fieldInfomation.Node_JudgeDevelopable(myNumber, targetNode) == false ){
        targetNode++;
        tmp++;
        if(targetNode == FieldInfomation.NodeNum)
          targetNode   = 0;

        if(tmp+1 == FieldInfomation.NodeNum){
          targetNode   = 0;
          whichSetting = 0;
          break;
        }
      }
    }else{
      println("error Development -> DoDevelopment");
    }

  }

}

//ターンの終了
class EndTurn extends PlayerActionBase{
  //PlayerStateMachine PlayerStateMachine;//プレイヤーステートマシン（親の参照）

  //コンストラクタ
  public EndTurn(PlayerStateMachine tmp){
    //PlayerActionBaseのコンストラクタを起動
    super(tmp);
  }

  public String Update(int elapsedTime){
    //BACKSPACEで一つ戻る
    if(PlayerStateMachineChildOFF() == "ChildOFF"){
      return "ChildOFF";
    }
    //ENTER押したらターンの終わり
    if( keyPushJudge.GetJudge("ENTER") ){
      return "ChangePlayer";
    }

    return "null";
  };
  public void Render(){};
  public void OnEnter(){
    messageBox.MessageON("Do you want to end your turn?", "YES:ENTER   NO:BACK_SPACE");
  };
  public void OnExit(){
    messageBox.MessageOFF();
  };
}
