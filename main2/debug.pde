//デバッグモード
class Debug extends IState{

  //コンストラクタ
  public Debug(){}
  public void Render(){
    fill(50, 50, 50, 255);
    textSize(25);
    text("Debug", 50, 50);
  }
}
