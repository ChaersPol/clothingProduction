package com.ruoyi.system.service.garment;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.garment.BizMaterial;
import com.ruoyi.system.domain.garment.BizMaterialLog;
import com.ruoyi.system.mapper.garment.BizMaterialMapper;

/**
 * 物资库存服务：采购入库自动增加、生产领料自动扣减，所有变动均写入物资流水
 */
@Service
public class MaterialService
{
    @Autowired
    private BizMaterialMapper materialMapper;

    public List<BizMaterial> selectList(BizMaterial query)
    {
        return materialMapper.selectMaterialList(query);
    }

    public List<BizMaterialLog> selectLogs(BizMaterialLog query)
    {
        return materialMapper.selectLogList(query);
    }

    public int updateMinStock(Long materialId, BigDecimal minStock)
    {
        if (materialId == null)
        {
            throw new ServiceException("请选择物资");
        }
        return materialMapper.updateMinStock(materialId, minStock == null ? BigDecimal.ZERO : minStock);
    }

    public int updateMaterial(BizMaterial material)
    {
        return materialMapper.updateMaterial(material);
    }

    /**
     * 手工库存调整（盘盈盘亏）：把库存直接改为实盘数量，并写入物资流水
     */
    @Transactional
    public int adjustStock(Long materialId, BigDecimal stockQty, String remark)
    {
        if (materialId == null)
        {
            throw new ServiceException("请选择物资");
        }
        if (stockQty == null || stockQty.compareTo(BigDecimal.ZERO) < 0)
        {
            throw new ServiceException("调整后库存不能小于 0");
        }
        BizMaterial locked = materialMapper.lockById(materialId);
        if (locked == null)
        {
            throw new ServiceException("物资不存在");
        }
        BigDecimal before = locked.getStockQty() == null ? BigDecimal.ZERO : locked.getStockQty();
        BigDecimal after = stockQty.setScale(2, RoundingMode.HALF_UP);
        if (before.compareTo(after) == 0)
        {
            throw new ServiceException("调整后库存与当前库存一致，无需调整");
        }
        materialMapper.updateStock(materialId, after);

        BizMaterialLog log = new BizMaterialLog();
        log.setMaterialId(locked.getMaterialId());
        log.setMaterialName(locked.getMaterialName());
        log.setSpec(locked.getSpec());
        log.setUnit(locked.getUnit());
        log.setChangeQty(after.subtract(before).setScale(2, RoundingMode.HALF_UP));
        log.setBeforeQty(before);
        log.setAfterQty(after);
        log.setBizType("手工调整");
        log.setBizNo("ADJ" + new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date()));
        log.setOperator(SecurityUtils.getUsername());
        log.setRemark(StringUtils.isEmpty(remark) ? "实物盘点调整" : remark);
        materialMapper.insertLog(log);
        return 1;
    }

    /**
     * 物资库存自动增减（正数入库、负数出库），按名称+规格自动匹配物资档案，
     * 档案不存在时自动建档（用于未采购先领料的场景，库存允许为负并进入预警）
     */
    @Transactional
    public void stockChange(String name, String spec, String unit, String type, BigDecimal delta,
            String bizType, String bizNo, String remark)
    {
        stockChange(name, spec, unit, type, delta, bizType, bizNo, remark, null);
    }

    @Transactional
    public void stockChange(String name, String spec, String unit, String type, BigDecimal delta,
            String bizType, String bizNo, String remark, BigDecimal price)
    {
        if (StringUtils.isEmpty(name) || delta == null || delta.compareTo(BigDecimal.ZERO) == 0)
        {
            return;
        }
        BizMaterial material = materialMapper.selectByKey(name.trim(), spec == null ? "" : spec.trim());
        if (material == null)
        {
            material = new BizMaterial();
            material.setMaterialName(name.trim());
            material.setSpec(spec == null ? "" : spec.trim());
            material.setUnit(unit);
            material.setMaterialType(type);
            material.setStockQty(BigDecimal.ZERO);
            material.setMinStock(BigDecimal.ZERO);
            material.setPrice(BigDecimal.ZERO);
            material.setCreateBy(SecurityUtils.getUsername());
            materialMapper.insertMaterial(material);
        }
        BizMaterial locked = materialMapper.lockById(material.getMaterialId());
        BigDecimal before = locked.getStockQty() == null ? BigDecimal.ZERO : locked.getStockQty();
        BigDecimal after = before.add(delta).setScale(2, RoundingMode.HALF_UP);
        materialMapper.updateStock(locked.getMaterialId(), after);
        if (price != null && price.compareTo(BigDecimal.ZERO) > 0)
        {
            materialMapper.updatePrice(locked.getMaterialId(), price);
        }

        BizMaterialLog log = new BizMaterialLog();
        log.setMaterialId(locked.getMaterialId());
        log.setMaterialName(locked.getMaterialName());
        log.setSpec(locked.getSpec());
        log.setUnit(locked.getUnit());
        log.setChangeQty(delta.setScale(2, RoundingMode.HALF_UP));
        log.setBeforeQty(before);
        log.setAfterQty(after);
        log.setBizType(bizType);
        log.setBizNo(bizNo);
        log.setOperator(SecurityUtils.getUsername());
        log.setRemark(remark);
        materialMapper.insertLog(log);
    }
}
