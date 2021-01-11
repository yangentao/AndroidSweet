package dev.entao.base

import java.util.*
import kotlin.collections.Map.Entry

/**
 * 使用HashMap和ArrayList实现
 *
 * @param <K>
 * @param <V>
 * @author yangentao
</V></K> */
class MultiHashMapArray<K, V>(capacity: Int = 8, listCapcity: Int = 8) {
	private var listCapcity = if (listCapcity < 4) 4 else listCapcity
	private val model: HashMap<K, ArrayList<V>> = HashMap(if (capacity < 8) 8 else capacity)

	val isEmpty: Boolean
		get() = model.isEmpty()

	fun clear() {
		model.clear()
	}

	fun containsKey(key: K): Boolean {
		return model.containsKey(key)
	}

	/**
	 * 需要遍历所有的value, 性能较差
	 *
	 * @param value
	 * @return
	 */
	fun containsValue(value: V): Boolean {
		for (value1 in model.values) {
			if (value1.contains(value)) {
				return true
			}
		}
		return false
	}

	fun entrySet(): Set<Entry<K, ArrayList<V>>> {
		return model.entries
	}

	/**
	 * 根据Key查找对应的Value的集合, 应该对返回的结果集只进行读操作
	 *
	 * @param key
	 * @return
	 */
	operator fun get(key: K): ArrayList<V>? {
		return model[key]
	}

	fun keySet(): Set<K> {
		return model.keys
	}

	fun put(key: K, value: V) {
		var ls: ArrayList<V>? = model[key]
		if (null == ls) {
			ls = ArrayList(listCapcity)
			model[key] = ls
		}
		ls.add(value)
	}

	fun putUnique(key: K, value: V) {
		var ls: ArrayList<V>? = model[key]
		if (null == ls) {
			ls = ArrayList(listCapcity)
			model[key] = ls
		}
		if (!ls.contains(value)) {
			ls.add(value)
		}
	}

	fun remove(key: K): ArrayList<V>? {
		return model.remove(key)
	}

	fun remove(key: K, value: V) {
		val ls = model[key]
		ls?.remove(value)
	}

	/**
	 * 低效率, 需要遍历
	 *
	 * @param value
	 */
	fun removeValue(value: V) {
		for (v in model.values) {
			v.remove(value)
		}
	}

	fun size(): Int {
		return model.size
	}

	fun values(): Collection<ArrayList<V>> {
		return model.values
	}

}
