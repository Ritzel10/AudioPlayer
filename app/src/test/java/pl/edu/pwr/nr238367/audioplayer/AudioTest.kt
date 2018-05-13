package pl.edu.pwr.nr238367.audioplayer

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class AudioTest {
    @Test
    fun durationToStringIsCorrect() {
        val audio = Audio("","",120)
        assertEquals("02:00", audio.durationToString)
        val audio2 = Audio("","",63)
        assertEquals("01:03", audio2.durationToString)
    }
}
