import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';

class CommonAppBar extends StatefulWidget{

  _CommonAppBarState state = _CommonAppBarState();

  String ?title;
  CommonAppBar({ @required this.title });

  @override
  State createState() {
    return state;
  }
}

class _CommonAppBarState extends State<CommonAppBar>{

  String ?title;

  @override
  Widget build(BuildContext context) {
    return AppBar(
      title: Text(title!),
      elevation: 0,
      backgroundColor: Colors.transparent,
    );

    void setTitle(newTitle){
      setState(() {
        title = newTitle;
      });
    }
  }

  @override
  void initState() {
    super.initState();
    title = widget.title;
  }
}