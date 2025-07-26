package com.doublez.common.redis;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.doublez.common.core.constants.Constants;
import com.doublez.common.core.utils.ThreadLocalUtil;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;

@Component
public class MyMetaObjectHandler  implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        // 只有当 createBy 字段没有值（为 null）时，才尝试自动填充
        if (Objects.isNull(this.getFieldValByName("createBy", metaObject))) {
            Long userId = ThreadLocalUtil.get(Constants.USER_ID, Long.class);
            this.strictInsertFill(metaObject, "createBy", Long.class, userId);
        }
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());

    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateBy", Long.class, ThreadLocalUtil.get(Constants.USER_ID, Long.class));
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }
}
