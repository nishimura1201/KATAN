/* フィールドに関するクラスとか */

//エッジ・ノード・エリアの情報を管理する
public class FieldInfomation{
    static final int EdgeNum = 72;//辺の数
    static final int NodeNum = 54;//ノードの数
    static final int AreaNum = 19;//エリアの数

    Edge[] edge = new Edge[EdgeNum];//辺
    Node[] node = new Node[NodeNum];//頂点
    Area[] area = new Area[AreaNum];//エリア

    float position_x[] = new float[NodeNum];//描画するノード位置のx座標
    float position_y[] = new float[NodeNum];//描画するノード位置のy座標
    int edgeNextNode1[] = new int[EdgeNum];//エッジの端のノード番号1
    int edgeNextNode2[] = new int[EdgeNum];//エッジの端のノード番号2


    //コンストラクタ
    public FieldInfomation(){
      //辺と頂点とエリア
      for(int i=0;i<EdgeNum;i++)edge[i] = new Edge();
      for(int i=0;i<NodeNum;i++)node[i] = new Node();
      for(int i=0;i<AreaNum;i++)area[i] = new Area();

      //csvの読み込み
      {
        //エリアに描画位置の情報を与える
        {
          area[0].SetPositon(-1.0, -2.0);
          area[1].SetPositon( 0.0, -2.0);
          area[2].SetPositon( 1.0, -2.0);
          area[3].SetPositon(-1.5, -1.0);
          area[4].SetPositon(-0.5, -1.0);
          area[5].SetPositon( 0.5, -1.0);
          area[6].SetPositon( 1.5, -1.0);
          area[7].SetPositon(-2.0, 0.0);
          area[8].SetPositon(-1.0, 0.0);
          area[9].SetPositon( 0.0, 0.0);
          area[10].SetPositon( 1.0, 0.0);
          area[11].SetPositon( 2.0, 0.0);
          area[12].SetPositon(-1.5, 1.0);
          area[13].SetPositon(-0.5, 1.0);
          area[14].SetPositon( 0.5, 1.0);
          area[15].SetPositon( 1.5, 1.0);
          area[16].SetPositon(-1.0, 2.0);
          area[17].SetPositon( 0.0, 2.0);
          area[18].SetPositon( 1.0, 2.0);
        }

        //エリアの種類をセットする
        {
          area[0].SetAreaType(AreaType.Forest);
          area[1].SetAreaType(AreaType.Pasture);
          area[2].SetAreaType(AreaType.Fields);
          area[3].SetAreaType(AreaType.Hills);
          area[4].SetAreaType(AreaType.Mountains);
          area[5].SetAreaType(AreaType.Hills);
          area[6].SetAreaType(AreaType.Pasture);
          area[7].SetAreaType(AreaType.Desert);
          area[8].SetAreaType(AreaType.Forest);
          area[9].SetAreaType(AreaType.Fields);
          area[10].SetAreaType(AreaType.Forest);
          area[11].SetAreaType(AreaType.Fields);
          area[12].SetAreaType(AreaType.Hills);
          area[13].SetAreaType(AreaType.Pasture);
          area[14].SetAreaType(AreaType.Pasture);
          area[15].SetAreaType(AreaType.Mountains);
          area[16].SetAreaType(AreaType.Mountains);
          area[17].SetAreaType(AreaType.Fields);
          area[18].SetAreaType(AreaType.Forest);
        }

        //エリアのホールドナンバーをセットする
        {
          area[0].SetHoldNumber(11);
          area[1].SetHoldNumber(12);
          area[2].SetHoldNumber(9);
          area[3].SetHoldNumber(4);
          area[4].SetHoldNumber(6);
          area[5].SetHoldNumber(5);
          area[6].SetHoldNumber(10);
          area[7].SetHoldNumber(0);
          area[8].SetHoldNumber(3);
          area[9].SetHoldNumber(11);
          area[10].SetHoldNumber(4);
          area[11].SetHoldNumber(8);
          area[12].SetHoldNumber(8);
          area[13].SetHoldNumber(10);
          area[14].SetHoldNumber(9);
          area[15].SetHoldNumber(3);
          area[16].SetHoldNumber(5);
          area[17].SetHoldNumber(2);
          area[18].SetHoldNumber(6);
        }

        //各エッジに隣り合うエッジ・ノード・エリアのリストを作る
        {
          //各エッジに、隣接しているエッジの番号を格納
          {
            String lines[] = loadStrings("data/EdgeNextEdge.csv");
            String lin;
            String [] splited;
            for(int i=1;i<EdgeNum+1;i++){//1行目はラベル
              lin = lines[i];
              splited = split(lin,',');
              for(int j=1;j<splited.length;j++){//1列目は対処エッジの番号
                if(splited[j].equals("") == false){
                  edge[i-1].AddNextEdgeNumber( int(splited[j]) );
                }
              }
            }
          }

          //各エッジに、隣接しているエリアの番号を格納
          {
            String lines[] = loadStrings("data/EdgeNextArea.csv");
            String lin;
            String [] splited;
            for(int i=1;i<EdgeNum+1;i++){//1行目はラベル
              lin = lines[i];
              splited = split(lin,',');
              for(int j=1;j<splited.length;j++){//1列目は対象エッジの番号
                if(splited[j].equals("") == false){
                  edge[i-1].AddNextAreaNumber( int(splited[j]) );
                }
              }
            }
          }

          //各エッジに、隣接しているノードの番号を格納
          {
            String lines[] = loadStrings("data/EdgeNextNode.csv");
            String lin;
            String [] splited;
            for(int i=1;i<EdgeNum+1;i++){//1行目はラベル
              lin = lines[i];
              splited = split(lin,',');
              for(int j=1;j<splited.length;j++){//1列目は対象エッジの番号
                if(splited[j].equals("") == false){
                  edge[i-1].AddNextNodeNumber( int(splited[j]) );
                }
              }
            }
          }

        }



        //各ノードに、隣接しているエッジの番号を格納
        {
          String lines[] = loadStrings("data/NodeNextEdge.csv");
          String lin;
          String [] splited;
          for(int i=1;i<NodeNum+1;i++){//1行目はラベル
            lin = lines[i];
            splited = split(lin,',');
            for(int j=1;j<splited.length;j++){//1列目は対処エッジの番号
              if(splited[j].equals("") == false){
                node[i-1].AddNextEdgeNumber( int(splited[j]) );
              }
            }
          }
        }

        //各ノードに、隣接しているエリアの番号を格納
        {
          String lines[] = loadStrings("data/NodeNextArea.csv");
          String lin;
          String [] splited;
          for(int i=1;i<NodeNum+1;i++){//1行目はラベル
            lin = lines[i];
            splited = split(lin,',');
            for(int j=1;j<splited.length;j++){//1列目は対象エッジの番号
              if(splited[j].equals("") == false){
                node[i-1].AddNextAreaNumber( int(splited[j]) );
              }
            }
          }
        }

        //各ノードに、隣接しているノードの番号を格納
        {
          String lines[] = loadStrings("data/NodeNextNode.csv");
          String lin;
          String [] splited;
          for(int i=1;i<NodeNum+1;i++){//1行目はラベル
            lin = lines[i];
            splited = split(lin,',');
            for(int j=1;j<splited.length;j++){//1列目は対象エッジの番号
              if(splited[j].equals("") == false){
                node[i-1].AddNextNodeNumber( int(splited[j]) );
              }
            }
          }
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
      }
    }


    public void Update(int elapsedTime){
    }

    public void Render(){
      pushMatrix();
      translate(FIELD_POSITION_X, FIELD_POSITION_Y);
      float x,y;
      int holder, cityLevel;

      //エリアの描画
      for(int i=0;i<AreaNum;i++){
        x = area[i].positionX * AREA_LENGTH;
        y = area[i].positionY * AREA_LENGTH * 3/4;
        pushMatrix();
        translate(x, y);
        DrawArea(area[i].areaType);
        DrawAreaHoldNumber(area[i].holdNumber);
        popMatrix();
      }

      //エッジの所有者を表示
      for(int i=0;i<EdgeNum;i++){
        holder = edge[i].holder;
        if(holder == 0){      strokeWeight( 5 );stroke( 0, 0, 40 );}
        else{     strokeWeight( 10 );stroke(150/PLAYER_NUMBER * holder+50, 200, 200 );};
        drawEdge(i);
      }

      //都市の描画
      for(int i=0;i<NodeNum;i++){
        x = position_x[i] * AREA_LENGTH;
        y = position_y[i] * AREA_LENGTH;

        holder = node[i].holder;
        cityLevel = node[i].cityLevel;

        pushMatrix();
        translate(x, y);
        DrawCity(holder,cityLevel);
        popMatrix();
      }

      popMatrix();
    }

    public void Debug_Render(){
      Render();
    }

    //指定されたエッジ番号のエッジの描画
    public void drawEdge(int edgeNumber){
      pushMatrix();
      int i = edgeNumber;
      float x1 = position_x[ edge[i].nextNodeNumber.get(0) ] * AREA_LENGTH;
      float x2 = position_x[ edge[i].nextNodeNumber.get(1) ] * AREA_LENGTH;
      float y1 = position_y[ edge[i].nextNodeNumber.get(0) ] * AREA_LENGTH;
      float y2 = position_y[ edge[i].nextNodeNumber.get(1) ] * AREA_LENGTH;
      line(x1,y1,x2,y2);
      popMatrix();
    }
    //指定されたエッジ番号のエッジの描画
    public void drawNode(int nodeNumber){
      pushMatrix();
      float x = position_x[ nodeNumber] * AREA_LENGTH;
      float y = position_y[ nodeNumber] * AREA_LENGTH;
      fill(0, 255, 255);//HSB
      ellipse(x,y,20,20);
      popMatrix();
    }

    //エッジの所有者の設定
    void SetEdgeOwner(int edgeNumber, int holder){
      edge[edgeNumber].SetHolder(holder);
    }

    //エッジの所有者の設定
    void SetNodeOwner(int nodeNumber, int holder, int cityLevel){
      if(holder == 0)cityLevel=0;//所有者がいないならcityLevelは0にしとく
      node[nodeNumber].SetHolder_and_Level(holder, cityLevel);
    }

    //ダイスの数・プレイヤー番号・エリアの種類をもとに、いくつ資材が得られるかを返す
    int DiceReturnMaterial(int diceNumber, int playerNumber, MaterialType materialType){
      return 1;
    }
}

//フィールドのエッジクラス(道路を敷く所)
class Edge{
  int holder = 0;//どのプレイヤーが保持している道路か.０なら未使用
  List<Integer> nextEdgeNumber = new ArrayList<Integer>();//隣り合ったエッジの番号を格納する配列.長さは不定
  List<Integer> nextAreaNumber = new ArrayList<Integer>();//隣り合ったエリアの番号を格納する配列.長さは不定
  List<Integer> nextNodeNumber = new ArrayList<Integer>();//隣り合ったノードの番号を格納する配列.長さは不定

  //道路を敷く.引数は取った人の識別番号
  public void BuildRoad(int tmp_holder){
    if(holder == 0){
      holder = tmp_holder;
    }else{
      println("Edge -> ErrorBuildRoad");
    }
  }


  //所有者を設定する
  public void SetHolder(int tmp_holder){
    holder = tmp_holder;
  }
  //隣り合うエッジの番号を格納させる
  public void AddNextEdgeNumber(int Number){
    nextEdgeNumber.add(Number);
  }
  //隣り合うエリアの番号を格納させる
  public void AddNextAreaNumber(int Number){
    nextAreaNumber.add(Number);
  }
  //隣り合うノードの番号を格納させる
  public void AddNextNodeNumber(int Number){
    nextNodeNumber.add(Number);
  }

}

//フィールドのノードクラス(都市を置く所)
class Node{
  int holder = 0;//どのプレイヤーが保持している都市か.０なら未使用
  int cityLevel = 0;//都市のレベル.0:未使用,1:都市レベル1,2:都市レベル2
  List<Integer> nextEdgeNumber = new ArrayList<Integer>();//隣り合ったエッジの番号を格納する配列.長さは不定
  List<Integer> nextAreaNumber = new ArrayList<Integer>();//隣り合ったエリアの番号を格納する配列.長さは不定
  List<Integer> nextNodeNumber = new ArrayList<Integer>();//隣り合ったノードの番号を格納する配列.長さは不定


  //村を作る.引数は取った人の識別番号
  public void BuildVillage(int tmp_holder){
    if(holder == 0 && cityLevel == 0){
      holder = tmp_holder;
      cityLevel++;
    }else{
      println("Node -> ErrorBuildCity");
    }
  }

  //村を都市に発展させる.
  public void Develop(){
    if(holder != 0 && cityLevel == 1){
      cityLevel++;
    }else{
      println("Node -> ErrorDevelop");
    }
  }

  //都市の所有者とレベルを設定する
  public void SetHolder_and_Level(int tmp_holder, int tmp_level){
    holder = tmp_holder;
    cityLevel = tmp_level;
  }

  //隣り合うエッジの番号を格納させる
  public void AddNextEdgeNumber(int Number){
    nextEdgeNumber.add(Number);
  }
  //隣り合うエリアの番号を格納させる
  public void AddNextAreaNumber(int Number){
    nextAreaNumber.add(Number);
  }
  //隣り合うノードの番号を格納させる
  public void AddNextNodeNumber(int Number){
    nextNodeNumber.add(Number);
  }
}

//フィールドの領域単位
class Area{
  int holdNumber = 0;//ターゲットとなる数値.サイコロがこの数値になったらアイテムゲット！
  AreaType areaType;//エリアの種類
  float positionX=0,positionY=0;//描画する位置の基準値(描画するときにはエリアの長さをかける)

  //PositionXとPositionYをセットする
  void SetPositon(float tmp_x,float tmp_y){
    positionX = tmp_x;
    positionY = tmp_y;
  }
  //areaTypeをセットする
  void SetAreaType(AreaType tmp_type){
    areaType = tmp_type;
  }
  //holdNumberをセットする
  void SetHoldNumber(int tmp){
    holdNumber = tmp;
  }
}

//エリアを描画.今は色の設定だけ.
void DrawArea(AreaType type){
  int tmp = AREA_LENGTH;
  image(ImageList_Area.get(type),0,0,tmp,tmp);
}

//都市を描画.今は色の設定だけ.
void DrawCity(int holder, int cityLevel){
  int tmp = CITY_LENGTH;
  if(holder == 0){

  }
  switch(cityLevel){
    case 1:
      image(ImageList_City1.get(String.valueOf(holder)),0,0,tmp,tmp);
      break;
    case 2:
      image(ImageList_City2.get(String.valueOf(holder)),0,0,tmp,tmp);
      break;
    default:
      image(Image_nonCity,0,0,tmp,tmp);
      break;

  }
}

//エリアのホールドナンバーを描画
void DrawAreaHoldNumber(int holdNumber){
  int tmp = AREA_HOLDNUMBER_LENGTH;
  switch(holdNumber){
    case 2:
      image(ImageList_Number.get("2"),0,0,tmp,tmp);
      break;
    case 3:
      image(ImageList_Number.get("3"),0,0,tmp,tmp);
      break;
    case 4:
      image(ImageList_Number.get("4"),0,0,tmp,tmp);
      break;
    case 5:
      image(ImageList_Number.get("5"),0,0,tmp,tmp);
      break;
    case 6:
      image(ImageList_Number.get("6"),0,0,tmp,tmp);
      break;
    case 8:
      image(ImageList_Number.get("8"),0,0,tmp,tmp);
      break;
    case 9:
      image(ImageList_Number.get("9"),0,0,tmp,tmp);
      break;
    case 10:
      image(ImageList_Number.get("10"),0,0,tmp,tmp);
      break;
    case 11:
      image(ImageList_Number.get("11"),0,0,tmp,tmp);
      break;
    case 12:
      image(ImageList_Number.get("12"),0,0,tmp,tmp);
      break;
    }
}
