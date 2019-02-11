//import java.util.Map;
//import java.util.HashMap;
import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;

//メインステートマシン
int drawCount;//経過フレームを数える
MainStateMachine mainStateMachine;
KeyPushJudge keyPushJudge;
MAP map;

//画像
HashMap<String, PImage> ImageList_Area;
HashMap<String, PImage> ImageList_Number;

void setup() {

  size(800, 600, P2D);
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


void draw() {
  fill(250, 0, 255, 255);
  rect(0, 0, width, height);
  keyPushJudge.Update();//キーが押されたかどうかの判定

  //移行メイン処理
  drawCount++;

  mainStateMachine.Update(drawCount);
  mainStateMachine.Render();

  map.Render();

}
