
import 'dart:async';

import 'package:background_example/timer_manager.dart';
import 'package:flutter/material.dart';
import 'package:fluttertoast/fluttertoast.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
        useMaterial3: true,
        splashFactory: InkSparkle.splashFactory,
      ),
      home: const MyHomePage(title: 'Flutter Demo Home Page'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({Key? key, required this.title}) : super(key: key);

  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  // 画面に表示する値
  int count = 0;

  // 前回の値
  int precount = 0;

  @override
  void initState() {
    super.initState();

    // 1秒間隔で実行
    Timer.periodic(
      Duration(seconds: 1),
      (Timer timer) {
          // ネイティブサービス（MainActivity経由）から値を取得しアプリ側に設定
          applyServiceCount();

          // 前回の値と変更があったら
          if(precount != count){
            // 画面更新
            setState(() {});
            precount = count;
          }
      }
   );
    
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            Text('Count: $count', style: Theme.of(context).textTheme.headline3),
            ElevatedButton(
              onPressed: () => {
                TimerManager.startTimer(),
                Fluttertoast.showToast(msg: "計測を開始")
              },
              child: const Text('Start'),
            ),
            ElevatedButton(
              onPressed: () => {
                TimerManager.stopTimer(),
                Fluttertoast.showToast(msg: "計測を停止"),
              },
              child: const Text('Stop'),
            ),
          ],
        ),
      ),
    );
  }

  // ネイティブ側から値を取得
  void applyServiceCount(){
    // 非同期の取得となる
    Future future = TimerManager.getCount();
    // 取得したらcount変数に設定
    future.then((value) => count = value);
  }
}

