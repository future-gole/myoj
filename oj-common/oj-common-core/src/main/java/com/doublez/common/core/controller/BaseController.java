package com.doublez.common.core.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.doublez.common.core.domain.R;
import com.doublez.common.core.domain.vo.TableDataInfo;

import java.util.List;

public class BaseController {
    public R<Void> toR(int rows){
        return rows > 0 ? R.ok() : R.fail();
    }

    public R<Void> toR(boolean result){
        return result ? R.ok() : R.fail();
    }

    public TableDataInfo getTableDataInfo(IPage<?> page) {
        // 当查询结果为空时，MP 返回的 page 对象本身不会是 null，但里面的 records 可能是空的
        if (page == null || CollectionUtil.isEmpty(page.getRecords())) {
            return TableDataInfo.empty();
        }
        return TableDataInfo.success(page.getRecords(), page.getTotal());
    }
}
