package com.example.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.demo.common.Result;
import com.example.demo.entity.Administrators;
import com.example.demo.mapper.AdministratorsMapper;
import com.example.demo.util.JwtUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/adm")
public class AdministratorsController {
    @Resource
    AdministratorsMapper administratorsMapper;


    @PostMapping("/login")
    public Result<?> login(@RequestBody Administrators adm){

        LambdaQueryWrapper<Administrators> wrapper = Wrappers.<Administrators>lambdaQuery();
        Administrators res = administratorsMapper.selectOne(wrapper.eq(Administrators::getAdmid,adm.getAdmid())
                .eq(Administrators::getPassword,adm.getPassword()));
        if(res == null){
            return Result.error("-1","用户名或密码错误");
        }else{
            Map<String,Object> info = new HashMap<>();
            info.put("admid",res.getAdmid());
            System.out.println("admid="+res.getAdmid());
            info.put("password",res.getPassword());

            String token = JwtUtil.sign(adm.getAdmid()+"",info);
            return Result.success(token);
        }
    }

}
