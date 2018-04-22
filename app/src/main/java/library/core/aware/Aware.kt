package library.core.aware

import library.core.theme.Theme

interface Aware {
    fun onThemeChanged(theme: Theme)
}