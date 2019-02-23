package org.bolango.net.dns;

import java.util.ArrayList;
import java.util.List;

import org.bolango.tools.nutlet.Nutlet;

/**
 * 测试DnsUtil
 *
 * @author fengyun
 * @version 1.00 2007-1-18 15:21:33
 */
public class DnsUtilTest extends Nutlet
{
    public void testQuery() throws Exception
    {
        List<QueryRequest> requests = new ArrayList<QueryRequest>();
        requests.add(new QueryRequest("mail.bolango.cn", QType.A));
        requests.add(new QueryRequest("www.bolango.cn", QType.A));
        requests.add(new QueryRequest("smtp.bolango.cn", QType.A));
        requests.add(new QueryRequest("pop.bolango.cn", QType.A));
        requests.add(new QueryRequest("svn.bolango.cn", QType.A));
        requests.add(new QueryRequest("ftp.bolango.cn", QType.A));

        List<QueryResponse> responses = DnsUtil.query(requests);
        assertNotNull(responses);
        Nutlet.assertEquals(responses.size(), requests.size());
        for (QueryResponse response : responses) {
            System.out.println("Response:" + response);
        }
    }
}