//デバッグモード
class Debug implements IState{
  int targetEdge = 0;//所有者を変更しようとするエッジの番号
  int targetNode = 0;//所有者を変更しようとするエッジの番号
  int targetHolder = 0;//設定しようとするプレイヤー番号,0なら未使用
  int targetCityLevel = 0;//設定しようとする都市のレベル
  int whichSetting = 0;//設定しようとしているのはどの要素か(0..エッジ,1..ノード)
  int kindOfSetting = 2;//設定できる要素の数
  //コンストラクタ
  Debug(){
    //エッジの初期設定
    fieldInfomation.SetEdgeOwner(0, 1);
    fieldInfomation.SetEdgeOwner(1, 1);
    fieldInfomation.SetEdgeOwner(2, 1);
    fieldInfomation.SetEdgeOwner(3, 1);
    fieldInfomation.SetEdgeOwner(4, 1);
    fieldInfomation.SetEdgeOwner(5, 1);

    fieldInfomation.SetEdgeOwner(6, 2);
    fieldInfomation.SetEdgeOwner(10, 2);
    fieldInfomation.SetEdgeOwner(11, 2);
    fieldInfomation.SetEdgeOwner(12, 2);
    fieldInfomation.SetEdgeOwner(13, 2);
    fieldInfomation.SetEdgeOwner(14, 2);
    fieldInfomation.SetEdgeOwner(15, 2);

  }

  public String Update(int elapsedTime){
    //設定する要素の選択
    if(keyPushJudge.GetJudge("s")){
      if(whichSetting == kindOfSetting-1)whichSetting = 0;
      else whichSetting++;
    }

    switch(whichSetting){
      //エッジの所有者の設定
      case 0:setEdgeOwner();break;
      //ノードの所有者の設定
      case 1:setNodeOwner();break;
    }

    return "null";
  };
  public void OnEnter(){};
  public void OnExit(){};

  //描画
  public void Render(){
    fill(50, 50, 50, 255);
    textSize(20);
    text("DebugMode", 10, 40);
    switch(whichSetting){
      case 0:text("Edge", 150, 40);break;
      case 1:text("Node", 150, 40);break;
    }
    //エッジの太描き
    fieldInfomation.Debug_Render();


    switch(whichSetting){
      //エッジの所有者の設定
      case 0:
        //変数の表示
        text("targetEdge:"+targetEdge, 50, 100);
        text("targetHolder:"+targetHolder, 50, 150);
        //選択しているエッジの強調描画
        stroke( 200, 200, 200 );
        strokeWeight( 10 );
        pushMatrix();
        translate(500, 300);
        fieldInfomation.drawEdge(targetEdge);
        popMatrix();
        break;

      //ノードの所有者の設定
      case 1:
        //変数の表示
        text("targetNode:"+targetNode, 50, 100);
        text("targetHolder:"+targetHolder, 50, 150);
        text("targetCityLevel:"+targetCityLevel, 50, 200);
        stroke( 200, 200, 200 );
        strokeWeight( 10 );
        pushMatrix();
        translate(500, 300);
        fieldInfomation.drawNode(targetNode);
        popMatrix();
        break;
    }

    //説明書き
    textSize(15);
    fill(0, 0, 0);//HSB
    text("s:Change element of setting", 10, 300);
    text("RIGHT:targetEdge+=1", 10, 320);
    text("LEFT:targetEdge-=1", 10, 340);
    text("UP:targetHolder+=1", 10, 360);
    text("DOWN:targetEdge(Node)+=10", 10, 380);
    text("l:cityLevel+=1(Node only)", 10, 400);

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
}
