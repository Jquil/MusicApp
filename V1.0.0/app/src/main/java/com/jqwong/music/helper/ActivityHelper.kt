package com.jqwong.music.helper

import android.app.Activity
import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException


class ActivityHelper {
    companion object{
        fun current(): Activity? {
            try {
                val activityThreadClass = Class.forName("android.app.ActivityThread")
                val activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(
                    null
                )
                val activitiesField: Field = activityThreadClass.getDeclaredField("mActivities")
                activitiesField.setAccessible(true)
                val activities = activitiesField.get(activityThread) as Map<*, *>
                for (activityRecord in activities.values) {
                    val activityRecordClass: Class<*> = activityRecord!!.javaClass
                    val pausedField: Field = activityRecordClass.getDeclaredField("paused")
                    pausedField.setAccessible(true)
                    if (!pausedField.getBoolean(activityRecord)) {
                        val activityField: Field = activityRecordClass.getDeclaredField("activity")
                        activityField.setAccessible(true)
                        return activityField.get(activityRecord) as Activity?
                    }
                }
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
            } catch (e: NoSuchMethodException) {
                e.printStackTrace()
            } catch (e: NoSuchFieldException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
            return null
        }
    }
}