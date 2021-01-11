package dev.entao.ui.util.app

import android.app.Service
import android.content.Intent

/**
 * onStartCommand返回 START_STICKY, 这个值可以改写
 * onStartCommand中解析参数, 回调onAction
 *
 * @author yangentao@gmail.com
 */
abstract class StickyService : BaseService() {

	override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
		PowerLock.acquire {
			onHandleIntent(intent)
		}
		return Service.START_STICKY
	}


	/**
	 * 在主线程运行, 如果需要长时间任务, 应该自己申请WakeLock, 并在子线程中完成
	 *
	 * @param intent
	 */
	protected abstract fun onHandleIntent(intent: Intent)
}
