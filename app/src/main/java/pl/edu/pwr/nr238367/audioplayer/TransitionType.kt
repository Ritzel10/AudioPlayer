package pl.edu.pwr.nr238367.audioplayer

enum class TransitionType {
    REPEAT,
    NORMAL,
    RANDOM;

    override fun toString(): String {
        return super.toString().toLowerCase()
    }
}