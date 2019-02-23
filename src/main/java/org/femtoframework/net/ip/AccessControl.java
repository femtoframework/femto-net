package org.femtoframework.net.ip;

/**
 * 访问控制接口
 *
 * @author fengyun
 * @version 1.00 2004-3-18 16:03:07
 */
public interface AccessControl
{
    String ALLOW = "allow";
    String DENY = "deny";

    /**
     * 判断指定的地址是否在“允许访问”的列表中
     *
     * @param addr
     * @return 是否在“允许访问”的列表中
     */
    public boolean isAllow(String addr);

    /**
     * 判断指定的地址是否在“禁止访问”的列表中
     *
     * @param addr
     * @return 是否在“禁止访问”的列表中
     */
    public boolean isDeny(String addr);

    /**
     * 判断指定的地址是否允许访问（根据配置逻辑来统一检查）
     *
     * @param addr
     * @return 是否允许访问（根据配置逻辑来统一检查）
     */
    public boolean accept(String addr);

    /**
     * 返回允许访问的主机地址
     *
     * @return 允许访问的主机地址
     */
    public String getAllow();

    /**
     * 返回允许访问的主机地址
     *
     * @return 允许访问的主机地址
     */
    public String getDeny();

    /**
     * 设置允许访问的主机地址
     *
     * @param allow 允许列表
     */
    public void setAllow(String allow);


    /**
     * 返回允许访问的主机地址
     *
     * @param deny 拒收列表
     */
    public void setDeny(String deny);

    /**
     * 添加允许访问的主机地址
     *
     * @param allow 允许列表
     */
    public void addAllow(String allow);

    /**
     * 添加允许访问的主机地址
     *
     * @param deny 拒收列表
     */
    public void addDeny(String deny);

    /**
     * 设置优先的字段('allow' || 'deny')，默认是'deny'
     *
     * @param first 优先字段
     */
    public void setFirst(String first);

    /**
     * 是否允许列表优先
     *
     * @return 是否允许列表优先
     */
    public boolean isAllowFirst();

    /**
     * 返回优先字段
     * <p/>
     * return 优先字段
     */
    public String getFirst();

    /**
     * 是否允许所有其它不在列表中的
     *
     * @param allowAll 是否允许所有的不在列表中的
     */
    public void setAllowAll(boolean allowAll);

    /**
     * 是否允许所有其它不在列表中的
     *
     * @return 是否允许所有其它不在列表中的
     */
    public boolean isAllowAll();
}
