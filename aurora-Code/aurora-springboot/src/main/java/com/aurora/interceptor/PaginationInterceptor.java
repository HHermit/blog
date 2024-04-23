package com.aurora.interceptor;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.aurora.util.PageUtil;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;
import java.util.Optional;

import static com.aurora.constant.CommonConstant.*;

/**
 *  @description: 分页拦截器
 *
 *  这个设置的PageUtil，在这个线程（一个请求流程）一直存在。
 *  
*/
@Component
@SuppressWarnings("all")
public class PaginationInterceptor implements HandlerInterceptor {

    /**
     *  @description: preHandle方法，在请求处理之前被调用
     *                获取请求中的CURRENT和SIZE参数的值，封装成PageUtil对象，方便后边的分页处理
     *  @param: [request, response, handler]
     *  @return: boolean
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String currentPage = request.getParameter(CURRENT);
        //如没有获取到size，就使用默认值
        String pageSize = Optional.ofNullable(request.getParameter(SIZE)).orElse(DEFAULT_SIZE);
        //检查currentPage是否 存在 且  不为空
        if (!Objects.isNull(currentPage) && !StringUtils.isEmpty(currentPage)) {
            PageUtil.setCurrentPage(new Page<>(Long.parseLong(currentPage), Long.parseLong(pageSize)));
        }
        return true;
    }


    /**
     *  @description: afterCompletion方法，在请求处理之后被调用，但是在视图被渲染之前
     *                移除本次请求当前线程所建立的PageUtil对象，不影响下一次请求的分页处理
     *  @param: [request, response, handler, ex]
     *  @return: void
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        PageUtil.remove();
    }

}