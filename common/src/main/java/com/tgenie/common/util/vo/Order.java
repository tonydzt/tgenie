package util.vo;

/**
 *
 * @author dzt
 * @date 17/8/25
 * Hope you know what you have done
 */
public class Order {

    private final String colName;
    private final boolean isASC;
    private final Quantifiable dict;

    public Order(String name) {
        this(name, true, null);
    }

    public Order(String name, boolean isASC) {
        this(name, isASC, null);
    }

    public Order(String name, Quantifiable dict) {
        this(name, true, dict);
    }

    private Order(String name, boolean isASC, Quantifiable dict) {
        this.colName = name;
        this.isASC = isASC;
        this.dict = dict;
    }

    public String getColName() {
        return colName;
    }

    public boolean isASC() {
        return isASC;
    }

    public Quantifiable getDict() {
        return dict;
    }

}
