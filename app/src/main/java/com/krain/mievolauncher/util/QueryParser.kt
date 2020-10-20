package com.krain.mievolauncher.util

class QueryParser {
    companion object {
        private val delim = arrayOf(' ', '\"')
        private inline fun String.space() = plus(delim[0])

        // Parses query in two passes, first to split by spaces, then to join args between quotes
        // Allows for extra spaces between arguments, but otherwise assumes properly formatted input
        fun parseArgs(seq: CharSequence): List<String> {
            var prefix = ""
            return seq
                .split(delim[0])
                .mapNotNull {
                    with(it.trim()) {
                        when {
                            isEmpty() -> null
                            get(0) == delim[1] -> {
                                // remove quote from start and add space to end
                                prefix = subSequence(1, length).toString().space()
                                null // leave out of map, to be added to suffix later
                            }
                            get(lastIndex) == delim[1] -> {
                                val term =
                                    prefix.plus(subSequence(0, lastIndex)) // add suffix to term
                                prefix = "" // empty constructed prefix
                                term // return term
                            }
                            prefix.isNotEmpty() -> {
                                // add intermediate word to prefix
                                prefix = prefix.plus(this.space())
                                null // then leave out of map
                            }
                            else -> this // arg is *correctly* formatted
                        }
                    }
                }
        }
    }
}