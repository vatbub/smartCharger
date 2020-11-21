package com.github.vatbub.smartcharge.apps

interface Matcher<T> {
    fun matches(obj: T): Boolean
}