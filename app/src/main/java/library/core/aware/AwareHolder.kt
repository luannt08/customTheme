package library.core.aware

import library.core.theme.Theme

class AwareHolder{
    private val mListAware = ArrayList<Aware>()

    fun addAware(aware: Aware) {
        mListAware.add(aware)
    }

    fun notifyThemeChanged(theme: Theme) {
        mListAware.forEach { aware -> aware.onThemeChanged(theme) }
    }
}