import 'package:music/utils/BaseStatus.dart';
import 'package:music/utils/CommonUtil.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

import 'StateLayout.dart';
abstract class BaseLayout2 extends StatefulWidget{

  @override
  State createState() {
    return getState();
  }

  BaseLayoutState2 getState();

}

abstract class BaseLayoutState2<T extends BaseLayout2> extends State<T> {

  BaseStatus? status;
  Widget? child;
  BaseStatus initStatus();
  Widget getChild();
  void renderFinish();
  void initState2();
  void load();

  var widgets = <Widget>[];

  @override
  void initState() {
    super.initState();
    initState2();
    status = initStatus();
    //child  = getChild();
    //widgets.add(child!);
  }

  @override
  Widget build(BuildContext context) {
    WidgetsBinding.instance?.addPostFrameCallback((timeStamp) {
      // render finish
      renderFinish();
    });
    widgets = [];
    widgets.add(getChild());

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
      body: Stack(
        children: widgets,
      )
    );
  }


}
