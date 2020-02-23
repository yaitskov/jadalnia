package org.dan.jadalnia.sys.type.number

import java.lang.Number


abstract class AbstractNum : Number(), Comparable<AbstractNum> {
    override fun longValue(): Long = intValue().toLong()
    override fun floatValue(): Float = intValue().toFloat()
    override fun doubleValue(): Double = intValue().toDouble()
    override fun compareTo(o: AbstractNum) = intValue().compareTo(o.intValue())
    override fun toString(): String = intValue().toString()
}