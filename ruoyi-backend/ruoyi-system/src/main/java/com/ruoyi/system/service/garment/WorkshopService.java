package com.ruoyi.system.service.garment;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.garment.BizBundle;
import com.ruoyi.system.domain.garment.BizDelivery;
import com.ruoyi.system.domain.garment.BizPick;
import com.ruoyi.system.mapper.garment.BizBundleMapper;
import com.ruoyi.system.mapper.garment.BizDeliveryMapper;
import com.ruoyi.system.mapper.garment.BizPickMapper;

/**
 * 车间管理服务：扫码领片 -> 完工交后道
 */
@Service
public class WorkshopService
{
    @Autowired
    private BizBundleMapper bundleMapper;
    @Autowired
    private BizPickMapper pickMapper;
    @Autowired
    private BizDeliveryMapper deliveryMapper;
    @Autowired
    private GarmentStockService stockService;

    /**
     * 扫码领片：裁片(红) -> 产线(蓝)
     */
    @Transactional
    public BizBundle pick(String code, String employee)
    {
        if (employee == null || employee.trim().isEmpty())
        {
            throw new ServiceException("请先选择员工");
        }
        BizBundle bundle = bundleMapper.selectByCode(code);
        if (bundle == null)
        {
            throw new ServiceException("扎卡不存在：" + code);
        }
        if (!"1".equals(bundle.getStatus()))
        {
            throw new ServiceException("该扎当前不在裁片库中（状态：" + statusLabel(bundle.getStatus()) + "），不能领片");
        }
        stockService.change(bundle.getSkuId(), GarmentStockService.CUT, -bundle.getQty(), "车间领片",
                bundle.getCutNo(), bundle.getBundleNo(), "领片人：" + employee);
        stockService.change(bundle.getSkuId(), GarmentStockService.LINE, bundle.getQty(), "车间领片",
                bundle.getCutNo(), bundle.getBundleNo(), "领片人：" + employee);
        Long userId = SecurityUtils.getUserId();
        bundleMapper.updatePickup(bundle.getBundleId(), employee, userId, "2");
        BizPick pick = new BizPick();
        pick.setBundleId(bundle.getBundleId());
        pick.setBundleNo(bundle.getBundleNo());
        pick.setSkuId(bundle.getSkuId());
        pick.setStyleNo(bundle.getStyleNo());
        pick.setColor(bundle.getColor());
        pick.setSize(bundle.getSize());
        pick.setQty(bundle.getQty());
        pick.setEmployee(employee);
        pick.setUserId(userId);
        pickMapper.insertPick(pick);
        bundle.setStatus("2");
        bundle.setPickupUser(employee);
        bundle.setPickupUserId(userId);
        return bundle;
    }

    /**
     * 完工交后道，支持分次交货
     * @param ownOnly 非管理员只能交本人领的扎
     */
    @Transactional
    public BizBundle deliver(Long bundleId, Integer qty, Integer defectQty, String employee, boolean ownOnly)
    {
        BizBundle bundle = bundleMapper.selectBundleById(bundleId);
        if (bundle == null)
        {
            throw new ServiceException("扎不存在");
        }
        if (ownOnly && (bundle.getPickupUserId() == null || !bundle.getPickupUserId().equals(SecurityUtils.getUserId())))
        {
            throw new ServiceException("只能对本人的扎交后道");
        }
        if (!"2".equals(bundle.getStatus()))
        {
            throw new ServiceException("该扎不在产线中，不能交后道");
        }
        if (qty == null || qty <= 0)
        {
            throw new ServiceException("本次完工数必须大于0");
        }
        int remaining = bundle.getQty() - (bundle.getDeliveredQty() == null ? 0 : bundle.getDeliveredQty());
        if (qty > remaining)
        {
            throw new ServiceException("本次完工数超过剩余数，剩余 " + remaining + " 件");
        }
        int defect = defectQty == null ? 0 : defectQty;
        if (defect > qty)
        {
            throw new ServiceException("次品数不能大于本次完工数");
        }
        int delivered = (bundle.getDeliveredQty() == null ? 0 : bundle.getDeliveredQty()) + qty;
        String status = delivered >= bundle.getQty() ? "3" : "2";
        bundleMapper.addDelivery(bundleId, qty, defect, status);
        BizDelivery delivery = new BizDelivery();
        delivery.setBundleId(bundleId);
        delivery.setBundleNo(bundle.getBundleNo());
        delivery.setSkuId(bundle.getSkuId());
        delivery.setStyleNo(bundle.getStyleNo());
        delivery.setColor(bundle.getColor());
        delivery.setSize(bundle.getSize());
        delivery.setQty(qty);
        delivery.setDefectQty(defect);
        delivery.setEmployee(employee);
        delivery.setUserId(SecurityUtils.getUserId());
        deliveryMapper.insertDelivery(delivery);
        bundle.setDeliveredQty(delivered);
        bundle.setStatus(status);
        return bundle;
    }

    public List<BizDelivery> selectDeliveryList(BizDelivery delivery)
    {
        return deliveryMapper.selectDeliveryList(delivery);
    }

    public List<BizBundle> selectBundles(BizBundle bundle)
    {
        return bundleMapper.selectBundleList(bundle);
    }

    public List<BizPick> selectPickList(BizPick pick)
    {
        return pickMapper.selectPickList(pick);
    }

    private String statusLabel(String status)
    {
        switch (status)
        {
            case "0": return "已开扎";
            case "1": return "裁片在库";
            case "2": return "产线中";
            case "3": return "已完工";
            default: return status;
        }
    }
}
