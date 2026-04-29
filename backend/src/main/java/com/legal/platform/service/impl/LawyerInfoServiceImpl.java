package com.legal.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.legal.platform.common.PageResult;
import com.legal.platform.common.Result;
import com.legal.platform.entity.ConsultOrder;
import com.legal.platform.entity.DocumentOrder;
import com.legal.platform.entity.IncomeRecord;
import com.legal.platform.entity.LawyerInfo;
import com.legal.platform.entity.ServiceEvaluation;
import com.legal.platform.entity.SysUser;
import com.legal.platform.mapper.ConsultOrderMapper;
import com.legal.platform.mapper.DocumentOrderMapper;
import com.legal.platform.mapper.IncomeRecordMapper;
import com.legal.platform.mapper.LawyerInfoMapper;
import com.legal.platform.mapper.ServiceEvaluationMapper;
import com.legal.platform.mapper.SysUserMapper;
import com.legal.platform.service.LawyerInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class LawyerInfoServiceImpl extends ServiceImpl<LawyerInfoMapper, LawyerInfo> implements LawyerInfoService {

    private static final List<MatchRule> MATCH_RULES = buildMatchRules();

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private ConsultOrderMapper consultOrderMapper;

    @Autowired
    private ServiceEvaluationMapper serviceEvaluationMapper;

    @Autowired
    private IncomeRecordMapper incomeRecordMapper;

    @Autowired
    private DocumentOrderMapper documentOrderMapper;

    @Override
    public PageResult<LawyerInfo> getLawyerList(Integer page, Integer size, String keyword, String expertise, Integer status) {
        LambdaQueryWrapper<LawyerInfo> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(LawyerInfo::getLawyerName, keyword)
                    .or().like(LawyerInfo::getLawFirm, keyword));
        }
        if (StringUtils.hasText(expertise)) {
            wrapper.like(LawyerInfo::getExpertises, expertise);
        }
        if (status != null) {
            wrapper.eq(LawyerInfo::getStatus, status);
        }
        wrapper.orderByDesc(LawyerInfo::getRating);
        IPage<LawyerInfo> pageResult = this.page(new Page<>(page, size), wrapper);
        return PageResult.of(pageResult.getRecords(), pageResult.getTotal(), pageResult.getSize(), pageResult.getCurrent());
    }

    @Override
    public Result<Map<String, Object>> intelligentMatch(String description, Integer limit) {
        if (!StringUtils.hasText(description)) {
            return Result.error("请输入法律问题描述");
        }

        int matchLimit = limit == null || limit <= 0 ? 6 : Math.min(limit, 20);
        String text = normalize(description);
        MatchHit bestHit = findBestMatch(text);
        MatchRule rule = bestHit.getRule();
        List<String> matchedKeywords = getMatchedKeywords(text, rule);

        LambdaQueryWrapper<LawyerInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LawyerInfo::getStatus, 1)
                .eq(LawyerInfo::getIsActive, 1)
                .orderByDesc(LawyerInfo::getRating)
                .orderByDesc(LawyerInfo::getServiceCount);
        List<LawyerInfo> lawyers = this.list(wrapper);

        List<Map<String, Object>> matchedLawyers = new ArrayList<>();
        for (LawyerInfo lawyer : lawyers) {
            double score = calculateLawyerScore(lawyer, rule, matchedKeywords);
            if (score > 0) {
                matchedLawyers.add(toLawyerMatchMap(lawyer, score, buildMatchReason(lawyer, rule, matchedKeywords, false)));
            }
        }

        Collections.sort(matchedLawyers, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> left, Map<String, Object> right) {
                return Double.compare((Double) right.get("matchScore"), (Double) left.get("matchScore"));
            }
        });

        if (matchedLawyers.isEmpty()) {
            for (LawyerInfo lawyer : lawyers) {
                double score = calculateFallbackScore(lawyer);
                matchedLawyers.add(toLawyerMatchMap(lawyer, score, buildMatchReason(lawyer, rule, matchedKeywords, true)));
                if (matchedLawyers.size() >= matchLimit) {
                    break;
                }
            }
        } else if (matchedLawyers.size() > matchLimit) {
            matchedLawyers = new ArrayList<>(matchedLawyers.subList(0, matchLimit));
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("caseType", rule.getCaseType());
        result.put("expertise", rule.getExpertise());
        result.put("confidence", calculateConfidence(bestHit.getScore()));
        result.put("matchedKeywords", matchedKeywords);
        result.put("description", rule.getDescription());
        result.put("suggestion", rule.getSuggestion());
        result.put("lawyers", matchedLawyers);
        result.put("total", matchedLawyers.size());
        return Result.success(result);
    }

    @Override
    public Result<LawyerInfo> getLawyerDetail(Long id) {
        LawyerInfo lawyerInfo = this.getById(id);
        if (lawyerInfo == null) {
            return Result.error("律师不存在");
        }
        return Result.success(lawyerInfo);
    }

    @Override
    public Result<LawyerInfo> getLawyerInfoByUserId(Long userId) {
        LambdaQueryWrapper<LawyerInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LawyerInfo::getUserId, userId);
        LawyerInfo lawyerInfo = this.getOne(wrapper);
        if (lawyerInfo == null) {
            return Result.error("律师信息不存在");
        }
        return Result.success(lawyerInfo);
    }

    @Override
    public Result<Boolean> applyLawyer(LawyerInfo lawyerInfo, Long userId) {
        LambdaQueryWrapper<LawyerInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LawyerInfo::getUserId, userId);
        if (this.count(wrapper) > 0) {
            return Result.error("您已申请过律师认证");
        }
        lawyerInfo.setUserId(userId);
        lawyerInfo.setStatus(0);
        this.save(lawyerInfo);
        SysUser user = sysUserMapper.selectById(userId);
        user.setRole("lawyer");
        sysUserMapper.updateById(user);
        return Result.success("申请成功，请等待审核", true);
    }

    @Override
    public Result<Boolean> auditLawyer(Long id, Integer status, String rejectReason) {
        LawyerInfo lawyerInfo = this.getById(id);
        if (lawyerInfo == null) {
            return Result.error("律师不存在");
        }
        lawyerInfo.setStatus(status);
        if (status == 2 && StringUtils.hasText(rejectReason)) {
            lawyerInfo.setRejectReason(rejectReason);
        }
        this.updateById(lawyerInfo);
        return Result.success("审核完成", true);
    }

    @Override
    public Result<Boolean> updateLawyerInfo(LawyerInfo lawyerInfo) {
        LawyerInfo existLawyer = this.getById(lawyerInfo.getId());
        if (existLawyer == null) {
            return Result.error("律师不存在");
        }
        existLawyer.setLawyerName(lawyerInfo.getLawyerName());
        existLawyer.setPhone(lawyerInfo.getPhone());
        existLawyer.setEmail(lawyerInfo.getEmail());
        existLawyer.setProvince(lawyerInfo.getProvince());
        existLawyer.setCity(lawyerInfo.getCity());
        existLawyer.setLawFirm(lawyerInfo.getLawFirm());
        existLawyer.setEducation(lawyerInfo.getEducation());
        existLawyer.setProfessionalTitle(lawyerInfo.getProfessionalTitle());
        existLawyer.setProfile(lawyerInfo.getProfile());
        existLawyer.setExpertises(lawyerInfo.getExpertises());
        existLawyer.setConsultPrice(lawyerInfo.getConsultPrice());
        existLawyer.setDeepConsultPrice(lawyerInfo.getDeepConsultPrice());
        existLawyer.setDocumentPrice(lawyerInfo.getDocumentPrice());
        existLawyer.setAuditPrice(lawyerInfo.getAuditPrice());
        existLawyer.setVideoPrice(lawyerInfo.getVideoPrice());
        this.updateById(existLawyer);
        SysUser user = sysUserMapper.selectById(existLawyer.getUserId());
        if (user != null) {
            user.setRealName(lawyerInfo.getLawyerName());
            user.setPhone(lawyerInfo.getPhone());
            user.setEmail(lawyerInfo.getEmail());
            user.setAvatar(lawyerInfo.getAvatar());
            sysUserMapper.updateById(user);
        }
        return Result.success("更新成功", true);
    }

    @Override
    public Result<Map<String, Object>> getLawyerStatistics(Long lawyerId) {
        Map<String, Object> stats = new HashMap<>();
        Set<Long> lawyerIds = getPossibleLawyerIds(lawyerId);
        
        LambdaQueryWrapper<ConsultOrder> pendingWrapper = new LambdaQueryWrapper<>();
        pendingWrapper.in(ConsultOrder::getLawyerId, lawyerIds)
                .in(ConsultOrder::getStatus, 1, 2, 3);
        long pendingOrders = consultOrderMapper.selectCount(pendingWrapper);

        LambdaQueryWrapper<DocumentOrder> pendingDocumentWrapper = new LambdaQueryWrapper<>();
        pendingDocumentWrapper.in(DocumentOrder::getLawyerId, lawyerIds)
                .in(DocumentOrder::getStatus, 1, 2);
        pendingOrders += documentOrderMapper.selectCount(pendingDocumentWrapper);
        stats.put("pendingOrders", pendingOrders);
        
        LambdaQueryWrapper<ConsultOrder> completedWrapper = new LambdaQueryWrapper<>();
        completedWrapper.in(ConsultOrder::getLawyerId, lawyerIds).eq(ConsultOrder::getStatus, 4);
        long completedOrders = consultOrderMapper.selectCount(completedWrapper);

        LambdaQueryWrapper<DocumentOrder> completedDocumentWrapper = new LambdaQueryWrapper<>();
        completedDocumentWrapper.in(DocumentOrder::getLawyerId, lawyerIds).eq(DocumentOrder::getStatus, 3);
        completedOrders += documentOrderMapper.selectCount(completedDocumentWrapper);
        stats.put("completedOrders", completedOrders);
        
        LambdaQueryWrapper<ServiceEvaluation> evalWrapper = new LambdaQueryWrapper<>();
        evalWrapper.eq(ServiceEvaluation::getLawyerId, lawyerId);
        List<ServiceEvaluation> evaluations = serviceEvaluationMapper.selectList(evalWrapper);
        double avgScore = evaluations.stream().mapToInt(ServiceEvaluation::getTotalScore).average().orElse(5.0);
        stats.put("avgScore", Math.round(avgScore * 100.0) / 100.0);
        stats.put("evalCount", evaluations.size());
        
        LambdaQueryWrapper<IncomeRecord> allIncomeWrapper = new LambdaQueryWrapper<>();
        allIncomeWrapper.eq(IncomeRecord::getLawyerId, lawyerId);
        List<IncomeRecord> allIncomeRecords = incomeRecordMapper.selectList(allIncomeWrapper);
        
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal pendingIncome = BigDecimal.ZERO;
        BigDecimal settledIncome = BigDecimal.ZERO;
        BigDecimal monthIncome = BigDecimal.ZERO;
        
        java.time.LocalDate now = java.time.LocalDate.now();
        java.time.LocalDateTime monthStart = now.withDayOfMonth(1).atStartOfDay();
        
        for (IncomeRecord record : allIncomeRecords) {
            if (record.getIncomeAmount() != null) {
                totalIncome = totalIncome.add(record.getIncomeAmount());
                
                int status = record.getStatus() != null ? record.getStatus() : 1;
                if (status == 1) {
                    pendingIncome = pendingIncome.add(record.getIncomeAmount());
                } else {
                    settledIncome = settledIncome.add(record.getIncomeAmount());
                }
                
                if (record.getCreateTime() != null && !record.getCreateTime().isBefore(monthStart)) {
                    monthIncome = monthIncome.add(record.getIncomeAmount());
                }
            }
        }
        
        stats.put("totalIncome", totalIncome);
        stats.put("pendingIncome", pendingIncome);
        stats.put("settledIncome", settledIncome);
        stats.put("monthIncome", monthIncome);
        
        return Result.success(stats);
    }

    private Set<Long> getPossibleLawyerIds(Long userId) {
        Set<Long> lawyerIds = new HashSet<>();
        if (userId == null) {
            return lawyerIds;
        }
        lawyerIds.add(userId);

        LambdaQueryWrapper<LawyerInfo> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(LawyerInfo::getUserId, userId);
        LawyerInfo lawyerByUserId = this.getOne(userWrapper);
        if (lawyerByUserId != null) {
            if (lawyerByUserId.getId() != null) {
                lawyerIds.add(lawyerByUserId.getId());
            }
            if (lawyerByUserId.getUserId() != null) {
                lawyerIds.add(lawyerByUserId.getUserId());
            }
        }

        LawyerInfo lawyerById = this.getById(userId);
        if (lawyerById != null) {
            if (lawyerById.getId() != null) {
                lawyerIds.add(lawyerById.getId());
            }
            if (lawyerById.getUserId() != null) {
                lawyerIds.add(lawyerById.getUserId());
            }
        }

        return lawyerIds;
    }

    @Override
    public Result<Map<String, Object>> getAuditStatus(Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("auditStatus", user.getAuditStatus());
        result.put("auditRemark", user.getAuditRemark());
        result.put("role", user.getRole());
        result.put("realName", user.getRealName());
        result.put("phone", user.getPhone());
        result.put("email", user.getEmail());
        
        if (user.getAuditStatus() != null && user.getAuditStatus() == 2) {
            LambdaQueryWrapper<LawyerInfo> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(LawyerInfo::getUserId, userId);
            LawyerInfo lawyerInfo = this.getOne(wrapper);
            if (lawyerInfo != null) {
                result.put("lawyerInfo", lawyerInfo);
            }
        }
        
        return Result.success(result);
    }

    @Override
    public Result<Boolean> resubmitAudit(Long userId, Map<String, Object> data) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        if (user.getAuditStatus() == null || user.getAuditStatus() != 3) {
            return Result.error("当前状态不允许重新提交审核");
        }
        
        if (data.containsKey("realName")) {
            user.setRealName((String) data.get("realName"));
        }
        if (data.containsKey("phone")) {
            user.setPhone((String) data.get("phone"));
        }
        if (data.containsKey("email")) {
            user.setEmail((String) data.get("email"));
        }
        user.setAuditStatus(1);
        user.setAuditRemark(null);
        sysUserMapper.updateById(user);
        
        return Result.success("重新提交成功，请等待审核", true);
    }

    private static List<MatchRule> buildMatchRules() {
        List<MatchRule> rules = new ArrayList<>();
        rules.add(new MatchRule(
                "未成年人监护权",
                "未成年人监护权",
                "问题涉及未成年人监护、抚养安排、探望权或监护人变更。",
                "建议准备户口簿、出生证明、离婚协议或判决书、孩子生活照护记录等材料。",
                Arrays.asList("监护权", "监护人", "变更监护", "抚养权", "抚养费", "探望权", "探视", "孩子归谁", "未成年子女", "离婚后孩子", "指定监护", "撤销监护"),
                Arrays.asList("未成年人监护权", "监护权", "抚养权", "未成年人保护", "婚姻家庭")
        ));
        rules.add(new MatchRule(
                "校园欺凌",
                "校园欺凌",
                "问题涉及在校未成年人被殴打、辱骂、孤立、勒索或网络欺凌。",
                "建议优先固定证据，保留聊天记录、伤情照片、就医记录、学校沟通记录，并及时要求学校介入。",
                Arrays.asList("校园欺凌", "霸凌", "校园霸凌", "同学欺负", "被欺负", "殴打", "辱骂", "孤立", "勒索", "威胁", "校方", "学校", "班主任", "老师不处理", "网络暴力", "学生受伤"),
                Arrays.asList("校园欺凌", "教育纠纷", "学生权益", "未成年人保护", "犯罪预防")
        ));
        rules.add(new MatchRule(
                "家庭暴力",
                "家庭暴力",
                "问题涉及家庭成员之间的殴打、威胁、虐待或人身安全风险。",
                "如存在持续伤害风险，建议先报警、就医验伤，并咨询律师申请人身安全保护令。",
                Arrays.asList("家庭暴力", "家暴", "被打", "殴打", "虐待", "威胁", "恐吓", "人身安全保护令", "报警", "验伤", "施暴", "未成年人遭家暴"),
                Arrays.asList("家庭暴力", "婚姻家庭", "未成年人保护", "人身损害")
        ));
        rules.add(new MatchRule(
                "家庭财产继承",
                "财产继承",
                "问题涉及遗产分割、遗嘱效力、房产继承或家庭财产继承安排。",
                "建议整理亲属关系证明、死亡证明、遗嘱、房产证、银行流水和其他遗产线索。",
                Arrays.asList("财产继承", "家庭财产继承", "继承", "遗产", "遗嘱", "法定继承", "房产继承", "遗产分割", "继承人", "父母去世", "过户", "遗赠", "遗留财产"),
                Arrays.asList("家庭财产继承", "财产继承", "继承", "婚姻家庭")
        ));
        rules.add(new MatchRule(
                "离婚纠纷",
                "离婚纠纷",
                "问题涉及离婚方式、夫妻共同财产、子女抚养或离婚后的权益安排。",
                "建议梳理婚姻登记信息、财产清单、债务凭证、子女照护情况和沟通记录。",
                Arrays.asList("离婚", "协议离婚", "诉讼离婚", "夫妻共同财产", "财产分割", "彩礼", "婚姻", "抚养费", "净身出户", "出轨", "分居"),
                Arrays.asList("离婚纠纷", "婚姻家庭", "财产继承", "未成年人监护权")
        ));
        rules.add(new MatchRule(
                "刑事辩护",
                "刑事辩护",
                "问题涉及刑事立案、拘留、取保候审、未成年人犯罪或刑事责任。",
                "建议尽快确认办案机关、案由、强制措施时间，并由家属委托律师会见。",
                Arrays.asList("刑事", "犯罪", "拘留", "取保候审", "逮捕", "派出所", "未成年人犯罪", "盗窃", "抢劫", "故意伤害", "辩护", "刑责", "看守所"),
                Arrays.asList("刑事辩护", "犯罪预防", "未成年人保护")
        ));
        rules.add(new MatchRule(
                "教育纠纷",
                "教育纠纷",
                "问题涉及学校管理、学生权益保护、教育机构责任或校内安全事故。",
                "建议保留学校通知、沟通记录、监控线索、医疗票据和相关规章制度。",
                Arrays.asList("学校责任", "教育纠纷", "学生权益", "校内受伤", "退学", "处分", "转学", "学籍", "补课机构", "培训机构", "老师体罚", "校车"),
                Arrays.asList("教育纠纷", "学生权益", "校园欺凌", "未成年人保护")
        ));
        rules.add(new MatchRule(
                "综合法律咨询",
                "未成年人保护",
                "暂未识别到明确案件类型，可先由综合能力较强的律师进行初步判断。",
                "建议补充事件时间、地点、涉及人员、已有证据和希望达成的结果。",
                Arrays.asList(),
                Arrays.asList("未成年人保护", "法律咨询", "学生权益", "婚姻家庭")
        ));
        return rules;
    }

    private MatchHit findBestMatch(String text) {
        MatchRule defaultRule = MATCH_RULES.get(MATCH_RULES.size() - 1);
        MatchRule bestRule = defaultRule;
        int bestScore = 0;

        for (MatchRule rule : MATCH_RULES) {
            if (rule == defaultRule) {
                continue;
            }
            int score = 0;
            for (String keyword : rule.getKeywords()) {
                if (contains(text, keyword)) {
                    score += keywordWeight(keyword);
                }
            }
            for (String alias : rule.getAliases()) {
                if (contains(text, alias)) {
                    score += keywordWeight(alias);
                }
            }
            if (score > bestScore) {
                bestScore = score;
                bestRule = rule;
            }
        }

        return new MatchHit(bestRule, bestScore);
    }

    private List<String> getMatchedKeywords(String text, MatchRule rule) {
        List<String> matched = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        for (String keyword : rule.getKeywords()) {
            if (contains(text, keyword) && seen.add(keyword)) {
                matched.add(keyword);
            }
        }
        for (String alias : rule.getAliases()) {
            if (contains(text, alias) && seen.add(alias)) {
                matched.add(alias);
            }
        }
        return matched;
    }

    private double calculateLawyerScore(LawyerInfo lawyer, MatchRule rule, List<String> matchedKeywords) {
        String lawyerText = normalize(safe(lawyer.getExpertises()) + " " + safe(lawyer.getProfile()) + " " + safe(lawyer.getLawFirm()));
        double score = 0;
        Set<String> seenAliases = new HashSet<>();

        for (String alias : rule.getAliases()) {
            if (seenAliases.add(alias) && contains(lawyerText, alias)) {
                score += alias.equals(rule.getExpertise()) ? 50 : 36;
            }
        }
        if (contains(lawyerText, rule.getCaseType())) {
            score += 45;
        }
        for (String keyword : matchedKeywords) {
            if (contains(lawyerText, keyword)) {
                score += keywordWeight(keyword) * 4;
            }
        }

        if (score > 0) {
            score += ratingScore(lawyer) + serviceScore(lawyer);
        }
        return Math.round(score * 10.0) / 10.0;
    }

    private double calculateFallbackScore(LawyerInfo lawyer) {
        return Math.round((ratingScore(lawyer) + serviceScore(lawyer)) * 10.0) / 10.0;
    }

    private double ratingScore(LawyerInfo lawyer) {
        BigDecimal rating = lawyer.getRating();
        return rating == null ? 0 : rating.doubleValue() * 8;
    }

    private double serviceScore(LawyerInfo lawyer) {
        int serviceCount = lawyer.getServiceCount() == null ? 0 : lawyer.getServiceCount();
        int caseCount = lawyer.getCaseCount() == null ? 0 : lawyer.getCaseCount();
        return Math.min(serviceCount, 500) / 20.0 + Math.min(caseCount, 300) / 30.0;
    }

    private Map<String, Object> toLawyerMatchMap(LawyerInfo lawyer, double score, String reason) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", lawyer.getId());
        item.put("userId", lawyer.getUserId());
        item.put("lawyerName", lawyer.getLawyerName());
        item.put("avatar", lawyer.getAvatar());
        item.put("lawFirm", lawyer.getLawFirm());
        item.put("province", lawyer.getProvince());
        item.put("city", lawyer.getCity());
        item.put("profile", lawyer.getProfile());
        item.put("expertises", lawyer.getExpertises());
        item.put("consultPrice", lawyer.getConsultPrice());
        item.put("rating", lawyer.getRating());
        item.put("serviceCount", lawyer.getServiceCount());
        item.put("caseCount", lawyer.getCaseCount());
        item.put("matchScore", score);
        item.put("matchReason", reason);
        return item;
    }

    private String buildMatchReason(LawyerInfo lawyer, MatchRule rule, List<String> matchedKeywords, boolean fallback) {
        if (fallback) {
            return "该律师综合评分和服务量较高，可先进行初步咨询。";
        }

        List<String> reasons = new ArrayList<>();
        String matchedExpertise = findMatchedExpertise(lawyer, rule);
        if (StringUtils.hasText(matchedExpertise)) {
            reasons.add("擅长" + matchedExpertise);
        } else {
            reasons.add("与" + rule.getCaseType() + "方向匹配");
        }
        if (!matchedKeywords.isEmpty()) {
            reasons.add("命中关键词：" + joinFirst(matchedKeywords, 3));
        }
        return String.join("；", reasons);
    }

    private String findMatchedExpertise(LawyerInfo lawyer, MatchRule rule) {
        String expertises = safe(lawyer.getExpertises());
        String normalizedExpertises = normalize(expertises);
        for (String alias : rule.getAliases()) {
            if (contains(normalizedExpertises, alias)) {
                return alias;
            }
        }
        return contains(normalizedExpertises, rule.getCaseType()) ? rule.getCaseType() : "";
    }

    private String joinFirst(List<String> values, int limit) {
        List<String> result = new ArrayList<>();
        for (String value : values) {
            result.add(value);
            if (result.size() >= limit) {
                break;
            }
        }
        return String.join("、", result);
    }

    private int calculateConfidence(int score) {
        if (score <= 0) {
            return 35;
        }
        return Math.min(96, 50 + score * 4);
    }

    private static boolean contains(String source, String target) {
        return StringUtils.hasText(target) && source.contains(normalize(target));
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }

    private static int keywordWeight(String keyword) {
        int length = keyword == null ? 0 : keyword.length();
        if (length >= 5) {
            return 5;
        }
        if (length >= 3) {
            return 4;
        }
        if (length >= 2) {
            return 2;
        }
        return 1;
    }

    private static class MatchRule {
        private final String caseType;
        private final String expertise;
        private final String description;
        private final String suggestion;
        private final List<String> keywords;
        private final List<String> aliases;

        MatchRule(String caseType, String expertise, String description, String suggestion, List<String> keywords, List<String> aliases) {
            this.caseType = caseType;
            this.expertise = expertise;
            this.description = description;
            this.suggestion = suggestion;
            this.keywords = keywords;
            this.aliases = aliases;
        }

        String getCaseType() {
            return caseType;
        }

        String getExpertise() {
            return expertise;
        }

        String getDescription() {
            return description;
        }

        String getSuggestion() {
            return suggestion;
        }

        List<String> getKeywords() {
            return keywords;
        }

        List<String> getAliases() {
            return aliases;
        }
    }

    private static class MatchHit {
        private final MatchRule rule;
        private final int score;

        MatchHit(MatchRule rule, int score) {
            this.rule = rule;
            this.score = score;
        }

        MatchRule getRule() {
            return rule;
        }

        int getScore() {
            return score;
        }
    }
}
