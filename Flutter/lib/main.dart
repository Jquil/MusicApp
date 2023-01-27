import 'package:music/component/LeaderBoard.dart';
import 'package:music/component/Mine.dart';
import 'package:music/component/Search.dart';
import 'package:music/db/MyDB.dart';
import 'package:music/db/MySP.dart';
import 'package:music/provider/SongProvider.dart';
import 'package:music/provider/SongQueueProvider.dart';
import 'package:music/provider/SongSheetProvider.dart';
import 'package:music/utils/AppVersion.dart';
import 'package:music/utils/MyAudio.dart';
import 'package:music/utils/MyNotification.dart';
import 'package:music/widget/PlayBar.dart';
import 'package:flutter/material.dart';
import 'package:font_awesome_flutter/font_awesome_flutter.dart';
import 'package:music/router/MyRouter.dart';
import 'package:provider/provider.dart';

void main(){
  runApp(
      MultiProvider(
        providers: [
          ChangeNotifierProvider(create: (_) => SongSheetProvider()),
          ChangeNotifierProvider(create: (_) => SongQueueProvider()),
          ChangeNotifierProvider(create: (_) => SongProvider()),
        ],
        child: const MyApp(),
      ),
  );
  init();
}

Future<void> init() async{
  MyDB();
  MySP();
  MyAudio();
  MyNotification();
}

class MyApp extends StatelessWidget {

  const MyApp({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    listen(context);
    return MaterialApp(
      title: 'MusicApp',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      darkTheme: ThemeData.dark(),
      home: const MyHomePage(title: 'Flutter music Home Page'),
      routes: MyRouter.instance.getRoutes(context),
    );
  }

  void listen(BuildContext context) async{

      // 播放进度
      MyAudio.player.onAudioPositionChanged.listen((event) {
          context.read<SongProvider>().currentLrcIndex(event.inMilliseconds);
          context.read<SongProvider>().setProgress(event.inSeconds);
          context.read<SongProvider>().setTime(event.inMinutes,event.inSeconds);
      });

      // 播放完成
      MyAudio.player.onPlayerCompletion.listen((event) {
        context.read<SongQueueProvider>().next(context);
      });

      // 播放状态
      MyAudio.player.onPlayerStateChanged.listen((event) {
        context.read<SongProvider>().setPlayState(event);
      });
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({Key? key, required this.title}) : super(key: key);
  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage>{

  int _currentIndex = 1;

  final List<BottomNavigationBarItem> _bottomNavItems = [
    BottomNavigationBarItem(icon: FaIcon(FontAwesomeIcons.alignLeft),title: Container()),
    BottomNavigationBarItem(icon: FaIcon(FontAwesomeIcons.home),title: Container()),
    BottomNavigationBarItem(icon: FaIcon(FontAwesomeIcons.search),title: Container()),
  ];

  final _pages = [ LeaderBoard(),Mine(),Search(), ];

  bool isCheck1 = false;int isCheck2 = 0;

  @override
  Widget build(BuildContext context) {
    if(!isCheck1 && isCheck2 == 0){
      isCheck2++;
      AppVersion.check(context);
      isCheck1 = false;
    }
    return Scaffold(
      resizeToAvoidBottomInset: false,
      body: IndexedStack(
        index: _currentIndex,
        children: _pages,
      ),
      bottomSheet: PlayBar(),
      bottomNavigationBar: BottomNavigationBar(
        items: _bottomNavItems,
        currentIndex: _currentIndex,
        elevation: 0,
        type: BottomNavigationBarType.fixed,
        onTap: (index){
          if(index != _currentIndex){
            setState(() {
              _currentIndex = index;
            });
          }
        },
      ),
    );
  }
}
