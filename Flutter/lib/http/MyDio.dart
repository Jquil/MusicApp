import 'package:dio/dio.dart';

class MyDio{

  static final MyDio instance = MyDio._internal();

  static final String _COOKIE  = "_ga=GA1.2.291993560.1625886876; _gid=GA1.2.445012141.1625886876; Hm_lvt_cdb524f42f0ce19b169a8071123a4797=1625903943,1625967386; Hm_lpvt_cdb524f42f0ce19b169a8071123a4797=1625967696; kw_token=IP4FK471YEF",
                      _CSRF    = "IP4FK471YEF",
                      _REFERER = "https://www.kuwo.cn/";

  static final _option = BaseOptions(
    baseUrl: '',
    connectTimeout: 60000,
    receiveTimeout: 60000,
    headers: {
      "COOKIE":_COOKIE,
      "CSRF":_CSRF,
      "REFERER":_REFERER
    },
    responseType: ResponseType.json
  );

  static Options option1 = Options(
      responseType: ResponseType.bytes
      );

  static final Dio dio = Dio(_option);

  factory MyDio() => instance;

  MyDio._internal(){
    // todo
  }


}