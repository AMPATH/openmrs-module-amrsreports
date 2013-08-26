/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.amrsreports.reporting.common;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

public class SortedSetMap<K, V> implements Map<K, SortedSet<V>> {

	// Internal properties

	protected static final long serialVersionUID = 1;
	private Map<K, SortedSet<V>> underlyingMap = null;
	private Comparator<V> setComparator = null;

	//***** Constructors *****

	/**
	 * Default constructor
	 */
	public SortedSetMap() {
		underlyingMap = new HashMap<K, SortedSet<V>>();
	}

	/**
	 * Constructor allowing to have an underlying LinkedHashMap
	 */
	public SortedSetMap(Boolean ordered) {
		if (ordered) {
			underlyingMap = new LinkedHashMap<K, SortedSet<V>>();
		} else {
			underlyingMap = new HashMap<K, SortedSet<V>>();
		}
	}

	/**
	 * Constructor allowing to have an underlying TreeMap
	 */
	public SortedSetMap(Comparator<K> comparator) {
		underlyingMap = new TreeMap<K, SortedSet<V>>(comparator);
	}

	/**
	 * Constructor allowing to have an underlying TreeMap and special comparator for individual sets
	 */
	public SortedSetMap(Comparator<K> keyComparator, Comparator<V> valueComparator) {
		underlyingMap = new TreeMap<K, SortedSet<V>>(keyComparator);
		setComparator = valueComparator;
	}

	//***** INSTANCE METHODS *****

	/**
	 * @see Map#clear()
	 */
	public void clear() {
		underlyingMap.clear();
	}

	/**
	 * @see Map#containsKey(Object)
	 */
	public boolean containsKey(Object key) {
		return underlyingMap.containsKey(key);
	}

	/**
	 * @see Map#containsValue(Object)
	 */
	public boolean containsValue(Object value) {
		return underlyingMap.containsValue(value);
	}

	/**
	 * @see Map#containsValue(Object)
	 */
	public boolean containsValueInList(K key, V value) {
		SortedSet<V> l = underlyingMap.get(key);
		return l != null && l.contains(value);
	}

	/**
	 * @see Map#entrySet()
	 */
	public Set<Entry<K, SortedSet<V>>> entrySet() {
		return underlyingMap.entrySet();
	}

	/**
	 * @see Map#get(Object)
	 */
	public SortedSet<V> get(Object key) {
		return underlyingMap.get(key);
	}

	/**
	 * @see Map#isEmpty()
	 */
	public boolean isEmpty() {
		return underlyingMap.isEmpty();
	}

	/**
	 * @see Map#keySet()
	 */
	public Set<K> keySet() {
		return underlyingMap.keySet();
	}

	/**
	 * @see Map#put(Object, Object)
	 */
	public SortedSet<V> put(K key, SortedSet<V> value) {
		return underlyingMap.put(key, value);
	}

	/**
	 * @see Map#put(Object, Object)
	 */
	public SortedSet<V> putInList(K key, V value) {
		SortedSet<V> l = get(key);
		if (l == null) {
			l = (setComparator != null) ? new TreeSet<V>(setComparator) : new TreeSet<V>();
			put(key, l);
		}
		l.add(value);
		return l;
	}

	/**
	 * @see Map#putAll(Map)
	 */
	public void putAll(Map<? extends K, ? extends SortedSet<V>> map) {
		underlyingMap.putAll(map);
	}

	/**
	 * @see Map#putAll(Map)
	 */
	public void putAll(K key, Collection<V> values) {
		if (values != null) {
			for (V v : values) {
				putInList(key, v);
			}
		}
	}

	/**
	 * @see Map#remove(Object)
	 */
	public SortedSet<V> remove(Object key) {
		return underlyingMap.remove(key);
	}

	/**
	 * @see Map#size()
	 */
	public int size() {
		return underlyingMap.size();
	}

	/**
	 * @see Map#values()
	 */
	public Collection<SortedSet<V>> values() {
		return underlyingMap.values();
	}

	public Comparator<V> getSetComparator() {
		return setComparator;
	}

	public void setSetComparator(Comparator<V> setComparator) {
		this.setComparator = setComparator;
	}
}
