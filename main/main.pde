//import java.util.Map;
//import java.util.HashMap;
import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;

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

void settings() {
  size(FIELD_LENGTH_X, FIELD_LENGTH_Y, P2D);
}
void setup() {

  noStroke();
  frameRate(60);

  background(250);
  colorMode(HSB);
  textSize(30);
  init();
}

//初期化
void init(){
   PFont font = loadFont("YuGothic-Regular-48.vlw");
   textFont(font, 48);
   imageMode(CENTER);

   drawCount = 0;//経過フレームを数える
   messageBox = new MessageBox();

   fieldInfomation = new FieldInfomation();
   mainStateMachine = new MainStateMachine();
   keyPushJudge = new KeyPushJudge();




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


  fieldInfomation.SetNodeOwner(0, 1, 2);
  fieldInfomation.SetNodeOwner(3, 1, 1);
  fieldInfomation.SetNodeOwner(6, 1, 2);
  fieldInfomation.SetNodeOwner(15, 1, 2);
  fieldInfomation.SetNodeOwner(30, 1, 1);
  fieldInfomation.SetNodeOwner(53, 1, 2);
  fieldInfomation.SetNodeOwner(1, 2, 2);
  fieldInfomation.SetNodeOwner(5, 2, 1);
  fieldInfomation.SetNodeOwner(11, 2, 2);
  fieldInfomation.SetNodeOwner(41, 2, 2);
  fieldInfomation.SetNodeOwner(43, 2, 1);
  fieldInfomation.SetNodeOwner(49, 2, 2);
  fieldInfomation.SetNodeOwner(10, 3, 2);
  fieldInfomation.SetNodeOwner(14, 3, 1);
  fieldInfomation.SetNodeOwner(20, 3, 2);
  fieldInfomation.SetNodeOwner(40, 3, 2);
  fieldInfomation.SetNodeOwner(45, 3, 1);
  fieldInfomation.SetNodeOwner(50, 3, 2);
}


void draw() {
  fill(0, 0, 155);//HSB
  rect(-10, -10, width+20, height+20);
  keyPushJudge.Update();//キーが押されたかどうかの判定

  //移行メイン処理
  drawCount++;

  mainStateMachine.Update(drawCount);

  fieldInfomation.Render();
  mainStateMachine.Render();

  messageBox.Render();


}
