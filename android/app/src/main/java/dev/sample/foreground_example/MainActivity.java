package dev.sample.foreground_example;

import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.util.Log;
import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.dart.DartExecutor;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import java.util.ArrayList;
import kotlin.Metadata;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/**
 * パッケージを変更する場合は以下のファイルに記載されているパッケージ名も変更してください。
 * android\app\src\main\AndroidManifest.xml
 * android\app\src\debug\AndroidManifest.xml
 * android\app\build.gradle
 */
public final class MainActivity extends FlutterActivity {
    private static final String TAG = "SampleService_Activity";

    /**
     * Dartコードとの連携処理
     * @param flutterEngine
     */
    @Override
    public void configureFlutterEngine(@NotNull FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);
        DartExecutor dartExec = flutterEngine.getDartExecutor();
        // Dartと連携するMethodChannel
        MethodChannel channel = new MethodChannel(dartExec.getBinaryMessenger(), "dev.sample/timer_manager");
        channel.setMethodCallHandler((MethodCallHandler)(new MethodCallHandler() {
            public final void onMethodCall(@NotNull MethodCall call, @NotNull Result result) {
                // チャネル呼び出し時のDartからの引数
                ArrayList args = (ArrayList)call.arguments();
                // 指定されたメソッド名に応じた処理の振り分け
                String methodName = call.method;
                switch(methodName) {
                    case "TimerManager.stopTimer":
                        MainActivity.this.stopTimer(MainActivity.this.getContext(), result);
                        break;
                    case "TimerManager.getCount":
                        MainActivity.this.getCount(MainActivity.this.getContext(), result);
                        break;
                    case "TimerManager.startTimer":
                        MainActivity.this.startTimer(MainActivity.this.getContext(), args,result);
                        break;
                    default :
                        result.notImplemented();
                }
            }
        }));
    }

    /**
     * サービス開始
     * @param context
     * @param args　Dart側から渡された引数
     * @param result Dartに戻す値を格納
     */
    private static void startTimer(Context context, ArrayList args, Result result) {
        Intrinsics.checkNotNull(args);
        String argsParam = (String)args.get(0);
        Log.d(MainActivity.TAG, "Start Timer! param = " + argsParam);
        if (VERSION.SDK_INT >= 26) {
            context.startForegroundService(new Intent(context, SampleService.class));
        }
        Log.d(MainActivity.TAG, "file path = " + context.getFilesDir());
        result.success(true);
    }

    /**
     * サービス停止
     * @param context
     * @param result Dartに戻す値を格納
     */
    private static void stopTimer(Context context, Result result) {
        Log.d(MainActivity.TAG, "Stop Timer!");
        Intent intent = new Intent(context, SampleService.class);
        SampleService.stopTimer();
        context.stopService(intent);
        result.success(true);
    }

    /**
     * 現在のカウント数を取得
     * @param context
     * @param result Dartに戻す値を格納
     */
    private static void getCount(Context context, Result result) {
        Log.d(MainActivity.TAG, "getCount!");
        int countNum = SampleService.getCount();
        result.success(countNum);
    }
}
