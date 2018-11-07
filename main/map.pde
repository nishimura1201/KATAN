/* フィールドに関するクラスとか */

//MAP
public class MAP{
    int EdgeNum = 72;//辺の数
    int NodeNum = 54;//ノードの数
    int AreaNum = 19;//エリアの数

    Edge[] edge = new Edge[EdgeNum];//辺
    Node[] node = new Node[NodeNum];//頂点
    Area[] area = new Area[AreaNum];//エリア

    //HashMap<String, PImage> ImageList = new HashMap<String, PImage>();

    //コンストラクタ
    public MAP(){
      //辺と頂点とエリア
      for(int i=0;i<EdgeNum;i++)edge[i] = new Edge();
      for(int i=0;i<NodeNum;i++)node[i] = new Node();
      for(int i=0;i<AreaNum;i++)area[i] = new Area();

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


    }


    public void Update(int elapsedTime){
    }

    public void Render(){
      pushMatrix();
      translate(500, 300);

      //エリアの描画
      float x,y;
      for(int i=0;i<AreaNum;i++){
        x = area[i].positionX * AREA_LENGTH;
        y = area[i].positionY * AREA_LENGTH * 3/4;
        pushMatrix();
        translate(x, y);
        {
          DrawArea(area[i].areaType);

          //ellipse(0,0,AREA_LENGTH*1.2,AREA_LENGTH*1.2);
          DrawAreaHoldNumber(area[i].holdNumber);
        }
        popMatrix();
      }

      popMatrix();
    }
}

//フィールドのエッジクラス(道路を敷く所)
class Edge{
  int playerNumber = 0;//どのプレイヤーが保持している道路か.０なら未使用

  //道路を敷く.引数は取った人の識別番号
  public void BuildRoad(int tmp_playerNumber){
    if(playerNumber == 0){
      playerNumber = tmp_playerNumber;
    }else{
      println("Edge -> ErrorBuildRoad");
    }
  }
}

//フィールドのノードクラス(都市を置く所)
class Node{
  int playerNumber = 0;//どのプレイヤーが保持している都市か.０なら未使用
  int CityLevel = 0;//都市のレベル.0:未使用,1:都市レベル1,2:都市レベル2

  //村を作る.引数は取った人の識別番号
  public void BuildVillage(int tmp_playerNumber){
    if(playerNumber == 0 && CityLevel == 0){
      playerNumber = tmp_playerNumber;
      CityLevel++;
    }else{
      println("Node -> ErrorBuildCity");
    }
  }

  //村を都市に発展させる.
  public void Develop(){
    if(playerNumber != 0 && CityLevel == 1){
      CityLevel++;
    }else{
      println("Node -> ErrorDevelop");
    }
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
  switch(type){
    case Hills:
      image(ImageList_Area.get("Hills"),0,0,tmp,tmp);
      break;
    case Pasture:
      image(ImageList_Area.get("Pasture"),0,0,tmp,tmp);
      break;
    case Mountains:
      image(ImageList_Area.get("Mountains"),0,0,tmp,tmp);
      break;
    case Forest:
      image(ImageList_Area.get("Forest"),0,0,tmp,tmp);
      break;
    case Fields:
      image(ImageList_Area.get("Fields"),0,0,tmp,tmp);
      break;
    case Grassland:
      image(ImageList_Area.get("Grassland"),0,0,tmp,tmp);
      break;
    case Desert:
      image(ImageList_Area.get("Desert"),0,0,tmp,tmp);
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
