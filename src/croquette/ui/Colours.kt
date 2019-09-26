/*
 *
 * MIT License
 *
 * Copyright (c) 2019 David Großkurth
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package croquette.ui

import java.awt.Color

val RED = Color(0xd24040)
val WHITE = Color(0xf0f0f0)
val BLACK = Color(0x101010)
val GREY = Color(0x505050)

/**
 * linear interpolates 2 colours and also returns a Color
 */
fun pureColourLerp(c1: Color, c2: Color, t: Double): Color {
    return Color(lerpColours(c1, c2, t))
}

/**
 * linear interpolates 2 colours
 */
fun lerpColours(c1: Color, c2: Color, t: Double): Int {
    return lerpColours(c1.rgb, c2.rgb, t)
}

/**
 * linear interpolates 2 colours
 */
fun lerpColours(c1: Int, c2: Int, t: Double): Int {
    val ir1 = (c1 and (0xff shl 16)) shr 16
    val ig1 = (c1 and (0xff shl 8)) shr 8
    val ib1 = c1 and 0xff

    val ir2 = (c2 and (0xff shl 16)) shr 16
    val ig2 = (c2 and (0xff shl 8)) shr 8
    val ib2 = c2 and 0xff

    val r1 = (ir1 / 255.0)
    val g1 = (ig1 / 255.0)
    val b1 = (ib1 / 255.0)

    val r2 = (ir2 / 255.0)
    val g2 = (ig2 / 255.0)
    val b2 = (ib2 / 255.0)

    val nr = lerp(r1, r2, t)
    val ng = lerp(g1, g2, t)
    val nb = lerp(b1, b2, t)

    val nri = (nr * 255.0).toInt()
    val ngi = (ng * 255.0).toInt()
    val nbi = (nb * 255.0).toInt()

    return (nri shl 16) or (ngi shl 8) or nbi
}

/**
 * linear interpolation
 */
private fun lerp(v0: Double, v1: Double, t: Double): Double = (1 - t) * v0 + t * v1