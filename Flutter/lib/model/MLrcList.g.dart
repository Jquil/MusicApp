// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'MLrcList.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

MLrcList _$MLrcListFromJson(Map<String, dynamic> json) {
  return MLrcList(
    (json['lrclist'] as List<dynamic>)
        .map((e) => MLrcItem.fromJson(e as Map<String, dynamic>))
        .toList(),
  );
}

Map<String, dynamic> _$MLrcListToJson(MLrcList instance) => <String, dynamic>{
      'lrclist': instance.lrclist,
    };
