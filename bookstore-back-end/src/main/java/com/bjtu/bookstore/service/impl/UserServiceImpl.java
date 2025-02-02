package com.bjtu.bookstore.service.impl;

import com.bjtu.bookstore.entity.User;
import com.bjtu.bookstore.mapper.UserMapper;
import com.bjtu.bookstore.service.UserLoginInterface;
import com.bjtu.bookstore.service.UserService;
import com.bjtu.bookstore.utils.encodeUtils.EncodeUtil;
import com.bjtu.bookstore.utils.exceptionHandler.exception.DefinitionException;
import com.bjtu.bookstore.utils.exceptionHandler.exception.ErrorEnum;
import com.bjtu.bookstore.utils.verifyCodeUtils.VerifyCodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


/**
 * @program: framework
 * @description: user service impl
 * @author: CodingLiOOT
 * @create: 2021-03-18 20:19
 * @version: 1.0
 **/
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private EncodeUtil encodeUtil;
    @Autowired
    private VerifyCodeUtils verifyCodeUtils;

    @Autowired
    private Map<String, UserLoginInterface> userLoginServiceMap;

    @Override
    public HashMap<String, String> userLogin(User user) {
        return userLoginServiceMap.get(user.getLoginType()).userLogin(user);
    }

    @Override
    public void register(User user) {
        List<User> users = userMapper.selectUserByNameOrMail(user.getUsername(), user.getMail());
        Optional.ofNullable(users).filter(u -> u.size() == 0)
                .<DefinitionException>orElseThrow(() -> {
                    throw new DefinitionException(ErrorEnum.DUPLICATE_USERNAME_OR_MAIL);
                });

        Optional.of(user).map(User::getVerifyCode).filter(
                value -> verifyCodeUtils.verifyCode(user.getMail(), user.getVerifyCode()))
                .<DefinitionException>orElseThrow(() -> {
                    throw new DefinitionException(ErrorEnum.ERROR_VERIFY_CODE);
                });

//        user.setId(5L);
        user.setId((long) (userMapper.getMaxId() + 1));
        user.setPassword(encodeUtil.genCode(user.getPassword(), user.getMail()));
        userMapper.register(user);
    }

    @Override
    public void forgetPassword(User user) {

        Optional.of(user).map(User::getVerifyCode).filter(
                value -> verifyCodeUtils.verifyCode(user.getMail(), value))
                .<DefinitionException>orElseThrow(() -> {
                    throw new DefinitionException(ErrorEnum.ERROR_VERIFY_CODE);
                });

        String username = Optional.ofNullable(userMapper.selectUsernameByMail(user.getMail()))
                .<DefinitionException>orElseThrow(() -> {
                    throw new DefinitionException(ErrorEnum.ERROR_NICKNAME_OR_PASSWORD);
                });

        String newPassword = encodeUtil.genCode(user.getNewPassword(), user.getMail());
        userMapper.updatePassword(username, newPassword);
    }

    @Override
    public void modifyPassword(User user) {
        User userBean = Optional.ofNullable(userMapper.selectUserByUserName(user.getUsername()))
                .<DefinitionException>orElseThrow(() -> {
                    throw new DefinitionException(ErrorEnum.ERROR_NICKNAME_OR_PASSWORD);
                });

        Optional.of(userBean).map(User::getPassword).filter(
                value -> encodeUtil.verifyEncode(user.getPassword(), userBean.getMail(),
                        value))
                .<DefinitionException>orElseThrow(() -> {
                    throw new DefinitionException(ErrorEnum.ERROR_NICKNAME_OR_PASSWORD);
                });

        String newPassword = encodeUtil.genCode(user.getNewPassword(), userBean.getMail());
        userMapper.updatePassword(user.getUsername(), newPassword);
    }

    //通过用户id获取用户信息
    @Override
    public User getInformation(User uid) {

        User userInfo = userMapper.getInformation(uid.getId());
        return userInfo;
    }

    //修改用户个人信息
    @Override
    public void modifyInformation(User user) {
        userMapper.modifyInformation(user);
    }

    @Override
    public HashMap<String, Object> getRightUsers(User user) {
        HashMap<String, Object> datas = new HashMap<>();
        datas.put("userList", userMapper.getRightUsers(user.getState(), user.getStartNum() - 1, 20));
        datas.put("allNum", userMapper.getRightNum(user.getState()));
        return datas;
    }

    @Override
    public void changeUserState(User user) {

        userMapper.changeUserState(user.getState(), user.getId());
    }
}
