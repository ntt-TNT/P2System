package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.dao.*;
import com.example.demo.entity.*;
import com.example.demo.mapper.ReviewMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/review")
public class ReviewController {
    @Resource
    ReviewMapper reviewMapper;
    @Autowired
    ReviewDao reviewDao;
    @Autowired
    OrdersDao ordersDao;
    @Autowired
    UserDao userDao;
    @Autowired
    GoodsDao goodsDao;
    @Autowired
    AccountRecordDao accountRecordDao;

    @PostMapping
    public Result<?> addReview(@RequestBody Review review){
        boolean ret = reviewDao.Insert(review);
        if (ret){
            if (review.getType()==2){
                Orders orders = ordersDao.selectOne(review.getOid());
                System.out.println(orders);
                orders.setStatus(3);
                //用户
                Users users = userDao.selectOne(orders.getUid()+"");
                users.setPoint(users.getPoint()+(int) orders.getSum());
                userDao.Update(users);
                //商品销量增加
                Goods goods = goodsDao.selectOne(orders.getGid()+"");
                System.out.println(goods);
                goods.setSale(goods.getSale()+orders.getNumber());
                //商家
                Users merchant = userDao.selectOne(orders.getMid()+"");
                merchant.setTurnover(merchant.getTurnover()+orders.getSum());
                //商品好评率
                List<Review> reviewList = reviewDao.OrderBy("time",1,orders.getGid()+"",2);
                double sumrate=0;
                for (int i=0; i<reviewList.size(); i++){
                    sumrate += reviewList.get(i).getStar();
                }
                sumrate += review.getStar();
                goods.setLikeRate(sumrate/(reviewList.size()+1));
                //手续费
                double commission = orders.getCommission();
                merchant.setAccount(merchant.getAccount()+orders.getSum()*(1-commission));
                goodsDao.Update(goods);
                userDao.Update(merchant);
                ordersDao.Update(orders);

                //新增商家收入记录
                AccountRecord accountRecord = new AccountRecord();
                accountRecord.setType(0);
                accountRecord.setAmount(orders.getSum());
                accountRecord.setOid(orders.getOid());
                accountRecord.setUid(orders.getMid());
                int res = accountRecordDao.addAccount(accountRecord);
                if(res==1){
                }else if (res==-1){
                    return Result.error("-1","余额不足");
                }else{
                    return Result.error("-1","支出添加失败");
                }

            }
            else if (review.getType()==1){
                //商家好评率
                List<Review> reviewList = reviewDao.OrderBy("time",1,review.getToid()+"",1);
                double sumrate=0;
                for (int i=0; i<reviewList.size(); i++){
                    sumrate += reviewList.get(i).getStar();
                }
                sumrate += review.getStar();
                Users merchant = userDao.selectOne(review.getToid()+"");
                merchant.setMLikeRate(sumrate/(reviewList.size()+1));
                userDao.Update(merchant);
            }
            else if (review.getType()==3){
                List<Review> reviewList = reviewDao.OrderBy("time",1,review.getToid()+"",3);
                double sumrate=0;
                for (int i=0; i<reviewList.size(); i++){
                    sumrate += reviewList.get(i).getStar();
                }
                sumrate += review.getStar();
                Users users = userDao.selectOne(review.getToid()+"");
                users.setULikeRate(sumrate/(reviewList.size()+1));
                userDao.Update(users);
            }
            return Result.success();
        }else {
            return Result.error("-1","错误");
        }
    }

    @GetMapping
    public Result<?> getReview(@RequestParam String gid){
        List<Review> reviewList = reviewDao.OrderBy("time",1,gid,2);
        return Result.success(reviewList);
    }
}
