-- =============================================================================
-- enterprise-platform 初始化数据
-- 默认管理员: admin / 123456（BCrypt）
-- 普通用户:   user  / 123456（BCrypt）
-- 执行前请先执行 01_schema.sql
-- =============================================================================

USE enterprise_platform;

-- -----------------------------------------------------------------------------
-- 角色
-- -----------------------------------------------------------------------------
INSERT INTO sys_role (id, role_code, role_name, sort, status, remark, created_by, updated_by) VALUES
                                                                                                  (1, 'ADMIN', '超级管理员', 1, 1, '拥有全部权限', 1, 1),
                                                                                                  (2, 'USER',  '普通用户',   2, 1, '基础访问权限', 1, 1);

-- -----------------------------------------------------------------------------
-- 用户
-- 密码均为 123456，BCrypt ($2b$10$...)，Spring BCryptPasswordEncoder 可校验
-- -----------------------------------------------------------------------------
INSERT INTO sys_user (id, username, password, nickname, real_name, email, phone, status, remark, created_by, updated_by) VALUES
                                                                                                                             (1, 'admin', '$2b$10$2KwTr1/bkPPQnpykXHEGu.33s0xjgS5AU.EpoZW.A.zBZUeAUHsQO', '管理员', '系统管理员', 'admin@example.com', '13800000001', 1, '默认超级管理员', 1, 1),
                                                                                                                             (2, 'user',  '$2b$10$2KwTr1/bkPPQnpykXHEGu.33s0xjgS5AU.EpoZW.A.zBZUeAUHsQO', '普通用户', '张三', 'user@example.com',  '13800000002', 1, '默认普通用户',   1, 1);

-- 用户角色
INSERT INTO sys_user_role (user_id, role_id) VALUES
                                                 (1, 1),
                                                 (2, 2);

-- -----------------------------------------------------------------------------
-- 权限（permission_code 与 @SaCheckPermission 保持一致）
-- -----------------------------------------------------------------------------
INSERT INTO sys_permission (id, permission_code, permission_name, permission_type, parent_id, path, sort, status, created_by, updated_by) VALUES
-- 系统管理目录
(100, 'system',              '系统管理',   1, 0,   NULL, 1, 1, 1, 1),
-- 用户管理
(110, 'system:user',         '用户管理',   1, 100, '/system/user', 1, 1, 1, 1),
(111, 'system:user:list',    '用户查询',   2, 110, NULL, 1, 1, 1, 1),
(112, 'system:user:add',     '用户新增',   2, 110, NULL, 2, 1, 1, 1),
(113, 'system:user:update',  '用户修改',   2, 110, NULL, 3, 1, 1, 1),
(114, 'system:user:delete',  '用户删除',   2, 110, NULL, 4, 1, 1, 1),
(115, 'system:user:export',  '用户导出',   2, 110, NULL, 5, 1, 1, 1),
(116, 'system:user:status',  '用户状态',   2, 110, NULL, 6, 1, 1, 1),
(117, 'system:user:password','重置密码',   2, 110, NULL, 7, 1, 1, 1),
-- 角色管理
(120, 'system:role',         '角色管理',   1, 100, '/system/role', 2, 1, 1, 1),
(121, 'system:role:list',    '角色查询',   2, 120, NULL, 1, 1, 1, 1),
(122, 'system:role:add',     '角色新增',   2, 120, NULL, 2, 1, 1, 1),
(123, 'system:role:update',  '角色修改',   2, 120, NULL, 3, 1, 1, 1),
(124, 'system:role:delete',  '角色删除',   2, 120, NULL, 4, 1, 1, 1),
-- 权限/菜单
(130, 'system:permission',   '权限管理',   1, 100, '/system/permission', 3, 1, 1, 1),
(131, 'system:permission:list', '权限查询', 2, 130, NULL, 1, 1, 1, 1),
(140, 'system:menu',         '菜单管理',   1, 100, '/system/menu', 4, 1, 1, 1),
(141, 'system:menu:list',    '菜单查询',   2, 140, NULL, 1, 1, 1, 1),
(142, 'system:menu:add',     '菜单新增',   2, 140, NULL, 2, 1, 1, 1),
(143, 'system:menu:update',  '菜单修改',   2, 140, NULL, 3, 1, 1, 1),
(144, 'system:menu:delete',  '菜单删除',   2, 140, NULL, 4, 1, 1, 1),
-- 字典 / 配置
(150, 'system:dict',         '字典管理',   1, 100, '/system/dict', 5, 1, 1, 1),
(151, 'system:dict:list',    '字典查询',   2, 150, NULL, 1, 1, 1, 1),
(160, 'system:config',       '参数配置',   1, 100, '/system/config', 6, 1, 1, 1),
(161, 'system:config:list',  '参数查询',   2, 160, NULL, 1, 1, 1, 1);

-- ADMIN 拥有全部权限
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 1, id FROM sys_permission WHERE deleted = 0;

-- USER 仅拥有列表查询类权限
INSERT INTO sys_role_permission (role_id, permission_id) VALUES
                                                             (2, 111),
                                                             (2, 121),
                                                             (2, 131),
                                                             (2, 141),
                                                             (2, 151),
                                                             (2, 161);

-- -----------------------------------------------------------------------------
-- 菜单（前端路由）
-- -----------------------------------------------------------------------------
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, permission_code, icon, sort, visible, status, created_by, updated_by) VALUES
                                                                                                                                                      (1,  0, '系统管理', 1, '/system',          'Layout',              'system',              'Setting', 1, 1, 1, 1, 1),
                                                                                                                                                      (2,  1, '用户管理', 2, 'user',             'system/user/index',   'system:user:list',    'User',    1, 1, 1, 1, 1),
                                                                                                                                                      (3,  1, '角色管理', 2, 'role',             'system/role/index',   'system:role:list',    'UserFilled', 2, 1, 1, 1, 1),
                                                                                                                                                      (4,  1, '权限管理', 2, 'permission',       'system/permission/index', 'system:permission:list', 'Key', 3, 1, 1, 1, 1),
                                                                                                                                                      (5,  1, '菜单管理', 2, 'menu',             'system/menu/index',   'system:menu:list',    'Menu',    4, 1, 1, 1, 1),
                                                                                                                                                      (6,  1, '字典管理', 2, 'dict',             'system/dict/index',   'system:dict:list',    'Collection', 5, 1, 1, 1, 1),
                                                                                                                                                      (7,  1, '参数配置', 2, 'config',           'system/config/index', 'system:config:list',  'Tools',   6, 1, 1, 1, 1),
                                                                                                                                                      (8,  0, '个人中心', 2, '/profile',         'profile/index',       NULL,                  'User',    9, 1, 1, 1, 1);

-- -----------------------------------------------------------------------------
-- 字典
-- -----------------------------------------------------------------------------
INSERT INTO sys_dict (id, dict_type, dict_name, status, remark, created_by, updated_by) VALUES
                                                                                            (1, 'user_status', '用户状态', 1, '启用/禁用', 1, 1),
                                                                                            (2, 'gender',      '性别',     1, NULL, 1, 1),
                                                                                            (3, 'yes_no',      '是否',     1, NULL, 1, 1);

INSERT INTO sys_dict_item (dict_type, item_label, item_value, sort, status, created_by, updated_by) VALUES
                                                                                                        ('user_status', '禁用', '0', 1, 1, 1, 1),
                                                                                                        ('user_status', '启用', '1', 2, 1, 1, 1),
                                                                                                        ('gender',      '未知', '0', 1, 1, 1, 1),
                                                                                                        ('gender',      '男',   '1', 2, 1, 1, 1),
                                                                                                        ('gender',      '女',   '2', 3, 1, 1, 1),
                                                                                                        ('yes_no',      '否',   '0', 1, 1, 1, 1),
                                                                                                        ('yes_no',      '是',   '1', 2, 1, 1, 1);

-- -----------------------------------------------------------------------------
-- 系统参数
-- -----------------------------------------------------------------------------
INSERT INTO sys_config (config_key, config_value, config_name, config_type, remark, created_by, updated_by) VALUES
                                                                                                                ('sys.account.captchaEnabled', 'true',  '登录是否开启验证码', 0, NULL, 1, 1),
                                                                                                                ('sys.account.loginFailMax',   '5',     '登录失败锁定阈值',   0, '超过后锁定一段时间', 1, 1),
                                                                                                                ('sys.account.loginLockSeconds','900',  '登录锁定秒数',       0, '默认 15 分钟', 1, 1),
                                                                                                                ('sys.user.initPassword',      '123456','用户初始密码',       0, '仅创建用户时使用明文约定，入库必须 BCrypt', 1, 1);
