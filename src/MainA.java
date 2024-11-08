
import java.io.*;
import java.util.*;

/*
 简易控制台文字地图
 操作方式：
 	方向数字+距离移动玩家：2为上1, 42为左2
代码结构: 
	launchGame()启动
		initMap()初始化地图
		clearLine(2000)控制台光标置底
		renderMap()渲染初始地图
		while()游戏循环
			inputHandle()阻塞式输入处理
			renderMap()刷新渲染地图
异常处理：
	顶级try-catch保证捕获所有异常
	内部所有异常软处理以保证游戏正常运行
	异常输出在LogErr
 */
public class MainA 
{
	String shapeSet = "～＆";
	public void setShapeSet(String shapeSet){ this.shapeSet = shapeSet; }
	int clearId = 0;
	int playerId = 1;
	//方向代码->坐标偏移 上左下右2486
	Map<Integer, int[]> directionMap = new HashMap<Integer, int[]>(){{
			put(2, new int[]{0, 1});
			put(4, new int[]{-1, 0});
			put(8, new int[]{0, -1});
			put(6, new int[]{1, 0});
		}};

	int size = 12;
	int totalSize = size * size;
	TextMap textMap;
	int[] player;

	public MainA(){
		try{
			launchGame();
		}catch (Exception e){
			LogErr("游戏主循环发生异常: ", e);
		}
	}

	public void launchGame(){
		initMap();

		//将光标置底，控制台上限2000行
		clearLine(2000);

		renderMap();

		while (true){
			inputHandle();

			renderMap();
		}
	}


	//初始化地图
	public void initMap(){
		//实例化地图
		textMap = new TextMap(clearId, size);

		//实例化玩家
		player = new int[3];
		player[2] = playerId;
		textMap.addObject(player);
	}


	//渲染地图
	public void renderMap(){
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

	//处理输入
	public void inputHandle(){
		//分割指令
		String text = getInput();
		String[] cmds = text.split(" ");

		//指令处理
		//移动主角: 2为上移1格 42为左移2格
		try{
			int direction = Integer.parseInt("" + text.charAt(0));
			int distance = text.length()==1?1: Integer.parseInt(text.substring(1, text.length()));
			movePlayer(direction, distance);
		}catch (Exception e){
			LogErr("移动指令异常: ", e);
		}
//		直接设置主角坐标
//		if (cmds.length > 1){
//			int x = Integer.parseInt(cmds[0]);
//			int y = Integer.parseInt(cmds[1]);
//			//xy转索引
//			x -= 1;y -= 1;
//			setPlayerPos(x, y);
//			Log(String.format("主角位置更新：x:%d, y:%d", x, y));
//		}
	}

	//从id获取渲染形状
	private String getShapeById(int id)
	{
		String ret="？";
		try{
			ret = "" + shapeSet.charAt(id);
		}catch (Exception e){
			LogErr(String.format("返回形状失败，自动转为%s, 不存在的形状id:%d", ret, id), e);
		}
		return ret;
	}

	//设置玩家位置
	public void setPlayerPos(int x, int y){
		if (x < 0 || x >= size || y < 0 || y >= size)
			LogErr(String.format("设置位置失败, xy坐标越界:%d,%d", x, y), new Exception());
		else{
			player[0] = x;player[1] = y;
		}
	}
	
	//移动玩家，指定方向数字与距离
	public void movePlayer(int directionNumber, int distance) throws NoSuchElementException{
		if (!directionMap.containsKey(directionNumber)){
			throw new NoSuchElementException("方向表没有方向数字:"+directionNumber);
		}
		int[] coordOffset = directionMap.get(directionNumber).clone();
		coordOffset[0] *= distance;
		coordOffset[1] *= distance;
		setPlayerPos(player[0]+coordOffset[0], player[1]+coordOffset[1]);
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
