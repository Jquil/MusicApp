import 'package:music/utils/BaseStatus.dart';
import 'package:music/utils/CommonUtil.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

import 'StateLayout.dart';
abstract class BaseLayout extends StatefulWidget{

  @override
  State createState() {
    return getState();
  }

  BaseLayoutState getState();

}

abstract class BaseLayoutState<T extends BaseLayout> extends State<T> {
  String title = "";
  BaseStatus? status;
  Widget? child;
  BaseStatus initStatus();
  Widget getChild(BuildContext context);
  void renderFinish();
  void initState2();

  var widgets = <Widget>[];

  @override
  void initState() {
    super.initState();
    status = initStatus();
    //child  = getChild();
    initState2();
  }

  @override
  Widget build(BuildContext context) {
    WidgetsBinding.instance?.addPostFrameCallback((timeStamp) {
      // render finish
      renderFinish();
    });
    widgets = [];
    widgets.add(getChild(context));
    Widget? statusWidget;
    switch(status){
      case BaseStatus.success:
        while(widgets.length != 1){
          widgets.removeLast();
        }
        break;
      case BaseStatus.loading:
        statusWidget = StateLayout.widget_loading(context);
        break;
      case BaseStatus.empty:
        statusWidget = StateLayout.widget_empty(context);
        break;
      case BaseStatus.error:
        statusWidget = StateLayout.widget_error(context);
        break;
    }
    if(statusWidget != null){
      widgets.add(statusWidget);
    }
    return Scaffold(
      resizeToAvoidBottomInset: false,
      appBar: AppBar(
        title: CommonUtil.title(text: title,size: 24,color: Theme.of(context).brightness == Brightness.dark ? Colors.white : Colors.black),
        backgroundColor: Colors.transparent,
        elevation: 0,
      ),
      body: Stack(
        children: widgets,
      ),
    );
  }


}
