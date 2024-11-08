# ConsoleTextMap V0.2.0
简易控制台文字地图

# 当前内容：
~~~
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

