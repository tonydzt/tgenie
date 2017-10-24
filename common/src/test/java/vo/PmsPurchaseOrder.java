package vo;

/**
 * PurchaseOrder采购单
 *
 * @author Tony
 * @date 16/7/13
 */
public class PmsPurchaseOrder {

    /**
     * 商品类型
     */
    private Integer productType;

    /**
     * 供应商Id
     */
    private Long vendorId;

    /**
     * 仓库id
     */
    private Long warehouseId;

    /**
     * 详细地址
     */
    private String deliverAddress;

    public Integer getProductType() {
        return productType;
    }

    public void setProductType(Integer productType) {
        this.productType = productType;
    }

    public Long getVendorId() {
        return vendorId;
    }

    public void setVendorId(Long vendorId) {
        this.vendorId = vendorId;
    }

    public String getDeliverAddress() {
        return deliverAddress;
    }

    public void setDeliverAddress(String deliverAddress) {
        this.deliverAddress = deliverAddress;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
    }

}
