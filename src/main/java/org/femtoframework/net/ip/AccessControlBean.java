package org.femtoframework.net.ip;


import org.femtoframework.util.ArrayUtil;

import java.net.InetSocketAddress;
import java.text.ParseException;

/**
 * 访问控制Bean
 *
 * @author fengyun
 * @version 1.00 2004-3-18 16:12:15
 */
public class AccessControlBean
    implements AccessControl
{
    private String allow;
    private String deny;

    private String first = DENY;

    /**
     * 是不是允许列表优先
     */
    private boolean allowFirst = false;

    /**
     * 是不是允许其它所有的
     */
    private boolean allowAll = true;

    private IPMatcher[] allowMatchers = null;
    private IPMatcher[] denyMatchers = null;

    /**
     * @param allow 允许
     */
    public void setAllow(String allow)
    {
        this.allow = allow;
        this.allowMatchers = parse(allow);
    }

    private IPMatcher[] parse(String pattern)
    {
        if (pattern == null) {
            return null;
        }
        try {
            return PatternMatcher.parse(pattern);
        }
        catch (ParseException pe) {
            throw new IllegalArgumentException("Parsing pattern exception:" + pattern, pe);
        }
    }

    /**
     * 设置拒收地址列表
     *
     * @param deny
     */
    public void setDeny(String deny)
    {
        this.deny = deny;
        this.denyMatchers = parse(deny);
    }

    /**
     * 添加允许访问的主机地址
     *
     * @param allow 允许列表
     */
    public void addAllow(String allow)
    {
        IPMatcher[] matchers = parse(allow);
        if (ArrayUtil.isValid(matchers)) {
            IPMatcher[] newMatchers = new IPMatcher[allowMatchers.length + matchers.length];
            System.arraycopy(allowMatchers, 0, newMatchers, 0, allowMatchers.length);
            System.arraycopy(matchers, 0, newMatchers, allowMatchers.length, matchers.length);

            StringBuilder sb = new StringBuilder(this.allow);
            sb.append(';').append(allow);

            this.allowMatchers = newMatchers;
            this.allow = sb.toString();
        }
    }

    /**
     * 添加允许访问的主机地址
     *
     * @param deny 拒收列表
     */
    public void addDeny(String deny)
    {
        IPMatcher[] matchers = parse(deny);
        if (ArrayUtil.isValid(matchers)) {
            IPMatcher[] newMatchers = new IPMatcher[denyMatchers.length + matchers.length];
            System.arraycopy(denyMatchers, 0, newMatchers, 0, denyMatchers.length);
            System.arraycopy(matchers, 0, newMatchers, denyMatchers.length, matchers.length);

            StringBuilder sb = new StringBuilder(this.deny);
            sb.append(';').append(deny);

            this.denyMatchers = newMatchers;
            this.deny = sb.toString();
        }
    }

    /**
     * 判断指定的地址是否在“允许访问”的列表中
     *
     * @param addr
     * @return 是否在“允许访问”的列表中
     */
    public boolean isAllow(String addr)
    {
        return allowMatchers != null && IPUtil.isMatch(allowMatchers, addr);
    }

    /**
     * 判断指定的地址是否在“允许访问”的列表中
     *
     * @param addr
     * @return 是否在“允许访问”的列表中
     */
    public boolean isAllow(InetSocketAddress addr)
    {
        return isAllow(addr.getAddress().getHostAddress());
    }

    /**
     * 判断指定的地址是否在“禁止访问”的列表中
     *
     * @param addr
     * @return 是否在“禁止访问”的列表中
     */
    public boolean isDeny(String addr)
    {
        return denyMatchers != null && IPUtil.isMatch(denyMatchers, addr);
    }

    /**
     * 判断指定的地址是否允许访问（根据配置逻辑来统一检查）
     *
     * @param addr
     * @return 是否允许访问（根据配置逻辑来统一检查）
     */
    public boolean accept(InetSocketAddress addr)
    {
        return accept(addr.getAddress().getHostAddress());
    }

    /**
     * 判断指定的地址是否允许访问（根据配置逻辑来统一检查）
     *
     * @param addr
     * @return 是否允许访问（根据配置逻辑来统一检查）
     */
    public boolean accept(String addr)
    {
        boolean accept = false;
        if (isAllowFirst()) {
            if (isAllow(addr)) { //继续下一步
                accept = true;
            }
            else if (isDeny(addr)) {
                //拒绝
                accept = false;
            }
            else if (isAllowAll()) {
                accept = true;
            }
        }
        else {
            if (isDeny(addr)) {
                accept = false;
            }
            else if (isAllow(addr)) {
                accept = true;
            }
            else if (isAllowAll()) {
                accept = true;
            }
        }
        return accept;
    }

    /**
     * 判断指定的地址是否在“禁止访问”的列表中
     *
     * @param addr
     * @return 是否在“禁止访问”的列表中
     */
    public boolean isDeny(InetSocketAddress addr)
    {
        return isDeny(addr.getAddress().getHostAddress());
    }


    /**
     * 返回允许访问的主机地址
     *
     * @return 允许访问的主机地址
     */
    public String getAllow()
    {
        return allow;
    }

    /**
     * 返回允许访问的主机地址
     *
     * @return 允许访问的主机地址
     */
    public String getDeny()
    {
        return deny;
    }

    /**
     * 设置优先的字段('allow' || 'deny')，默认是'deny'
     *
     * @param first 优先字段
     */
    public void setFirst(String first)
    {
        if (DENY.equals(first)) {
            allowFirst = false;
        }
        else if (ALLOW.equals(first)) {
            allowFirst = true;
        }
        else {
            //还有其它字段
            allowFirst = false;
        }
        this.first = first;
    }

    /**
     * 返回优先字段
     * <p/>
     * return 优先字段
     */
    public String getFirst()
    {
        return first;
    }

    /**
     * 是否允许列表优先
     *
     * @return 是否允许列表优先
     */
    public boolean isAllowFirst()
    {
        return allowFirst;
    }

    /**
     * 是否允许所有其它不在列表中的
     *
     * @param allowAll 是否允许所有的不在列表中的
     */
    public void setAllowAll(boolean allowAll)
    {
        this.allowAll = allowAll;
    }

    /**
     * 是否允许所有其它不在列表中的
     *
     * @return 是否允许所有其它不在列表中的
     */
    public boolean isAllowAll()
    {
        return allowAll;
    }
}
