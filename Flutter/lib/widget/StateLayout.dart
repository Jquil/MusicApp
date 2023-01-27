import 'package:music/utils/CommonUtil.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:font_awesome_flutter/font_awesome_flutter.dart';

class StateLayout{

  static final double _size = 120.0;

  // 加载widget
  static Widget widget_loading(BuildContext context){
    return Stack(
      children: [
        Container(
          width: double.infinity,
          color: CommonUtil.getStateLayoutBG(context),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
              Image.asset("assets/images/loading.gif",width: _size)
            ],
          ),
        )
      ],
    );
  }

  // 空widget
  static Widget widget_empty(BuildContext context){
    return Stack(
      children: [
        Container(
          width: double.infinity,
          color: CommonUtil.getStateLayoutBG(context),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
              FaIcon(FontAwesomeIcons.stickyNote,size: _size),
              Text("没有找到数据~")
            ],
          ),
        )
      ],
    );
  }

  // 错误widget
  static Widget widget_error(BuildContext context){
    return Stack(
      children: [
        GestureDetector(
          //onTap: widget.reloadData?.call(),
          child:
          Container(
            width: double.infinity,
            color: CommonUtil.getStateLayoutBG(context),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              crossAxisAlignment: CrossAxisAlignment.center,
              children: [
                FaIcon(FontAwesomeIcons.exclamationCircle,size: _size,color: Colors.red),
                Text("发生未知错误，请点击重试~",style: TextStyle(color: Colors.red))
              ],
            ),
          ),
        )

      ],
    );
  }
}