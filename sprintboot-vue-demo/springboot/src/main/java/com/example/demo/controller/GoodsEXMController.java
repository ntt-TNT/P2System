package com.example.demo.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.Result;
import com.example.demo.dao.GoodsDao;
import com.example.demo.entity.Goods;
import com.example.demo.mapper.GoodsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@CrossOrigin
@RestController
@RequestMapping("/exmG")
public class GoodsEXMController {
    @Resource
    GoodsMapper goodsMapper;
    @Autowired
    GoodsDao goodsDao;
    @PutMapping("/opeStatus/{status}")
    public Result<?> opeStatus(@RequestBody Goods goods, @PathVariable String status){
        UpdateWrapper updateWrapper = new UpdateWrapper();
        updateWrapper.eq("Gid",goods.getGid());
        if (status.equals("3")){//
            updateWrapper.set("Status",3);
        }
        else if (status.equals("4")){
            updateWrapper.set("Status",4);
        }else
            return Result.error("1","仅可为3,4");
        boolean ret = goodsDao.UpdateStatus(updateWrapper);
        if(ret){
            return Result.success();
        }else{
            return Result.error("1","update fail!");
        }
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable String id){
        if (id ==null||id ==""){
            return Result.error("1","id为空!");
        }
        //手动增加uid后插入数据库
        boolean ret = goodsDao.DeleteByID(id);
        if(ret){
            return Result.success();
        }else{
            return Result.error("1","insert fail!");
        }
    }
    @GetMapping("/findFor/{uid}")
    public Result<?> findFor(@RequestParam(defaultValue = "1") Integer pageNumber,
                             @RequestParam(defaultValue = "10") Integer pageSize,
                             @RequestParam(defaultValue = "") String searchText,
                             @RequestParam(defaultValue = "gname") String type,@PathVariable String uid){
        Page<Goods> goodsPage = new Page<>(pageNumber,pageSize);
        LambdaQueryWrapper<Goods> wrapper = Wrappers.<Goods>lambdaQuery();
        wrapper.eq(Goods::getMid,uid);
        if(StrUtil.isNotBlank(searchText)&&type.equals("gname")){
            wrapper.like(Goods::getGname, searchText);
        }else if (StrUtil.isNotBlank(searchText)&&type.equals("gid")){
            wrapper.like(Goods::getGid,searchText);
        }else if (StrUtil.isNotBlank(searchText)&&type.equals("status")){
            wrapper.like(Goods::getStatus,searchText);
        }
        goodsMapper.selectPage(goodsPage,wrapper);
        return Result.success(goodsPage);
    }
    @GetMapping
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNumber,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(defaultValue = "") String searchText,
                              @RequestParam(defaultValue = "gname") String type){
        Page<Goods> goodsPage = new Page<>(pageNumber,pageSize);
        LambdaQueryWrapper<Goods> wrapper = Wrappers.<Goods>lambdaQuery();
        if(StrUtil.isNotBlank(searchText)&&type.equals("gname")){
            wrapper.like(Goods::getGname, searchText);
        }else if (StrUtil.isNotBlank(searchText)&&type.equals("gid")){
            wrapper.like(Goods::getGid,searchText);
        }else if (StrUtil.isNotBlank(searchText)&&type.equals("status")){
            wrapper.like(Goods::getStatus,searchText);
        }
        goodsMapper.selectPage(goodsPage,wrapper);
        return Result.success(goodsPage);

    }
}
