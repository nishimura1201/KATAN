public MaterialType debug_AddMaterial_m = MaterialType.Brick;//デバッグに使うためのグローバル変数
public int debug_AddMaterial_num = 1;//デバッグに使うためのグローバル変数

//デバッグモード
class Debug implements IState{
  int targetEdge = 0;//所有者を変更しようとするエッジの番号
  int targetNode = 0;//所有者を変更しようとするエッジの番号
  int targetHolder = 0;//設定しようとするプレイヤー番号,0なら未使用
  int targetCityLevel = 0;//設定しようとする都市のレベル
  int whichSetting = 0;//設定しようとしているのはどの要素か(0..エッジ,1..ノード)
  final static int KIND_OF_SETTING = 3;//設定できる要素の数
  int targetPlayer = 0;//設定しようとしているプレイヤー
  int targetMaterial = 0;//設定しようとしている資材の番号

  //コンストラクタ
  Debug(){
  }

  public String Update(int elapsedTime){
    //設定する要素の選択
    if(keyPushJudge.GetJudge("s")){
      if(whichSetting == KIND_OF_SETTING-1)whichSetting = 0;
      else whichSetting++;
    }

    switch(whichSetting){
      //エッジの所有者の設定
      case 0:setEdgeOwner();break;
      //ノードの所有者の設定
      case 1:setNodeOwner();break;
      //資材の設定
      case 2:setMaterial();break;
      default:break;
    }

    return "null";
  };
  public void OnEnter(){};
  public void OnExit(){};
  public void MessageOrder(String message){};
  //描画
  public void Render(){
    //編集しようとしているプレイヤーのパラメータ表示
    {
      String playerName = mainStateMachine.orderPlayerName[targetPlayer];//プレイヤーの名前
      IState tmpPSM = mainStateMachine.mStates.get(playerName);//対象プレイヤーのステートマシン
      tmpPSM.MessageOrder( "ParameterRender" );
    }

    //今デバッグしようとしている要素名を表示
    fill(50, 50, 50, 255);
    textSize(20);
    text("DebugMode", FIELD_LENGTH_X - 400, FIELD_LENGTH_Y - 180);
    switch(whichSetting){
      case 0:text("Edge", FIELD_LENGTH_X - 250, FIELD_LENGTH_Y - 180);break;
      case 1:text("Node", FIELD_LENGTH_X - 250, FIELD_LENGTH_Y - 180);break;
      case 2:text("Material", FIELD_LENGTH_X - 250, FIELD_LENGTH_Y - 180);break;
      default:break;
    }

    //エッジの太描き
    fieldInfomation.Debug_Render();

    //デバッグする要素ごとに変数値の表示
    switch(whichSetting){
      //エッジの所有者の設定
      case 0:
        //変数の表示
        text("targetEdge:"+targetEdge, FIELD_LENGTH_X - 350, FIELD_LENGTH_Y - 150);
        text("targetHolder:"+targetHolder, FIELD_LENGTH_X - 350, FIELD_LENGTH_Y - 130);
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
        text("targetHolder:"+targetHolder, FIELD_LENGTH_X - 350, FIELD_LENGTH_Y - 130);
        text("targetCityLevel:"+targetCityLevel, FIELD_LENGTH_X - 350, FIELD_LENGTH_Y - 110);
        stroke( 200, 200, 200 );
        strokeWeight( 10 );
        pushMatrix();
        translate(FIELD_POSITION_X, FIELD_POSITION_Y);
        fieldInfomation.drawNode(targetNode);
        popMatrix();
        break;

      case 2:
        //変数の表示
        text("targetPlayer:"+mainStateMachine.orderPlayerName[targetPlayer], FIELD_LENGTH_X - 350, FIELD_LENGTH_Y - 150);
        text("targetMaterial:" + MaterialType.toString(targetMaterial), FIELD_LENGTH_X - 350, FIELD_LENGTH_Y - 130);
        stroke( 200, 200, 200 );
        strokeWeight( 10 );
        pushMatrix();
        translate(FIELD_POSITION_X, FIELD_POSITION_Y);
        popMatrix();
        break;

      default:break;
    }

    //説明書き
    fill(50, 50, 50, 255);
    textSize(20);
    switch(whichSetting){
      case 0:
      case 1:
        text("s:Change element of setting", 300, FIELD_LENGTH_Y-100 - 20);
        text("RIGHT:targetEdge+=1", 300, FIELD_LENGTH_Y-100 + 0);
        text("LEFT:targetEdge-=1", 300, FIELD_LENGTH_Y-100 + 20);
        text("UP:targetHolder+=1", 300, FIELD_LENGTH_Y-100 + 40);
        text("DOWN:targetEdge(Node)+=10", 300, FIELD_LENGTH_Y-100 + 60);
        text("l:cityLevel+=1(Node only)", 300, FIELD_LENGTH_Y-100 + 80);
        break;
      case 2:
        text("RIGHT:targetPlayer+=1", 300, FIELD_LENGTH_Y-100 + 0);
        text("UP:targetMaterial+=1", 300, FIELD_LENGTH_Y-100 + 20);
        text("ENTER:add number", 300, FIELD_LENGTH_Y-100 + 40);
        break;
    }


  }

  //エッジの所有者の設定
  public void setEdgeOwner(){
    //右を押したらtargetEdgeを進めて、左を押したらtargetEdgeを減らす
    //上を押したら所有者の切り替え
    //下を押したら+10
    //ENTERで所有者の決定
    if(keyPushJudge.GetJudge("RIGHT")){
      if(targetEdge+1 == FieldInfomation.EdgeNum)targetEdge=0;
      else targetEdge++;
    }else if(keyPushJudge.GetJudge("LEFT")){
      if(targetEdge == 0)targetEdge=FieldInfomation.EdgeNum-1;
      else targetEdge--;
    }else if(keyPushJudge.GetJudge("UP")){
      //プレイヤー人数+(未使用状態)だから+1する
      if(targetHolder+1 == PLAYER_NUMBER+1)targetHolder = 0;
      else targetHolder++;
    }else if(keyPushJudge.GetJudge("DOWN")){
      if(targetEdge+10 > FieldInfomation.EdgeNum)targetEdge = 0;
      else targetEdge+=10;
    }else if(keyPushJudge.GetJudge("ENTER")){
      fieldInfomation.SetEdgeOwner(targetEdge, targetHolder);
    }
  }

  //ノードの所有者の設定
  public void setNodeOwner(){
    //右を押したらtargetNodeを進めて、左を押したらtargetNodeを減らす
    //上を押したら所有者の切り替え
    //下を押したら+10
    //ENTERで所有者とレベルの決定
    if(keyPushJudge.GetJudge("RIGHT")){
      if(targetNode+1 == FieldInfomation.NodeNum)targetNode=0;
      else targetNode++;
    }else if(keyPushJudge.GetJudge("LEFT")){
      if(targetNode == 0)targetNode=FieldInfomation.NodeNum-1;
      else targetNode--;
    }else if(keyPushJudge.GetJudge("UP")){
      //プレイヤー人数+(未使用状態)だから+1する
      if(targetHolder+1 == PLAYER_NUMBER+1)targetHolder = 0;
      else targetHolder++;
    }else if(keyPushJudge.GetJudge("DOWN")){
      if(targetNode+10 > FieldInfomation.NodeNum)targetNode = 0;
      else targetNode+=10;
    }else if(keyPushJudge.GetJudge("l")){
      if(targetCityLevel == 2)targetCityLevel = 0;
      else targetCityLevel++;
    }
    else if(keyPushJudge.GetJudge("ENTER")){
      fieldInfomation.SetNodeOwner(targetNode, targetHolder, targetCityLevel);
    }
  }

  //資材の設定
  public void setMaterial(){
    if(keyPushJudge.GetJudge("RIGHT")){
      if(targetPlayer+1 == PLAYER_NUMBER)targetPlayer = 0;
      else targetPlayer+=1;
    }else if(keyPushJudge.GetJudge("UP")){
      if(targetMaterial+1 == MaterialType.values().length)targetMaterial = 0;
      else targetMaterial+=1;
    }else if(keyPushJudge.GetJudge("ENTER")){
      String playerName = mainStateMachine.orderPlayerName[targetPlayer];//プレイヤーの名前
      IState tmpPSM = mainStateMachine.mStates.get(playerName);//対象プレイヤーのステートマシン
      debug_AddMaterial_m = MaterialType.fromInt( targetMaterial );//変更する要素
      tmpPSM.MessageOrder( "debug_AddMaterial" );
    }

  }
}
