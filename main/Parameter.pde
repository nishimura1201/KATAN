//キーボード・マウスクリック関連。それぞれの値に意味はない
public static final int MOUSE_CLICK = 1;
public static final int MOUSE_NOTCLICK = 2;
public static final int TARGETKEY_PRESSED = 3;
public static final int TARGETKEY_RELEASED = 4;

//プレイヤーの人数
public static final int PLAYER_NUMBER = 3;
//エリアの一辺の長さ
public static final int AREA_LENGTH = 100;
//都市描画の一辺の長さ
public static final int CITY_LENGTH = 50;
//ホールドナンバーの半径
public static final int AREA_HOLDNUMBER_LENGTH = 40;

//フィールドの大きさ
public static final int FIELD_LENGTH_X = 1100;
public static final int FIELD_LENGTH_Y = 800;
//フィールドを書く位置
public static final int FIELD_POSITION_X = 800;
public static final int FIELD_POSITION_Y = 250;

//エリアの種類
enum AreaType{
  Hills,
  Pasture,
  Mountains,
  Forest,
  Fields,
  Grassland,
  Desert
}

//資材の種類
enum MaterialType{
  Brick(0, "Brick"),
  Lumber(1, "Lumber"),
  Wool(2, "Wool"),
  Grain(3, "Grain"),
  Iron(4, "Iron");

  private final int id;
  private final String name;
  private MaterialType(final int id,final String name) {
    this.id = id;
    this.name = name;
  }
  public int getId() {
    return id;
  }
  public String getName() {
    return name;
  }

  //数値から対応する要素の名前を返す
  public static String toString(int tmp) {
    for (MaterialType num : values()) {
        if (num.getId() == tmp) { // id が一致するものを探す
            return num.getName();
        }
    }
    println("MaterialType : unknown number\n");
    return "null";
  }

  //数値から対応する要素を返す
  public static MaterialType fromInt(int tmp) {
    switch(tmp){
      case 0:return( MaterialType.Brick );
      case 1:return( MaterialType.Lumber );
      case 2:return( MaterialType.Wool );
      case 3:return( MaterialType.Grain );
      case 4:return( MaterialType.Iron );

      default:return( MaterialType.Brick );
    }
  }
}

//プレイヤー関数が持つ選択肢の種類
enum PlayerSelectable{
  dice("dice"),
  choiceCard("choiceCard"),
  tradeWithOther("tradeWithOther"),
  useCard("useCard"),
  development("development");

  private final String text;
  private PlayerSelectable(final String text){
    this.text = text;
  }
  public String getString(){
    return this.text;
  }
}

//----------------------------------------
//関数間でやり取りするための変数群,共有変数的な立ち位置

//Playerステートマシンで使うための共有変数群
public static class Parameter_Player{
  //GetMaterial_FromDiceで使う変数
  public static String resultSTR;
  public static int diceNumber;
  public static int playerNumber;
}
