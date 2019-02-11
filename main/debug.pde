//デバッグモード
class Debug implements IState{
  int targetEdge = 0;//所有者を変更しようとするエッジの番号
  int targetHolder = 0;//設定しようとするプレイヤー番号,0なら未使用
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
    //園児の所有者の設定
    setEdgeOwner();

    return "null";
  };
  public void OnEnter(){};
  public void OnExit(){};

  //描画
  public void Render(){
    fill(50, 50, 50, 255);
    text("DebugMode", 10, 40);

    //エッジの太描き
    fieldInfomation.Debug_Render();

    //変数の表示
    text("targetEdge:"+targetEdge, 50, 100);
    text("targetHolder:"+targetHolder, 50, 150);

    //選択しているエッジの強調描画
    stroke( 200, 200, 200 );
    strokeWeight( 10 );
    fieldInfomation.drawEdge(targetEdge);
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

}


//↓不要
//エッジとノードの所有者を設定するための関数が詰まったクラス
class SetOwner{
  int EdgeNum = 72;//辺の数
  int NodeNum = 54;//ノードの数

  int edgeHolder[] = new int[EdgeNum];//エッジの所持者を格納する
  int nodeHolder[] = new int[NodeNum];//ノードの所持者を格納する
  float position_x[] = new float[NodeNum];//描画する頂点位置のx座標
  float position_y[] = new float[NodeNum];//描画する頂点位置のy座標
  int edgeNextNode1[] = new int[EdgeNum];//エッジの端のノード番号1
  int edgeNextNode2[] = new int[EdgeNum];//エッジの端のノード番号2


  //コンストラクタ
  SetOwner(){
    //初期化
    for(int i=0;i<EdgeNum;i++){
      edgeHolder[i] = 0;
    }
    for(int i=0;i<NodeNum;i++){
      nodeHolder[i] = 0;
    }

    //ノードの描画座標の位置を格納
    {
      String lines[] = loadStrings("data/NodeDrawPosition.csv");
      String lin;
      String [] splited;
      for(int i=1;i<NodeNum+1;i++){//1行目はラベル
        lin = lines[i];
        splited = split(lin,',');
        position_x[i-1] = float(splited[1]);//x座標
        position_y[i-1] = float(splited[2])/4;//y座標
      }
    }

    //エッジの端にあるノードの番号を格納
    {
      String lines[] = loadStrings("data/EdgeNextNode.csv");
      String lin;
      String [] splited;
      for(int i=1;i<EdgeNum+1;i++){//1行目はラベル
        lin = lines[i];
        splited = split(lin,',');
        edgeNextNode1[i-1] = int(splited[1]);
        edgeNextNode2[i-1] = int(splited[2]);
      }
    }

  }

  //エッジの所有者の設定
  void SetEdgeOwner(){

  }

  //更新
  void Update(){

  }

  //描画
  void Render(){
    pushMatrix();
    translate(500, 300);
    stroke( 100, 0, 0 );
    strokeWeight( 3 );
    for(int i=0;i<EdgeNum;i++){
      float x1 = position_x[ edgeNextNode1[i] ] * AREA_LENGTH;
      float x2 = position_x[ edgeNextNode2[i] ] * AREA_LENGTH;
      float y1 = position_y[ edgeNextNode1[i] ] * AREA_LENGTH;
      float y2 = position_y[ edgeNextNode2[i] ] * AREA_LENGTH;
      line(x1,y1,x2,y2);
    }
    popMatrix();
  }
}
