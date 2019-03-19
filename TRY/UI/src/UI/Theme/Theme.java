package UI.Theme;

import static UI.UIMisc.FinalSettings.*;

public class Theme {

    private eThemeType mCurrentTheame;
    private String mCurrentBackgound;

    public Theme(){
        this.mCurrentTheame = eThemeType.Aviad;
        this.mCurrentBackgound = AVIAD_THEME_IMAGE_BACKGROUND;
    }

    public void setAviadTheme() {
        this.mCurrentTheame = eThemeType.Aviad;
        this.mCurrentBackgound = AVIAD_THEME_IMAGE_BACKGROUND;
    }

    public void setGuyTheme(){
        this.mCurrentTheame = eThemeType.Binsk;
        this.mCurrentBackgound = Guy_THEME_IMAGE_BACKGROUND;
    }

    public eThemeType getCurrentTheame() {
        return this.mCurrentTheame;
    }

    public String getCurrentThemeBackground() {
        return this.mCurrentBackgound + "\n" + BACKGROUND_SETTINGS;
    }

    public void setDefaultTheme() {
        this.mCurrentTheame = eThemeType.Default;
        this.mCurrentBackgound = DEFAULT_THEME_IMAGE_BACKGROUND;
    }
}

