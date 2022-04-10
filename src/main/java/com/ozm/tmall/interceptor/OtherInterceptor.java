package com.ozm.tmall.interceptor;

import com.ozm.tmall.entity.pojo.Category;
import com.ozm.tmall.entity.pojo.OrderItem;
import com.ozm.tmall.entity.pojo.User;
import com.ozm.tmall.entity.service.CategoryService;
import com.ozm.tmall.entity.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

public class OtherInterceptor implements HandlerInterceptor {

    @Autowired
    OrderItemService orderItemService;
    @Autowired
    CategoryService categoryService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return true;
    }

        @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
                        int cartTotalItemNumber = 0;
        if (null != user) {
            List<OrderItem> orderItemList = orderItemService.listByUser(user);
            for (OrderItem orderItem : orderItemList) {
                cartTotalItemNumber += orderItem.getNumber();
            }
        }
                session.setAttribute("cartTotalItemNumber", cartTotalItemNumber);

                                                                        String contextPath = request.getServletContext().getContextPath();
                        request.getServletContext().setAttribute("contextPath", contextPath);

                        List<Category> cs = categoryService.list();
        request.getServletContext().setAttribute("categories_below_search", cs);

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
