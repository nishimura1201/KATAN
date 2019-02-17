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

//メッセージボックス
MessageBox messageBox;

//画像
HashMap<AreaType, PImage> ImageList_Area;
HashMap<String, PImage> ImageList_Number;
HashMap<String, PImage> ImageList_City1;
HashMap<String, PImage> ImageList_City2;
PImage Image_nonCity;

public void settings() {
  size(FIELD_LENGTH_X, FIELD_LENGTH_Y, P2D);
}
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

   messageBox = new MessageBox();


   //画像
   ImageList_Area = new HashMap<AreaType, PImage>();
   ImageList_Number = new HashMap<String, PImage>();
   ImageList_City1 = new HashMap<String, PImage>();
   ImageList_City2 = new HashMap<String, PImage>();

  //画像読み込み
  ImageList_Area.put(AreaType.Desert   ,loadImage("img/area/Desert.png"));
  ImageList_Area.put(AreaType.Fields   ,loadImage("img/area/Fields.png"));
  ImageList_Area.put(AreaType.Forest   ,loadImage("img/area/Forest.png"));
  ImageList_Area.put(AreaType.Mountains,loadImage("img/area/Mountains.png"));
  ImageList_Area.put(AreaType.Pasture  ,loadImage("img/area/Pasture.png"));
  ImageList_Area.put(AreaType.Hills    ,loadImage("img/area/Hills.png"));

  ImageList_Number.put("2"   ,loadImage("img/number/2.png"));
  ImageList_Number.put("3"   ,loadImage("img/number/3.png"));
  ImageList_Number.put("4"   ,loadImage("img/number/4.png"));
  ImageList_Number.put("5"   ,loadImage("img/number/5.png"));
  ImageList_Number.put("6"   ,loadImage("img/number/6.png"));
  ImageList_Number.put("8"   ,loadImage("img/number/8.png"));
  ImageList_Number.put("9"   ,loadImage("img/number/9.png"));
  ImageList_Number.put("10"  ,loadImage("img/number/10.png"));
  ImageList_Number.put("11"  ,loadImage("img/number/11.png"));
  ImageList_Number.put("12"  ,loadImage("img/number/12.png"));

  ImageList_City1.put("1"   ,loadImage("img/city/city1_1.png"));
  ImageList_City1.put("2"   ,loadImage("img/city/city1_2.png"));
  ImageList_City1.put("3"   ,loadImage("img/city/city1_3.png"));

  ImageList_City2.put("1"   ,loadImage("img/city/city2_1.png"));
  ImageList_City2.put("2"   ,loadImage("img/city/city2_2.png"));
  ImageList_City2.put("3"   ,loadImage("img/city/city2_3.png"));

  Image_nonCity = loadImage("img/city/nonCity.png");
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

  messageBox.Render();


}
//メインのステートマシンやインタフェイスについて記述

//メインステートマシン
class MainStateMachine extends StateChanger{
    String orderPlayerName[] = new String[PLAYER_NUMBER];//プレイヤーのターン順序
    int whoseTurn = 0;//今誰のターンなのか管理する
    boolean debugFlag = false;//デバッグモードONのフラグ
    //コンストラクタ
    public MainStateMachine(){
      super();

      Add("player1",new PlayerStateMachine( "player1", 0));
      Add("player2",new PlayerStateMachine( "player2", 1));
      Add("player3",new PlayerStateMachine( "player3", 2));
      Add("debug",new Debug());
      //プレイヤーのターン順序
      orderPlayerName[0] = "player1";
      orderPlayerName[1] = "player2";
      orderPlayerName[2] = "player3";
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
          if(whoseTurn+1 == orderPlayerName.length){
            whoseTurn = 0;
          }else{
            whoseTurn+=1;
          }
          Change(orderPlayerName[whoseTurn]);
          break;
      }

      return "null";
    }

    public void Render(){
        mCurrentState.Render();
        fill(50, 50, 50, 255);
        textSize(20);
        text("D         :debug mode", 10, FIELD_LENGTH_Y-100 + 0);
        text("A         :change player", 10, FIELD_LENGTH_Y-100 + 20);
        text("Z         :change motion", 10, FIELD_LENGTH_Y-100 + 40);
        text("ENTER     :choice motion", 10, FIELD_LENGTH_Y-100 + 60);
        text("BACK SPACE:back to player", 10, FIELD_LENGTH_Y-100 + 80);
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

  //子供のステートを追加する
  public void Add(String name, IState state){
    mStates.put(name,state);
    childList.add(name);//子リストに追加
  }
  //メッセージを受け取ってそれに応じた関数をキックする
  public void MessageOrder(String message){};
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
  public void MessageOrder(String message);

}

//空のステート
class emptyState implements IState{
  public String Update(int elapsedTime){return "null";};
  public void Render(){};
  public void OnEnter(){};
  public void OnExit(){};
  public void MessageOrder(String message){};
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
//プレイヤーのステートマシンなどについて記述

//プレイヤーのアクションを管理するステートマシン
class PlayerStateMachine extends StateChanger{
  int listIndex = 0;//どの子を選択しようとしているのかというindex
  String myName;//自分の名前
  int myNumber;//自分の番号、プレイヤー番号
  List<String> cardList = new ArrayList<String>();//所持しているカードのリスト
  HashMap<MaterialType, Integer> material = new HashMap<MaterialType, Integer>();//資材を管理するやつ
  int parameterRectSizeX = 200;//パラメータ表示の枠サイズX
  int parameterRectSizeY = 180;//パラメータ表示の枠サイズY
  int actionChoicesRectSizeX = 180;//行動選択表示の枠サイズX
  int actionChoicesRectSizeY = 150;//行動選択表示の枠サイズY

  //コンストラクタ メインステートマシンの実体と次のプレイヤーの名前
  public PlayerStateMachine(String tmp_myName,int tmp_myNumber){

    myName = tmp_myName;
    myNumber = tmp_myNumber;

    CardAdd("card1");
    CardAdd("card2");
    CardAdd("card3");
    CardAdd("card4");

    //資材の初期化
    for (MaterialType m : MaterialType.values()) {
      material.put(m,0);
    }

    //順番大事
    Add(PlayerSelectable.dice.getString()           ,new Dice(this));
    Add(PlayerSelectable.development.getString()    ,new Development(this));
    Add(PlayerSelectable.choiceCard.getString()     ,new ChoiceCard(this));
    Add(PlayerSelectable.tradeWithOther.getString() ,new TradeWithOther(this));
    //Add(PlayerSelectable.useCard.getString()        ,new UseCard(this));

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
  //資材を追加する(資材の種類, 追加する個数)
  public void AddMaterial(MaterialType m, int num){
    int tmp = material.get(m);
    material.put(m, tmp+num);
  }

  //ダイスの目から、自分の所有する開拓地に合わせた資材をゲット
  //結果はStringにまとめて戻すように
  public void GetMaterial_FromDice(){
    //fieldInfomationにアクセスして、自分の開拓地の周辺にある資材を返してもらう
    //引数をエリアの種類とプレイヤーの番号に、返り値を取得した個数にする
    int result;//戻り値の箱
    String str = "";
    for(MaterialType m :MaterialType.values()){
      result = fieldInfomation.DiceReturnMaterial(Parameter_Player.diceNumber,
                                                  Parameter_Player.playerNumber,
                                                  m);
      AddMaterial(m,result);
      if(result > 0){
        str += m.getName();
        str += ":";
        str += result;
        str += ", ";
      }
    }
    Parameter_Player.resultSTR = str;
    //タイプと個数をもとにAddMaterialで保持数の更新
    //Parameter_Player.resultSTR = Integer.toString(Parameter_Player.diceNumber);
    //取得した資材の結果をSringにまとめる
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
      if(keyPushJudge.GetJudge("RIGHT") == true){
        return "ChangePlayer";
      }
      if(keyPushJudge.GetJudge("DOWN") == true){
        listIndex++;
        if(childList.size() == listIndex)listIndex = 0;
      }
      if(keyPushJudge.GetJudge("ENTER") == true){
        //println(childList.get(listIndex));
        Change(childList.get(listIndex));
      }
    }

    return "null";
  };
  public void Render(){
    //デバッグ用に今選択くしている行動を画面右下に表示
    fill(50, 50, 50, 255);
    textSize(20);
    text(myName +"  "+ childList.get(listIndex), FIELD_LENGTH_X - 400, FIELD_LENGTH_Y - 200);

    ActionChoicesRender();//行動選択の表示
    ParameterRender();//パラメータの表示
    mCurrentState.Render();//子の呼び出し
  };

  //選択肢の表示
  public void ActionChoicesRender(){
    //選択肢の表示
    pushMatrix();
    translate(30,80);
    {
      //枠の表示
      DrawRect(0, 0, actionChoicesRectSizeX, actionChoicesRectSizeY);
      DrawRect(4, 4, actionChoicesRectSizeX-8, actionChoicesRectSizeY-8);

      //選択肢の表示
      textSize(30);
      fill(50, 50, 50, 255);
      text("->", 10, 40 + 30*listIndex);
      text("Dice", 50, 40);
      text("Develop", 50, 70);
      text("Card", 50, 100);
      text("Trade", 50, 130);
    }
    popMatrix();
  }

  //パラメータの表示
  public void ParameterRender(){
    fill(50, 50, 50, 255);

    //名前の表示
    textSize(30);
    text(myName, 50, 30);

    //資材の表示
    pushMatrix();
    translate(300,80);
    {
      //枠の表示
      DrawRect(0, 0, parameterRectSizeX, parameterRectSizeY);
      DrawRect(4, 4, parameterRectSizeX-8, parameterRectSizeY-8);

      //パラメータの表示
      textSize(30);
      fill(50, 50, 50, 255);
      text("Brick     :"+material.get(MaterialType.Brick), 10, 40);
      text("Lumber :"+material.get(MaterialType.Lumber), 10, 70);
      text("Wool     :"+material.get(MaterialType.Wool), 10, 100);
      text("Grain    :"+material.get(MaterialType.Grain), 10, 130);
      text("Iron       :"+material.get(MaterialType.Iron), 10, 160);
    }
    popMatrix();
  }

  public void MessageOrder(String message){
    switch(message){
      //デバッグ用のAddMaterialをキックする
      case "debug_AddMaterial":
        AddMaterial(debug_AddMaterial_m, debug_AddMaterial_num);
        break;
      case "ParameterRender":
        ParameterRender();
        break;
      case "GetMaterial_FromDice":
        GetMaterial_FromDice();
        break;
    }
  };
  public void OnEnter(){};
  public void OnExit(){};
}


//サイコロを振る
class Dice extends PlayerActionBase{
  String m_state = "YESorNO";//簡易に状態を管理する
  int diceNumber = 0;//振って出たダイスの合計値
  int targetPlayer = -1;//メッセージウィンドウの進捗を管理,最初に++したいから-1から始める

  //コンストラクタ
  public Dice(PlayerStateMachine tmp){
    //PlayerActionBaseのコンストラクタを起動
    super(tmp);
  }

  public String Update(int elapsedTime){
    switch(m_state){
      //本当にダイスwp振るかどうかの確認
      case "YESorNO":
        //BACKSPACEで一つ戻る
        if(PlayerStateMachineChildOFF() == "ChildOFF"){
          return "ChildOFF";
        }
        //ENTERで決定
        if(keyPushJudge.GetJudge("ENTER")){
          diceNumber = PApplet.parseInt(random(1,7)) + PApplet.parseInt(random(1,7));
          Parameter_Player.diceNumber = diceNumber;
          messageBox.MessageON("dice sum-->" + Integer.toString(diceNumber), "");
          m_state = "Distribution";

        }
        break;

      //ダイスの目に応じて資材の分配
      case "Distribution":
        //ENTER押すごとに,プレイヤーに資材の分配と成果の表示を行う
        String tmpStr = "";
        if(keyPushJudge.GetJudge("ENTER")){
          targetPlayer++;
          if(targetPlayer == PLAYER_NUMBER)return "ChildOFF";
        
          Parameter_Player.playerNumber = targetPlayer;//プレイヤー番号の設定

          //プレイヤーごとに資材の分配の計算と更新
          String playerName = mainStateMachine.orderPlayerName[targetPlayer];//プレイヤーの名前
          IState tmpPSM = mainStateMachine.mStates.get(playerName);//対象プレイヤーのステートマシン
          tmpPSM.MessageOrder("GetMaterial_FromDice");//ダイスの合計値から資源の取得

          messageBox.MessageON("dice sum-->" + Integer.toString(diceNumber), mainStateMachine.orderPlayerName[targetPlayer] +":"+ Parameter_Player.resultSTR);

        }

//        return "ChildOFF";
        return "null";

      default:
        break;
    }


    return "null";
  };
  public void Render(){
    //DIceと表示する
    fill(50, 50, 50, 255);
    textSize(20);
    text("Dice", FIELD_LENGTH_X - 200, FIELD_LENGTH_Y - 180);
    textSize(30);
    text("Dice",200, 30);

    //ステートに応じた表示
    switch(m_state){
      //本当にダイスwp振るかどうかの確認
      case "YESorNO":
        break;

      //ダイスの目に応じて資材の分配
      case "Distribution":
        break;

      default:
        break;
    }
  };
  public void OnEnter(){
    m_state = "YESorNO";
    diceNumber = 0;
    targetPlayer = -1;//最初に++したいから-1から始める
    messageBox.MessageON("roll dice?(ENTER or BACKSPACE)","");
  };
  public void OnExit(){
    messageBox.MessageOFF();
  };
}

//カードを選択する
class ChoiceCard extends PlayerActionBase{
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
    textSize(20);
    text("choiseCard", FIELD_LENGTH_X - 200, FIELD_LENGTH_Y - 180);
    for(int i=0;i<cardList.size();i++){
       text(cardList.get(i), FIELD_LENGTH_X - 200, FIELD_LENGTH_Y - 180 + 20*(i+1));
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
    textSize(20);
    text("TradeWithOther", FIELD_LENGTH_X - 200, FIELD_LENGTH_Y - 180);

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
    textSize(20);
    text("UseCard", FIELD_LENGTH_X - 200, FIELD_LENGTH_Y - 180);

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
    textSize(20);
    text("Development", FIELD_LENGTH_X - 200, FIELD_LENGTH_Y - 180);

  };
  public void OnEnter(){};
  public void OnExit(){};
}
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
    keyList.put("l", TARGETKEY_RELEASED);
    keyList.put("s", TARGETKEY_RELEASED);
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
    keyListTrigger.put("l", 0);
    keyListTrigger.put("s", 0);
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
    //_ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _
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

      //_ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _
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
        case 'l':
          if(keyListTrigger.get("l") == 0){
            keyList.put("l", TARGETKEY_PRESSED);
            keyListTrigger.put("l", 1);
          }
          break;
        case 's':
          if(keyListTrigger.get("s") == 0){
            keyList.put("s", TARGETKEY_PRESSED);
            keyListTrigger.put("s", 1);
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

//枠の表示
public void DrawRect(int leftUP, int leftDown,int width, int height){
  stroke(100,50,50);
  fill(255);
  strokeWeight(2);
  rect(leftUP, leftDown, width, height);
}

public class MessageBox{
  String message1;
  String message2;
  int messageBox_X = 500;
  int messageBox_Y = 70;

  //コンストラクタ
  MessageBox(){
    message1 = "";
    message2 = "";
  }

  public void Render(){
    pushMatrix();
    translate(30,400);

    DrawRect(0, 0, messageBox_X, messageBox_Y);
    DrawRect(4, 4, messageBox_X-8, messageBox_Y-8);

    textSize(20);
    fill(50, 50, 50, 255);
    text(message1, 20, 25);
    text(message2, 20, 55);

    popMatrix();
  }

  public void MessageON(String tmp1, String tmp2){
    message1 = tmp1;
    message2 = tmp2;
  }

  public void MessageOFF(){
    message1 = "";
    message2 = "";
  }


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
    public void SetEdgeOwner(int edgeNumber, int holder){
      edge[edgeNumber].SetHolder(holder);
    }

    //エッジの所有者の設定
    public void SetNodeOwner(int nodeNumber, int holder, int cityLevel){
      if(holder == 0)cityLevel=0;//所有者がいないならcityLevelは0にしとく
      node[nodeNumber].SetHolder_and_Level(holder, cityLevel);
    }

    //ダイスの数・プレイヤー番号・エリアの種類をもとに、いくつ資材が得られるかを返す
    public int DiceReturnMaterial(int diceNumber, int playerNumber, MaterialType materialType){
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
  image(ImageList_Area.get(type),0,0,tmp,tmp);
}

//都市を描画.今は色の設定だけ.
public void DrawCity(int holder, int cityLevel){
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
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "main" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
