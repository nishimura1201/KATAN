//キーボード・マウスクリック関連。それぞれの値に意味はない
public static final int MOUSE_CLICK = 1;
public static final int MOUSE_NOTCLICK = 2;
public static final int TARGETKEY_PRESSED = 3;
public static final int TARGETKEY_RELEASED = 4;

//エリアの一辺の長さ
public static final int AREA_LENGTH = 100;
//ホールドナンバーの半径
public static final int AREA_HOLDNUMBER_LENGTH = 40;


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
