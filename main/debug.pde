//デバッグモード
class Debug{
  boolean debugFlag = false;

  //コンストラクタ
  Debug(){
  }


  //更新
  void Update(){
    if(keyPushJudge.GetJudge("d") == true){
      if(debugFlag == true)
        debugFlag = false;
      else
        debugFlag = true;
    }
  }

  //描画
  void Render(){
    //エッジの太描き
    if(debugFlag == true){
      map.Debug_Render();
    }
  }
}

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
