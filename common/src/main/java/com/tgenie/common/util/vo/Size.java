package com.tgenie.common.util.vo;

/**
 *
 * @author dzt
 * @date 17/8/25
 * Hope you know what you have done
 */
public enum Size implements Quantifiable {

    DICT("DICT"),
    S("S"),
    M("M"),
    L("L"),
    XL("XL"),
    XXL("XXL"),
    IIXL("2XL"),
    XXXL("XXXL"),
    IIIXL("3XL"),
    XXXXL("XXXXL"),
    IVXL("4XL"),
    VXL("5XL");

    private final String name;

    Size(String name) {
        this.name = name;
    }

    @Override
    public Integer orderOf (String name) {
        Size[] sizes = Size.values();
        for (Size size : sizes) {
            if (size.name.equals(name)) {
                return size.ordinal();
            }
        }
        return Integer.MAX_VALUE;
    }

}
