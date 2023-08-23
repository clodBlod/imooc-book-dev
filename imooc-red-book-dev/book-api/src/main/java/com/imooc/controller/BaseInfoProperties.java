//package com.imooc.controller;
//
//import com.github.pagehelper.PageInfo;
//import com.imooc.utils.PagedGridResult;
//import org.springframework.validation.BindingResult;
//import org.springframework.validation.FieldError;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class BaseInfoProperties {
//
//    public static final Integer COMMON_START_PAGE = 1;
//    public static final Integer COMMON_PAGE_SIZE = 10;
//
//    public static final String MOBILE_SMSCODE = "mobile:smscode";
//    public static final String REDIS_USER_TOKEN = "redis_user_token";
//    // 我的关注总数
//    public static final String REDIS_MY_FOLLOWS_COUNTS = "redis_my_follows_counts";
//    // 我的粉丝总数
//    public static final String REDIS_MY_FANS_COUNTS = "redis_my_fans_counts";
//
//    // 视频和发布者获赞数
//    public static final String REDIS_VLOG_BE_LIKED_COUNTS = "redis_vlog_be_liked_counts";
//    public static final String REDIS_VLOGER_BE_LIKED_COUNTS = "redis_vloger_be_liked_counts";
//
//    // 使用优雅异常返回之后，这个方法就没用了
//    public Map getErrors(BindingResult result) {
//        HashMap<String, String> map = new HashMap<>();
//        List<FieldError> errorList = result.getFieldErrors();
//        for (FieldError ff : errorList) {
//            // 错误所对应的属性字段名
//            String field = ff.getField();
//            // 错误的信息
//            String msg = ff.getDefaultMessage();
//            map.put(field,msg);
//        }
//        return map;
//    }
//
//    // 分页返回响应体
//    public PagedGridResult setterPageGrid(List<?> list, Integer page) {
//        PageInfo<?> pageList = new PageInfo<>(list);
//        PagedGridResult pagedGridResult = new PagedGridResult();
//        pagedGridResult.setPage(page);
//        pagedGridResult.setTotal(pageList.getPages());
//        pagedGridResult.setRecords(pageList.getTotal());
//        pagedGridResult.setRows(list);
//        return pagedGridResult;
//    }
//}
