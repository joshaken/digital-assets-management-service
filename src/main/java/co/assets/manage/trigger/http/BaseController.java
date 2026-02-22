package co.assets.manage.trigger.http;

public class BaseController {
    protected Long getCurrentEnterpriseId() {
        //从token中获取企业ID，这里写死返回企业ID 1
        return 1L;
    }

}
