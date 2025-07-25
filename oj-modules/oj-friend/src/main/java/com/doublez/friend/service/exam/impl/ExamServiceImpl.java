package com.doublez.friend.service.exam.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.doublez.common.core.domain.vo.TableDataInfo;
import com.doublez.friend.domain.exam.dto.ExamQueryDTO;
import com.doublez.friend.domain.exam.vo.ExamVO;
import com.doublez.friend.manager.ExamCacheManager;
import com.doublez.friend.mapper.exam.ExamMapper;
import com.doublez.friend.service.exam.IExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExamServiceImpl implements IExamService {
    @Autowired
    private ExamMapper examMapper;

    @Autowired
    private ExamCacheManager examCacheManager;

    @Override
    public IPage<ExamVO> list(ExamQueryDTO examQueryDTO) {
        Page<ExamVO> page = new Page<>(examQueryDTO.getPageNum(), examQueryDTO.getPageSize());
        return examMapper.selectExamList(page, examQueryDTO);
    }

    @Override
    public TableDataInfo redisList(ExamQueryDTO examQueryDTO) {
        //从redis当中获取  竞赛列表的数据
        Long total = examCacheManager.getListSize(examQueryDTO.getType(), null);
        List<ExamVO> examVOList;
        if (total == null || total <= 0) {
            //数据库重建
            IPage<ExamVO> list = list(examQueryDTO);
            examVOList = list.getRecords();
            total = list.getTotal();
            //重写填回缓存
            examCacheManager.refreshCache(examQueryDTO.getType(), null);
        } else {
            //直接查询redis
            examVOList = examCacheManager.getExamVOList(examQueryDTO, null);
            total = examCacheManager.getListSize(examQueryDTO.getType(), null);
        }
        if (CollectionUtil.isEmpty(examVOList)) {
            return TableDataInfo.empty();
        }
        return TableDataInfo.success(examVOList, total);
    }

    @Override
    public String getFirstQuestion(Long examId) {
        //确认缓存，没有则刷新
        checkAndRefresh(examId);
        //获取第一题
        return examCacheManager.getFirstQuestion(examId).toString();
    }

    private void checkAndRefresh(Long examId) {
        Long listSize = examCacheManager.getExamQuestionListSize(examId);
        if (listSize == null || listSize <= 0) {
            examCacheManager.refreshExamQuestionCache(examId);
        }
    }
}
