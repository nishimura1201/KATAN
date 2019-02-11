import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.*; 
import java.io.File; 
import java.io.FileNotFoundException; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class main extends PApplet {

//import java.util.Map;
//import java.util.HashMap;




//メインステートマシン
int drawCount;//経過フレームを数える
MainStateMachine mainStateMachine;
KeyPushJudge keyPushJudge;
FieldInfomation fieldInfomation;


//画像
HashMap<String, PImage> ImageList_Area;
HashMap<String, PImage> ImageList_Number;

public void setup() {

  
  noStroke();
  frameRate(60);

  background(250);
  colorMode(HSB);
  textSize(30);
  init();
}

//初期化
public void init(){
   PFont font = loadFont("YuGothic-Regular-48.vlw");
   textFont(font, 48);
   imageMode(CENTER);

   drawCount = 0;//経過フレームを数える
   fieldInfomation = new FieldInfomation();
   mainStateMachine = new MainStateMachine();
   keyPushJudge = new KeyPushJudge();


   //画像
   ImageList_Area = new HashMap<String, PImage>();
   ImageList_Number = new HashMap<String, PImage>();


  //画像読み込み
  ImageList_Area.put("Desert"   ,loadImage("img/area/Desert.png"));
  ImageList_Area.put("Fields"   ,loadImage("img/area/Fields.png"));
  ImageList_Area.put("Forest"   ,loadImage("img/area/Forest.png"));
  ImageList_Area.put("Mountains",loadImage("img/area/Mountains.png"));
  ImageList_Area.put("Pasture"  ,loadImage("img/area/Pasture.png"));
  ImageList_Area.put("Hills"    ,loadImage("img/area/Hills.png"));

  ImageList_Number.put("2"   ,loadImage("img/Number/2.png"));
  ImageList_Number.put("3"   ,loadImage("img/Number/3.png"));
  ImageList_Number.put("4"   ,loadImage("img/Number/4.png"));
  ImageList_Number.put("5"   ,loadImage("img/Number/5.png"));
  ImageList_Number.put("6"   ,loadImage("img/Number/6.png"));
  ImageList_Number.put("8"   ,loadImage("img/Number/8.png"));
  ImageList_Number.put("9"   ,loadImage("img/Number/9.png"));
  ImageList_Number.put("10"  ,loadImage("img/Number/10.png"));
  ImageList_Number.put("11"  ,loadImage("img/Number/11.png"));
  ImageList_Number.put("12"  ,loadImage("img/Number/12.png"));

}


public void draw() {
  fill(0, 0, 255);//HSB
  rect(-10, -10, width+20, height+20);
  keyPushJudge.Update();//キーが押されたかどうかの判定

  //移行メイン処理
  drawCount++;

  mainStateMachine.Update(drawCount);

  fieldInfomation.Render();
  mainStateMachine.Render();


}
//メインのステートマシンやインタフェイスについて記述

//メインステートマシン
class MainStateMachine extends StateChanger{
    String orderPlayer[] = new String[PLAYER_NUMBER];//プレイヤーのターン順序
    int whoseTurn = 0;//今誰のターンなのか管理する
    boolean debugFlag = false;//デバッグモードONのフラグ
    //コンストラクタ
    public MainStateMachine(){
      super();

      Add("player1",new PlayerStateMachine( "player1"));
      Add("player2",new PlayerStateMachine( "player2"));
      Add("player3",new PlayerStateMachine( "player3"));
      Add("debug",new Debug());
      //プレイヤーのターン順序
      orderPlayer[0] = "player1";
      orderPlayer[1] = "player2";
      orderPlayer[2] = "player3";
      //最初はplayer1から
      Change("player1");
    }


    public String Update(int elapsedTime){
      //DebugModeのON,OFF
      if(keyPushJudge.GetJudge("d")){
        if(debugFlag){
          debugFlag = false;
          Change("player1");
        }
        else{
          debugFlag = true;
          Change("debug");
        }
      }

      //子ステート(PlayerStateとdebug)の実行
      String order = mCurrentState.Update(elapsedTime);
      switch(order){
        //次のプレイヤーにステートを移す
        case "ChangePlayer":
          if(whoseTurn+1 == orderPlayer.length){
            whoseTurn = 0;
          }else{
            whoseTurn+=1;
          }
          Change(orderPlayer[whoseTurn]);
          break;
      }

      return "null";
    }

    public void Render(){
        mCurrentState.Render();
    }
}

//複数の子を管理するステートマシンのベースとなるクラス
public class StateChanger implements IState{
  Map<String, IState> mStates = new HashMap<String, IState>();
  IState mCurrentState = new emptyState();
  List<String> childList = new ArrayList<String>();//子の名前のリスト
  boolean childOn = false;//子に主導権が移っているかどうか

  //コンストラクタ
  public StateChanger(){
    mStates.put("empty",new emptyState());//empty の Add
  }
  //子の主導権を消し、自分に主導権が移る.子が呼ぶ
  public void ChildOFF(){
    Change("empty");
    childOn = false;
  }

  public void Change(String stateName){
    mCurrentState.OnExit();
    mCurrentState = mStates.get(stateName);
    mCurrentState.OnEnter();

    childOn = true;
  }

  public void Add(String name, IState state){
    mStates.put(name,state);
    childList.add(name);//子リストに追加
  }
  public String Update(int elapsedTime){return "null";};
  public void Render(){};
  public void OnEnter(){};
  public void OnExit(){};
}

//複数の子を管理するステートマシンのベースとなるクラス
public class PlayerActionBase extends StateChanger{
  PlayerStateMachine PlayerStateMachine;//プレイヤーステートマシン（親の参照）
  //コンストラクタ
  public PlayerActionBase(PlayerStateMachine tmp){
    super();
    PlayerStateMachine = tmp;
  }

  public String PlayerStateMachineChildOFF(){
    if(keyPushJudge.GetJudge("BACKSPACE") == true){
      return "ChildOFF";
    }
    return "null";
  };
}

//ステートのベースとなるインタフェイス
public interface IState{
  public String Update(int elapsedTime);
  public void Render();
  public void OnEnter();
  public void OnExit();
}

//空のステート
class emptyState implements IState{
  public String Update(int elapsedTime){return "null";};
  public void Render(){};
  public void OnEnter(){};
  public void OnExit(){};
}
//キーボード・マウスクリック関連。それぞれの値に意味はない
public static final int MOUSE_CLICK = 1;
public static final int MOUSE_NOTCLICK = 2;
public static final int TARGETKEY_PRESSED = 3;
public static final int TARGETKEY_RELEASED = 4;

//プレイヤーの人数
public static final int PLAYER_NUMBER = 3;
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
//プレイヤーのステートマシンなどについて記述

//プレイヤーのアクションを管理するステートマシン
class PlayerStateMachine extends StateChanger{
  int listIndex = 0;//どの子を選択しようとしているのかというindex
  String MyName;//自分の名前
  List<String> cardList = new ArrayList<String>();//所持しているカードのリスト

  //コンストラクタ メインステートマシンの実体と次のプレイヤーの名前
  public PlayerStateMachine(String tmp_MyName){

    MyName = tmp_MyName;

    CardAdd("card1");
    CardAdd("card2");
    CardAdd("card3");
    CardAdd("card4");


    Add(PlayerSelectable.dice.getString()           ,new Dice(this));
    Add(PlayerSelectable.choiceCard.getString()     ,new ChoiceCard(this));
    Add(PlayerSelectable.tradeWithOther.getString() ,new TradeWithOther(this));
    Add(PlayerSelectable.useCard.getString()        ,new UseCard(this));
    Add(PlayerSelectable.development.getString()    ,new Development(this));

  }

  //子の主導権を消し、自分に主導権が戻る.子が呼ぶ
  public void ChildOFF(){
    Change("empty");
    childOn = false;
  }

  //所持カードを追加
  public void CardAdd(String cardName){
    cardList.add(cardName);
  }
  //カードリストを返す
  public List<String> GetCardList(){
    return cardList;
  }


  public String Update(int elapsedTime){

    //子に主導権が移ってるならここでの操作は行わんようにっていうやつ
    if(childOn == true){
      String order = mCurrentState.Update(elapsedTime);//子の呼び出し
      switch(order){
        case "ChildOFF":
          ChildOFF();
          break;
      }
    }else{
      //次のプレイヤーに所有権を移す
      if(keyPushJudge.GetJudge("a")){
        return "ChangePlayer";
      }
      if(keyPushJudge.GetJudge("z")){
        listIndex++;
        if(childList.size() == listIndex)listIndex = 0;
      }
      if(keyPushJudge.GetJudge("ENTER")){
        //println(childList.get(listIndex));
        Change(childList.get(listIndex));
      }
    }

    return "null";
  };
  public void Render(){
    fill(50, 50, 50, 255);
    textSize(25);
    text(MyName +"  "+ childList.get(listIndex), 50, 50);
    mCurrentState.Render();//子の呼び出し
  };
  public void OnEnter(){};
  public void OnExit(){};
}


//サイコロを振る
class Dice extends PlayerActionBase{
  //コンストラクタ
  public Dice(PlayerStateMachine tmp){
    //PlayerActionBaseのコンストラクタを起動
    super(tmp);
  }

  public String Update(int elapsedTime){
    return PlayerStateMachineChildOFF();//BACKSPACEで一つ戻る
  };
  public void Render(){
    fill(50, 50, 50, 255);
    text("Dice", 100, 100);
  };
  public void OnEnter(){};
  public void OnExit(){};
}

//カードを選択する
class ChoiceCard extends PlayerActionBase{
  //PlayerStateMachine PlayerStateMachine;//プレイヤーステートマシン（親の参照）
  List<String> cardList;
  int cardIndex = 0;//カード選択のためのindex

  //コンストラクタ
  public ChoiceCard(PlayerStateMachine tmp){
    //PlayerActionBaseのコンストラクタを起動
    super(tmp);
  }

  //cardListを設定
  public void SetcardList(List<String> tmp){
    cardList = tmp;
  };

  public String Update(int elapsedTime){
    return PlayerStateMachineChildOFF();//BACKSPACEで一つ戻る
  };
  public void Render(){
    fill(50, 50, 50, 255);
    text("choiseCard", 100, 100);
    for(int i=0;i<cardList.size();i++){
       text(cardList.get(i), 200, 100 + 50*(i+1));
    }
  };
  public void OnEnter(){
    //プレイヤーの所持しているカードを取得
    cardList = PlayerStateMachine.GetCardList();
  };
  public void OnExit(){};
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
    text("TradeWithOther", 100, 100);

  };
  public void OnEnter(){};
  public void OnExit(){};
}

//カードの使用
class UseCard extends PlayerActionBase{
  //PlayerStateMachine PlayerStateMachine;//プレイヤーステートマシン（親の参照）

  //コンストラクタ
  public UseCard(PlayerStateMachine tmp){
    //PlayerActionBaseのコンストラクタを起動
    super(tmp);
  }

  public String Update(int elapsedTime){
    return PlayerStateMachineChildOFF();//BACKSPACEで一つ戻る
  };
  public void Render(){
    fill(50, 50, 50, 255);
    text("UseCard", 100, 100);

  };
  public void OnEnter(){};
  public void OnExit(){};
}

//開発
class Development extends PlayerActionBase{
  //PlayerStateMachine PlayerStateMachine;//プレイヤーステートマシン（親の参照）

  //コンストラクタ
  public Development(PlayerStateMachine tmp){
    //PlayerActionBaseのコンストラクタを起動
    super(tmp);
  }

  public String Update(int elapsedTime){
    return PlayerStateMachineChildOFF();//BACKSPACEで一つ戻る

  };
  public void Render(){
    fill(50, 50, 50, 255);
    text("Development", 100, 100);

  };
  public void OnEnter(){};
  public void OnExit(){};
}
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
      //プレイヤー人数+(未使用状態)だから+2する
      if(targetHolder+2 == PLAYER_NUMBER)targetHolder = 0;
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
        position_x[i-1] = PApplet.parseFloat(splited[1]);//x座標
        position_y[i-1] = PApplet.parseFloat(splited[2])/4;//y座標
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
        edgeNextNode1[i-1] = PApplet.parseInt(splited[1]);
        edgeNextNode2[i-1] = PApplet.parseInt(splited[2]);
      }
    }

  }

  //エッジの所有者の設定
  public void SetEdgeOwner(){

  }

  //更新
  public void Update(){

  }

  //描画
  public void Render(){
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
int mouseClickTorF = MOUSE_NOTCLICK;

//キーが押された瞬間をとらえるためのクラス
public class KeyPushJudge{
  HashMap<String, Integer > keyList= new HashMap<String, Integer>();
  HashMap<String, Integer > keyListTrigger= new HashMap<String, Integer>();

  //コンストラクタ
  public KeyPushJudge(){
    keyList.put("a", TARGETKEY_RELEASED);
    keyList.put("z", TARGETKEY_RELEASED);
    keyList.put("x", TARGETKEY_RELEASED);
    keyList.put("c", TARGETKEY_RELEASED);
    keyList.put("d", TARGETKEY_RELEASED);
    keyList.put("RIGHT", TARGETKEY_RELEASED);
    keyList.put("LEFT", TARGETKEY_RELEASED);
    keyList.put("UP", TARGETKEY_RELEASED);
    keyList.put("DOWN", TARGETKEY_RELEASED);
    keyList.put("ENTER", TARGETKEY_RELEASED);
    keyList.put("BACKSPACE", TARGETKEY_RELEASED);

    keyListTrigger.put("a", 0);
    keyListTrigger.put("z", 0);
    keyListTrigger.put("x", 0);
    keyListTrigger.put("c", 0);
    keyListTrigger.put("d", 0);
    keyListTrigger.put("RIGHT", 0);
    keyListTrigger.put("LEFT", 0);
    keyListTrigger.put("UP", 0);
    keyListTrigger.put("DOWN", 0);
    keyListTrigger.put("ENTER", 0);
    keyListTrigger.put("BACKSPACE", 0);
  }

  public void Update(){


    for (String tmp_key : keyList.keySet()) {
      keyList.put(tmp_key, TARGETKEY_RELEASED);
    }

    //PRESSED の判定
    if(keyPressed==true){
      switch(keyCode){
        case RIGHT:
          if(keyListTrigger.get("RIGHT") == 0){
            keyList.put("RIGHT", TARGETKEY_PRESSED);
            keyListTrigger.put("RIGHT", 1);
          }
          break;
        case LEFT:
          if(keyListTrigger.get("LEFT") == 0){
            keyList.put("LEFT", TARGETKEY_PRESSED);
            keyListTrigger.put("LEFT", 1);
          }
          break;
        case UP:
          if(keyListTrigger.get("UP") == 0){
            keyList.put("UP", TARGETKEY_PRESSED);
            keyListTrigger.put("UP", 1);
          }
          break;
        case DOWN:
          if(keyListTrigger.get("DOWN") == 0){
            keyList.put("DOWN", TARGETKEY_PRESSED);
            keyListTrigger.put("DOWN", 1);
          }
          break;
      }
      switch(key){
        case 'a':
          if(keyListTrigger.get("a") == 0){
            keyList.put("a", TARGETKEY_PRESSED);
            keyListTrigger.put("a", 1);
          }
          break;
        case 'z':
          if(keyListTrigger.get("z") == 0){
            keyList.put("z", TARGETKEY_PRESSED);
            keyListTrigger.put("z", 1);
          }
          break;
        case 'x':
          if(keyListTrigger.get("x") == 0){
            keyList.put("x", TARGETKEY_PRESSED);
            keyListTrigger.put("x", 1);
          }
          break;
        case 'c':
          if(keyListTrigger.get("c") == 0){
            keyList.put("c", TARGETKEY_PRESSED);
            keyListTrigger.put("c", 1);
          }
          break;
        case 'd':
          if(keyListTrigger.get("d") == 0){
            keyList.put("d", TARGETKEY_PRESSED);
            keyListTrigger.put("d", 1);
          }
          break;
        case ENTER:
          if(keyListTrigger.get("ENTER") == 0){
            keyList.put("ENTER", TARGETKEY_PRESSED);
            keyListTrigger.put("ENTER", 1);
          }
          break;
        case BACKSPACE:
          if(keyListTrigger.get("BACKSPACE") == 0){
            keyList.put("BACKSPACE", TARGETKEY_PRESSED);
            keyListTrigger.put("BACKSPACE", 1);
          }
          break;
      }
    }else{
      //RELEASED に初期化
      for (String tmp_key : keyList.keySet()) {
        keyListTrigger.put(tmp_key, 0);
      }

    }
  }




  //指定されたキーが押されたのかどうかtrue か false で返す
  public boolean GetJudge(String tmp){
    if(keyList.get(tmp) == TARGETKEY_PRESSED)return true;
    else if (keyList.get(tmp) == TARGETKEY_RELEASED)return false;
    else {println("keyJudgeError");return false;}
  }
}
public void mousePressed() {
  mouseClickTorF = MOUSE_CLICK;
}
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
          area[0].SetPositon(-1.0f, -2.0f);
          area[1].SetPositon( 0.0f, -2.0f);
          area[2].SetPositon( 1.0f, -2.0f);
          area[3].SetPositon(-1.5f, -1.0f);
          area[4].SetPositon(-0.5f, -1.0f);
          area[5].SetPositon( 0.5f, -1.0f);
          area[6].SetPositon( 1.5f, -1.0f);
          area[7].SetPositon(-2.0f, 0.0f);
          area[8].SetPositon(-1.0f, 0.0f);
          area[9].SetPositon( 0.0f, 0.0f);
          area[10].SetPositon( 1.0f, 0.0f);
          area[11].SetPositon( 2.0f, 0.0f);
          area[12].SetPositon(-1.5f, 1.0f);
          area[13].SetPositon(-0.5f, 1.0f);
          area[14].SetPositon( 0.5f, 1.0f);
          area[15].SetPositon( 1.5f, 1.0f);
          area[16].SetPositon(-1.0f, 2.0f);
          area[17].SetPositon( 0.0f, 2.0f);
          area[18].SetPositon( 1.0f, 2.0f);
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
                  edge[i-1].AddNextEdgeNumber( PApplet.parseInt(splited[j]) );
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
                  edge[i-1].AddNextAreaNumber( PApplet.parseInt(splited[j]) );
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
                  edge[i-1].AddNextNodeNumber( PApplet.parseInt(splited[j]) );
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
                node[i-1].AddNextEdgeNumber( PApplet.parseInt(splited[j]) );
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
                node[i-1].AddNextAreaNumber( PApplet.parseInt(splited[j]) );
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
                node[i-1].AddNextNodeNumber( PApplet.parseInt(splited[j]) );
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
            position_x[i-1] = PApplet.parseFloat(splited[1]);//x座標
            position_y[i-1] = PApplet.parseFloat(splited[2])/4;//y座標
          }
        }
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
    public void Debug_Render(){
      strokeWeight( 5 );
      int holder;
      for(int i=0;i<EdgeNum;i++){
        holder = edge[i].holder;
        if(holder == 0)stroke( 0, 0, 20 );
        else stroke( 255/PLAYER_NUMBER * holder, 250, 250 );
        drawEdge(i);
      }
    }

    //指定されたエッジ番号のエッジの描画
    public void drawEdge(int edgeNumber){
      pushMatrix();
      translate(500, 300);
      int i = edgeNumber;
      float x1 = position_x[ edge[i].nextNodeNumber.get(0) ] * AREA_LENGTH;
      float x2 = position_x[ edge[i].nextNodeNumber.get(1) ] * AREA_LENGTH;
      float y1 = position_y[ edge[i].nextNodeNumber.get(0) ] * AREA_LENGTH;
      float y2 = position_y[ edge[i].nextNodeNumber.get(1) ] * AREA_LENGTH;
      line(x1,y1,x2,y2);
      popMatrix();
    }

    //エッジの所有者の設定
    public void SetEdgeOwner(int edgeNumber, int holder){
      edge[edgeNumber].SetHolder(holder);
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
  int CityLevel = 0;//都市のレベル.0:未使用,1:都市レベル1,2:都市レベル2
  List<Integer> nextEdgeNumber = new ArrayList<Integer>();//隣り合ったエッジの番号を格納する配列.長さは不定
  List<Integer> nextAreaNumber = new ArrayList<Integer>();//隣り合ったエリアの番号を格納する配列.長さは不定
  List<Integer> nextNodeNumber = new ArrayList<Integer>();//隣り合ったノードの番号を格納する配列.長さは不定


  //村を作る.引数は取った人の識別番号
  public void BuildVillage(int tmp_holder){
    if(holder == 0 && CityLevel == 0){
      holder = tmp_holder;
      CityLevel++;
    }else{
      println("Node -> ErrorBuildCity");
    }
  }

  //村を都市に発展させる.
  public void Develop(){
    if(holder != 0 && CityLevel == 1){
      CityLevel++;
    }else{
      println("Node -> ErrorDevelop");
    }
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
  public void SetPositon(float tmp_x,float tmp_y){
    positionX = tmp_x;
    positionY = tmp_y;
  }
  //areaTypeをセットする
  public void SetAreaType(AreaType tmp_type){
    areaType = tmp_type;
  }
  //holdNumberをセットする
  public void SetHoldNumber(int tmp){
    holdNumber = tmp;
  }
}

//エリアを描画.今は色の設定だけ.
public void DrawArea(AreaType type){
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
public void DrawAreaHoldNumber(int holdNumber){
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
  public void settings() {  size(800, 600, P2D); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "main" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
