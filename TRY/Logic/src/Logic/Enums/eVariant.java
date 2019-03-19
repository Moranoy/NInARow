package Logic.Enums;

import java.io.Serializable;

public enum eVariant implements Serializable {
    Regular,
    Popout,
    Circular;

    static public eVariant CreateFromString(String variantString) {
        eVariant variant;

        if(variantString.contentEquals("Regular")) {
            variant = eVariant.Regular;
        } else if (variantString.contentEquals("Popout")) {
            variant = eVariant.Popout;
        } else {
            variant = eVariant.Circular;
        }

        return variant;
    }
}
