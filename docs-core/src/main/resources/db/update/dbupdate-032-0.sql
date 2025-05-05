-- 创建用户注册请求表
create cached table T_USER_REGISTRATION_REQUEST (
  URR_ID_C varchar(36) not null,
  URR_USERNAME_C varchar(50) not null,
  URR_PASSWORD_C varchar(60) not null,
  URR_EMAIL_C varchar(100) not null,
  URR_CREATEDATE_D datetime not null,
  URR_PROCESSDATE_D datetime,
  URR_PROCESSEDBY_C varchar(36),
  URR_STATUS_C varchar(10) not null,
  primary key (URR_ID_C)
);

-- 更新数据库版本号
update T_CONFIG set CFG_VALUE_C = '32' where CFG_ID_C = 'DB_VERSION';