package co.assets.manage.trigger.http;

public class BaseController {
    protected Long getCurrentTenantId() {
        //从token中获取租户ID，这里写死返回ID 1
        return 1L;
    }

}
