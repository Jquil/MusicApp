// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'MSong.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

MSong _$MSongFromJson(Map<String, dynamic> json) {
  return MSong(
    json['musicrid'] as String,
    json['artist'] as String,
    json['pic'] as String,
    json['album'] as String,
    json['artistid'] as int,
    json['albumpic'] as String,
    json['songTimeMinutes'] as String,
    json['pic120'] as String,
    json['name'] as String,
  );
}

Map<String, dynamic> _$MSongToJson(MSong instance) => <String, dynamic>{
      'musicrid': instance.musicrid,
      'artist': instance.artist,
      'pic': instance.pic,
      'album': instance.album,
      'artistid': instance.artistid,
      'albumpic': instance.albumpic,
      'songTimeMinutes': instance.songTimeMinutes,
      'pic120': instance.pic120,
      'name': instance.name,
    };
