package enums;

import com.tgenie.common.util.VoUtil;
import com.tgenie.common.util.enums.NameTranslator;
import com.tgenie.common.util.vo.UpdateUnit;
import vo.PmsPurchaseOrder;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author dzt
 * @date 17/10/23
 * Hope you know what you have done
 */
public enum PmsPurchaseOrderNameEnum {

    PRODUCT_TYPE("产品类型"),
    VENDOR_ID("供应商id"),
    WAREHOUSE_ID("仓库id"),
    DELIVER_ADDRESS("发货地址");

    private String name;

    PmsPurchaseOrderNameEnum(String name) {
        this.name = name;
    }

    public static NameTranslator getNameTranslator() {
        return  (filedName) ->
                PmsPurchaseOrderNameEnum.valueOf(camel2Underline(filedName, false).toUpperCase()).getName();

    }

    public String getName() {
        return name;
    }

    public static void main(String[] args) throws IllegalAccessException {
        PmsPurchaseOrder originVo = new PmsPurchaseOrder();
        originVo.setVendorId(1L);
        originVo.setWarehouseId(2L);
        originVo.setProductType(3);
        originVo.setDeliverAddress("4");

        PmsPurchaseOrder newVo = new PmsPurchaseOrder();
        newVo.setVendorId(1L);
        newVo.setWarehouseId(5L);
        newVo.setProductType(3);
        newVo.setDeliverAddress("7");

        List<UpdateUnit> updateUnitList = VoUtil.checkUpdatedColumn(originVo, newVo, PmsPurchaseOrderNameEnum.getNameTranslator());
        StringBuilder sb = new StringBuilder();
        for (UpdateUnit updateUnit : updateUnitList) {
            sb.append(updateUnit).append(";");
        }

        System.out.println(sb.toString());
    }

    public static String underline2Camel(String line,boolean smallCamel){
        if(line==null||"".equals(line)){
            return "";
        }
        StringBuffer sb=new StringBuffer();
        Pattern pattern=Pattern.compile("([A-Za-z\\d]+)(_)?");
        Matcher matcher=pattern.matcher(line);
        while(matcher.find()){
            String word=matcher.group();
            sb.append(smallCamel&&matcher.start()==0?Character.toLowerCase(word.charAt(0)):Character.toUpperCase(word.charAt(0)));
            int index=word.lastIndexOf('_');
            if(index>0){
                sb.append(word.substring(1, index).toLowerCase());
            }else{
                sb.append(word.substring(1).toLowerCase());
            }
        }
        return sb.toString();
    }

    public static String camel2Underline(String line, boolean upperCase){
        if(line==null||"".equals(line)){
            return "";
        }
        line=String.valueOf(line.charAt(0)).toUpperCase().concat(line.substring(1));
        StringBuffer sb=new StringBuffer();
        Pattern pattern=Pattern.compile("[A-Z]([a-z\\d]+)?");
        Matcher matcher=pattern.matcher(line);
        while(matcher.find()){
            String word=matcher.group();
            sb.append(upperCase?word.toUpperCase():word.toLowerCase());
            sb.append(matcher.end()==line.length()?"":"_");
        }
        return sb.toString();
    }

}
