/*-
 * #%L
 * Smart Charge
 * %%
 * Copyright (C) 2016 - 2020 Frederik Kammel
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.github.vatbub.smartcharge.util

class ObservableList<E> private constructor(
    private val list: MutableList<E>,
    private val onAdd: ((index: Int?, element: E) -> Unit)?,
    private val onSet: ((index: Int, previousValue: E, newValue: E) -> Unit)?,
    private val onRemove: ((element: E) -> Unit)?,
    private val onClear: (() -> Unit)?,
    @Suppress("unused") private val dummy: Int? = null
) : MutableList<E> by list {
    constructor(
        onAdd: ((index: Int?, element: E) -> Unit)? = null,
        onSet: ((index: Int, previousValue: E, newValue: E) -> Unit)? = null,
        onRemove: ((element: E) -> Unit)? = null,
        onClear: (() -> Unit)? = null
    ) : this(
        mutableListOf(),
        onAdd,
        onSet,
        onRemove,
        onClear
    )

    constructor(
        contents: List<E>,
        onAdd: ((index: Int?, element: E) -> Unit)? = null,
        onSet: ((index: Int, previousValue: E, newValue: E) -> Unit)? = null,
        onRemove: ((element: E) -> Unit)? = null,
        onClear: (() -> Unit)? = null
    ) : this(
        contents.toMutableList(),
        onAdd,
        onSet,
        onRemove,
        onClear
    )

    override fun add(element: E): Boolean =
        list.add(element)
            .also { onAdd?.invoke(null, element) }

    override fun add(index: Int, element: E) =
        list.add(index, element)
            .also { onAdd?.invoke(index, element) }

    override fun addAll(index: Int, elements: Collection<E>): Boolean =
        list.addAll(index, elements)
            .also {
                elements.forEachIndexed { localIndex, element ->
                    onAdd?.invoke(index + localIndex, element)
                }
            }

    override fun addAll(elements: Collection<E>): Boolean =
        list.addAll(elements)
            .also { elements.forEachIndexed { index, element -> onAdd?.invoke(index, element) } }

    override fun clear() =
        list.clear()
            .also { onClear?.invoke() }

    override fun remove(element: E): Boolean =
        list.remove(element)
            .also {
                if (it) onRemove?.invoke(element)
            }

    override fun removeAll(elements: Collection<E>): Boolean {
        val elementsToBeRemoved = elements.filter { list.contains(it) }
        val result = list.removeAll(elements)
        elementsToBeRemoved.forEach { onRemove?.invoke(it) }
        return result
    }

    override fun removeAt(index: Int): E =
        list.removeAt(index)
            .also { onRemove?.invoke(it) }

    override fun retainAll(elements: Collection<E>): Boolean {
        val elementsToBeRemoved = list.filterNot { elements.contains(it) }
        val result = list.retainAll(elements)
        elementsToBeRemoved.forEach { onRemove?.invoke(it) }
        return result
    }

    override fun set(index: Int, element: E): E =
        list.set(index, element)
            .also { onSet?.invoke(index, it, element) }
}
