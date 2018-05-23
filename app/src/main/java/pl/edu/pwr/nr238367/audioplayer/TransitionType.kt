package pl.edu.pwr.nr238367.audioplayer

enum class TransitionType {
    NO_TRANSITION,
    NORMAL,
    RANDOM;

    override fun toString(): String {
        return super.toString().toLowerCase()
    }
}