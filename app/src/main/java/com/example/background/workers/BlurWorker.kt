package com.example.background.workers

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.background.KEY_IMAGE_URI

private const val TAG = "BlurWorker"
class BlurWorker(context: Context, workerParams: WorkerParameters):Worker(context,workerParams) {

    override fun doWork(): Result {
        val appContext = applicationContext

        val resourceUri = inputData.getString(KEY_IMAGE_URI)


        makeStatusNotification("Blurring img", appContext)

        sleep()

        return  try {

            if (TextUtils.isEmpty(resourceUri)){

                Log.e(TAG, "Invalid input uri")

                throw IllegalArgumentException("Invalid input uri")

            }

            val pic = BitmapFactory.decodeStream(

                appContext
                    .contentResolver
                    .openInputStream(Uri.parse(resourceUri))
            )

            val output = blurBitmap(pic,appContext)

            val outputUri = writeBitmapToFile(appContext, output)

            makeStatusNotification("Output is $outputUri", appContext)

            val outputData = workDataOf(KEY_IMAGE_URI to outputUri.toString())

            Result.success(outputData)

        }catch (throwable: Throwable){

            Log.e(TAG, " Error applying blur")
            throwable.printStackTrace()
            Result.failure()
        }





    }
}