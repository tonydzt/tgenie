package util.vo;

/**
 *
 * @author dzt
 * @date 17/8/26
 * Hope you know what you have done
 */
public interface Quantifiable {

    /**
     * 返回字典值在字典中的顺序
     * @param name  字典值
     * @return      字典顺序
     */
    Integer orderOf(String name);
}
