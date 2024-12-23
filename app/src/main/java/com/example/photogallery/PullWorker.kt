package com.example.photogallery

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.*
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

import kotlinx.coroutines.flow.first

private const val TAG = "PollWorker"


class PullWorker (
  private val  context: Context,
   private val  workerParams: WorkerParameters,

) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val preferenceRepository = PreferenceRepository.get()
        val photoRepository = PhotoRepository()

        val query = preferenceRepository.storeQuery.first()
        val lastResultId = preferenceRepository.lastResultId.first()

        if (query.isEmpty()) {
            Log.i(TAG, "No saved query, finishing early.")
            return Result.success()
        }

        return try {
            val items = photoRepository.searchPhoto(query)
            if (items.isNotEmpty()) {
                val newResultId = items.first().id
                if (newResultId == lastResultId) {
                    Log.i(TAG, "Still have the same result: $newResultId")
                } else {
                    Log.i(TAG, "Got a new result: $newResultId")
                    preferenceRepository.setLastResultId(newResultId)
                    notifyUser()
                }
            }
            Result.success()
        } catch (ex: Exception) {
            Log.e(TAG, "Background update failed", ex)
            Result.failure()
        }
    }
    private fun notifyUser() {
        val newIntent = MainActivity.newIntent(context)
        val resources = context.resources
        val  pendingIntent = PendingIntent.getActivity(context,0,newIntent,PendingIntent.FLAG_IMMUTABLE)
        val notification = NotificationCompat
            .Builder(context, NOTIFICATION_CHANNEL_ID)
            .setTicker(resources.getString(R.string.new_pictures_title))
            .setSmallIcon(android.R.drawable.ic_menu_report_image)
            .setContentTitle(resources.getString(R.string.new_pictures_title))
            .setContentText(resources.getString(R.string.new_pictures_text))
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        with( NotificationManagerCompat.from(context)){
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED){
                Log.e(TAG,"Not Permission granted")
                return@with
            }
            notify(0,notification)
        }

    }
}
