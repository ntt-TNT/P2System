package com.example.demo.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.Result;
import com.example.demo.dao.UserDao;
import com.example.demo.entity.Users;
import com.example.demo.mapper.UsersMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@CrossOrigin
@RestController
@RequestMapping("/exmU")
public class UserEXMController {
    @Resource
    UsersMapper usersMapper;
    @Autowired
    UserDao userDao;
    @PutMapping("/{status}")
    public Result<?> changeStatus(@RequestBody Users users,@PathVariable String status){
        UpdateWrapper updateWrapper = new UpdateWrapper();
        updateWrapper.eq("Uid",users.getUid());
        if(status.equals("1"))//通过审核
            updateWrapper.set("Status",1);
        else if(status.equals("0"))//不通过审核
            updateWrapper.set("Status",0);
        boolean ret = userDao.UpdateStatus(updateWrapper);
        if(ret){
            return Result.success();
        }else{
            return Result.error("1","update fail!");
        }
    }



    @GetMapping
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNumber,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(defaultValue = "") String searchText,
                              @RequestParam(defaultValue = "gname") String type){
        Page<Users> usersPage = new Page<>(pageNumber,pageSize);
        LambdaQueryWrapper<Users> wrapper = Wrappers.<Users>lambdaQuery();
        if(StrUtil.isNotBlank(searchText)&&type.equals("uname")){
            wrapper.like(Users::getUname, searchText);
        }else if (StrUtil.isNotBlank(searchText)&&type.equals("uid")){
            wrapper.like(Users::getUid,searchText);
        }else if (StrUtil.isNotBlank(searchText)&&type.equals("status")){
            wrapper.like(Users::getStatus,searchText);
        }
        usersMapper.selectPage(usersPage,wrapper);
        return Result.success(usersPage);

    }
}
