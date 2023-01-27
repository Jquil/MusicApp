import 'package:flutter_local_notifications/flutter_local_notifications.dart';
import 'package:music/model/MSong.dart';

class MyNotification{

  static FlutterLocalNotificationsPlugin? fnp;

  static NotificationDetails? details;

  static MyNotification instance = MyNotification._internal();



  factory MyNotification() => instance;

  MyNotification._internal(){
    fnp = FlutterLocalNotificationsPlugin();

    var initializationSettingsAndroid = AndroidInitializationSettings('app_icon');
    var initializationSettingsIOS = IOSInitializationSettings();
    var initializationSettings = InitializationSettings(android: initializationSettingsAndroid, iOS: initializationSettingsIOS);
    fnp?.initialize(initializationSettings);

    var androidPlatformChannelSpecifics = AndroidNotificationDetails("8888", "小王音乐",priority: Priority.high,importance: Importance.max);
    var iOSPlatformChannelSpecifics = IOSNotificationDetails(presentSound: false);
    details = NotificationDetails(
        android: androidPlatformChannelSpecifics,
        iOS: iOSPlatformChannelSpecifics
    );
  }

  // 通知
  void show(MSong song) async{
    fnp?.show(
        0,
        '小王音乐',
        '当前正在播放：来自${song.artist}的"${song.name}"',
        details,
        payload: 'No Sound'
    );
  }
}