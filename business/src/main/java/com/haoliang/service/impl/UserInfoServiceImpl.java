package com.haoliang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haoliang.mapper.ProfitLogsMapper;
import com.haoliang.mapper.UserInfoDao;
import com.haoliang.model.ProfitLogs;
import com.haoliang.model.UserInfoEntity;
import com.haoliang.service.AccountService;
import com.haoliang.service.ProfitLogsService;
import com.haoliang.service.UserInfoService;
import com.haoliang.utils.Help;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Dominick Li
 * @Description
 * @CreateTime 2022/11/1 17:59
 **/
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoDao, UserInfoEntity> implements UserInfoService {
    @Resource
    private AccountService accountService;

    @Resource
    private UserInfoDao userInfoDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<UserInfoEntity> page = this.page(
                new Query<UserInfoEntity>().getPage(params),
                new QueryWrapper<UserInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void subtractTeamPerformanceById(int uid, BigDecimal amount) {
        userInfoDao.subtractTeamPerformanceById(amount,uid);
    }

    @Override
    public void addTeamPerformanceById(int uid, BigDecimal amount) {
        userInfoDao.addTeamPerformanceById(amount,uid);
    }

    @Override
    public List<UserInfoEntity> selectSuperUserList(){
        return baseMapper.selectSuperUserList();
    }

    @Override
    public void createUser(String userAddress, String inviteUserCode) {

        //判断当前用户是否存在
        QueryWrapper<UserInfoEntity> userWrapper = new QueryWrapper<>();
        userWrapper.eq("address", userAddress);
        UserInfoEntity createUserEntity = this.getOne(userWrapper);
        if (Help.isNotNull(createUserEntity)) {
            return;
        }

        //判断邀请人是否存在
        QueryWrapper<UserInfoEntity> inviteWapper = new QueryWrapper<>();
        inviteWapper.eq("invite_code", inviteUserCode);
        UserInfoEntity inviteUserEntity = this.getOne(inviteWapper);
        if (Help.isNull(inviteUserEntity)) {
            return;
        }

        // 生产随机邀请码
        String inviteCode = Help.generateShortUUID(8);
        while (true) {
            QueryWrapper<UserInfoEntity> tWapper = new QueryWrapper<>();
            tWapper.eq("invite_code", inviteCode);
            UserInfoEntity userExistEntity = this.getOne(tWapper);
            if (Help.isNotNull(userExistEntity)) {
                inviteCode = Help.generateShortUUID(8);
            } else {
                break;
            }
        }

        //保存用户信息
        UserInfoEntity userEntity = new UserInfoEntity();
        userEntity.setAddress(userAddress);
        userEntity.setRobotLevel(0);
        userEntity.setLevel(0);
        userEntity.setDirectNum(0);
        userEntity.setTeamPerformance(BigDecimal.ZERO);
        userEntity.setDirectInviteid(inviteUserEntity.getId());
        userEntity.setRechargeMax(new BigDecimal(3000));
        userEntity.setInviteCode(inviteCode);
        userEntity.setCreated(new Date());

        this.save(userEntity);

        userEntity = this.getById(userEntity.getId());
        // 添加用户资产账户
        accountService.addAccount(userEntity);

        //保存上下级关系
        //代码块
        Map<String, Object> map = new HashMap<>();
        map.put("pid", inviteUserEntity.getId());
        map.put("uid", userEntity.getId());
        this.insertTreePath(map);

        //增加直推人的直推数量
//        UserInfoEntity updateDirectUser = new UserInfoEntity().builder()
//                .id(inviteUserEntity.getId())
//                .directNum(inviteUserEntity.getDirectNum()+1)
//                .build();
//        this.updateById(updateDirectUser);
    }

    @Override
    public void insertTreePath(Map<String, Object> map) {
        baseMapper.insertTreePath(map);
    }
}
