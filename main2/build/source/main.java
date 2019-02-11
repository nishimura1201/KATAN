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
MAP map;

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
   mainStateMachine = new MainStateMachine();
   keyPushJudge = new KeyPushJudge();
   map = new MAP();
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
  fill(250, 0, 255, 255);
  rect(0, 0, width, height);
  keyPushJudge.Update();//キーが押されたかどうかの判定

  //移行メイン処理
  drawCount++;

  mainStateMachine.Update(drawCount);
  mainStateMachine.Render();

  map.Render();

}
//メインのステートマシンやインタフェイスについて記述

//メインステートマシン
class MainStateMachine extends StateChanger{

    //コンストラクタ
    public MainStateMachine(){
      super();

      Add("player1",new PlayerStateMAchine(this, "player1","player2"));
      Add("player2",new PlayerStateMAchine(this, "player2","player3"));
      Add("player3",new PlayerStateMAchine(this, "player3","player1"));

      Change("player1");
    }


    public void Update(int elapsedTime){
        mCurrentState.Update(elapsedTime);
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
  public void Update(int elapsedTime){};
  public void Render(){};
  public void OnEnter(){};
  public void OnExit(){};
}

//複数の子を管理するステートマシンのベースとなるクラス
public class PlayerActionBase extends StateChanger{
  PlayerStateMAchine playerStateMachine;//プレイヤーステートマシン（親の参照）


  //コンストラクタ
  public PlayerActionBase(PlayerStateMAchine tmp_playerStateMachine){
    super();
    playerStateMachine = tmp_playerStateMachine;
  }

  public void playerStateMachineChildOFF(){
    if(keyPushJudge.GetJudge("BACKSPACE") == true){
      playerStateMachine.ChildOFF();
    }
  };
}

//ステートのベースとなるインタフェイス
public interface IState{
  public void Update(int elapsedTime);
  public void Render();
  public void OnEnter();
  public void OnExit();
}

//空のステート
class emptyState implements IState{
  public void Update(int elapsedTime){};
  public void Render(){};
  public void OnEnter(){};
  public void OnExit(){};
}
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
//プレイヤーのステートマシンなどについて記述

//プレイヤーのアクションを管理するステートマシン
class PlayerStateMAchine extends StateChanger{
  MainStateMachine MainStateMachine;//メインステートマシン（親を参照するためのもの）

  int listIndex = 0;//どの子を選択しようとしているのかというindex

  String MyName;//自分の名前
  String nextPlayerName;//次の順番のプレイヤーの名前

  List<String> cardList = new ArrayList<String>();//所持しているカードのリスト

  //コンストラクタ メインステートマシンの実体と次のプレイヤーの名前
  public PlayerStateMAchine(MainStateMachine tmp,String tmp_MyName,String tmp_nextPlayerName){
    super();

    MainStateMachine = tmp;
    MyName = tmp_MyName;
    nextPlayerName = tmp_nextPlayerName;

    cardList.add("card1");
    cardList.add("card2");
    cardList.add("card3");
    cardList.add("card4");



    Add("dice1" ,new Dice(this));
    Add("choiceCard" ,new ChoiceCard(this));
    Add("tradeWithOther" ,new TradeWithOther(this));
    Add("useCard" ,new UseCard(this));
    Add("development" ,new Development(this));

  }

  //子の主導権を消し、自分に主導権が移る.子が呼ぶ
  public void ChildOFF(){
    Change("empty");
    childOn = false;
  }

  //次このステートマシンの親に当たるメインステートマシンの関数を呼んで、次のプレイヤーにステートを渡す
  public void ChangetoNextPlayer(){
    MainStateMachine.Change(nextPlayerName);
  }

  //カードリストを返す
  public List<String> GetCardList(){
    return cardList;
  }

  public void Update(int elapsedTime){

    //子に主導権が移ってるならここでの操作は行わんようにっていうやつ
    if(childOn == true){
      mCurrentState.Update(elapsedTime);//子の呼び出し
    }else{
      if(keyPushJudge.GetJudge("a") == true){
        ChangetoNextPlayer();
      }
      if(keyPushJudge.GetJudge("z") == true){
        listIndex++;
        if(childList.size() == listIndex)listIndex = 0;
      }
      if(keyPushJudge.GetJudge("ENTER") == true){
        //println(childList.get(listIndex));
        Change(childList.get(listIndex));
      }
    }
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
  //PlayerStateMAchine playerStateMachine;//プレイヤーステートマシン（親の参照）

  //コンストラクタ
  public Dice(PlayerStateMAchine tmp_playerStateMachine){
    super(tmp_playerStateMachine);
  }

  public void Update(int elapsedTime){
    playerStateMachineChildOFF();//BACKSPACEで一つ戻る
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
  //PlayerStateMAchine playerStateMachine;//プレイヤーステートマシン（親の参照）
  List<String> cardList;
  int cardIndex = 0;//カード選択のためのindex

  //コンストラクタ
  public ChoiceCard(PlayerStateMAchine tmp_playerStateMachine){
    super(tmp_playerStateMachine);
  }

  public void Update(int elapsedTime){
    playerStateMachineChildOFF();//BACKSPACEで一つ戻る
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
    cardList = playerStateMachine.GetCardList();
  };
  public void OnExit(){};
}

//他プレイヤーとの交易
class TradeWithOther extends PlayerActionBase{
  //PlayerStateMAchine playerStateMachine;//プレイヤーステートマシン（親の参照）

  //コンストラクタ
  public TradeWithOther(PlayerStateMAchine tmp_playerStateMachine){
    super(tmp_playerStateMachine);
  }

  public void Update(int elapsedTime){
    playerStateMachineChildOFF();//BACKSPACEで一つ戻る

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
  //PlayerStateMAchine playerStateMachine;//プレイヤーステートマシン（親の参照）

  //コンストラクタ
  public UseCard(PlayerStateMAchine tmp_playerStateMachine){
    super(tmp_playerStateMachine);
  }

  public void Update(int elapsedTime){
    playerStateMachineChildOFF();//BACKSPACEで一つ戻る
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
  //PlayerStateMAchine playerStateMachine;//プレイヤーステートマシン（親の参照）

  //コンストラクタ
  public Development(PlayerStateMAchine tmp_playerStateMachine){
    super(tmp_playerStateMachine);
  }

  public void Update(int elapsedTime){
    playerStateMachineChildOFF();//BACKSPACEで一つ戻る

  };
  public void Render(){
    fill(50, 50, 50, 255);
    text("Development", 100, 100);

  };
  public void OnEnter(){};
  public void OnExit(){};
}
int mouseClickTorF = MOUSE_NOTCLICK;

//キーが押された瞬間をとらえるためのクラス
public class KeyPushJudge{
  int keypushA = TARGETKEY_RELEASED;
  HashMap<String, Integer > keyList= new HashMap<String, Integer>();
  HashMap<String, Integer > keyListTrigger= new HashMap<String, Integer>();
  
  //コンストラクタ
  public KeyPushJudge(){
    keyList.put("a", TARGETKEY_RELEASED);
    keyList.put("z", TARGETKEY_RELEASED);
    keyList.put("x", TARGETKEY_RELEASED);
    keyList.put("c", TARGETKEY_RELEASED);
    keyList.put("ENTER", TARGETKEY_RELEASED);
    keyList.put("BACKSPACE", TARGETKEY_RELEASED);
    
    keyListTrigger.put("a", 0);
    keyListTrigger.put("z", 0);
    keyListTrigger.put("x", 0);
    keyListTrigger.put("c", 0);
    keyListTrigger.put("ENTER", 0);
    keyListTrigger.put("BACKSPACE", 0);
  }
  
  public void Update(){
    
    
    for (String tmp_key : keyList.keySet()) {
      keyList.put(tmp_key, TARGETKEY_RELEASED);
    }        

    //PRESSED の判定
    if(keyPressed==true){
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

      //各ノードに隣り合うエッジ・ノード・エリアのリストを作る
      {
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
      }

      for(int i=0;i<NodeNum;i++){
        println( node[i].nextNodeNumber );
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
  List<Integer> nextEdgeNumber = new ArrayList<Integer>();//隣り合ったエッジの番号を格納する配列.長さは不定
  List<Integer> nextAreaNumber = new ArrayList<Integer>();//隣り合ったエリアの番号を格納する配列.長さは不定
  List<Integer> nextNodeNumber = new ArrayList<Integer>();//隣り合ったノードの番号を格納する配列.長さは不定

  //道路を敷く.引数は取った人の識別番号
  public void BuildRoad(int tmp_playerNumber){
    if(playerNumber == 0){
      playerNumber = tmp_playerNumber;
    }else{
      println("Edge -> ErrorBuildRoad");
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

//フィールドのノードクラス(都市を置く所)
class Node{
  int playerNumber = 0;//どのプレイヤーが保持している都市か.０なら未使用
  int CityLevel = 0;//都市のレベル.0:未使用,1:都市レベル1,2:都市レベル2
  List<Integer> nextEdgeNumber = new ArrayList<Integer>();//隣り合ったエッジの番号を格納する配列.長さは不定
  List<Integer> nextAreaNumber = new ArrayList<Integer>();//隣り合ったエリアの番号を格納する配列.長さは不定
  List<Integer> nextNodeNumber = new ArrayList<Integer>();//隣り合ったノードの番号を格納する配列.長さは不定


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
