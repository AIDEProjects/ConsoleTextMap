
import java.io.*;
import java.util.*;

/*
 简易控制台文字地图
 x y回车改变主角位置
 */
public class MainA 
{
	String shapes = "～＆";
	int clearId = 0;
	int playerId = 1;

	int size = 12;
	int totalSize = size * size;
	TextMap textMap;
	int[] player;

	public MainA(){
		try{
			gameLoop();
		}catch (Exception e){
			LogErr("游戏主循环发生异常: ", e);
		}
	}

	public void gameLoop(){
		initGame();

		//将光标置底，控制台上限2000行
		clearLine(2000);

		renderMap();

		while (true){
			inputHandle();

			renderMap();
		}
	}


	public void initGame(){
		//实例化地图
		textMap = new TextMap(clearId, size);

		//实例化玩家
		player = new int[3];
		player[2] = playerId;
		textMap.addObject(player);
	}


	public void renderMap(){
		//渲染地图
		String mapRenderer="", lineRenderer="";
		int[] mapData = textMap.getMap();
		for (int i=0;i < totalSize;i++){
			int x = i % size;
			//换行时
			if (x == 0 && i != 0){
				//从下往上绘制
				mapRenderer = "\n" + lineRenderer + mapRenderer;
				lineRenderer = "";
			}
			//从左到右绘制
			lineRenderer += getShapeById(mapData[i]);
			if (i == totalSize - 1){
				//补上最后一行
				mapRenderer = lineRenderer + mapRenderer;
			}
		}
		Log(mapRenderer);
	}


	public void inputHandle(){
		//获取指令列表
		String text = getInput();
		String[] cmds = text.split(" ");

		//指令处理
		if (cmds.length > 1){
			int x = Integer.parseInt(cmds[0]);
			int y = Integer.parseInt(cmds[1]);
			//设置主角
			setPos(x, y, playerId);
			Log(String.format("主角位置更新：x:%d, y:%d", x, y));
		}
	}


	//从id获取渲染形状
	private String getShapeById(int id)
	{
		String ret="？";
		try{
			ret = "" + shapes.charAt(id);
		}catch (Exception e){
			LogErr(String.format("返回形状失败，自动转为%s, 不存在的形状id:%d", ret, id), e);
		}
		return ret;
	}

	public void setPos(int x, int y, int id){
		//xy转索引
		x -= 1;y -= 1;
		if (x < 0 || x >= size || y < 0 || y >= size)
			LogErr(String.format("设置位置失败, xy坐标越界:%d,%d", x, y), new Exception());
		else{
			player[0] = x;player[1] = y;
		}
	}


	//统一打印方法
	public void Log(Object str){
		System.out.println("" + str);
	}
	public void LogErr(String msg, Exception e){
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		Log(msg + "\n" + sw);
	}

	//统一获取输入方法
	public String getInput(){
		Scanner scan = new Scanner(System.in);
		String text="";
		if (scan.hasNextLine()){
			text = scan.nextLine();
		}
		return text;
	}

	//清空控制台屏幕
	public void clearLine(int line){
		String lines = "";
		for (int i=0;i < line - 1;i++){
			lines += "\n";
		}
		Log(lines);
	}


	/*
	 文字地图
	 属性: 
	 包含清屏文字id表
	 每层物体id组
	 方法: 
	 可获取实际渲染id表
	 */
	public class TextMap {
		private int clearId;//存储清屏id
		private final int[] map;//用于存储实际地图id表

		/*
		 物体集：int[]为物体xyid引用
		 以及获取添加移除操作方法
		 */
		private final List<int[]> objectSet;
		public int[] getObject(int index){ 
			int[] ret = null;
			try{
				ret = objectSet.get(index);
			}catch (Exception e){}
			return ret;
		}
		public void addObject(int[] player) {
			if (!objectSet.contains(player))
				objectSet.add(player);
			else
				LogErr("重复添加物体obj: ", new Exception());
		}
		public void removeObject(int index){
			try{
				objectSet.remove(index);
			}catch (Exception e){
				LogErr("移除物体自index出错: ", e);
			}
		}
		public void removeObject(int[] obj){
			try{
				objectSet.remove(obj);
			}catch (Exception e){
				LogErr("移除物体自obj出错: ", e);
			}
		}


		//初始化地图
		public TextMap(int clearId, int size){
			this.clearId = clearId;
			map = new int[size * size];
			objectSet = new ArrayList<int[]>();
		}

		//获取计算后实际地图
		public int[] getMap(){
			//清屏
			for (int i=0;i < map.length;i++){
				map[i] = clearId;
			}
			//将物体渲染到地图上
			for (int i=0;i < objectSet.size();i++){
				int[] obj = objectSet.get(i);
				int x = obj[0];
				int y = obj[1];
				int id = obj[2];
				//二维坐标转一维
				int coord = x + y * size;
				map[coord] = id;
			}
			return map;
		}
	}
}
