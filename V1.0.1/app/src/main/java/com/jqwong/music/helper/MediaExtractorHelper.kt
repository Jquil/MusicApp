package com.jqwong.music.helper

import android.media.MediaExtractor
import android.media.MediaFormat
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import kotlin.experimental.or

class MediaExtractorHelper {
    companion object{
        fun aac(video:String,save:String):Pair<Boolean,String>{
            fun getAudioType(aacType:Int):Int{
                // AAC HE V2 = AAC LC + SBR + PS
                // AAV HE = AAC LC + SBR
                // 所以无论是 AAC_HEv2 还是 AAC_HE 都是 AAC_LC
                return when(aacType){
                    0,2,3 -> {
                        aacType+1
                    }

                    1,4,28->{
                        2
                    }

                    else -> {
                        2
                    }
                }
            }
            fun getSampleRateIndex(frequency:Int,aacType:Int):Int{
                var _freq = frequency
                val array = listOf(96000, 88200, 64000, 48000, 44100, 32000,
                    24000, 22050, 16000, 12000, 11025, 8000, 7350)
                if (aacType == 28 || aacType == 4){
                    _freq /= 2;
                }
                for (i in 0 until 13){
                    if (_freq == array[i])
                        return i
                }
                return 4 //默认是44100
            }
            try {
                val file = File(save)
                if(file.exists())
                    file.delete()
                val extractor = MediaExtractor()
                extractor.setDataSource(video)
                for(i in 0 until extractor.trackCount){
                    val format = extractor.getTrackFormat(i)
                    format.getString(MediaFormat.KEY_MIME).let {
                        if(!it.isNullOrEmpty() && it.startsWith("audio/")){
                            extractor.selectTrack(i)
                            val buffer = ByteBuffer.allocate(1024*100)
                            val fos = FileOutputStream(save)
                            while (true){
                                val len = extractor.readSampleData(buffer,0)
                                if(len <= 0)
                                    break
                                val buffer2 = ByteArray(len)
                                buffer.get(buffer2)
                                val aacProfile = format.getInteger(MediaFormat.KEY_AAC_PROFILE)
                                var chCount = format.getInteger(MediaFormat.KEY_CHANNEL_COUNT)
                                val sampleRate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE)
                                if (aacProfile == 28)
                                    chCount /= 2
                                val header = ByteArray(7)
                                val atsLen = len+7
                                header[0] = 0xff.toByte()
                                header[1] = 0xf0.toByte()
                                header[1] = header[1] or (0 shl 3)
                                header[1] = header[1] or (0 shl 1)
                                header[1] = header[1] or 1
                                header[2] = ((getAudioType(aacProfile) - 1) shl 6).toByte()
                                header[2] = header[2] or (getSampleRateIndex(sampleRate,aacProfile) and 0x0f).shl(2).toByte()
                                header[2] = header[2] or (0 shl 1)
                                header[2] = header[2] or (chCount and 0x04).shl(2).toByte()
                                header[3] = (chCount and 0x03 shl 6).toByte()
                                header[3] = header[3] or (0 shl 5)
                                header[3] = header[3] or (0 shl 4)
                                header[3] = header[3] or (0 shl 3)
                                header[3] = header[3] or (0 shl 2)
                                header[3] = header[3] or (atsLen and 0x1800).shr(11).toByte()
                                header[4] = ((atsLen and 0x7f8) shr 3).toByte()
                                header[5] = ((atsLen and 0x7) shl 5).toByte()
                                header[5] = header[5] or 0x1f
                                header[6] = 0xfc.toByte()
                                fos.write(header)
                                fos.write(buffer2)
                                extractor.advance()
                            }
                            extractor.release()
                            fos.flush()
                            fos.close()
                            return Pair(true,"ok")
                        }
                    }

                }
            }
            catch (e:Exception){
                return Pair(false,e.message.toString())
            }
            return Pair(false,"failed")
        }
    }
}