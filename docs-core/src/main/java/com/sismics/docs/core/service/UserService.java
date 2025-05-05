package com.sismics.docs.core.service;

import com.sismics.docs.core.dao.UserDao;
import com.sismics.docs.core.model.jpa.User;
import java.util.Date;
import java.util.UUID;

/**
 * User service.
 *
 * @author Teedy
 */
public class UserService {
    /**
     * Creates a new user.
     * 
     * @param username          Username
     * @param password          Password
     * @param email             Email
     * @param themeId           Theme ID
     * @param locale            Locale
     * @param createDefaultTags If true, creates default tags
     * @return Created user
     */
    public User create(String username, String password, String email, String themeId, String locale,
            boolean createDefaultTags) throws Exception {
        // 创建用户对象
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        // 设置其他必要字段
        user.setCreateDate(new Date());
        user.setRoleId("user"); // 设置默认角色
        user.setPrivateKey(UUID.randomUUID().toString()); // 生成私钥
        user.setStorageQuota(1000000000L); // 设置默认存储配额
        user.setStorageCurrent(0L); // 初始存储使用量为0
        user.setOnboarding(true); // 新用户需要引导

        // 生成用户ID
        String userId = UUID.randomUUID().toString();

        // 调用UserDao创建用户
        UserDao userDao = new UserDao();
        userId = userDao.create(user, userId);

        // 如果需要创建默认标签
        if (createDefaultTags) {
            // 创建默认标签的代码
        }

        return userDao.getById(userId);
    }
}