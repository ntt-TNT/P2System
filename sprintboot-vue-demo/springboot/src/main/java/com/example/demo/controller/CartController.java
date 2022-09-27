package com.example.demo.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.demo.common.Result;
import com.example.demo.dao.CartDao;
import com.example.demo.dao.UserDao;
import com.example.demo.entity.Cart;
import com.example.demo.mapper.CartMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/cart")
public class CartController {
    @Resource
    CartMapper cartMapper;
    @Autowired
    CartDao cartDao;
    @Autowired
    UserDao userDao;

    @PostMapping
    public Result<?> addCartGood(@RequestBody Cart cart){
        if (cart.getUid() == ""){
            return Result.error("-1","请先登录");
        }else if(!userDao.exist(cart.getUid())){
            return Result.error("-1","请等审核通过");
        }
        else{
            Cart old = cartDao.exist(cart.getGid(),cart.getUid());
            if (old!=null){
                cart.setCid(old.getCid());
                cart.setNumber(cart.getNumber()+old.getNumber());
                cartDao.Update(cart);
            }else {
                cartMapper.insert(cart);
            }
            return Result.success();
        }

    }

    @GetMapping
    public Result<?> getCartGood(@RequestParam() String uid){
        List<Cart> cartList = cartMapper.selectList(Wrappers.<Cart>lambdaQuery().eq(Cart::getUid,uid));
        if (cartList.size()!=0){
            return Result.success(cartList);
        }else {
            return Result.error("-1","购物车为空");
        }
    }


    @PutMapping
    public Result<?> getCartGood(@RequestBody Cart cart){
        cartMapper.updateById(cart);
        return Result.success();
    }

    @DeleteMapping("/{cid}")
    public Result<?> delete(@PathVariable Long cid){
        cartMapper.deleteById(cid);
        return Result.success();
    }
}
