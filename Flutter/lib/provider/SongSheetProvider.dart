import 'package:music/model/MSongSheet.dart';
import 'package:flutter/foundation.dart';

class SongSheetProvider with ChangeNotifier{

  List<MSongSheet> data = [];

  void initData(List<MSongSheet> sheets){
    data = sheets;
    notifyListeners();
  }

  void addData(MSongSheet sheet){
    data.add(sheet);
    notifyListeners();
  }

  void removeData(MSongSheet sheet){
    for(int i = 0; i < data.length; i++){
      if(data[i].id == sheet.id){
        data.remove(data[i]);
      }
    }
    notifyListeners();
  }


  void changeNums(int id,bool flag){
    data.forEach((el) {
      if(el.id == id){
        flag ? el.nums++ : el.nums--;
      }
    });
    notifyListeners();
  }


  void updateSheetName(int sheetId,String sheetName){
    data.forEach((el) {
      if(el.id == sheetId){
        el.name = sheetName;
        return;
      }
    });
    notifyListeners();
  }

  void deleteSheet(int sheetId){
    int index = -1;
    for(int i = 0; i < data.length; i++){
      if(data[i].id == sheetId){
          index = i;
          break;
      }
    }
    if(index != -1){
      data.removeAt(index);
    }
    notifyListeners();
  }


  Future<bool> isOnly(int sheetId) async{
    bool flag = false;
    data.forEach((el) {
      if(el.id == sheetId && el.only == 1){
        flag = true;
      }
    });
    return flag;
  }
}