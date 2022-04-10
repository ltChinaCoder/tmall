package com.ozm.tmall.web;

import com.ozm.tmall.comparator.*;
import com.ozm.tmall.entity.pojo.*;
import com.ozm.tmall.entity.service.*;
import com.ozm.tmall.util.Result;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
public class ForeRESTController {
    @Autowired
    CategoryService categoryService;
    @Autowired
    ProductService productService;
    @Autowired
    UserService userService;
    @Autowired
    ProductImageService productImageService;
    @Autowired
    PropertyValueService propertyValueService;
    @Autowired
    ReviewService reviewService;
    @Autowired
    OrderItemService orderItemService;
    @Autowired
    OrderService orderService;


        @GetMapping("/forehome")
    public Object home() throws Exception {
        List<Category> cs = categoryService.list();
        productService.fill(cs);           productService.fillByRow(cs);
        categoryService.removeCategoryFromProduct(cs);
        return cs;
    }

        @PostMapping("/foreregister")
    public Object register(@RequestBody User user) {
        String name = user.getName();
        String password = user.getPassword();
                name = HtmlUtils.htmlEscape(name);
        user.setName(name);
                boolean exist = userService.isExist(name);
        if (exist) {
            String message = "用户名已经被使用,不能使用";
                        return Result.fail(message);
        } else {
                                                            String salt = new SecureRandomNumberGenerator().nextBytes().toString();            int times = 2;             String algorithmName = "md5";            String encodedPassword = new SimpleHash(algorithmName, password, salt, times).toString();
            user.setSalt(salt);
            user.setPassword(encodedPassword);                        userService.add(user);
            return Result.success();
        }
    }

        @PostMapping("/forelogin")
    public Object login(@RequestBody User userParam, HttpSession session) {
                String name = userParam.getName();
        name = HtmlUtils.htmlEscape(name);
                Subject subject = SecurityUtils.getSubject();
                UsernamePasswordToken token = new UsernamePasswordToken(name, userParam.getPassword());
        try {
            subject.login(token);             User user = userService.getByName(name);
                        session.setAttribute("user", user);
            return Result.success();
        } catch (AuthenticationException e) {
            String message = "账号密码错误";
            return Result.fail(message);
        }



    }

        @GetMapping("/foreproduct/{pid}")
    public Object product(@PathVariable("pid") int pid) {
                Product product = productService.get(pid);
        List<ProductImage> images_single = productImageService.listSingleProductImages(product);
        List<ProductImage> images_detail = productImageService.listDetailProductImages(product);
        product.setProductSingleImages(images_single);
        product.setProductDetailImages(images_detail);
        List<PropertyValue> propertyValues = propertyValueService.list(product);        List<Review> reviews = reviewService.list(product);        productService.setSaleAndReviewNumber(product);         productImageService.setFirstProdutImage(product);                Map<String, Object> map = new HashMap<>();
        map.put("product", product);
        map.put("pvs", propertyValues);        map.put("reviews", reviews);
        return Result.success(map);
    }


        @GetMapping("/forecheckLogin")
    public Object checkLogin(HttpSession session) {
                Subject subject = SecurityUtils.getSubject();         if (subject.isAuthenticated()) {            return Result.success();
        } else {
            return Result.fail("未登录");
        }

    }


        @GetMapping("/forecategory/{cid}")
    public Object category(@PathVariable int cid, String sort) {
        Category category = categoryService.get(cid);
                                productService.fill(category);
        productService.setSaleAndReviewNumber(category.getProducts());
                categoryService.removeCategoryFromProduct(category);
                        if (null != sort) {
            switch (sort) {
                case "review":
                    Collections.sort(category.getProducts(), new ProductReviewComparator());
                    break;
                case "date":
                    Collections.sort(category.getProducts(), new ProductDateComparator());
                    break;

                case "saleCount":
                    Collections.sort(category.getProducts(), new ProductSaleCountComparator());
                    break;

                case "price":
                    Collections.sort(category.getProducts(), new ProductPriceComparator());
                    break;

                case "all":
                    Collections.sort(category.getProducts(), new ProductAllComparator());
                    break;
            }
        }

        return category;
    }


        @PostMapping("/foresearch")
    public Object search(@RequestParam("keyword") String keyword) {
                if (null == keyword)
            keyword = "";
                List<Product> ps = productService.search(keyword, 0, 20);
                productImageService.setFirstProdutImages(ps);
        productService.setSaleAndReviewNumber(ps);
        return ps;
    }

        @GetMapping("/forebuyone")
    public Object buyone(@RequestParam("pid") int pid, @RequestParam("num") int num, HttpSession session) {
                                        return buyoneAndAddCart(pid, num, session);
    }

                    @GetMapping("foreaddCart")
    public Object addCart(int pid, int num, HttpSession session) {
        buyoneAndAddCart(pid, num, session);
        return Result.success();
    }


        private int buyoneAndAddCart(int pid, int num, HttpSession session) {

        Product p = productService.get(pid);
        User user = (User) session.getAttribute("user");
        boolean found = false;         int oiid = 0;
                                List<OrderItem> orderItemList = orderItemService.listByUser(user);
        for (OrderItem orderItem : orderItemList) {
                        if (orderItem.getProduct().getId() == p.getId()) {
                orderItem.setNumber(orderItem.getNumber() + num);
                                orderItemService.update(orderItem);
                found = true;
                oiid = orderItem.getId();
                break;
            }
        }

                if (!found) {
            OrderItem orderItem = new OrderItem();
            orderItem.setUser(user);
            orderItem.setProduct(p);
            orderItem.setNumber(num);
            orderItemService.add(orderItem);
            oiid = orderItem.getId();
        }

        return oiid;

    }

        @GetMapping("/forebuy")
    public Object buy(String[] oiid, HttpSession session) {
                                List<OrderItem> orderItems = new ArrayList<>();         float total = 0;        for (String strid : oiid) {
            int id = Integer.parseInt(strid);
            OrderItem oi = orderItemService.get(id);
            total += oi.getProduct().getPromotePrice() * oi.getNumber();
                        orderItems.add(oi);
        }
                productImageService.setFirstProdutImagesOnOrderItems(orderItems);
                session.setAttribute("ois", orderItems);

        Map<String, Object> map = new HashMap<>();
        map.put("orderItems", orderItems);
        map.put("total", total);
        return Result.success(map);


    }


        @GetMapping("/forecart")
    public Object cart(HttpSession session) {
                User user = (User) session.getAttribute("user");
                List<OrderItem> orderItemList = orderItemService.listByUser(user);
                productImageService.setFirstProdutImagesOnOrderItems(orderItemList);
        return orderItemList;
    }


        @GetMapping("/foredeleteOrderItem")
    public Object deleteOrderItem(@RequestParam("oiid") int oiid, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (null == user)             return Result.fail("未登录");
        orderItemService.delete(oiid);
        return Result.success();
    }

        @GetMapping("/forechangeOrderItem")
    public Object changeOrderItem(@RequestParam("pid") int pid, @RequestParam("num") int num, HttpSession session) {
                Product product = productService.get(pid);
        User user = (User) session.getAttribute("user");
                if (null == user)
            return Result.fail("未登录");
        List<OrderItem> orderItemList = orderItemService.listByUser(user);
        for (OrderItem orderItem : orderItemList) {
            if (orderItem.getProduct().getId() == product.getId()) {
                orderItem.setNumber(num);
                                orderItemService.update(orderItem);
                break;
            }
        }
        return Result.success();
    }

        @PostMapping("/forecreateOrder")
    public Object createOrder(@RequestBody Order order, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (null == user) {
            return Result.fail("未登录");
        }

        String orderCode = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + RandomUtils.nextInt(10000);
        order.setOrderCode(orderCode);
        order.setCreateDate(new Date());
        order.setUser(user);         order.setStatus(OrderService.waitPay);
                List<OrderItem> orderItemList = (List<OrderItem>) session.getAttribute("ois");
        float total = orderService.add(order, orderItemList);

        Map<String, Object> map = new HashMap<>();
        map.put("oid", order.getId());
        map.put("total", total);

        return Result.success(map);

    }

        @GetMapping("/forepayed")
    public Object payed(@RequestParam("oid") int oid) {
                Order order = orderService.get(oid);
        order.setStatus(OrderService.waitDelivery);
        order.setPayDate(new Date());
        orderService.update(order);
        return order;
    }

        @GetMapping("/forebought")
    public Object bought(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (null == user)
            return Result.fail("未登录");
        List<Order> os = orderService.listByUserWithoutDelete(user);
                orderService.removeOrderFromOrderItem(os);
        return os;
    }

            @GetMapping(value = "/foreconfirmPay")
    public Object confirmPay(int oid) {
        Order order = orderService.get(oid);
                orderItemService.fill(order);
                orderService.cacl(order);
        orderService.removeOrderFromOrderItem(order);
        return order;
    }


            @GetMapping(value = "/foreorderConfirmed")
    public Object orderConfirmed(@RequestParam("oid") int oid) {
                Order order = orderService.get(oid);
        order.setStatus(OrderService.waitReview);
        order.setConfirmDate(new Date());
        orderService.update(order);
        return order;
    }

        @PutMapping(value = "/foredeleteOrder")
    public Object deleteOrder(@RequestParam("oid") int oid) {
        Order o = orderService.get(oid);
        o.setStatus(OrderService.delete);
        orderService.update(o);
        return Result.success();
    }

        @GetMapping(value = "/forereview")
    public Object review(@RequestParam("oid") int oid) {
                Order order = orderService.get(oid);
        orderItemService.fill(order);
        orderService.removeOrderFromOrderItem(order);
        Product product = order.getOrderItems().get(0).getProduct();
                List<Review> reviews = reviewService.list(product);
                productService.setSaleAndReviewNumber(product);
        Map<String, Object> map = new HashMap<>();
        map.put("p", product);
        map.put("o", order);
        map.put("reviews", reviews);

        return Result.success(map);
    }

        @PostMapping(value = "/foredoreview")
    public Object doreview(@RequestParam("oid") int oid,
                           @RequestParam("pid") int pid,
                           @RequestParam("content") String content,
                           HttpSession session
    ) {
                Order order = orderService.get(oid);
        order.setStatus(OrderService.finish);
                orderService.update(order);
                content = HtmlUtils.htmlEscape(content);
                        Review review = new Review();
        Product product = productService.get(pid);
        User user = (User) session.getAttribute("user");
        review.setProduct(product);
        review.setContent(content);
        review.setCreateDate(new Date());
        review.setUser(user);
                reviewService.add(review);
        return Result.success();

    }


}
