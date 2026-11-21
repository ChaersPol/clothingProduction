package com.ruoyi.system.service.garment;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.garment.BizBundle;
import com.ruoyi.system.domain.garment.BizDefect;
import com.ruoyi.system.domain.garment.BizPack;
import com.ruoyi.system.domain.garment.BizQc;
import com.ruoyi.system.mapper.garment.BizBundleMapper;
import com.ruoyi.system.mapper.garment.BizDefectMapper;
import com.ruoyi.system.mapper.garment.BizPackMapper;
import com.ruoyi.system.mapper.garment.BizQcMapper;

/**
 * 后道管理服务：检验 -> 包装入库（产线蓝 -> 成品黑）
 */
@Service
public class FinishingService
{
    @Autowired
    private BizBundleMapper bundleMapper;
    @Autowired
    private BizQcMapper qcMapper;
    @Autowired
    private BizPackMapper packMapper;
    @Autowired
    private BizDefectMapper defectMapper;
    @Autowired
    private GarmentStockService stockService;

    @Transactional
    public int saveQc(BizQc qc)
    {
        if (qc.getBundleId() == null)
        {
            throw new ServiceException("请选择扎号");
        }
        BizBundle bundle = bundleMapper.selectBundleById(qc.getBundleId());
        if (bundle == null)
        {
            throw new ServiceException("扎不存在");
        }
        if (qc.getCheckQty() == null || qc.getCheckQty() <= 0)
        {
            throw new ServiceException("检验数量必须大于0");
        }
        int qualified = qc.getQualifiedQty() == null ? 0 : qc.getQualifiedQty();
        int defect = qc.getDefectQty() == null ? 0 : qc.getDefectQty();
        if (qualified + defect != qc.getCheckQty())
        {
            throw new ServiceException("合格数 + 次品数 必须等于检验数");
        }
        qc.setBundleNo(bundle.getBundleNo());
        qc.setSkuId(bundle.getSkuId());
        if (StringUtils.isEmpty(qc.getInspector()))
        {
            qc.setInspector(com.ruoyi.common.utils.SecurityUtils.getUsername());
        }
        return qcMapper.insertQc(qc);
    }

    @Transactional
    public int savePack(BizPack pack)
    {
        if (pack.getBundleId() == null)
        {
            throw new ServiceException("请选择扎号");
        }
        BizBundle bundle = bundleMapper.selectBundleById(pack.getBundleId());
        if (bundle == null)
        {
            throw new ServiceException("扎不存在");
        }
        pack.setBundleNo(bundle.getBundleNo());
        pack.setSkuId(bundle.getSkuId());
        if (pack.getQty() == null || pack.getQty() <= 0)
        {
            throw new ServiceException("包装数量必须大于0");
        }
        if (StringUtils.isEmpty(pack.getPackMethod()))
        {
            pack.setPackMethod("袋装");
        }
        if (pack.getPerBag() == null || pack.getPerBag() <= 0)
        {
            pack.setPerBag(1);
        }
        if (StringUtils.isEmpty(pack.getPacker()))
        {
            pack.setPacker(com.ruoyi.common.utils.SecurityUtils.getUsername());
        }
        return packMapper.insertPack(pack);
    }

    /**
     * 成品扫码入库：产线库存减少、成品库存增加，一步完成
     */
    @Transactional
    public BizBundle stockIn(String code, Integer qty, String packMethod, Integer perBag, String packer, String packCode)
    {
        BizBundle bundle = bundleMapper.selectByCode(code);
        if (bundle == null)
        {
            throw new ServiceException("扎卡不存在：" + code);
        }
        if (!"2".equals(bundle.getStatus()) && !"3".equals(bundle.getStatus()))
        {
            throw new ServiceException("该扎还没交到后道，不能包装入库");
        }
        if (qty == null || qty <= 0)
        {
            throw new ServiceException("入库数量必须大于0");
        }
        int packed = bundle.getPackedQty() == null ? 0 : bundle.getPackedQty();
        int delivered = bundle.getDeliveredQty() == null ? 0 : bundle.getDeliveredQty();
        int available = (delivered > 0 ? delivered : bundle.getQty()) - packed;
        if (qty > available)
        {
            throw new ServiceException("入库数量超过后道可用数，剩余 " + available + " 件");
        }
        stockService.change(bundle.getSkuId(), GarmentStockService.LINE, -qty, "包装出产线",
                bundle.getCutNo(), bundle.getBundleNo(), "后道包装入库");
        stockService.change(bundle.getSkuId(), GarmentStockService.FINISH, qty, "成品入库",
                bundle.getCutNo(), bundle.getBundleNo(), "后道包装入库");
        int newPacked = packed + qty;
        String status = newPacked >= bundle.getQty() ? "3" : bundle.getStatus();
        bundleMapper.addPacked(bundle.getBundleId(), qty, status);
        // 已登记过包装数量的部分不再重复生成包装记录，避免包装产量重复统计
        int recorded = packMapper.sumPackQtyByBundle(bundle.getBundleId());
        int gap = newPacked - recorded;
        if (gap > 0)
        {
            BizPack pack = new BizPack();
            pack.setBundleId(bundle.getBundleId());
            pack.setBundleNo(bundle.getBundleNo());
            pack.setSkuId(bundle.getSkuId());
            pack.setQty(gap);
            pack.setPackMethod(StringUtils.isEmpty(packMethod) ? "袋装" : packMethod);
            pack.setPerBag(perBag == null || perBag <= 0 ? 1 : perBag);
            pack.setPacker(packer);
            pack.setRemark("成品扫码入库");
            packMapper.insertPack(pack);
        }
        if (!StringUtils.isEmpty(packCode))
        {
            bundle.setPackCode(packCode);
            bundleMapper.updateBundle(bundle);
        }
        bundle.setPackedQty(newPacked);
        bundle.setStatus(status);
        return bundle;
    }

    @Transactional
    public int saveDefect(BizDefect defect)
    {
        if (defect.getBundleId() == null)
        {
            throw new ServiceException("请选择扎号");
        }
        BizBundle bundle = bundleMapper.selectBundleById(defect.getBundleId());
        if (bundle == null)
        {
            throw new ServiceException("扎不存在");
        }
        if (defect.getQty() == null || defect.getQty() <= 0)
        {
            throw new ServiceException("数量必须大于0");
        }
        defect.setBundleNo(bundle.getBundleNo());
        defect.setSkuId(bundle.getSkuId());
        if (StringUtils.isEmpty(defect.getDefectType()))
        {
            defect.setDefectType("返修");
        }
        if (StringUtils.isEmpty(defect.getHandler()))
        {
            defect.setHandler(com.ruoyi.common.utils.SecurityUtils.getUsername());
        }
        return defectMapper.insertDefect(defect);
    }

    public List<BizQc> selectQcList(BizQc qc)
    {
        return qcMapper.selectQcList(qc);
    }

    public List<BizPack> selectPackList(BizPack pack)
    {
        return packMapper.selectPackList(pack);
    }

    public List<BizDefect> selectDefectList(BizDefect defect)
    {
        return defectMapper.selectDefectList(defect);
    }

    public List<Map<String, Object>> statByPacker(String date)
    {
        return packMapper.statByPacker(date);
    }
}
