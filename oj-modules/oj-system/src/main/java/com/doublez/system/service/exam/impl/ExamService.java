package com.doublez.system.service.exam.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.doublez.common.core.enums.ResultCode;
import com.doublez.common.security.exception.ServiceException;
import com.doublez.system.domain.exam.Exam;
import com.doublez.system.domain.exam.ExamQuestion;
import com.doublez.system.domain.exam.dto.ExamAddDTO;
import com.doublez.system.domain.exam.dto.ExamEditDTO;
import com.doublez.system.domain.exam.dto.ExamQueryDTO;
import com.doublez.system.domain.exam.dto.ExamQuestionAddDTO;
import com.doublez.system.domain.exam.vo.ExamDetailVO;
import com.doublez.system.domain.exam.vo.ExamVO;
import com.doublez.system.domain.question.Question;
import com.doublez.system.domain.question.vo.QuestionVO;
import com.doublez.system.mapper.ExamMapper;
import com.doublez.system.mapper.ExamQuestionMapper;
import com.doublez.system.mapper.QuestionMapper;
import com.doublez.system.service.exam.IExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class ExamService extends ServiceImpl<ExamQuestionMapper, ExamQuestion> implements IExamService {
    @Autowired
    private ExamMapper examMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private ExamQuestionMapper examQuestionMapper;

    @Override
    public IPage<ExamVO> list(ExamQueryDTO examQueryDTO) {
        Page<ExamVO> page = new Page<>(examQueryDTO.getPageNum(), examQueryDTO.getPageSize());
        return examMapper.selectExamList(page, examQueryDTO);
    }

    @Override
    public String add(ExamAddDTO examAddDTO) {
        checkExamSaveParams(examAddDTO,null);
        Exam exam = BeanUtil.copyProperties(examAddDTO, Exam.class);
        examMapper.insert(exam);
        return exam.getExamId().toString();
    }


    @Override
    public boolean questionAdd(ExamQuestionAddDTO examQuestionAddDTO) {
        //判断竞赛是否存在
        Exam exam = getExam(examQuestionAddDTO.getExamId());
        //检查需要添加的题目是否合法
        Set<Long> questionIds = examQuestionAddDTO.getQuestionIds();
        List<Question> questions = questionMapper.selectBatchIds(questionIds);
        if(CollectionUtil.isEmpty(questions) || questions.size() < questionIds.size()){
            throw new ServiceException(ResultCode.EXAM_QUESTION_NOT_EXITS);
        }
        return saveExamQuestion(exam,questionIds);
    }

    @Override
    public ExamDetailVO getDetail(Long examId) {
        //竞赛是否存在
        Exam exam = getExam(examId);
        ExamDetailVO examDetailVO = new ExamDetailVO();
        examDetailVO.setTitle(exam.getTitle());
        examDetailVO.setEndTime(exam.getEndTime());
        examDetailVO.setStartTime(exam.getStartTime());
        //查询对应题目id
        List<ExamQuestion> examQuestions = examQuestionMapper.selectList(new LambdaQueryWrapper<ExamQuestion>()
                .select(ExamQuestion::getQuestionId)
                .eq(ExamQuestion::getExamId, examId)
                .orderByAsc(ExamQuestion::getQuestionOrder));
        List<Long> list = examQuestions.stream().map(ExamQuestion::getQuestionId).toList();
        //先判断是不是为空，要不然会报sql错误
        if(!CollectionUtil.isEmpty(list)){
            //查询对应题目
            List<Question> questions = questionMapper.selectList(new LambdaQueryWrapper<Question>()
                    .select(Question::getQuestionId, Question::getTitle, Question::getDifficulty)
                    .in(Question::getQuestionId, list));
            List<QuestionVO> questionVOS = BeanUtil.copyToList(questions, QuestionVO.class);
            examDetailVO.setExamQuestionList(questionVOS);
        }
        return examDetailVO;
    }

    @Override
    public int edit(ExamEditDTO examEditDTO) {
        Exam exam = getExam(examEditDTO.getExamId());
//        if (Constants.TRUE.equals(exam.getStatus())) {
//            throw new ServiceException(ResultCode.EXAM_IS_PUBLISH);
//        }
        checkExam(exam);
        checkExamSaveParams(examEditDTO, examEditDTO.getExamId());
        exam.setTitle(examEditDTO.getTitle());
        exam.setStartTime(examEditDTO.getStartTime());
        exam.setEndTime(examEditDTO.getEndTime());
        return examMapper.updateById(exam);
    }

    @Override
    public int delete(Long examId) {
        Exam exam = getExam(examId);
        if (Constants.TRUE.equals(exam.getStatus())) {
            throw new ServiceException(ResultCode.EXAM_IS_PUBLISH);
        }
        checkExam(exam);
        examQuestionMapper.delete(new LambdaQueryWrapper<ExamQuestion>()
                .eq(ExamQuestion::getExamId, examId));
        return examMapper.deleteById(exam);
    }

    @Override
    public int questionDelete(Long examId, Long questionId) {
        Exam exam = getExam(examId);
        checkExam(exam);
        if (Constants.TRUE.equals(exam.getStatus())) {
            throw new ServiceException(ResultCode.EXAM_IS_PUBLISH);
        }
        return examQuestionMapper.delete(new LambdaQueryWrapper<ExamQuestion>()
                .eq(ExamQuestion::getExamId, examId)
                .eq(ExamQuestion::getQuestionId, questionId));
    }

    private void checkExam(Exam exam) {
        if (exam.getStartTime().isBefore(LocalDateTime.now())) {
            throw new ServiceException(ResultCode.EXAM_STARTED);
        }
    }

    private void checkExamSaveParams(ExamAddDTO examSaveDTO, Long examId) {
        //判断竞赛是否存在
        Long num = examMapper.selectCount(new LambdaQueryWrapper<Exam>()
                .eq(Exam::getTitle, examSaveDTO.getTitle())
                .ne(examId != null, Exam::getExamId, examId));
        if (num > 0) {
            throw new ServiceException(ResultCode.FAILED_ALREADY_EXISTS);
        }
        //对竞赛开始和结束时间进行判断
        if(examSaveDTO.getStartTime().isBefore(LocalDateTime.now())){
            throw new ServiceException(ResultCode.EXAM_START_TIME_BEFORE_CURRENT_TIME);
        }
        if(examSaveDTO.getStartTime().isAfter(examSaveDTO.getEndTime())){
            throw new ServiceException(ResultCode.EXAM_START_TIME_AFTER_END_TIME);
        }
    }

    private boolean saveExamQuestion(Exam exam, Set<Long> questionIds) {
        int num = 1;
        List<ExamQuestion> eqList = new ArrayList<>();
        for(Long questionId : questionIds){
            ExamQuestion eq = new ExamQuestion();
            eq.setQuestionId(questionId);
            eq.setExamId(exam.getExamId());
            eq.setQuestionOrder(num++);
            eqList.add(eq);
        }
        return saveBatch(eqList);
    }

    private Exam getExam(Long examId) {
        Exam exam = examMapper.selectById(examId);
        if(exam == null){
            throw new ServiceException(ResultCode.EXAM_NOT_EXITS);
        }
        return exam;
    }
}
