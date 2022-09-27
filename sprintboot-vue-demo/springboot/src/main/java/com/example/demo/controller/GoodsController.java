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
@RequestMapping("/goods")
public class GoodsController {
    @Resource
    GoodsMapper goodsMapper;
    @Autowired
    GoodsDao goodsDao;

    @GetMapping
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(defaultValue = "") String search,
                              @RequestParam(defaultValue = "好评率") String sort){
        LambdaQueryWrapper<Goods> wrapper = Wrappers.<Goods>lambdaQuery();
        wrapper.eq(Goods::getStatus,1);
        if(StrUtil.isNotBlank(search)){
            wrapper.like(Goods::getGname, search);
        }
        if (sort.equals("好评率")){
            wrapper.orderByDesc(Goods::getLikeRate);
        }else if (sort.equals("价格从高到低")){
            wrapper.orderByDesc(Goods::getLikeRate);
        }else if (sort.equals("价格从低到高")){
            wrapper.orderByAsc(Goods::getLikeRate);
        }else if (sort.equals("可议价")){
            wrapper.eq(Goods::isBargain,1);
        }else if (sort.equals("新旧度从高到低")){
            wrapper.orderByDesc(Goods::getGcondition);
        }else if (sort.equals("新旧度从低到高")){
            wrapper.orderByAsc(Goods::getGcondition);
        }
        Page<Goods> goodsPage = goodsMapper.selectPage(new Page<>(pageNum,pageSize), wrapper);
        return Result.success(goodsPage);
    }

    @GetMapping("/findgoods")
    public Result<?> findGoods(@RequestParam(defaultValue = "1") Integer pageNumber,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(defaultValue = "") String searchText){
        Page<Goods> usersPage = new Page<>(pageNumber,pageSize);
        LambdaQueryWrapper<Goods> wrapper = Wrappers.<Goods>lambdaQuery();
        if(StrUtil.isNotBlank(searchText)){
            wrapper.like(Goods::getGname, searchText);
        }
        goodsMapper.selectPage(usersPage,wrapper);
        return Result.success(usersPage);

    }

    @GetMapping("/goodDetails")
    public Result<?> searchGood(@RequestParam() String Gid){
        Goods good = goodsDao.selectOne(Gid);
        if (good != null){
            return Result.success(good);
        }else {
            return Result.error("-1","商品未搜索到");
        }
    }


    @PostMapping
    public Result<?> save(@RequestBody Goods goods){
        //
        goods.setBargain(true);
        goods.setStatus(3);
        boolean ret = goodsDao.Insert(goods);
        if(ret){
            return Result.success();
        }else{
            return Result.error("1","insert fail!");
        }

    }

    @PutMapping
    public Result<?> update(@RequestBody Goods goods){
        boolean ret = goodsDao.Update(goods);
        if(ret){
            return Result.success();
        }else{
            return Result.error("1","update fail!");
        }
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable String id){
        //手动增加uid后插入数据库
        boolean ret = goodsDao.DeleteByID(id);
        if(ret){
            return Result.success();
        }else{
            return Result.error("1","insert fail!");
        }
    }

    @PutMapping("/exmStatus/{status}")
    public Result<?> exmStatus(@RequestBody Goods goods, @PathVariable String status){
        UpdateWrapper updateWrapper = new UpdateWrapper();
        updateWrapper.eq("Gid",goods.getGid());
        if(status.equals("1"))
            updateWrapper.set("Status",1);
        else if(status.equals("0"))
            updateWrapper.set("Status",0);
        else
            return Result.error("1","仅可为0，1");
        boolean ret = goodsDao.UpdateStatus(updateWrapper);
        if(ret){
            return Result.success();
        }else{
            return Result.error("1","update fail!");
        }
    }
}
