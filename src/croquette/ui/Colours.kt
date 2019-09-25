package croquette.ui

import java.awt.Color

//val BLACK = Color(0x101820)
val RED = Color(0xd24040)
val BLUE = Color(0x00a0c8)
val WHITE = Color(0xf0f0f0)
//val GREY = Color(0x736464)
val BLACK = Color(0x101010)
val GREY = Color(0x505050)

fun pureColourLerp(c1: Color, c2: Color, t: Double): Color {
    return Color(lerpColours(c1, c2, t))
}

fun lerpColours(c1: Color, c2: Color, t: Double): Int {
    return lerpColours(c1.rgb, c2.rgb, t)
}

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

private fun lerp(v0: Double, v1: Double, t: Double): Double {
    return (1 - t) * v0 + t * v1
}