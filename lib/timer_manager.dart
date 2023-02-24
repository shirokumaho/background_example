import 'package:flutter/services.dart';

class TimerManager {
  static const _channel = MethodChannel('dev.sample/timer_manager');

  static Future<void> startTimer() async =>
    await _channel.invokeMethod('TimerManager.startTimer', ["6001"]);
    
  static Future<bool> stopTimer() async =>
    await _channel.invokeMethod('TimerManager.stopTimer');

  static Future<int> getCount() async =>
    await _channel.invokeMethod('TimerManager.getCount');
}
