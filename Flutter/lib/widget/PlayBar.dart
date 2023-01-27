import 'package:audioplayers/audioplayers.dart';
import 'package:cached_network_image/cached_network_image.dart';
import 'package:fluttertoast/fluttertoast.dart';
import 'package:music/common/Constant.dart';
import 'package:music/provider/SongProvider.dart';
import 'package:music/router/MyRouter.dart';
import 'package:music/utils/CommonUtil.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/painting.dart';
import 'package:font_awesome_flutter/font_awesome_flutter.dart';
import 'package:music/utils/MyAudio.dart';
import 'package:provider/src/provider.dart';
class PlayBar extends StatelessWidget{

  @override
  Widget build(BuildContext context) {
    var provider = context.watch<SongProvider>();
    return GestureDetector(
      onTap: (){
        if(provider.song == null){
          Fluttertoast.showToast(msg: "欸,没有发现播放的歌曲哟~");
        }
        else{
          Navigator.pushNamed(context, MyRouter.PAGE_SONG);
        }
      },
      child: Container(
        width: double.infinity,
        color: Colors.transparent,
        height: 66,
        child: Column(
          children: [
            // 播放进度条
            SizedBox(
              width: double.infinity,
              height: 6.0,
              child: LinearProgressIndicator(
                value: provider.progress,
                backgroundColor: Colors.grey.shade300,
                valueColor: AlwaysStoppedAnimation<Color>(Colors.blue),
              ),
            ),
            // Container可用高度为：66 - 6 - 5 - 5 = 50
            Container(
              margin: EdgeInsets.only(left: 15,right: 15,top: 5,bottom: 5),
              child: Row(
                children: [
                  ClipRRect(
                    borderRadius: BorderRadius.circular(10),
                    child: provider.song == null ? CachedNetworkImage(imageUrl:  Constant.PICURL[0],width: 44,height: 44,fit: BoxFit.cover) : Image.network(provider.song!.pic,width: 44,height: 44,fit: BoxFit.cover),
                  ),
                  Expanded(
                      child: Container(
                        margin: EdgeInsets.only(left: 10,right: 15),
                        child: Row(
                          children: [
                            Expanded(
                                child: Column(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children: [
                                    CommonUtil.title(text: provider.song == null ? "none~" : CommonUtil.subString(provider.song!.name, 10),size: 18),
                                    CommonUtil.content(text: provider.song == null ? "none~" : CommonUtil.subString(provider.song!.artist, 5),size: 12),
                                  ],
                                )),
                            IconButton(
                                onPressed: (){
                                  provider.state == AudioPlayerState.PAUSED ? MyAudio.resume(context) : MyAudio.pause(context);
                                },
                                icon: Icon(provider.state == AudioPlayerState.PAUSED ? Icons.play_circle_outline : Icons.pause_circle_outline,color: CommonUtil.getStateLayoutBG2(context),size: 36,)),
                          ],
                        ),
                      )
                  )
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}