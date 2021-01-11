package dev.entao.base

import java.util.*
import kotlin.collections.Map.Entry

/**
 * 使用HashMap和LinkedList实现
 * 允许一个Key对应多个值的Map
 * 与Map接口基本一致
 * 注意: Value是否重复使用Value的equal方法来判断, 在put和remove方法中会用到Value.equal方法
 * @param <K>
 * @param <V>
 * @author yangentao
</V></K> */
class MultiHashMap<K, V>(capacity: Int = 8) {
	private val model: HashMap<K, LinkedList<V>> = HashMap(if (capacity < 8) 8 else capacity)

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
		for (ls in model.values) {
			if (ls.contains(value)) {
				return true
			}
		}
		return false
	}

	fun entrySet(): Set<Entry<K, LinkedList<V>>> {
		return model.entries
	}

	/**
	 * 根据Key查找对应的Value的集合, 应该对返回的结果集只进行读操作
	 *
	 * @param key
	 * @return
	 */
	operator fun get(key: K): List<V>? {
		return model[key]
	}

	fun keySet(): Set<K> {
		return model.keys
	}

	fun put(key: K, value: V) {
		var ls: LinkedList<V>? = model[key]
		if (null == ls) {
			ls = LinkedList()
			model[key] = ls
		}
		if (!ls.contains(value)) {
			ls.add(value)
		}
	}

	fun remove(key: K): LinkedList<V>? {
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
		for (ls in model.values) {
			ls.remove(value)
		}
	}

	fun size(): Int {
		return model.size
	}

	fun values(): Collection<LinkedList<V>> {
		return model.values
	}

}
