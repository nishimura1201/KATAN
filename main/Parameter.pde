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
  Brick,
  Lumber,
  Wool,
  Grain,
  Iron
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
