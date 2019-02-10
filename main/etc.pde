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
    keyList.put("d", TARGETKEY_RELEASED);
    keyList.put("ENTER", TARGETKEY_RELEASED);
    keyList.put("BACKSPACE", TARGETKEY_RELEASED);

    keyListTrigger.put("a", 0);
    keyListTrigger.put("z", 0);
    keyListTrigger.put("x", 0);
    keyListTrigger.put("c", 0);
    keyListTrigger.put("d", 0);
    keyListTrigger.put("ENTER", 0);
    keyListTrigger.put("BACKSPACE", 0);
  }

  void Update(){


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
  boolean GetJudge(String tmp){
    if(keyList.get(tmp) == TARGETKEY_PRESSED)return true;
    else if (keyList.get(tmp) == TARGETKEY_RELEASED)return false;
    else {println("keyJudgeError");return false;}
  }
}
void mousePressed() {
  mouseClickTorF = MOUSE_CLICK;
}
