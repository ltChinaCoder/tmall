package com.ozm.tmall.entity.controller;

import com.ozm.tmall.entity.pojo.Category;
import com.ozm.tmall.entity.service.CategoryService;
import com.ozm.tmall.util.ImageUtil;
import com.ozm.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@RestController  public class CategoryController {

    @Autowired
    CategoryService categoryService;

        
    @GetMapping("/categories")
    public Page4Navigator<Category> list(
            @RequestParam(value = "start", defaultValue = "0") int start,
            @RequestParam(value = "szie", defaultValue = "5") int size) throws Exception {
        start = start < 0 ? 0 : start;
                Page4Navigator<Category> page = categoryService.list(start, size, 5);
        return page;
    }

        @PostMapping("/categories")
        public Object list(Category bean, MultipartFile image, HttpServletRequest request) throws IOException {
                categoryService.add(bean);
                                saveOrUpdateImageFile(bean, image, request);
        return bean;
    }

        public void saveOrUpdateImageFile(Category bean, MultipartFile image, HttpServletRequest request)
            throws IOException {
        File imageFolder = new File(request.getServletContext().getRealPath("img/category"));
        File file = new File(imageFolder, bean.getId() + ".jpg");
        if (!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        image.transferTo(file);
        BufferedImage img = ImageUtil.change2jpg(file);
        ImageIO.write(img, "jpg", file);
    }

        @DeleteMapping("/categories/{id}")
    public String delete(@PathVariable("id") int id, HttpServletRequest request) {
        categoryService.delete(id);
                File imageFolder = new File(request.getServletContext().getRealPath("img/category"));
        File file = new File(imageFolder, id + ".jpg");
        file.delete();
        return null;
    }

        @GetMapping("/categories/{id}")
    public Category get(@PathVariable("id") int id) throws Exception {
        Category bean = categoryService.get(id);
        return bean;
    }

        @PutMapping("/categories/{id}")
            public Category update(Category bean, MultipartFile image, HttpServletRequest httpServletRequest) throws IOException {
        String name = httpServletRequest.getParameter("name");
        bean.setName(name);
        categoryService.update(bean);
                if (image != null) {
            saveOrUpdateImageFile(bean, image, httpServletRequest);
        }
        return bean;
    }


}
