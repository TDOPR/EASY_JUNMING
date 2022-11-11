//package com.haoliang.manager;
//
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import com.haoliang.enums.BusinessTypeEnum;
//import com.haoliang.enums.CoinEnum;
//import com.haoliang.enums.CommuRewardEnum;
//import com.haoliang.enums.RobotEnum;
//import com.haoliang.model.*;
//import com.haoliang.service.*;
//import com.haoliang.utils.Help;
//import org.springframework.beans.factory.annotation.Autowired;
//import javax.security.auth.login.AccountException;
//import java.math.BigDecimal;
//import java.util.*;
//
//public class TradeManager {
//    @Autowired
//    private UserInfoService userInfoService;
//
//    @Autowired
//    private AddressPoolService addressPoolService;
//
//    @Autowired
//    private RechargeService rechargeService;
//
//    @Autowired
//    private AccountService accountService;
//
//    @Autowired
//    private AccountDetailService accountDetailService;
//
//    @Autowired
//    private UserOrderService userOrderService;
//
//    @Autowired
//    private TreePathService treePathsService;
//
//    @Autowired
//    private UserWithdrawService userWithdrawService;
//
//    @Autowired
//    private  RateService rateService;
//
//    private final static BigDecimal FEE_RATE = new BigDecimal("0.01");
//    private final static BigDecimal FA_FEE_RATE = new BigDecimal("0.03");
//    private final static BigDecimal RECHARGE_VALUE = new BigDecimal("300");
//    private final static BigDecimal SUPER_RATE = new BigDecimal("0.05");
//
//
///*
//* 静态收益
//* */
//    public void staticIncome() {
//        //获取当日的收益比例
//        Random r = new Random();
//        int rR = r.nextInt(100);
//        int rRate = 0;
//
//        if (rR >= 0 && rR <= 69) {
//            rRate = 45;
//        } else if (rR >= 70 && rR <= 79) {
//            rRate = 50;
//        } else if (rR >= 80 && rR <= 89) {
//            rRate = 60;
//        } else if (rR >= 90 && rR <= 93) {
//            rRate = 70;
//        } else if (rR >= 94 && rR <= 97) {
//            rRate = 80;
//        } else if (rR >= 98 && rR <= 99) {
//            rRate = 90;
//        }
//
//        BigDecimal staticRate = new BigDecimal(rRate).divide(new BigDecimal(10000), 4, BigDecimal.ROUND_DOWN);
//
//        RateEntity rateEntity = RateEntity.builder()
//                .rate(staticRate)
//                .createTime(new Date())
//                .build();
//        rateService.save(rateEntity);
//
//        BigDecimal superStaticRate = staticRate.multiply(SUPER_RATE);
//
//        List<Map> superUserList = accountService.selectSuperUserList();
//        BigDecimal allAmount = new BigDecimal(accountService.selectAllAmount());
//        BigDecimal value = allAmount.multiply(superStaticRate);
//        for (Map accountMap : superUserList) {
//            int uid = Integer.parseInt(accountMap.get("id").toString());
//
//            accountService.addSuperAmountByAid(uid, value, CoinEnum.SUPER_STATIC_USDT.getCoinId());
//        }
//
//        List<Map> userList = accountService.selectStaticIncomeUserList();
//        for (Map userMap : userList) {
//            int uid = Integer.parseInt(userMap.get("uid").toString());
//            BigDecimal amount = new BigDecimal(userMap.get("amount").toString());
//
//            BigDecimal staticReward = amount.multiply(staticRate);
//
//            QueryWrapper<UserInfoEntity> userInfoEntityQueryWrapper = new QueryWrapper<>();
//            userInfoEntityQueryWrapper.eq("id", uid);
//            UserInfoEntity userInfoEntity = userInfoService.getOne(userInfoEntityQueryWrapper);
//
//                commuTeamReward(uid, staticReward, CoinEnum.SUPER_STATIC_USDT.getCoinId());
//                commuAlgebraPrize(uid, staticReward, CoinEnum.SUPER_STATIC_USDT.getCoinId());
//        }
//    }
//
//    /*
//    * 金额托管
//    * */
//    public void tradeRecharge(String userAddress, BigDecimal amount) throws AccountException {
//        if (!existUser(userAddress)) {
//            return;
//        }
//
//        QueryWrapper<UserInfoEntity> userInfoEntityQueryWrapper = new QueryWrapper<>();
//        userInfoEntityQueryWrapper.eq("address", userAddress);
//        UserInfoEntity userInfoEntity = userInfoService.getOne(userInfoEntityQueryWrapper);
//
//        if (!upgradeRechargeAmount(userInfoEntity, amount, 1)) {
//            return;
//        }
//
//        //生成订单
//        UserRecharge userRecharge = UserRecharge.builder()
//                .address(userAddress)
//                .type(0)
//                .uid(userInfoEntity.getId())
//                .amount(amount)
//                .createTime(new Date()).build();
//        userOrderService.save(userRecharge);
//        //生成账单
//        accountService.addAmount(userInfoEntity.getId(), CoinEnum.RECHARGE_USDT.getCoinId(), amount, BusinessTypeEnum.RECHARGE, userRecharge.getId());
//        updatePerformance(userInfoEntity.getId(), amount, true);
//    }
//
//
//    /*
//    * 取出托管
//    * */
//    public void tradeWithdraw(String userAddress, BigDecimal amount, int type) throws AccountException {
//        if (!existUser(userAddress)) {
//            return;
//        }
//
//        QueryWrapper<UserInfoEntity> userInfoEntityQueryWrapper = new QueryWrapper<>();
//        userInfoEntityQueryWrapper.eq("address", userAddress);
//        UserInfoEntity userInfoEntity = userInfoService.getOne(userInfoEntityQueryWrapper);
//
//        if (!upgradeRechargeAmount(userInfoEntity, amount, 2)) {
//            return;
//        }
//
//        //生成订单
//        UserRecharge userRecharge = UserRecharge.builder()
//                .address(userAddress)
//                .type(type)
//                .uid(userInfoEntity.getId())
//                .amount(amount)
//                .createTime(new Date()).build();
//        userOrderService.save(userRecharge);
//
//        accountService.subtractAmount(userInfoEntity.getId(), CoinEnum.RECHARGE_USDT.getCoinId(), amount, BusinessTypeEnum.RECHARGE, userRecharge.getId());
//        updatePerformance(userInfoEntity.getId(), amount, false);
//
//        BigDecimal fee;
//        //收取手续费
//        if(type==1){
//            fee = amount.multiply(FEE_RATE);
//        }else {
//            fee = amount.multiply(FA_FEE_RATE);
//        }
//
//        BigDecimal mum = amount.subtract(fee);
//
//        //生成提现记录
//        UserWithdraw userWithdraw = UserWithdraw.builder()
//                .address(userAddress)
//                .id(userInfoEntity.getId())
//                .coinName("USDT")
//                .coinType("EVM")
//                .coinId(1)
//                .num(amount)
//                .fee(fee)
//                .mum(mum)
//                .status(4)
//                .lastUpdateTime(new Date())
//                .created(new Date()).build();
//        userWithdrawService.save(userWithdraw);
//    }
//
//
//    /*
//    * 更新最大可托管额度
//    * */
//    private boolean upgradeRechargeAmount(UserInfoEntity userInfoEntity, BigDecimal amount, int type) {
//        BigDecimal rechargeMax = userInfoEntity.getRechargeMax();
//        QueryWrapper<AccountEntity> accountEntityQueryWrapper = new QueryWrapper<>();
//        accountEntityQueryWrapper.eq("uid", userInfoEntity.getId());
//        accountEntityQueryWrapper.eq("coin_id", CoinEnum.RECHARGE_USDT.getCoinId());
//        AccountEntity accountEntity = accountService.getOne(accountEntityQueryWrapper);
//        BigDecimal alreadyRecharge = accountEntity.getAlreadyRechargeAmount();
//        if (type == 1) {
//            BigDecimal newAlreadyRecharge = alreadyRecharge.add(amount);
//            if (rechargeMax.compareTo(newAlreadyRecharge) < 0) {
//                return false;
//            }
//            accountEntity.setAlreadyRechargeAmount(newAlreadyRecharge);
//            accountService.updateById(accountEntity);
//
//            return true;
//        } else {
//            BigDecimal newAlreadyRecharge = alreadyRecharge.subtract(amount);
//            accountEntity.setAlreadyRechargeAmount(newAlreadyRecharge);
//            accountService.updateById(accountEntity);
//            return true;
//        }
//    }
//
//
//    /*
//    * 托管机器人
//    * */
//    public void tradeRobot(String userAddress, BigDecimal amount) throws Exception {
//        if (!existUser(userAddress)) {
//            return;
//        }
//        //判断当前用户是否已有机器人，查看是首次购买还是补差价
//        QueryWrapper<UserInfoEntity> userInfoEntityQueryWrapper = new QueryWrapper<>();
//        userInfoEntityQueryWrapper.eq("address", userAddress);
//        UserInfoEntity userInfoEntity = userInfoService.getOne(userInfoEntityQueryWrapper);
//        int alreadyRobotLevel = userInfoEntity.getRobotLevel();
//
//        int type;
//        BusinessTypeEnum businessTypeEnum;
//        int robotLevel = 0;
//        if (alreadyRobotLevel == 0) {
//            //首次购买机器人
//            type = 1;
//            businessTypeEnum = BusinessTypeEnum.BUY_ROBOT;
//            if (amount.compareTo(RobotEnum.ROBOT_ONE.getPrice()) == 0) {
//                robotLevel = 1;
//            } else if (amount.compareTo(RobotEnum.ROBOT_TW0.getPrice()) == 0) {
//                robotLevel = 2;
//            } else if (amount.compareTo(RobotEnum.ROBOT_THREE.getPrice()) == 0) {
//                robotLevel = 3;
//            } else if (amount.compareTo(RobotEnum.ROBOT_FOUR.getPrice()) == 0) {
//                robotLevel = 4;
//            }
//            //更新我的上级有效直推数量
//            QueryWrapper<UserInfoEntity> inviteEntityWrapper = new QueryWrapper<>();
//            inviteEntityWrapper.eq("id", userInfoEntity.getDirectInviteid());
//            UserInfoEntity inviteEntity = userInfoService.getOne(inviteEntityWrapper);
//            inviteEntity.setDirectNum(inviteEntity.getDirectNum() + 1);
//            userInfoService.updateById(inviteEntity);
//        } else {
//            //补差价升级机器人
//            type = 2;
//            businessTypeEnum = BusinessTypeEnum.UPGRADE_ROBOT;
//            BigDecimal alreadyRobotAmount = BigDecimal.ZERO;
//            if (alreadyRobotLevel == RobotEnum.ROBOT_ONE.getCode()) {
//                alreadyRobotAmount = RobotEnum.ROBOT_ONE.getPrice();
//            } else if (alreadyRobotLevel == RobotEnum.ROBOT_TW0.getCode()) {
//                alreadyRobotAmount = RobotEnum.ROBOT_TW0.getPrice();
//            } else if (alreadyRobotLevel == RobotEnum.ROBOT_THREE.getCode()) {
//                alreadyRobotAmount = RobotEnum.ROBOT_THREE.getPrice();
//            } else if (alreadyRobotLevel == RobotEnum.ROBOT_FOUR.getCode()) {
//                alreadyRobotAmount = RobotEnum.ROBOT_FOUR.getPrice();
//            }
//            BigDecimal currentRobotAmount = alreadyRobotAmount.add(amount);
//            if (currentRobotAmount.compareTo(RobotEnum.ROBOT_TW0.getPrice()) == 0) {
//                robotLevel = 2;
//            } else if (currentRobotAmount.compareTo(RobotEnum.ROBOT_THREE.getPrice()) == 0) {
//                robotLevel = 3;
//            } else if (currentRobotAmount.compareTo(RobotEnum.ROBOT_FOUR.getPrice()) == 0) {
//                robotLevel = 4;
//            }
//        }
//
//        //生成订单
//        UserRecharge userRecharge = UserRecharge.builder()
//                .address(userAddress)
//                .type(type)
//                .uid(userInfoEntity.getId())
//                .amount(amount)
//                .createTime(new Date()).build();
//        userOrderService.save(userRecharge);
//        //生成账单
//        accountDetailService.generateBill(userInfoEntity.getId(), businessTypeEnum, userRecharge.getId(), amount);
//        //修改机器人等级，以及用户的托管上限
//        UserInfoEntity updateUserInfoEntity = UserInfoEntity.builder()
//                .id(userInfoEntity.getId())
//                .rechargeMax(RobotEnum.getRechargeMaxByCode(robotLevel))
//                .robotLevel(robotLevel).build();
//        userInfoService.updateById(updateUserInfoEntity);
//        updatePerformance(userInfoEntity.getId(), amount, true);
//        //发机器人推广奖
//        robotReward(userInfoEntity.getDirectInviteid(), robotLevel, amount, userRecharge.getId());
//    }
//
//
//
//    /*
//    * 从recharge表拉取  已托管未处理用户信息
//    * */
//    public void reUsdt(String userAddress, BigDecimal amount) {
//        if (!existUser(userAddress)) {
//            return;
//        }
//
//        QueryWrapper<UserInfoEntity> userWrapper = new QueryWrapper<>();
//        userWrapper.eq("address", userAddress);
//        UserInfoEntity userInfoEntity = userInfoService.getOne(userWrapper);
//        if (Help.isNull(userInfoEntity.getUsdtAddress())) {
//            String usdtAddress = addressPoolService.getAddress();
//            UserInfoEntity updateUserInfoEntity = UserInfoEntity.builder()
//                    .address(userAddress)
//                    .usdtAddress(usdtAddress)
//                    .calculateAmount(amount)
//                    .build();
//            userInfoService.save(updateUserInfoEntity);
//            addressPoolService.deleteByAddress(usdtAddress);
//        } else {
//            UserInfoEntity updateUserInfoEntity = UserInfoEntity.builder()
//                    .address(userAddress)
//                    .calculateAmount(amount)
//                    .build();
//            userInfoService.save(updateUserInfoEntity);
//        }
//    }
//
//
//    public void rechargeEvent() {
//
//        List<RechargeEntity> rechargeEntitys = rechargeService.selectPatch();
//        for (RechargeEntity rechargeEntity : rechargeEntitys) {
//            int uid = rechargeEntity.getId();
//            String address = rechargeEntity.getAddress();
//            QueryWrapper<UserInfoEntity> userInfoEntityQueryWrapper = new QueryWrapper<>();
//            userInfoEntityQueryWrapper.eq("address", address);
//            UserInfoEntity userInfoEntity = userInfoService.getOne(userInfoEntityQueryWrapper);
//            if (Help.isNull(userInfoEntity)) {
//                UserInfoEntity newUserInfoEntity = UserInfoEntity.builder()
//                        .address(address)
//                        .id(uid)
//                        .build();
//                userInfoService.save(newUserInfoEntity);
//            } else {
//                BigDecimal amount = userInfoEntity.getCalculateAmount().add(rechargeEntity.getCalculateAmount());
//                UserInfoEntity newUserInfoEntity = UserInfoEntity.builder()
//                        .address(address)
//                        .id(uid)
//                        .calculateAmount(amount)
//                        .build();
//                userInfoService.save(newUserInfoEntity);
//            }
//            rechargeService.updateType(uid);
//
//        }
//    }
//
//
//    /*
//    * 创建用户
//    * */
//    public void createUser(String userAddress, String inviteUserCode) throws Exception {
//        //需要注册用户
//        userInfoService.createUser(userAddress, inviteUserCode);
//    }
//
//
//    /*
//    * 更新团队业绩
//    * */
//    private void updatePerformance(int userId, BigDecimal amount, boolean type) {
//        QueryWrapper<TreePath> treePathsEntityQueryWrapper = new QueryWrapper<>();
//        treePathsEntityQueryWrapper.eq("descendant", userId);
//        List<TreePath> treePathsEntityList = treePathsService.list(treePathsEntityQueryWrapper);
//        for (TreePath treePathsEntity : treePathsEntityList) {
//            if (type) {
//                userInfoService.addTeamPerformanceById(treePathsEntity.getAncestor(), amount);
//            } else {
//                QueryWrapper<UserInfoEntity> userInfoEntityQueryWrapper = new QueryWrapper<>();
//                userInfoEntityQueryWrapper.eq("id", treePathsEntity.getAncestor());
//                UserInfoEntity userInfoEntity = userInfoService.getOne(userInfoEntityQueryWrapper);
//                if (userInfoEntity.getTeamPerformance().compareTo(amount) < 0) {
//                    amount = userInfoEntity.getTeamPerformance();
//                }
//                userInfoService.subtractTeamPerformanceById(treePathsEntity.getAncestor(), amount);
//            }
//            levelChange(treePathsEntity.getAncestor());
//        }
//    }
//
//
//    /*
//    * 更新级别
//    * */
//    private void levelChange(int userId) {
//        QueryWrapper<TreePath> treePathEntityQueryWrapper = new QueryWrapper<>();
//        treePathEntityQueryWrapper.eq("ancestor", userId);
//        treePathEntityQueryWrapper.eq("level", 1);
//        List<TreePath> treePathsEntity = treePathsService.list(treePathEntityQueryWrapper);
//
//        BigDecimal smallTeamPerformance;
//        BigDecimal bigTeamPerformance = BigDecimal.ZERO;
//
//        for (TreePath treePathEntity : treePathsEntity) {
//            QueryWrapper<UserInfoEntity> userInfoEntityQueryWrapper = new QueryWrapper<>();
//            userInfoEntityQueryWrapper.eq("id", treePathEntity.getDescendant());
//            UserInfoEntity userInfoEntity = userInfoService.getOne(userInfoEntityQueryWrapper);
//            if (userInfoEntity.getTeamPerformance().compareTo(bigTeamPerformance) >= 0) {
//                bigTeamPerformance = userInfoEntity.getTeamPerformance();
//            }
//        }
//
//        QueryWrapper<UserInfoEntity> userInfoEntityQueryWrapper = new QueryWrapper<>();
//        userInfoEntityQueryWrapper.eq("id", userId);
//        UserInfoEntity userInfoEntity = userInfoService.getOne(userInfoEntityQueryWrapper);
//
//        int userLevel = userInfoEntity.getLevel();
//
//        smallTeamPerformance = userInfoEntity.getTeamPerformance().subtract(bigTeamPerformance);
//
//        if(smallTeamPerformance.compareTo(CommuRewardEnum.REWARD_RATE_FIVE.getValue())>=0){
//            userLevel = CommuRewardEnum.REWARD_RATE_FIVE.getLevel();
//        } else if (smallTeamPerformance.compareTo(CommuRewardEnum.REWARD_RATE_FOUR.getValue())>=0) {
//            userLevel = CommuRewardEnum.REWARD_RATE_FOUR.getLevel();
//        }else if (smallTeamPerformance.compareTo(CommuRewardEnum.REWARD_RATE_THREE.getValue())>=0) {
//            userLevel = CommuRewardEnum.REWARD_RATE_THREE.getLevel();
//        }else if (smallTeamPerformance.compareTo(CommuRewardEnum.REWARD_RATE_TW0.getValue())>=0) {
//            userLevel = CommuRewardEnum.REWARD_RATE_TW0.getLevel();
//        }else if (smallTeamPerformance.compareTo(CommuRewardEnum.REWARD_RATE_ONE.getValue())>=0) {
//            userLevel = CommuRewardEnum.REWARD_RATE_ONE.getLevel();
//        }
//
//        UserInfoEntity newUserInfoEntity = UserInfoEntity.builder()
//                .id(userId)
//                .level(userLevel)
//                .build();
//        userInfoService.updateById(newUserInfoEntity);
//    }
//
//
//    /*
//    * 社区团队奖
//    * */
//    private void commuTeamReward(int userId, BigDecimal amount, long coinId, int orderId) throws Exception{
//
//        QueryWrapper<UserInfoEntity> userInfoEntityQueryWrapper = new QueryWrapper<>();
//        userInfoEntityQueryWrapper.eq("id", userId);
//        UserInfoEntity userInfoEntity = userInfoService.getOne(userInfoEntityQueryWrapper);
//
//        QueryWrapper<TreePath> treePathsEntityQueryWrapper = new QueryWrapper<>();
//        treePathsEntityQueryWrapper.eq("descendant", userId);
//        treePathsEntityQueryWrapper.ne("ancestor", userId);
//        treePathsEntityQueryWrapper.orderByDesc("level");
//        List<TreePath> treePathsEntityList = treePathsService.list(treePathsEntityQueryWrapper);
//
//        boolean teamRewardFlag = false;
//        BigDecimal lastRate;
//        int lastLevel = 0;
//        BigDecimal rate = BigDecimal.ZERO;
//        BusinessTypeEnum businessTypeEnum=BusinessTypeEnum.REWARD_COMMU_TEAM;
//
//        for (TreePath treePathsEntity : treePathsEntityList) {
//            Map userMap = treePathsService.getUserLevelById(treePathsEntity.getAncestor());
//            int id = Integer.parseInt(userMap.get("ID").toString());
//            int level = Integer.parseInt(userMap.get("LEVEL").toString());
//
//            QueryWrapper<UserInfoEntity> inviteInfoEntityQueryWrapper = new QueryWrapper<>();
//            inviteInfoEntityQueryWrapper.eq("id", userId);
//            UserInfoEntity inviteInfoEntity = userInfoService.getOne(userInfoEntityQueryWrapper);
//
//            if (level > lastLevel) {
//                teamRewardFlag = true;
//                rate = CommuRewardEnum.getRateByLevel(level);
//            }
//            if (teamRewardFlag) {
//                lastRate = rate;
//                amount = amount.multiply(rate.subtract(lastRate)).divide(new BigDecimal(100), 4, BigDecimal.ROUND_DOWN);
//                lastLevel = level;
//                accountService.addAmount(inviteInfoEntity.getDirectInviteid(), coinId, amount, businessTypeEnum, orderId);
//            }
//        }
//    }
//
//
//    /*
//    * 社区代数奖
//    * */
//    private void commuAlgebraPrize(int inviteId,int coinId, int userId, BigDecimal reward, BusinessTypeEnum businessTypeEnum, int orderId) throws Exception{
//
//        List<Map> treePathsEntityList = treePathsService.getPathById(userId);
//
//        BigDecimal algebraReward = BigDecimal.ZERO;
//        BigDecimal userRechargeAmount;
//
//        for(Map userMap: treePathsEntityList){
//            int uid = Integer.parseInt(userMap.get("uid").toString());
//            int level = Integer.parseInt(userMap.get("LEVEL").toString());
//
//            userRechargeAmount = new BigDecimal(accountService.selectUserRechargeAmount(uid));
//
//            if(userRechargeAmount.compareTo(RECHARGE_VALUE)>0){
//
//                if(level==1){
//                    algebraReward = reward.multiply(new BigDecimal(25)).divide(new BigDecimal(100));
//                } else if (level==2) {
//                    algebraReward = reward.multiply(new BigDecimal(15)).divide(new BigDecimal(100));
//                } else if (level==3) {
//                    algebraReward = reward.multiply(new BigDecimal(5)).divide(new BigDecimal(100));
//                }
//                accountService.addAmount(inviteId, coinId, algebraReward, businessTypeEnum, orderId);
//            }
//        }
//    }
//
//
//    /*
//    * 购买机器人奖励
//    * */
//    private void robotReward(int inviteId, int userRobotLevel, BigDecimal amount, int orderId) throws
//            AccountException {
//        //查询我的直推信息
//        QueryWrapper<UserInfoEntity> userInviteEntityWrapper = new QueryWrapper<>();
//        userInviteEntityWrapper.eq("id", inviteId);
//        UserInfoEntity userInviteEntity = userInfoService.getOne(userInviteEntityWrapper);
//        int directNum = userInviteEntity.getDirectNum();
//        BigDecimal inviteRechargeAmount = new BigDecimal(accountService.selectUserRechargeAmount(inviteId));
//        int rate;
//        switch (directNum) {
//            case 0:
//                rate = 0;
//                break;
//            case 1:
//                rate = 15;
//                break;
//            case 2:
//                rate = 20;
//                break;
//            case 3:
//                rate = 25;
//                break;
//            case 4:
//                rate = 30;
//                break;
//            default:
//                rate = 30;
//        }
//        BusinessTypeEnum businessTypeEnum;
//        long coinId;
//        BigDecimal reward = amount.multiply(new BigDecimal(rate)).divide(new BigDecimal(100), 4, BigDecimal.ROUND_DOWN);
//        if (inviteRechargeAmount.compareTo(RECHARGE_VALUE)>=0) {
//            businessTypeEnum = BusinessTypeEnum.REWARD_ROBOT;
//            coinId = CoinEnum.ROBOT_USDT.getCoinId();
//            accountService.addAmount(inviteId, coinId, reward, businessTypeEnum, orderId);
//        }
//    }
//}
