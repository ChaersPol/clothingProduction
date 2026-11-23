package com.ruoyi.system.service.garment;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DictUtils;
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
     * 新增物资档案：名称+规格唯一，单位必须取自「计量单位」字典
     */
    @Transactional
    public int addMaterial(BizMaterial material)
    {
        if (StringUtils.isEmpty(material.getMaterialName()))
        {
            throw new ServiceException("请填写物资名称");
        }
        material.setMaterialName(material.getMaterialName().trim());
        material.setSpec(material.getSpec() == null ? "" : material.getSpec().trim());
        checkUnit(material.getUnit());
        if (StringUtils.isEmpty(material.getMaterialType()))
        {
            throw new ServiceException("请选择物资类型");
        }
        if (materialMapper.selectByKey(material.getMaterialName(), material.getSpec()) != null)
        {
            throw new ServiceException("物资档案已存在同名同规格的物资：" + material.getMaterialName()
                    + (StringUtils.isEmpty(material.getSpec()) ? "" : " / " + material.getSpec()));
        }
        if (material.getStockQty() == null)
        {
            material.setStockQty(BigDecimal.ZERO);
        }
        if (material.getMinStock() == null)
        {
            material.setMinStock(BigDecimal.ZERO);
        }
        if (material.getPrice() == null)
        {
            material.setPrice(BigDecimal.ZERO);
        }
        material.setCreateBy(SecurityUtils.getUsername());
        int rows = materialMapper.insertMaterial(material);
        String code = StringUtils.isEmpty(material.getMaterialCode()) ? "WL" + material.getMaterialId() : material.getMaterialCode();
        materialMapper.updateCode(material.getMaterialId(), code);
        return rows;
    }

    /**
     * 修改物资档案（名称/规格/类型/单位/单价/安全库存），库存数量不在这里改
     */
    @Transactional
    public int updateMaterialProfile(BizMaterial material)
    {
        if (material.getMaterialId() == null)
        {
            throw new ServiceException("请选择物资");
        }
        BizMaterial old = materialMapper.selectMaterialById(material.getMaterialId());
        if (old == null)
        {
            throw new ServiceException("物资不存在");
        }
        if (StringUtils.isEmpty(material.getMaterialName()))
        {
            throw new ServiceException("请填写物资名称");
        }
        material.setMaterialName(material.getMaterialName().trim());
        material.setSpec(material.getSpec() == null ? "" : material.getSpec().trim());
        checkUnit(material.getUnit());
        if (StringUtils.isEmpty(material.getMaterialType()))
        {
            throw new ServiceException("请选择物资类型");
        }
        BizMaterial exist = materialMapper.selectByKey(material.getMaterialName(), material.getSpec());
        if (exist != null && !exist.getMaterialId().equals(material.getMaterialId()))
        {
            throw new ServiceException("物资档案已存在同名同规格的物资：" + material.getMaterialName()
                    + (StringUtils.isEmpty(material.getSpec()) ? "" : " / " + material.getSpec()));
        }
        BigDecimal stock = old.getStockQty() == null ? BigDecimal.ZERO : old.getStockQty();
        if (!material.getUnit().trim().equals(old.getUnit() == null ? "" : old.getUnit().trim())
                && stock.compareTo(BigDecimal.ZERO) != 0)
        {
            throw new ServiceException("该物资当前库存为 " + stock.stripTrailingZeros().toPlainString() + " " + old.getUnit()
                    + "，不能直接修改单位，请先盘点清零或新建一条物资档案");
        }
        // 名称/规格是 BOM、采购明细与物资档案的关联键，已被引用时改名会导致历史单据与库存分叉
        String oldName = old.getMaterialName() == null ? "" : old.getMaterialName().trim();
        String oldSpec = old.getSpec() == null ? "" : old.getSpec().trim();
        if (!material.getMaterialName().equals(oldName) || !material.getSpec().equals(oldSpec))
        {
            int bomRef = materialMapper.countBomRef(oldName, oldSpec);
            int purchaseRef = materialMapper.countPurchaseRef(oldName, oldSpec);
            if (bomRef > 0 || purchaseRef > 0)
            {
                throw new ServiceException("物资「" + oldName + (StringUtils.isEmpty(oldSpec) ? "" : " / " + oldSpec)
                        + "」已被" + (bomRef > 0 ? "用料清单" : "") + (bomRef > 0 && purchaseRef > 0 ? "、" : "")
                        + (purchaseRef > 0 ? "采购单" : "") + "引用，不能修改名称或规格；如确需变更，请新建一条物资档案");
            }
        }
        if (material.getMinStock() == null)
        {
            material.setMinStock(old.getMinStock());
        }
        if (material.getPrice() == null)
        {
            material.setPrice(old.getPrice());
        }
        material.setUpdateBy(SecurityUtils.getUsername());
        return materialMapper.updateMaterial(material);
    }

    /**
     * 校验计量单位是否在「计量单位」字典内（字典缓存未加载时仅校验非空，避免误拦）
     */
    private void checkUnit(String unit)
    {
        if (StringUtils.isEmpty(unit))
        {
            throw new ServiceException("请选择计量单位");
        }
        String values = DictUtils.getDictValues("garment_unit");
        if (StringUtils.isNotEmpty(values) && !("," + values + ",").contains("," + unit.trim() + ","))
        {
            throw new ServiceException("计量单位「" + unit + "」不在单位字典中，请重新选择");
        }
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
            materialMapper.updateCode(material.getMaterialId(), "WL" + material.getMaterialId());
        }
        else if (StringUtils.isNotEmpty(unit) && StringUtils.isNotEmpty(material.getUnit())
                && !unit.trim().equals(material.getUnit().trim()))
        {
            // 单位口径不一致时直接拦截，禁止把不同单位的数量直接累加进同一库存
            throw new ServiceException("物资「" + material.getMaterialName() + "」档案单位为【" + material.getUnit()
                    + "】，本次业务单位为【" + unit + "】，单位不一致无法入库，请先在物资档案与业务单据中统一单位");
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
