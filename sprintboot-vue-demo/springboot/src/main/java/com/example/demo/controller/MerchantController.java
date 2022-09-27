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
@RequestMapping("/merchant")
public class MerchantController {
    @Resource
    UsersMapper usersMapper;
    @Autowired
    UserDao userDao;

    //申请成为商家
    @PostMapping("/{id}")
    public Result<?> apply(@PathVariable String id){
        UpdateWrapper updateWrapper = new UpdateWrapper();
        updateWrapper.eq("Uid",id);
        try{
            int target = 2;//待审核
            updateWrapper.set("isMerchant",target);
            boolean ret = userDao.UpdateStatus(updateWrapper);
            if(ret){
                return Result.success();
            }else{
                return Result.error("1","judge fail!");
            }
        }catch (Exception e){
            e.printStackTrace();
            return Result.error("1","judge fail!");
        }

    }

    //获取某id用户是否为商家
    @GetMapping("/isMerchant/{id}")
    public Result<?> isMerchant(@PathVariable String id){
        //获取uid为id的用户信息
        LambdaQueryWrapper<Users> wrapper = Wrappers.<Users>lambdaQuery();
        Users res = usersMapper.selectOne(wrapper.eq(Users::getUid,id).eq(Users::getIsMerchant,1));
        if(res == null){
            return Result.error("-1","此用户不是商家");
        }else {
            return Result.success(res.getIsMerchant());
        }
    }

    //删除用户在商家列表中的显示
    @PutMapping
    public Result<?> delete(@RequestBody Users users){
        //手动修改isMMerchant为0
        users.setIsMerchant(0);
        boolean ret = userDao.Update(users);
        if(ret){
            return Result.success();
        }else{
            return Result.error("1","删除失败");
        }
    }

    //同意或拒绝成为商家
    @PutMapping("/{isMerchant}")
    public Result<?> judgeM(@RequestBody Users users,@PathVariable String isMerchant){
        try{
            int target = Integer.parseInt(isMerchant);
            if (target == 1){
                users.setIsMerchant(1);
                users.setTurnover(0);
                users.setGrade(5);
                users.setMLikeRate(5);
            }else if (target == -1){
                users.setIsMerchant(-1);
            }
            boolean ret = userDao.Update(users);
            if(ret){
                return Result.success();
            }else{
                return Result.error("1","judge fail!");
            }
        }catch (Exception e){
            e.printStackTrace();
            return Result.error("1","judge fail!");
        }
    }

    //调整商家等级
    @PutMapping("/grade/{toGrade}")
    public Result<?> changeGrade(@RequestBody Users users,@PathVariable String toGrade){
        try{
            int target = Integer.parseInt(toGrade);
            users.setGrade(target);
            boolean ret = userDao.Update(users);
            if(ret){
                return Result.success();
            }else{
                return Result.error("1","judge fail!");
            }
        }catch (Exception e){
            e.printStackTrace();
            return Result.error("1","judge fail!");
        }
    }

    @GetMapping
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNumber,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(defaultValue = "") String searchText,
                              @RequestParam(defaultValue = "gname") String type){
        Page<Users> usersPage = new Page<>(pageNumber,pageSize);
        LambdaQueryWrapper<Users> wrapper = Wrappers.<Users>lambdaQuery();
        wrapper.ne(Users::getIsMerchant,0);//除了没申请的
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
