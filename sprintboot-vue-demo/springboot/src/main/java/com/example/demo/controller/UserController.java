package com.example.demo.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.Result;
import com.example.demo.dao.UserDao;
import com.example.demo.entity.Users;
import com.example.demo.mapper.UsersMapper;
import com.example.demo.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    UsersMapper usersMapper;
    @Autowired
    UserDao userDao;

    @PostMapping
    public Result<?> save(@RequestBody Users users){
        if(users.getUpsw() == null){
            return Result.error("2","未输入密码");
        }
        //判断数据库中是否已存在此用户名
        LambdaQueryWrapper<Users> wrapper = Wrappers.<Users>lambdaQuery();
        Users res = usersMapper.selectOne(wrapper.eq(Users::getUname,users.getUname()));
        if(res != null){
            return Result.error("-2","此用户名已存在");
        }
        //
        users.setAccount(0);
        users.setPoint(0);
        users.setStatus(3);
        users.setULikeRate(100);
        //手动增加uid后插入数据库
        boolean ret = userDao.Insert(users);
        if(ret==true){
            return Result.success();
        }else{
            return Result.error("1","insert fail!");
        }

    }

    @PostMapping("/login")
    public Result<?> login(@RequestBody Users users){
        //
        LambdaQueryWrapper<Users> wrapper = Wrappers.<Users>lambdaQuery();
        Users res = usersMapper.selectOne(wrapper.eq(Users::getUname,users.getUname())
                .eq(Users::getUpsw,users.getUpsw()));
        if(res == null){
            return Result.error("-1","用户名或密码错误");
        }else{
            Map<String,Object> info = new HashMap<>();
            info.put("uid",res.getUid());
            info.put("uname",res.getUname());
            String token = JwtUtil.sign(res.getUid()+"",info);
            return Result.success(token);
        }
    }
    @PutMapping
    public Result<?> update(@RequestBody Users users){
        //
        boolean ret = userDao.Update(users);
        if(ret){
            return Result.success();
        }else{
            return Result.error("1","update fail!");
        }
    }
    @PutMapping("/{amount}")
    public Result<?> invest(@RequestBody Users users,@PathVariable double amount){
        //
        users.setAccount(amount+users.getAccount());
        boolean ret = userDao.Update(users);
        if(ret){
            return Result.success();
        }else{
            return Result.error("1","update fail!");
        }
    }
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable String id){
        //手动增加uid后插入数据库
        boolean ret = userDao.DeleteByID(id);
        if(ret){
            return Result.success();
        }else{
            return Result.error("1","insert fail!");
        }
    }
    @GetMapping("/judgeName/{name}")
    public Result<?> judgeName(@PathVariable String name){
        //判断数据库中是否已存在此用户名
        LambdaQueryWrapper<Users> wrapper = Wrappers.<Users>lambdaQuery();
        Users res = usersMapper.selectOne(wrapper.eq(Users::getUname,name));
        if(res == null){
            return Result.success();
        }else{
            return Result.error("-2","此用户名已存在");
        }
    }

    @GetMapping("/getInfo/{name}")
    public Result<?> getInfo(@PathVariable String name){
        //判断数据库中是否已存在此用户名
        LambdaQueryWrapper<Users> wrapper = Wrappers.<Users>lambdaQuery();
        Users res = usersMapper.selectOne(wrapper.eq(Users::getUname,name));
        if(res == null){
            return Result.error("1","此用户名不存在");
        }else{
            return Result.success(res);
        }
    }

    @GetMapping
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNumber,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(defaultValue = "") String searchText){
        Page<Users> usersPage = new Page<>(pageNumber,pageSize);
        LambdaQueryWrapper<Users> wrapper = Wrappers.<Users>lambdaQuery();
        if(StrUtil.isNotBlank(searchText)){
            wrapper.like(Users::getUname, searchText);
        }
        usersMapper.selectPage(usersPage,wrapper);
        return Result.success(usersPage);

    }

    @GetMapping("/userone")
    public Result<?> finduser(@RequestParam String uid){
        LambdaQueryWrapper<Users> wrapper = Wrappers.<Users>lambdaQuery();
        Users res = usersMapper.selectOne(wrapper.eq(Users::getUid,uid));
        if(res == null){
            return Result.error("1","此用户不存在");
        }else{
            return Result.success(res);
        }
    }

}
