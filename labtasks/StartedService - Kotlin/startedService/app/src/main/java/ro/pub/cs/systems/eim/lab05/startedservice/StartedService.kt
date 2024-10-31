package ro.pub.cs.systems.eim.lab05.startedservice

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log


class ProcessingThread(private val context: Context) : Thread() {

    override fun run() {
        while (true) {
            Log.d(Constants.TAG, "Thread.run() was invoked, PID: " + android.os.Process.myPid() + " TID: " + android.os.Process.myTid());
            sendMessage(Constants.MESSAGE_STRING)
            sleep()
            sendMessage(Constants.MESSAGE_INTEGER)
            sleep()
            sendMessage(Constants.MESSAGE_ARRAY_LIST)
            sleep()
        }
    }

    private fun sleep() {
        try {
            Thread.sleep(Constants.SLEEP_TIME)
        } catch (interruptedException: InterruptedException) {
            interruptedException.printStackTrace()
        }
    }

    private fun sendMessage(messageType: Int) {
        val intent = Intent().apply {
            when (messageType) {
                Constants.MESSAGE_STRING -> {
                    action = Constants.ACTION_STRING
                    putExtra(Constants.DATA, Constants.STRING_DATA)
                }

                Constants.MESSAGE_INTEGER -> {
                    action = Constants.ACTION_INTEGER
                    putExtra(Constants.DATA, Constants.INTEGER_DATA)

                }

                Constants.MESSAGE_ARRAY_LIST -> {
                    action = Constants.ACTION_ARRAY_LIST
                    putExtra(Constants.DATA, Constants.ARRAY_LIST_DATA)
                }
            }
        }
        context.sendBroadcast(intent)
    }

}

class StartedService : Service() {


    private val TAG = "ForegroundService"
    private val CHANNEL_ID = "11"
    private val CHANNEL_NAME = "ForegroundServiceChannel"

    @SuppressLint("ObsoleteSdkInt")
    private fun dummyNotification() {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            manager.createNotificationChannel(channel)
        }

        val notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(applicationContext, CHANNEL_ID).build()
        } else {
            Notification.Builder(applicationContext).build()
        }

        startForeground(1, notification)
    }



    override fun onCreate() {
        super.onCreate()
        Log.d(Constants.TAG, "onCreate() method was invoked")
    }

    override fun onBind(intent: Intent): IBinder? {
        Log.d(Constants.TAG, "onBind() method was invoked")
        return null
    }

    override fun onUnbind(intent: Intent): Boolean {
        Log.d(Constants.TAG, "onUnbind() method was invoked")
        return true
    }

    override fun onRebind(intent: Intent) {
        Log.d(Constants.TAG, "onRebind() method was invoked")
    }

    override fun onDestroy() {
        Log.d(Constants.TAG, "onDestroy() method was invoked")
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(Constants.TAG, "onStartCommand() method was invoked")
        // TODO: exercise 5 - implement and start the ProcessingThread
        Log.d(Constants.TAG, "Thread.run() was invoked, PID: " + android.os.Process.myPid() + " TID: " + android.os.Process.myTid());
        val processingThread = ProcessingThread(this)
        processingThread.start()

        dummyNotification()

        return START_REDELIVER_INTENT
    }
}
