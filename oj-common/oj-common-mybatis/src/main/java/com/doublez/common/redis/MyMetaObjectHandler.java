package com.doublez.common.redis;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.doublez.common.core.constants.Constants;
import com.doublez.common.core.utils.ThreadLocalUtil;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
@Component
public class MyMetaObjectHandler  implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {

        this.strictInsertFill(metaObject, "createBy", Long.class, ThreadLocalUtil.get(Constants.USER_ID, Long.class));
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());

    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateBy", Long.class, ThreadLocalUtil.get(Constants.USER_ID, Long.class));
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }
}
